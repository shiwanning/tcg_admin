package com.tcg.admin.controller;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.State;
import com.tcg.admin.model.Task;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.service.TaskHistoryService;
import com.tcg.admin.service.WorkFlowService;
import com.tcg.admin.service.impl.IMService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.TaskQueryTO;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.ExportExcel;
import com.tcg.admin.utils.ExportTaskExcelUtil;
import com.tcg.admin.utils.ReportTO;

@RestController
@RequestMapping(value = {"/resources/task", "/resources/taskHistory"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskResource.class);
	
	@Autowired
	TaskHistoryService taskHistoryService;

	@Autowired
	private WorkFlowService workflowManager;

	@Autowired
	private IMService imService;

	@Autowired
	private OperatorLoginService operatorLoginService;

	@Autowired
    private MerchantService merchantService;
    
	@Autowired
    private RoleService roleService;
	
	
	@GetMapping
	public JsonResponseT<Page<Task>> getAll(@RequestHeader(value = "Authorization", required = false)  String token,
	        @RequestParam(value = "merchantCode", required = false)String merchantCode,
						   @RequestParam(value = "ownerName", required = false)String ownerName,
						   @RequestParam(value = "startDate", required = false)String startDate,
						   @RequestParam(value = "endDate", required = false)String endDate,
						   @RequestParam(value = "status", required = false)String status,
						   @RequestParam(value = "stateType", required = false)String stateType,
						   @RequestParam(value = "stateId", required = false)Integer stateId,
						   @RequestParam(value = "pageNo", required = false) int pageNo,
						   @RequestParam(value = "pageSize", required = false) int pageSize,
						   @RequestParam(value = "defStat", required = false) List<String> defStat) {
		
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        List<Integer> merchantIds = Lists.newLinkedList();
        
        if(merchantCode == null && !roleService.isSysAdmin(userInfo)) {
            // 取得订阅的品牌
            List<Merchant> merchants = merchantService.queryOperatorMerchants(true);
            merchantIds = extract(merchants, on(Merchant.class).getMerchantId());
        }
        
        if(merchantCode != null) {
            merchantIds.add(merchantService.findMerchantByMerchantCode(merchantCode).getMerchantId());
        }
	    
	    TaskQueryTO taskQueryTO = new TaskQueryTO();
		taskQueryTO.setMerchantIds(merchantIds);
		taskQueryTO.setOwnerName(ownerName);
		taskQueryTO.setStartDate(startDate);
		taskQueryTO.setEndDate(endDate);
		taskQueryTO.setStatus(status);
		taskQueryTO.setStateType(stateType);
		taskQueryTO.setStateId(stateId);
		taskQueryTO.setPageNo(pageNo);
		taskQueryTO.setPageSize(pageSize);
		taskQueryTO.setDefStat(defStat);
		JsonResponseT<Page<Task>> response = new JsonResponseT<>(true);
		response.setValue(taskHistoryService.getAll(taskQueryTO));
		return response;
	}

	@GetMapping("/stateList")
	public JsonResponseT<List<State>> getStateList(@RequestHeader(value = "Authorization", required = false)  String token,
								 @RequestParam(value = "type", required = false)String type) {
		JsonResponseT<List<State>> response = new JsonResponseT<>(true);
		response.setValue(taskHistoryService.getStateList(type));
		return response;
	}

	@PostMapping(value = "/export", produces = "application/vnd.ms-excel")
	public void getAllReport(HttpServletResponse httpResponse,
							 @RequestBody TaskQueryTO taskQueryTO) {

		Page<Task> result  = taskHistoryService.getAll(taskQueryTO);
		String language = RequestHelper.getLanguage();
		Map<Integer, ReportTO> data = Maps.newHashMap();
		data.put(1, ExportTaskExcelUtil.returnTaskTO(taskQueryTO.getStartDate(), taskQueryTO.getEndDate(), ExportTaskExcelUtil.returnTaskArray(result), language));
		data.put(2, ExportTaskExcelUtil.returnTransactionTO(taskQueryTO.getStartDate(), taskQueryTO.getEndDate(), ExportTaskExcelUtil.returnTaskTransactionArray(result),language));

		ExportExcel.exportExcel(httpResponse,"none.xls",data);
	}

	/**
	 * @Description : Withdraw a claimed task of others
	 * **/
	@GetMapping("/counterClaimTask")
	public JsonResponseT<Object> counterClaimTask(@RequestParam(value = "taskId", required = false) int taskId) {

		JsonResponseT<Object> jsonResponseT = new JsonResponseT<>(true);
		workflowManager.counterClaimTask(operatorLoginService.getSessionUser(RequestHelper.getToken()), taskId);
		try {
			imService.uploadChanges(taskId, true);
		} catch (Exception e) {
			LOGGER.error("ERROR IN IM SERVICE", e);
		}
		jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

		return jsonResponseT;

	}

}
