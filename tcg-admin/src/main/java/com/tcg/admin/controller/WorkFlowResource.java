package com.tcg.admin.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableList;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.State;
import com.tcg.admin.model.Task;
import com.tcg.admin.model.Transaction;
import com.tcg.admin.service.StateService;
import com.tcg.admin.service.TaskService;
import com.tcg.admin.service.TransactionService;
import com.tcg.admin.service.WorkFlowService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.to.response.TaskCountTo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping(value = "/resources/workflow", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Workflow", description = "Workflow Management")
public class WorkFlowResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowResource.class);

	private static final List<String> WITHDWAR_STATE_TYPES = ImmutableList.of("WTD");
	private static final List<String> DEPOSIT_STATE_TYPES = ImmutableList.of("DEP", "UCD");
	private static final List<String> RISKS_STATE_TYPES = ImmutableList.of("RIS");
	private static final List<String> OTHER_STATE_TYPES = ImmutableList.of("SYS", "MER", "ATF");
	
	@Autowired
	private WorkFlowService workFlowManager;

	@Autowired
	private TaskService taskService;

	@Autowired
	private StateService stateService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private OperatorLoginService operatorLoginService;
	
	@GetMapping("/states")
	public JsonResponseT<List<State>> findStates() {
		JsonResponseT<List<State>> response = new JsonResponseT<>(true);
		response.setValue(stateService.findAll());
		return response;
	}

	@PostMapping("/createState")
	public JsonResponseT<State> createState(@RequestBody State state) {
		JsonResponseT<State> response = new JsonResponseT<>(true);
		response.setValue(stateService.createState(state));
		return response;
	}

	@PutMapping("/updateState")
	public JsonResponseT<State> updateState(@RequestBody State state) {
		JsonResponseT<State> response = new JsonResponseT<>(true);
		stateService.updateState(state);
		return response;
	}


	@GetMapping("/tasks")
	public JsonResponseT<List<Task>> findTasks() {
		JsonResponseT<List<Task>> response = new JsonResponseT<>(true);
		response.setValue(taskService.findAll());
		return response;
	}


	@PostMapping("/createTask")
	public JsonResponseT<Task> createTask(@RequestBody Task task) {
		JsonResponseT<Task> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
		String operator = userInfo.getUser().getOperatorId().toString();
		task.setCreateOperator(operator);
		task.setUpdateOperator(operator);

		response.setValue(taskService.createTask(task));
		return response;
	}

	@PutMapping("/updateTask")
	public JsonResponseT<Task> updateTask(@RequestBody Task task) {
		JsonResponseT<Task> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
		String operator = userInfo.getUser().getOperatorId().toString();
		task.setUpdateOperator(operator);
		taskService.updateTask(task);
		return response;
	}
	
	@DeleteMapping("/cancelTask/{taskId}")
	public JsonResponseT<Task> cancelTask(@PathVariable("taskId") Integer taskId) {
		JsonResponseT<Task> response = new JsonResponseT<>(true);
		taskService.cancelTask(taskId);
		return response;
	}

	@GetMapping("/transactions")
	public JsonResponseT<List<Transaction>> findTransactions() {
		JsonResponseT<List<Transaction>> response = new JsonResponseT<>(true);
		response.setValue(transactionService.findAll());
		return response;
	}

	@PostMapping("/insertTransaction")
	public JsonResponseT<Transaction> insertTransaction(@RequestBody Transaction transaction) {
		JsonResponseT<Transaction> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
		String operator = userInfo.getUser().getOperatorId().toString();
		transaction.setCreateOperator(operator);
		transaction.setUpdateOperator(operator);
		response.setValue(transactionService.insertTransaction(transaction));
		return response;
	}
	
	
	/**
	 * @Description : Load all available task for operator
	 * **/
	@GetMapping("/getAvailableTasks")
    public JsonResponseT<Map<String, Object>> getAvailableTasks(
            @RequestParam(value = "pageNo", required = false) int pageNo, 
            @RequestParam(value = "maxResult", required = false) int maxResult,
            @ApiParam(value = "type: (1:Withdrawal, 2:Deposit, 3:Risk, 4:Other)", allowableValues="1,2,3,4")@RequestParam(value = "type", required = false) Integer type,
            @ApiParam(value = "stateId") @RequestParam(value = "stateId", required = false) List<Integer> stateIds) {

	    List<String> stateTypes = getStateTypesFromType(type);
	    
        JsonResponseT<Map<String, Object>> jsonResponseT = new JsonResponseT<>(true);
		jsonResponseT.setValue(workFlowManager.getAllAvailableTask(operatorLoginService.getSessionUser(RequestHelper.getToken()), pageNo, maxResult, stateTypes, stateIds));
		jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);
		
        return jsonResponseT;

	}
	
	@GetMapping("/getAvailableTasksCount")
    public JsonResponseT<TaskCountTo> getAvailableTasksCount() {
	    
	    
        JsonResponseT<TaskCountTo> jsonResponseT = new JsonResponseT<>(true);
        
        UserInfo<Operator> user = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        Map<Integer, Integer> availableTasksCountMap = workFlowManager.getAllAvailableTaskCount(user);
        
		jsonResponseT.setValue(mapToTaskCountTo(availableTasksCountMap));
		
        return jsonResponseT;

	}
	
	private TaskCountTo mapToTaskCountTo(Map<Integer, Integer> availableTasksCountMap) {
		TaskCountTo result = new TaskCountTo();
		
		for(Entry<Integer, Integer> entry : availableTasksCountMap.entrySet()) {
			TaskCountTo.StateTo stateTo = new TaskCountTo.StateTo();
			stateTo.setCount(entry.getValue());
			stateTo.setStateId(entry.getKey());
			result.getStatesCountInfo().add(stateTo);
		}
		
		return result;
	}

	private List<String> getStateTypesFromType(Integer type) {
	    if(type == null) {
	        return Collections.emptyList();
	    }
	    switch(type) {
	      case 1: return WITHDWAR_STATE_TYPES;
	      case 2: return DEPOSIT_STATE_TYPES;
	      case 3: return RISKS_STATE_TYPES;
	      case 4: return OTHER_STATE_TYPES;
	      default: return Collections.emptyList();
	    }
    }

    @GetMapping("/myTask")
    public JsonResponseT<Map<String, Object>> getTasksOfOperator(
            @RequestParam(value = "pageNo", required = false) int pageNo, 
            @RequestParam(value = "maxResult", required = false) int maxResult) {

        JsonResponseT<Map<String, Object>> jsonResponseT = new JsonResponseT<>(true);
		jsonResponseT.setValue(workFlowManager.getTasksOfOperator(operatorLoginService.getSessionUser(RequestHelper.getToken()), pageNo, maxResult));
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
	
	/**
	 * @Description : Load all task that is already claimed
	 * @return : A list or empty result
	 * **/
	@GetMapping("/getClaimedTasks")
    public JsonResponseT<Map<String, Object>> claimedTaskList(
            @RequestParam(value = "pageNo", required = false) int pageNo, 
            @RequestParam(value = "maxResult", required = false) int maxResult) {

        JsonResponseT<Map<String, Object>> jsonResponseT = new JsonResponseT<>(true);
		jsonResponseT.setValue(workFlowManager.getAllClaimedTasks(operatorLoginService.getSessionUser(RequestHelper.getToken()),pageNo, maxResult));
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
	
	/**
	 * @Description : undertake a task in the list
	 * @return : A URI of the state
	 * **/
	@GetMapping("/claimTask")
    public JsonResponseT<String> undertakeTask(
            @RequestParam(value = "taskId", required = false) int taskId) {

        JsonResponseT<String> jsonResponseT = new JsonResponseT<>(true);
        jsonResponseT.setValue(workFlowManager.undertakeTask(operatorLoginService.getSessionUser(RequestHelper.getToken()), taskId));
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
	
	/**
	 * @Description : Withdraw your claimed task
	 * **/
	@GetMapping("/withdrawTask")
    public JsonResponseT<Object> withdrawTask(@RequestParam(value = "taskId", required = false) int taskId) {

        JsonResponseT<Object> jsonResponseT = new JsonResponseT<>(true);
        workFlowManager.withdrawTask(operatorLoginService.getSessionUser(RequestHelper.getToken()), taskId);
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
	
	/**
	 * @Description : Withdraw a claimed task of others
	 * **/
	@GetMapping("/counterClaimTask")
    public JsonResponseT<Object> counterClaimTask(@RequestParam(value = "taskId", required = false) int taskId) {

        JsonResponseT<Object> jsonResponseT = new JsonResponseT<>(true);
        workFlowManager.counterClaimTask(operatorLoginService.getSessionUser(RequestHelper.getToken()), taskId);
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
	
	@PostMapping("/view/{taskId}")
	public JsonResponse logView(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable("taskId")Integer taskId){
		workFlowManager.logViewer(operatorLoginService.getSessionUser(RequestHelper.getToken()), taskId);		
		return new JsonResponse(true);
	}
	
	/**
	 * @Description : undertake a task in the list
	 * **/
	@GetMapping("/getStateLabel")
    public JsonResponseT<Object> getStateLabelReference() {

		JsonResponseT<Object>  jsonResponseT = new JsonResponseT<>(true);
        jsonResponseT.setValue(workFlowManager.getStateLabel());
        jsonResponseT.setMessage(AdminErrorCode.REQUEST_SUCCESS);

        return jsonResponseT;

	}
}
