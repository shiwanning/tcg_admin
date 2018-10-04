package com.tcg.admin.service.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Operator;
import com.tcg.admin.persistence.BehaviorLogRepositoryCustom;
import com.tcg.admin.persistence.springdata.IBehaviorLogRepository;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.service.BehaviorLogService;
import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.service.StateService;
import com.tcg.admin.to.NoneAdminInfo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.VerificationData;
import com.tcg.admin.utils.HostAddressUtils;
import com.tcg.admin.utils.StringTools;


@Service
@Transactional
public class BehaviorLogServiceImpl implements BehaviorLogService {

    /**
	 * 格式化時間樣式
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BehaviorLogServiceImpl.class);

	@Autowired
	private IBehaviorLogRepository behaviorLogRepository;

	@Autowired
	private BehaviorLogRepositoryCustom behaviorLogRepo;

	@Autowired
	private IMenuItemRepository menuItemRepository;

	@Autowired
    private OperatorAuthenticationService operatorAuthService;

	@Autowired
	private MenuItemService menuItemService;

	@Autowired
	private StateService stateService;

	@Autowired
	private IRoleMenuPermissionRepository roleMenuRepository;

	@Autowired
	private IMerchantOperatorRepository merchantOperatorRepository;

	@Autowired
	private MerchantService merchantService;
	/**
	 * saveBehaviorLog
	 * @param req
	 * @param behaviorRequestWrapper
	 * @param customerName
	 * @param startDate
     * @param endTime
     */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveBehaviorLog(HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,  Date startDate, Date endTime, String merchantCode, MenuItem menuItem, String pathStrings) {
		BehaviorLog behaviorLog = getBehaviorLog(req, behaviorRequestWrapper, customerName, startDate, endTime, merchantCode, menuItem);
		if(!StringUtils.equals(behaviorLog.getOperatorName(),"not-login") ){
			this.replaceParameters(behaviorLog, pathStrings);
			LOGGER.info("[{}][{}][{}][{}][{}]",behaviorLog.getOperatorName(),behaviorLog.getIp(),behaviorLog.getBrowser(),behaviorLog.getUrl(),behaviorLog.getParameters());
			String parameters = behaviorLog.getParameters();
			if(StringUtils.isNotBlank(parameters) && parameters.getBytes().length > 2950){
				parameters = parameters.substring(0, 2950);
				behaviorLog.setParameters(parameters);
			}
			behaviorLogRepository.saveAndFlush(behaviorLog);
		}
	}

	/**
	 * replace parameters if behavior parameters is empty
	 * @param behaviorLog
	 * @param pathStrings
     */
	private void replaceParameters(BehaviorLog behaviorLog, String pathStrings){
		if((StringUtils.isBlank(behaviorLog.getParameters()) || StringUtils.equals(behaviorLog.getParameters(),"{}")) && StringUtils.isNotEmpty(pathStrings)){
			behaviorLog.setParameters(pathStrings);
		}
	}

