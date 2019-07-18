package com.tcg.admin.controller.subsystem;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.primitives.Ints;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.core.BehaviorRequestWrapper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Task;
import com.tcg.admin.model.Transaction;
import com.tcg.admin.service.TransactionService;
import com.tcg.admin.service.WorkFlowService;
import com.tcg.admin.service.impl.IMService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.TaskTO;
import com.tcg.admin.to.UpdateTaskResult;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.AuthorizationUtils;

import io.swagger.annotations.Api;

@RestController
@RequestMapping(value = "/resources/subsystem/workflow", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Subsystem", description = "Workflow Management")
public class WorkflowSubsystemResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowSubsystemResource.class);
	
    @Autowired
	private WorkFlowService workflowManager;

    @Autowired
	private OperatorLoginService operatorLoginService;

    @Autowired
	private IMService imService;
    
	@Autowired
	private TransactionService transactionService;

	/**
	 * if modify path, need to modify behaviorLogServiceBean-subSystem also.
	 * @throws UnsupportedEncodingException 
     */
    @PutMapping("/task")
	public JsonResponseT<Task> createTask(
	        HttpServletRequest request,
                               @RequestParam(value = "subsystemTaskId", required = false) String subsysTaskId, 
                               @RequestParam(value = "stateId", required = false) Integer stateId,
                               @RequestParam(value = "taskDescription", required = false) String taskDescription, 
                               @RequestParam(value = "merchantCode", required = false) String merchantCode,
                               @RequestParam(value = "isSystem", required = false) boolean isSystem) {

    	String localSubsysTaskId = subsysTaskId;
    	Integer localStateId = stateId;
    	String localTaskDescription = taskDescription;
    	String localMerchantCode =merchantCode ;
    	boolean localIsSystem = isSystem;
    	
        if(stateId == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localSubsysTaskId = parameterMap.get("subsystemTaskId");
            localStateId = parameterMap.get("stateId") == null ? null : Ints.tryParse(parameterMap.get("stateId"));
            localTaskDescription = parameterMap.get("taskDescription");
            localMerchantCode = parameterMap.get("merchantCode");
            localIsSystem = Boolean.valueOf(parameterMap.get("isSystem"));
        }
        
		JsonResponseT<Task> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo;
		TaskTO taskTO = new TaskTO();
		taskTO.setSubsysTaskId(localSubsysTaskId);
		taskTO.setStateId(localStateId);
		taskTO.setTaskDescription(localTaskDescription);
		taskTO.setMerchantCode(localMerchantCode);
		if (!localIsSystem) {
			userInfo = RequestHelper.getCurrentUser();
			taskTO.setOperatorName(userInfo.getUser().getOperatorName());
		}
		Task task = workflowManager.createTask(taskTO);
		response.setValue(task);
		callImServiceeByTaskId(task.getTaskId(), true);
		return response;
	}

    @PostMapping("/task")
	public JsonResponse updateTask( 
	        HttpServletRequest request,
	        @RequestParam(value="taskId", required = false) Integer taskId,  
	        @RequestParam(value="stateId", required = false) Integer stateId,
	        @RequestHeader(value = "close", required = true, defaultValue="false") Boolean close			) {
        
    	Integer localTaskId = taskId;
    	Integer localStateId = stateId;
    	Boolean localClose = close;
    	
        if(stateId == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localStateId = parameterMap.get("stateId") == null ? null : Ints.tryParse(parameterMap.get("stateId"));
            localTaskId = parameterMap.get("taskId") == null ? null : Ints.tryParse(parameterMap.get("taskId"));
            localClose = Boolean.valueOf(parameterMap.get("close"));
        }
        
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		TaskTO taskTO = new TaskTO();
		taskTO.setTaskId(localTaskId);
		taskTO.setStateId(localStateId);
		taskTO.setClose(localClose);
		workflowManager.updateTask(taskTO, userInfo);
		callImServiceeByTaskId(taskId, true);
		return new JsonResponse(true);
	}

	/**
	 * in case other teams did not modify , use queryParam stateId (the next state ,ex reject or approve)
	 * @throws UnsupportedEncodingException 
     */
    @DeleteMapping("/task/{taskId}")
	public JsonResponse closeTask(
	        HttpServletRequest request,
	        @PathVariable("taskId") Integer taskId, 
	        @RequestHeader(value = "Authorization", required = false)  String token, 
	        @RequestParam(value = "stateId", required = false) Integer nextStateId) {
        
    	Integer localNextStateId = nextStateId;
    	
        if(nextStateId == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localNextStateId = parameterMap.get("stateId") == null ? null : Ints.tryParse(parameterMap.get("stateId"));
        }
        
		UserInfo<Operator> userInfo;
		userInfo = AuthorizationUtils.getSessionUser(token);
		TaskTO taskTO = new TaskTO();
		taskTO.setTaskId(taskId);
		taskTO.setStateId(localNextStateId);
		workflowManager.closeTask(taskTO, userInfo);
		callImServiceeByTaskId(taskId, false);
		return new JsonResponse(true);
	}

	/*
	處理異常情況的API,如果產生異常的task, 透過這個api可以關掉(just for create merchant)
	 */
    @DeleteMapping("/merchantId/{merchantId}")
	public JsonResponse closeTaskByMerchantId(@PathVariable("merchantId") String merchantId) {
		UserInfo<Operator> userInfo;
		userInfo = RequestHelper.getCurrentUser();
		TaskTO taskTO = new TaskTO();
		taskTO.setSubsysTaskId(merchantId);
		Task task = workflowManager.closeTaskByMerchantId(taskTO, userInfo);
		callImServiceeByTaskId(task.getTaskId(), true);
		return new JsonResponse(true);
	}

    @DeleteMapping("/task/closeTaskWOChecking/{taskId}")
	public JsonResponse closeTask(@PathVariable("taskId") Integer taskId) {
		UserInfo<Operator> userInfo = new UserInfo<>();
		Operator operator = new Operator();
		operator.setOperatorName("administrator");
		userInfo.setUser(operator);
		TaskTO taskTO = new TaskTO();
		taskTO.setTaskId(taskId);
		workflowManager.closeTask(taskTO,userInfo);
		imService.uploadChanges(taskId, false);
		return new JsonResponse(true);
	}

    @DeleteMapping("/task/closeTask/{taskId}")
    public JsonResponse closeTaskById(
            HttpServletRequest request,
            @PathVariable("taskId") Integer taskId, 
            @RequestParam(value="stateId", required = false) Integer nextStateId) {
    	Integer localNextStateId = nextStateId;
        if(nextStateId == null) {
            BehaviorRequestWrapper br = (BehaviorRequestWrapper) request;
            String body = br.getBody();
            Map<String, String> parameterMap = RequestHelper.getMapFromUrlFormat(body);
            localNextStateId = parameterMap.get("stateId") == null ? null : Ints.tryParse(parameterMap.get("stateId"));
        }
        
        
        UserInfo<Operator> userInfo = new UserInfo<>();
        Operator operator = new Operator();
        operator.setOperatorName("administrator");
        userInfo.setUser(operator);
        TaskTO taskTO = new TaskTO();
        taskTO.setTaskId(taskId);
		taskTO.setStateId(localNextStateId);
        workflowManager.closeTask(taskTO,userInfo);
        imService.uploadChanges(taskId, false);
        return new JsonResponse(true);
    }

    @PostMapping("/task/lock/{taskId}")
	public JsonResponse lockTask(@RequestHeader(value = "Authorization", required = false)String token, 
	        @PathVariable("taskId")Integer taskId) {
    	UpdateTaskResult updateTaskResult = workflowManager.undertakeTask(AuthorizationUtils.getSessionUser(token), taskId);
		if(updateTaskResult.getIsUpdate()) {
			callImServiceeByTaskId(taskId, false);
		}
		return new JsonResponse(true);
	}

    @DeleteMapping("/task/lock/{taskId}")
	public JsonResponse unlockTask(@RequestHeader(value = "Authorization", required = false) String token, 
	        @PathVariable("taskId")Integer taskId) {
		workflowManager.withdrawTask(AuthorizationUtils.getSessionUser(token), taskId);
		callImServiceeByTaskId(taskId, true);
		return new JsonResponse(true);
	}

    @GetMapping("/task/viewers/{taskId}")
	public JsonResponseT<List<String>> retrieveTaskViewers(@RequestHeader(value = "Authorization", required = false) String token, 
	        @PathVariable("taskId")Integer taskId){
		JsonResponseT<List<String>> response=new JsonResponseT<>(true);
		response.setValue(workflowManager.getTaskViewers(AuthorizationUtils.getSessionUser(token),taskId));
		return response;
	}
	
	/**
	 * @Description : Withdraw a claimed task of others
	 **/
    @GetMapping("/counterClaimTask")
    public JsonResponseT<Object> counterClaimTask(@RequestParam(value="taskId", required = false) int taskId) {

        JsonResponseT<Object> jsonResponseT = new JsonResponseT<>(true);
        workflowManager.counterClaimTask(RequestHelper.getCurrentUser(), taskId);
        callImServiceeByTaskId(taskId, true);
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
    
	@GetMapping("/task/transactions")
	public JsonResponseT<List<Transaction>> findTransactions(
			@RequestParam("taskId") Long taskId) {
		JsonResponseT<List<Transaction>> response = new JsonResponseT<>(true);
		response.setValue(transactionService.findBtTaskId(taskId));
		return response;
	}
	
	private void callImServiceeByTaskId(Integer taskId, Boolean isNew) {
		try {
			imService.uploadChanges(taskId, isNew);
		} catch (Exception e) {
			LOGGER.error("ERROR IN IM SERVICE", e);
		}
	}
}
