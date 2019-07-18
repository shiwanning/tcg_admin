package com.tcg.admin.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Lists;
import com.tcg.admin.persistence.springdata.*;
import com.tcg.admin.to.*;
import com.tcg.admin.utils.IpUtils;
import org.apache.commons.lang.ObjectUtils;
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
import com.tcg.admin.common.constants.BehaviorLogConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Operator;
import com.tcg.admin.persistence.BehaviorLogRepositoryCustom;
import com.tcg.admin.service.BehaviorLogService;
import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.StateService;
import com.tcg.admin.to.condition.BehaviorCondition;
import com.tcg.admin.utils.AuthorizationUtils;
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
	private MenuItemService menuItemService;

	@Autowired
	private StateService stateService;

	@Autowired
	private IRoleMenuPermissionRepository roleMenuRepository;

	@Autowired
	private IMerchantOperatorRepository merchantOperatorRepository;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private BehaviorLogRepositoryCustom behaviorLogRepositoryCustom;

	@Autowired
	private IOperatorRepository operatorRepository;
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
	public void saveBehaviorLog(HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,  Date date, String merchantCode, MenuItem menuItem, String pathStrings) {
		BehaviorLog behaviorLog = getBehaviorLog(req, behaviorRequestWrapper, customerName, date, merchantCode, menuItem);
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

	private BehaviorLog getBehaviorLog(HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,  Date date, String merchantCode, MenuItem menuItem) {
		String uri = req.getRequestURI();

		//set up behaviorLog
		BehaviorLog behaviorLog = new BehaviorLog();
		behaviorLog.setUrl(uri);
		behaviorLog.setIp(HostAddressUtils.getClientIpAddr(req));
		behaviorLog.setStartProcessDate(date);
		behaviorLog.setEndProcessDate(date);
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
		}else if(uri.contains("/tcg-admin/resources/operator_sessions") || uri.contains("/google-otp-login")){
			//[login,logout];set ResourceType, resourceId, operatorName
			this.operatorSessions(behaviorLog, uri, behaviorRequestWrapper, req);
		}else if(uri.contains("/announcement/markAsRead")){
			behaviorLog.setResourceType(8);
		}
		return behaviorLog;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void writeBehaviorLog(HttpServletRequest req, BehaviorRequestWrapper behaviorRequestWrapper, String customerName,  Date date, String merchantCode) {
		BehaviorLog behaviorLog = getBehaviorLog(req, behaviorRequestWrapper, customerName, date, merchantCode, null);
		LOGGER.info("[{}][{}][{}][{}][{}]",behaviorLog.getOperatorName(),behaviorLog.getIp(),behaviorLog.getBrowser(),behaviorLog.getUrl(),behaviorLog.getParameters());
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<BehaviorLog> queryBehaviorLog(BehaviorCondition condition, int pageNo, int pageSize) {
		NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
		// get menuIds of operator
		boolean isAdmin = noneAdminInfo.isAdmin();

		List<Integer> menuIds = isAdmin ? menuItemRepository.queryMenuIdsForAdmin():roleMenuRepository.findMenuIdListByOperator(noneAdminInfo.getOperatorId());
		// get merchants of operator
		List<String> merchantCodeList = isAdmin ? null : merchantOperatorRepository.findMerchantCodeListByOperatorId(noneAdminInfo.getOperatorId());

		Pageable pageable = new PageRequest(pageNo, pageSize);
		condition.setMenuIds(menuIds);
		condition.setMerchantCodeList(merchantCodeList);
		return behaviorLogRepo.findUserBehaviorLog(condition ,pageable);
	}

	@Override
	public Page<BehaviorLog> queryBehaviorLogOfSystem(BehaviorCondition condition, int pageNo, int pageSize) {
		NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
		// get menuIds of operator

		List<Integer> menuIds = noneAdminInfo.isAdmin() ? menuItemRepository.queryMenuIdsForAdmin():menuItemRepository.queryMenuIdsForSystem();

		condition.setMenuIds(menuIds);
		Pageable pageable = new PageRequest(pageNo, pageSize);
		return behaviorLogRepo.findUserBehaviorLogForSystem(condition, pageable);

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
			behaviorLog.setResourceType(resource.getAccessType()!= null ? Integer.parseInt(resource.getAccessType().trim()):BehaviorLogConstant.VIEW);
			behaviorLog.setResourceId(resource.getMenuId());
		}else if(StringTools.equals(uri,"/tcg-admin/resources/subsystem/verification/url")){
			MenuItem resource = menuItemRepository.findByUrl(req.getParameter("uri"));
			behaviorLog.setResourceType(resource.getAccessType()!= null ? Integer.parseInt(resource.getAccessType().trim()):BehaviorLogConstant.VIEW);
			behaviorLog.setResourceId(resource.getMenuId());
		}else if(uri.startsWith("/tcg-admin/resources/subsystem/workflow/task")){
			List<String> taskList = Arrays.asList(uri.split("/"));
			if(!"task".equals(taskList.get(taskList.size()-1))){
				this.setBehaviorLog(behaviorLog, "task");
			} else {
				setBehaviorLogByTask(req.getMethod(), behaviorLog);
			}
		}

		return behaviorLog;
	}

	private void setBehaviorLogByTask(String method, BehaviorLog behaviorLog) {
		String requestBody = behaviorLog.getParameters(); //subsystemTaskId=1600&stateId=200&taskDescription=create%20merchant4&merchantCode=SYSTEM&isSystem=false
		List<String> formParameters = Arrays.asList(requestBody.split("&"));//subsystemTaskId=1600,stateId=200,taskDescription=create%20merchant4,merchantCode=SYSTEM&isSystem=false
		//create Task
		if("PUT".equals(method)){
			Integer stateId = 0;
			for(String para: formParameters){
				if(StringTools.startsWith(para,"stateId")){
					stateId = Integer.valueOf(para.split("=")[1]);
					break;
				}
			}
			behaviorLog.setResourceType(BehaviorLogConstant.EDIT);
			behaviorLog.setResourceId(stateService.getState(stateId).getMenuId());
		}

		//update Task
		if("POST".equals(method)){
			String taskId = "";
			for(String para: formParameters){
				if(StringTools.startsWith(para,"taskId")){
					taskId = para.split("=")[1];
					break;
				}
			}
			this.setBehaviorLog(behaviorLog, taskId);
		}
	}

	private BehaviorLog setBehaviorLog(BehaviorLog behaviorLog, String taskId){
			MenuItem menuItem = menuItemService.getMenuItemViaTaskId(Integer.parseInt(taskId));
			if(menuItem != null){
				if(StringTools.isNotEmpty(menuItem.getAccessType())) {
					behaviorLog.setResourceType(BehaviorLogConstant.TASK);
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
		if(StringTools.equals(uri,"/tcg-admin/resources/operator_sessions") || uri.endsWith("/google-otp-login")){
			String requestBody = behaviorRequestWrapper.getBody();
			Map<String, Object> requestBodyJson;
			try {
				requestBodyJson = new ObjectMapper().readValue(requestBody, new TypeReference<Map<String,Object>>(){});
			} catch (IOException e) {
				throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR,"Bad session request", e);
			}
			String operatorName;

			if(requestBodyJson.get("operatorName") == null) {
				operatorName = AuthorizationUtils.getSessionUser(requestBodyJson.get("token").toString()).getUser().getOperatorName();
			}else{
				operatorName = requestBodyJson.get("operatorName").toString();
			}
			behaviorLog.setOperatorName(operatorName);
			behaviorLog.setBrowser(req.getHeader("User-Agent"));
			behaviorLog.setResourceType(BehaviorLogConstant.LOGIN);
		//logout
		}else if(uri.startsWith("/tcg-admin/resources/operator_sessions/")){
			behaviorLog.setResourceType(BehaviorLogConstant.LOGOUT);
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
		if(!"0".equals(accessType.trim())){
			UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
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

	@Override
	public Map<String, Object> statisticalLoginStatus(String userName, int pageNo, int pageSize, Date checkDate) {

		Operator tempOperator = operatorRepository.findByOperatorName(userName);
		if (tempOperator == null) {
			throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
		}
		Map<String, Object> objectMap = new HashMap<String, Object>();
		String formatDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(checkDate);
		List<Object[]> objects = behaviorLogRepositoryCustom.statisticalLoginStatus(userName, formatDate);
		LinkedList<StatisticalLoginStatus> queryList = Lists.newLinkedList();
		for(Object[] obj : objects){
			StatisticalLoginStatus statistical = new StatisticalLoginStatus();
			statistical.setIpAddress(ObjectUtils.toString(obj[0]));
			statistical.setErrorCount(Integer.parseInt(ObjectUtils.toString(obj[1])));
			statistical.setSuccessCount(Integer.parseInt(ObjectUtils.toString(obj[2])));
			queryList.add(statistical);
		}

		LinkedList<StatisticalLoginStatus> returnList = Lists.newLinkedList();

		if(!queryList.isEmpty()) {
			for (int i = (pageNo - 1) * pageSize; i < pageNo * pageSize; i++) {
				if (i < queryList.size()){
					StatisticalLoginStatus sta = queryList.get(i);
					if("127.0.01".equals(sta.getIpAddress())){
						sta.setArea("localhost");
					}else{
						sta.setArea(sta.getIpAddress() == null ? "" : IpUtils.getArea(sta.getIpAddress()));
					}
					returnList.add(queryList.get(i));
				}

			}
		}
		objectMap.put("totalCount", queryList.size());
		objectMap.put("returnList", returnList);
		return objectMap;
	}

}