	private BehaviorLog getBehaviorLog(HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,  Date startDate, Date endTime, String merchantCode, MenuItem menuItem) {
		String uri = req.getRequestURI();

		//set up behaviorLog
		BehaviorLog behaviorLog = new BehaviorLog();
		behaviorLog.setUrl(uri);
		behaviorLog.setIp(HostAddressUtils.getClientIpAddr(req));
		behaviorLog.setStartProcessDate(startDate);
		behaviorLog.setEndProcessDate(endTime);
		setBehaviorLogParameters(behaviorLog, behaviorRequestWrapper);
		behaviorLog.setOperatorName(customerName);
		behaviorLog.setMerchantCode(merchantCode);
		//set behaviorLog:resourceType, resourceId
		//do not log those in ignore list

		if(menuItem != null){
			behaviorLog.setResourceType(Integer.parseInt(menuItem.getAccessType()));
			behaviorLog.setResourceId(menuItem.getMenuId());
		}else if(uri.startsWith("/tcg-admin/resources/subsystem/")){
			//[verification/basic,verification/url,workflow/task]
			this.subSystem(behaviorLog, uri, req);
		}else if(uri.contains("/tcg-admin/resources/operator_sessions")){
			//[login,logout];set ResourceType, resourceId, operatorName
			this.operatorSessions(behaviorLog, uri, behaviorRequestWrapper, req);
		}
		return behaviorLog;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void writeBehaviorLog(HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,  Date startDate, Date endTime, String merchantCode) {
		BehaviorLog behaviorLog = getBehaviorLog(req, behaviorRequestWrapper, customerName, startDate, endTime, merchantCode, null);
		LOGGER.info("[{}][{}][{}][{}][{}]",behaviorLog.getOperatorName(),behaviorLog.getIp(),behaviorLog.getBrowser(),behaviorLog.getUrl(),behaviorLog.getParameters());
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<BehaviorLog> queryBehaviorLog(String merchant,String username, int actionType, List<Integer> resourceIdList, String keyword, String startDateTime, String endDateTime, int pageNo, int pageSize) {
		NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
		// get menuIds of operator
		boolean isAdmin = noneAdminInfo.isAdmin();

		List<Integer> menuIds = isAdmin ? menuItemRepository.queryMenuIdsForAdmin():roleMenuRepository.findMenuIdListByOperator(noneAdminInfo.getOperatorId());
		// get merchants of operator
		List<String> merchantCodeList = isAdmin ? null : merchantOperatorRepository.findMerchantCodeListByOperatorId(noneAdminInfo.getOperatorId());

		Pageable pageable = new PageRequest(pageNo, pageSize);
		return behaviorLogRepo.findUserBehaviorLog(merchant, username, actionType, resourceIdList, keyword, startDateTime, endDateTime, menuIds, merchantCodeList ,pageable);
	}

	@Override
	public Page<BehaviorLog> queryBehaviorLogOfSystem(String merchant,String username, int actionType, List<Integer> resourceIdList, String keyword, String startDateTime, String endDateTime, int pageNo, int pageSize) {
		NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
		// get menuIds of operator

		List<Integer> menuIds = noneAdminInfo.isAdmin() ? menuItemRepository.queryMenuIdsForAdmin():menuItemRepository.queryMenuIdsForSystem();

		Pageable pageable = new PageRequest(pageNo, pageSize);
		return behaviorLogRepo.findUserBehaviorLogForSystem(merchant, username, actionType, resourceIdList, keyword, startDateTime, endDateTime, menuIds, pageable);

	}

	/**
	 * from subSystem
	 * @param behaviorLog
	 * @param uri
	 * @param req
     * @return
     */
	private BehaviorLog subSystem(BehaviorLog behaviorLog,String uri, HttpServletRequest req){
		if(StringTools.equals(uri,"/tcg-admin/resources/subsystem/verification/basic")){
			Integer menuId = Integer.valueOf(req.getParameter("menuId"));
			MenuItem resource = menuItemRepository.findOne(menuId);
			behaviorLog.setResourceType(resource.getAccessType()!= null ? Integer.parseInt(resource.getAccessType().trim()):VIEW);
			behaviorLog.setResourceId(resource.getMenuId());
		}else if(StringTools.equals(uri,"/tcg-admin/resources/subsystem/verification/url")){
			MenuItem resource = menuItemRepository.findByUrl(req.getParameter("uri"));
			behaviorLog.setResourceType(resource.getAccessType()!= null ? Integer.parseInt(resource.getAccessType().trim()):VIEW);
			behaviorLog.setResourceId(resource.getMenuId());
		}else if(uri.startsWith("/tcg-admin/resources/subsystem/workflow/task")){
			List<String> taskList = Arrays.asList(uri.split("/"));
			String taskId = taskList.get(taskList.size()-1);

			//depend on the path 1."/task" (post, put) and 2.others
			if(StringTools.equals(taskId,"task")){
				String requestBody = behaviorLog.getParameters(); //subsystemTaskId=1600&stateId=200&taskDescription=create%20merchant4&merchantCode=SYSTEM&isSystem=false
				List<String> formParameters = Arrays.asList(requestBody.split("&"));//subsystemTaskId=1600,stateId=200,taskDescription=create%20merchant4,merchantCode=SYSTEM&isSystem=false

				//create Task
				if("PUT".equals(req.getMethod())){
					Integer stateId = 0;
					for(String para: formParameters){
						if(StringTools.startsWith(para,"stateId")){
							stateId = Integer.valueOf(para.split("=")[1]);
							break;
						}
					}
					behaviorLog.setResourceType(EDIT);
					behaviorLog.setResourceId(stateService.getState(stateId).getMenuId());
				}

				//update Task
				if("POST".equals(req.getMethod())){
					for(String para: formParameters){
						if(StringTools.startsWith(para,"taskId")){
							taskId = para.split("=")[1];
							break;
						}
					}
					this.setBehaviorLog(behaviorLog,taskId);
				}
			}else{
				this.setBehaviorLog(behaviorLog,taskId);
			}
		}

		return behaviorLog;
	}

	private BehaviorLog setBehaviorLog(BehaviorLog behaviorLog, String taskId){
			MenuItem menuItem = menuItemService.getMenuItemViaTaskId(Integer.parseInt(taskId));
			if(menuItem != null){
				if(StringTools.isNotEmpty(menuItem.getAccessType())) {
					behaviorLog.setResourceType(TASK);
				}
				behaviorLog.setResourceId(menuItem.getMenuId());
			}
		behaviorLog.setParameters("TaskId: " +taskId);
		return behaviorLog;
	}

	/**
	 * operator_sessions
	 * login: /operator_sessions
	 * logout: /operator_sessions/{token}
	 * @param behaviorLog
	 * @param uri
	 * @param behaviorRequestWrapper
	 * @param req
     * @return
     */

	private BehaviorLog operatorSessions(BehaviorLog behaviorLog, String uri, BehaviorRequestWrapper behaviorRequestWrapper, HttpServletRequest req){
		//login
		if(StringTools.equals(uri,"/tcg-admin/resources/operator_sessions")){
			String requestBody = behaviorRequestWrapper.getBody();
			Map<String, Object> requestBodyJson;
			try {
				requestBodyJson = new ObjectMapper().readValue(requestBody, new TypeReference<Map<String,Object>>(){});
			} catch (IOException e) {
				throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION,"Bad session request", e);
			}
			String operatorName;

			if(requestBodyJson.get("operatorName") == null) {
				operatorName = operatorAuthService.getOperatorByToken(requestBodyJson.get("token").toString()).getUser().getOperatorName();
			}else{
				operatorName = requestBodyJson.get("operatorName").toString();
			}
			behaviorLog.setOperatorName(operatorName);
			behaviorLog.setBrowser(req.getHeader("User-Agent"));
			behaviorLog.setResourceType(LOGIN);
		//logout
		}else if(uri.startsWith("/tcg-admin/resources/operator_sessions/")){
			behaviorLog.setResourceType(LOGOUT);
		}

		return behaviorLog;
	}

	/**
	 * setBehaviorLogParameters
	 * @param behaviorLog
	 * @param behaviorRequestWrapper
     * @return
     */
	private BehaviorLog setBehaviorLogParameters(BehaviorLog behaviorLog, BehaviorRequestWrapper behaviorRequestWrapper){
		StringBuilder urlParams = new StringBuilder();

		if ("POST".equals(behaviorRequestWrapper.getMethod()) || "PUT".equals(behaviorRequestWrapper.getMethod())) {
			String requestBody = behaviorRequestWrapper.getBody();
			requestBody = requestBody.replaceFirst("\"password\"\\s?:\\s?\"[^\"]+\"", "\"password\":\"########\"");
			requestBody = requestBody.replaceFirst("\"confirmNewPassword\"\\s?:\\s?\"[^\"]+\"", "\"confirmNewPassword\":\"########\"");
			requestBody = requestBody.replaceFirst("\"newPassword\"\\s?:\\s?\"[^\"]+\"", "\"newPassword\":\"########\"");
			requestBody = requestBody.replaceFirst("\"confirmNewPassword\"\\s?:\\s?\"[^\"]+\"", "\"confirmNewPassword\":\"########\"");
			requestBody = requestBody.replaceFirst("\"confirmPassword\"\\s?:\\s?\"[^\"]+\"", "\"confirmPassword\":\"########\"");
			behaviorLog.setParameters(requestBody);
		}else if("GET".equals(behaviorRequestWrapper.getMethod()) || "DELETE".equals(behaviorRequestWrapper.getMethod())){
			for(Entry<String, String[]> parameter: behaviorRequestWrapper.getParameterMap().entrySet()){
				urlParams.append("key=").append(parameter.getKey()).append(":values=").append(Arrays.toString(parameter.getValue())).append("; ");
			}
			behaviorLog.setParameters(urlParams.toString());
		}
		return behaviorLog;
	}

	@Override
	public void saveBehaviorLog(VerificationData verificationData, MenuItem menuItem ){
		String accessType = menuItem.getAccessType();
		UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
		String params = verificationData.getParams();
		if(StringUtils.isNotBlank(params) && params.getBytes().length > 2950){
            byte[] newBytes= new byte[2950];
            System.arraycopy(params.getBytes(), 0, newBytes, 0, 2950);
            params = new String(newBytes);
		}
		BehaviorLog behaviorLog = new BehaviorLog();
		behaviorLog.setIp(verificationData.getIp());
		behaviorLog.setOperatorName(userInfo.getUserName());
		behaviorLog.setUrl(menuItem.getUrl());
		behaviorLog.setResourceType(Integer.parseInt(accessType));
		behaviorLog.setParameters(params);
		behaviorLog.setStartProcessDate(new Date());
		behaviorLog.setEndProcessDate(new Date());
		behaviorLog.setResourceId(menuItem.getMenuId());
		behaviorLog.setMerchantCode(verificationData.getMerchant());

		behaviorLogRepository.saveAndFlush(behaviorLog);
	}
}
