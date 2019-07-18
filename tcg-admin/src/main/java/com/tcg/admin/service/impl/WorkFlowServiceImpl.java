package com.tcg.admin.service.impl;


import java.text.SimpleDateFormat;
import java.util.*;

import com.tcg.admin.persistence.springdata.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.tcg.admin.common.constants.RoleIdConstant;
import com.tcg.admin.common.constants.TaskConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.State;
import com.tcg.admin.model.StateRelationship;
import com.tcg.admin.model.Task;
import com.tcg.admin.model.TaskType;
import com.tcg.admin.model.Transaction;
import com.tcg.admin.persistence.TaskRepositoryCustom;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.StateService;
import com.tcg.admin.service.TaskService;
import com.tcg.admin.service.TransactionService;
import com.tcg.admin.service.WorkFlowService;
import com.tcg.admin.to.TaskTO;
import com.tcg.admin.to.UpdateTaskResult;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.BaseUrlUtils;
import com.tcg.admin.utils.StringTools;

@Service
@Transactional
public class WorkFlowServiceImpl implements WorkFlowService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorkFlowServiceImpl.class);
	
	@Autowired
    IMenuItemRepository menuItemRepository;

	@Autowired
    TaskRepositoryCustom taskRepositoryCustom;

	@Autowired
    private IMerchantRepository merchantRepository;

	@Autowired
    private IRoleOperatorRepository roleOperatorRepository;

	@Autowired
    private IMerchantOperatorRepository merchantOperatorRepository;

	@Autowired
	private TaskService taskService;

	@Autowired
	private TransactionService transactionService;

	@Autowired
	private RoleMenuPermissionService roleMenuPermissionService;

	@Autowired
	private IStateLabelType stateLabel;
	
	@Autowired
    private ISystemHelperTempRepository systemHelperTempRepository;
	
	@Autowired
	private ITaskRepository taskRepository;

	@Autowired
	private StateService stateService;
	
	@Autowired
	private CacheManager redisCacheManager;

	@Autowired
	private IOperatorRepository operatorRepository;
	/**
	 * @description : Get all available task for operator
	 * @param userInfo : user information who made the request
	 * @param pageNo : page of result in the list
	 * @param maxResult : maximum size of list to return
	 * @return : A list of available task
	 * */
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getAllAvailableTask(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult, List<String> stateTypes, List<Integer> stateIds){
		
		/* operator validation */
		validateOperator(userInfo, null);
		Integer opId = userInfo.getUser().getOperatorId();
		
		List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(userInfo);

		List<Integer> roleIds = roleOperatorRepository.findRoleIdListByOperatorId(opId);

		List<Integer> merchantIds = Lists.newLinkedList();
		if(CollectionUtils.isNotEmpty(merchList)){
		    for(Map<String,String> map :merchList){
		    	String merchantId = map.get("merchantId");
		    	if(merchantId != null) {
		    		merchantIds.add(Integer.parseInt(merchantId));
		    	}
			}
		}
		return taskRepositoryCustom.getAvailableTaskByMerhants(roleIds.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID) ? null : merchantIds, roleIds, pageNo, maxResult, stateTypes, stateIds);
	}

	/**
	 * @description : Get all claimed task for operator
	 * @param userInfo : user information who made the request
	 * @param pageNo : page of result in the list
	 * @param maxResult : maximum size of list to return
	 * @return : A list of claimed task
	 * */
	@Override
	public Map<String, Object> getAllClaimedTasks(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult) {
		
		/* operator validation */
		validateOperator(userInfo,null);
		
		/*Get the list of merchant id's of the operator*/
		List<Integer>  operatorMerchantIds = merchantOperatorRepository.findMerchantIdListByOperatorId(userInfo.getUser().getOperatorId());
		
		/*Process the request*/
		return taskRepositoryCustom.getClaimedTaskByMerhants(operatorMerchantIds, pageNo, maxResult);
	}
	
	/**
	 * @description : tag the task to operator
	 * @param userInfo : user information who made the request
	 * @param taskId : id of the task
	 * @return : A URI String of the state
	 * */
	@Override
	public UpdateTaskResult undertakeTask(UserInfo<Operator> userInfo, Integer taskId){

		UpdateTaskResult result = new UpdateTaskResult();
		
		/*Get the selected task to claim*/
		Task task = taskService.getTask(taskId);
		Integer operatorId = userInfo.getUser().getOperatorId();
		
		if(task.getStatus().equalsIgnoreCase(TaskConstant.CLOSED_STATUS)){
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_TASK_ALREADY_CLOSE, "Task already close");
		}
		
		if(!task.getOwner().equals(Integer.valueOf(0)) && !task.getOwner().equals(operatorId) && task.getStatus().equalsIgnoreCase(TaskConstant.PROCESSING_STATUS) ){
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_IN_PROCESS, "Task already being processed");
		}
		
		/* operator validation */
		validateOperator(userInfo, task.getMerchantId());
		
		List<StateRelationship> nextStates = stateService.getStateRelationship(task.getState().getStateId());
		
		boolean hasPermissions = false;
		if(CollectionUtils.isNotEmpty(nextStates)){
			for( StateRelationship obj: nextStates){
				if(this.hasMenuPermissions(userInfo, obj.getState().getMenuId())){
					hasPermissions = true;
					break;
				}
			}
			
		}
		
		if(hasPermissions){
			
			String operatorName = userInfo.getUser().getOperatorName();
			// 同一个用户重复锁任务
			if(task.getOwner().equals(operatorId) && TaskConstant.PROCESSING_STATUS.equals(task.getStatus())) {
				result.setIsUpdate(false);
			} else {
				/*update the Task in database*/
				task.setStatus(TaskConstant.PROCESSING_STATUS);
				
				//Push into states
				this.generateAndSaveTask(task, operatorId, operatorName);

				/*insert in transaction*/
				this.generateAndSaveTransaction(task, operatorId, TaskConstant.TRA_CLAIM_STATUS, null, operatorName);
			}
			
			/*search the menu URL*/
			String uri = menuItemRepository.findOne(task.getState().getMenuId()).getUrl();
			
			/*build a query parameters*/
			Map<String, String> map = Maps.newHashMap();
			map.put("subSystemTask", task.getSubSystemTask());
			
			/*combine the URI and the parameters*/
			result.setUrl(BaseUrlUtils.buildURL(uri, map));
			
			return result;
		}else {
		    LOGGER.error("Operator has no permission yet, taskId: " + task.getTaskId() + 
		        ", operator: " + userInfo.getUser().getOperatorName());
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_NO_PERMISSION, "Operator has no permission yet");
		}
	}
	
	
	/**
	 * @description : withdraw the claimed task
	 * @param userInfo : user information who made the request
	 * @param taskId : id of the task
	 * */
	@Override
	public void withdrawTask(UserInfo<Operator> userInfo, Integer taskId){
		
		/*Get the selected task to withdraw from database*/
		Task task = taskService.getTask(taskId);
		
		if(!task.getOwner().equals(userInfo.getUser().getOperatorId())){
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_LOCKED_BY_OTHER_USER, "Task being processed by another user");
		}
		
		/* operator validation */
		validateOperator(userInfo, task.getMerchantId());
		
        String operatorName = userInfo.getUser().getOperatorName();
			/*update the Task in database*/
		if(!task.getStatus().equals(TaskConstant.CLOSED_STATUS)){
			task.setStatus(TaskConstant.OPEN_STATUS);
		}
		task.setUpdateTime(new Date());
		this.generateAndSaveTask(task, 0, operatorName);
		
		/*insert in transaction*/
		this.generateAndSaveTransaction(task, 0,TaskConstant.TRA_UNCLAIM_STATUS, null, operatorName);

	}


	@Override
	public Map<String, Object> supportMcsTaskInfo(Integer taskId) {

		String taskOwner = null;
		Date updateTime = null;
		Map<String, Object> resultMap = new HashMap<>();
		Task task = taskService.getTask(taskId);
		if(task != null){
			Operator operator = operatorRepository.findByOperatorId(task.getOwner());
			if(operator != null){
				taskOwner = operator.getOperatorName();
			}
			updateTime = task.getUpdateTime();

		}
		resultMap.put("taskOwner", taskOwner);
		resultMap.put("claimedDate", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format (updateTime));

		return  resultMap;
	}

	/**
	 * @description : counterclaim the task of other operator
	 * @param userInfo : user information who made the request
	 * @param taskId : id of the task
	 * */
	@Override
	public void counterClaimTask(UserInfo<Operator> userInfo, Integer taskId){
		
		/*Get the selected task to withdraw from database*/
		Task task = taskService.getTask(taskId);
		
		/* operator validation */
		validateOperator(userInfo, task.getMerchantId());
		
			String operatorName = userInfo.getUser().getOperatorName();
			/*update the Task in database*/
			if(!task.getStatus().equals(TaskConstant.CLOSED_STATUS)){
				task.setStatus(TaskConstant.OPEN_STATUS);
			}

			task.setUpdateTime(new Date());
			this.generateAndSaveTask(task, 0, operatorName);
			/*insert in transaction*/
			this.generateAndSaveTransaction(task, 0, TaskConstant.TRA_UNCLAIM_STATUS, null, operatorName);

	}
	
	
	/**
	 * @description : generic saving of transaction in database used by many methods
	 * @param task : the object of selected task
	 * @param operatorId : who will be the owner / null for unclaiming
	 * @param transactionType : kind of transaction e.g (UNCLAIM, CLAIM etc..)
	 * @param reason : reason why to unclaim that task from other operator / null for withdraw task yourself
	 * @param updater : operator id of operator
	 * 
	 * @notes: set to 0 the @param(operatorId) for withdraw/counterclaim a task
	 * */
	private void generateAndSaveTransaction(Task task, Integer operatorId, String transactionType, String reason, String updater){

		/* status: P and C , record owner Name and open Time */
		String ownerName = StringUtils.equals(task.getStatus(), TaskConstant.OPEN_STATUS) ? null: updater ;

		Transaction transaction = new Transaction();
		transaction.setTaskId(task.getTaskId());
		transaction.setOwner(operatorId);
		transaction.setStateId(task.getState().getStateId());
		transaction.setStateName(task.getState().getStateName());
		transaction.setMerchantId(task.getMerchantId() != null ? task.getMerchantId() : null);
		transaction.setCreateOperator(updater);
		transaction.setUpdateOperator(updater);
		transaction.setOwnerName(ownerName);
		transaction.setComments(reason);
		transaction.setTransactionType(transactionType);
		transaction.setOpenTime(task.getOpenTime());
		transaction.setCloseTime(task.getCloseTime());
		transaction.setStatus(task.getStatus());
		transaction.setSubSystemTask(task.getSubSystemTask());
		transaction.setDescription(task.getDescription());
		/*save the transaction*/
		transactionService.insertTransaction(transaction);
	}
	
	
	/**
	 * @description : generic saving of task in database used by many methods
	 * @param task : the object of selected task
	 * @param operatorId : who will be the owner / null for unclaiming
	 * @param updater : operator id of operator
	 * 
	 * @notes: set to 0 the @param(operatorId) for withdraw/counterclaim a task
	 * */
	private void generateAndSaveTask(Task task, Integer operatorId, String updater){
		task.setOwner(operatorId);
		task.setUpdateOperator(updater);
		/*save the task*/

		/* status: P and C , record owner Name and open Time */
		switch(task.getStatus()) {
			case TaskConstant.PROCESSING_STATUS:
				task.setOwnerName(updater);
				task.setOpenTime(new Date());
				task.setCloseTime(null);
				break;
			case TaskConstant.OPEN_STATUS:
				task.setOwnerName(null);
				task.setOpenTime(null);
				task.setCloseTime(null);
				break;
			case TaskConstant.CLOSED_STATUS:
				task.setCloseTime(new Date());
				task.setOwner(TaskConstant.NO_OWNER);
				task.setOwnerName(null);
				break;
			default:
		}
		taskService.saveOrUpdateTask(task);
	}
	
	/**
	 * @description :checks if operator has a permission to the task
	 * */
	private boolean hasMenuPermissions(UserInfo<Operator> userInfo, Integer menuId){
		
		MenuItem menuItem= roleMenuPermissionService.queryMenuItemById(menuId);		
		return roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), menuItem);
		
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Task createTask(TaskTO taskTo) {
        if(taskTo.getStateId() == null) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, "State is null");
        }
        LOGGER.info("createTask taskTo: stateId=" + taskTo.getStateId() + ", subsystemTaskId: " + taskTo.getSubsysTaskId());
        if(inCacheAndPut(taskTo)) {
        	LOGGER.warn("duplicate taskTo: stateId=" + taskTo.getStateId() + ", subsystemTaskId: " + taskTo.getSubsysTaskId());
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, "duplicate task");
        }
        
		Merchant merchant = null;

		if(StringUtils.isNotEmpty(taskTo.getMerchantCode())){
			merchant = merchantRepository.findByMerchantCode(taskTo.getMerchantCode());
		}
		
		Task task = taskService.getSubsysTask(taskTo.getSubsysTaskId(), taskTo.getStateId(), TaskConstant.CLOSED_STATUS);
        if(task != null) {
           return task;
        }else{
            return saveTaskTo(taskTo, merchant);
        }
	}

    private boolean inCacheAndPut(TaskTO taskTo) {
        try {
            Cache cache = redisCacheManager.getCache("tac-task-create");
            boolean inCache = cache.get("createTask:" + taskTo.getStateId() + "-" + taskTo.getSubsysTaskId()) != null;
            cache.put("createTask:" + taskTo.getStateId() + "-" + taskTo.getSubsysTaskId(), taskTo);
            return inCache;
        } catch(Exception e) {
            LOGGER.warn("cacheException", e);
            return false;
        }
    }

    @Override
	public void updateTask(TaskTO task, UserInfo<Operator> operatorInfo) {
		Task actualTask = taskService.getTask(task.getTaskId());
		
		State state = stateService.getState(task.getStateId());
		if(state == null) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, "State Not Found");
		} else {
			List<StateRelationship> nextStates = stateService.getStateRelationship(actualTask.getStateId(),task.getStateId());
			if(CollectionUtils.isEmpty(nextStates)){
				throw new AdminServiceBaseException(AdminErrorCode.NEXT_STATE_ID_NOT_CORRECT, "The next state id not correct");
			}
			actualTask.setState(stateService.getState(task.getStateId()));
		}
		
		if(!actualTask.getOwner().equals(operatorInfo.getUser().getOperatorId())){
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_LOCKED_BY_OTHER_USER, "Task being processed by another user");
		}
		
		Date now = new Date();
		String operator = operatorInfo.getUser().getOperatorName();
		String traStatus;
		
		if(task.isClose()) {
			actualTask.setStatus(TaskConstant.CLOSED_STATUS);
			actualTask.setOwnerName(null);
			actualTask.setCloseTime(now);
			traStatus = TaskConstant.TRA_CLOSE_STATUS;
		} else {
			actualTask.setStatus(TaskConstant.OPEN_STATUS);
			traStatus = TaskConstant.TRA_PROCESS_STATUS;
		}
		
		actualTask.setUpdateOperator(operatorInfo.getUser().getOperatorName());
		actualTask.setUpdateTime(now);
		
		taskService.saveOrUpdateTask(actualTask);
		generateAndSaveTransaction(actualTask, 0, traStatus, "", operator);
		
	}

	@Override
	public Task updateTaskByMerchantId(TaskTO task, UserInfo<Operator> operatorInfo) {
		Task actualTask = taskService.getTaskByMerchantId(task.getSubsysTaskId());

		actualTask.setStatus(TaskConstant.OPEN_STATUS);
		actualTask.setOwner(TaskConstant.NO_OWNER);
		actualTask.setOwnerName(null);
		actualTask.setOpenTime(null);
		actualTask.setCloseTime(null);
		actualTask.setUpdateOperator(operatorInfo.getUserName());
		actualTask.setState(stateService.getState(200));
		taskService.saveOrUpdateTask(actualTask);
		generateAndSaveTransaction(actualTask, TaskConstant.NO_OWNER, TaskConstant.TRA_PROCESS_STATUS, "", operatorInfo.getUser().getOperatorName());

		return actualTask;
	}

	@Override
	public void closeTask(TaskTO task, UserInfo<Operator> operatorInfo) {
		Task actualTask = taskService.getTask(task.getTaskId());
		
		if(actualTask.getStatus().equalsIgnoreCase(TaskConstant.CLOSED_STATUS)){
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_TASK_ALREADY_CLOSE, "Task already close");
		}

		if(task.getStateId() != null){
			List<StateRelationship> nextStates = stateService.getStateRelationship(actualTask.getStateId(),task.getStateId());
			if(CollectionUtils.isEmpty(nextStates)){
				throw new AdminServiceBaseException(AdminErrorCode.NEXT_STATE_ID_NOT_CORRECT, "The next state id not correct");
			}
			actualTask.setState(stateService.getState(task.getStateId()));

			//throw
		}
		
		if(task.getStateId() != null){
			actualTask.setStateId(task.getStateId());
		}
		
		actualTask.setStatus("C");
		actualTask.setOwner(TaskConstant.NO_OWNER);
		actualTask.setOwnerName(null);
		String operator;
		operator = operatorInfo.getUser().getOperatorName();
		actualTask.setUpdateOperator(operator);
		actualTask.setCloseTime(new Date());
		actualTask.setUpdateTime(new Date());
		taskService.saveOrUpdateTask(actualTask);
		generateAndSaveTransaction(actualTask, 0, TaskConstant.TRA_CLOSE_STATUS, "", operator);
	}

	@Override
	public Task closeTaskByMerchantId(TaskTO task, UserInfo<Operator> operatorInfo) {
		Task actualTask = taskService.getTaskByMerchantId(task.getSubsysTaskId());

		if(actualTask.getStatus().equalsIgnoreCase(TaskConstant.CLOSED_STATUS)){
			throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_TASK_ALREADY_CLOSE, "Task already close");
		}

		actualTask.setStatus(TaskConstant.CLOSED_STATUS);
		actualTask.setOwner(TaskConstant.NO_OWNER);
		actualTask.setOwnerName(null);
		String operator;
		operator = operatorInfo.getUser().getOperatorName();
		actualTask.setUpdateOperator(operator);
		actualTask.setUpdateTime(new Date());
		actualTask.setCloseTime(new Date());
		taskService.saveOrUpdateTask(actualTask);
		generateAndSaveTransaction(actualTask, TaskConstant.NO_OWNER, TaskConstant.TRA_CLOSE_STATUS, "", operator);
		return actualTask;
	}

	@Override
	public void closeTaskWithOutCheck(TaskTO task, UserInfo<Operator> operatorInfo) {
		Task actualTask = taskService.getTask(task.getTaskId());
		if(!actualTask.getStatus().equalsIgnoreCase(TaskConstant.CLOSED_STATUS)){
			actualTask.setStatus(TaskConstant.CLOSED_STATUS);
			actualTask.setOwner(TaskConstant.NO_OWNER);

			/* TODO fix : merchant reject will close task and give approive stateId 201 */
			/* future work: wps- call updateTask to change stateId  */
			if(task.getStateId() != null){
				actualTask.setState(stateService.getState(task.getStateId()));
			}

			String operator;
			operator = operatorInfo.getUser().getOperatorName();
			actualTask.setUpdateOperator(null);
			actualTask.setUpdateTime(new Date());
			actualTask.setCloseTime(new Date());
			taskService.saveOrUpdateTask(actualTask);
			generateAndSaveTransaction(actualTask, TaskConstant.NO_OWNER, TaskConstant.TRA_CLOSE_STATUS, "", operator);
		}
	}

	/**
	 * @description : validate the operator
	 * @notes: set to null the @param (merchantId) if want to check the operator only
	 * */
	private void validateOperator(UserInfo<Operator> userInfo, Integer merchantId){
		List<Integer> roles = roleOperatorRepository.findRoleIdListByOperatorId(userInfo.getUser().getOperatorId());
		if(roles.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID)){
			return;
		}
		List<Integer> operatorMerchantIds = merchantOperatorRepository.findMerchantIdListByOperatorId(userInfo.getUser().getOperatorId());
		
		/*checks if the operator is a member of any merchant*/
		if (CollectionUtils.isEmpty(operatorMerchantIds)) {
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_IS_NOT_MERCHANT, "Operator has no merchant");
        }
		
		/*checks if task is belong to the operator merchant(s)*/
		if(merchantId != null && !operatorMerchantIds.contains(merchantId)){
		    throw new AdminServiceBaseException(AdminErrorCode.WORKFLOW_NO_PERMISSION, "Task isn't belong to operator's merchant(s)");
		}

	}


	@Override
	public void logViewer(UserInfo<Operator> userInfo, Integer taskId) {
        Cache viewCache = redisCacheManager.getCache("task-operator-viewers");
		Task task = taskService.getTask(taskId);
		String taskStateKey = "taskView:" + task.getTaskId() + "-" + task.getState().getStateId();
		LinkedHashSet<String> viewerNames = viewCache.get(taskStateKey, LinkedHashSet.class);
		if(viewerNames ==null){
			viewerNames= Sets.newLinkedHashSet();
		}
		viewerNames.add(userInfo.getUser().getOperatorName());
		viewCache.put(taskStateKey, viewerNames);
	}


	@Override
	public List<String> getTaskViewers(UserInfo<Operator> operator,Integer taskId) {
		Cache viewCache = redisCacheManager.getCache("task-operator-viewers");
		
		Task task = taskService.getTask(taskId);
		String taskStateKey = "taskView:" + task.getTaskId()+ "-" + task.getState().getStateId();
		LinkedHashSet<String> viewerNames = viewCache.get(taskStateKey, LinkedHashSet.class);
		if(viewerNames == null){
			viewerNames= Sets.newLinkedHashSet();
		}
		viewerNames.add(operator.getUser().getOperatorName());	
		viewCache.put(taskStateKey, viewerNames);
		
		List<String> returnList = Lists.newLinkedList((Set<String>) viewerNames);
		
		returnList.remove(operator.getUser().getOperatorName());
		
		return returnList;
	}


	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<TaskType> getStateLabel() {
	    
	    UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
	    
	    boolean isViewSysHelper = roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), roleMenuPermissionService.queryMenuItemById(10800));
	    boolean isViewMerchant = roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), roleMenuPermissionService.queryMenuItemById(10400));

	    List<TaskType> stateLabels = stateLabel.findAll();
	    List<TaskType> filterStateLabels = Lists.newLinkedList();
	    
	    for(TaskType taskType : stateLabels) {
	        if((!isViewSysHelper && "SYS".equals(taskType.getTypeCode())) ||
	                (!isViewMerchant && "MER".equals(taskType.getTypeCode()))) {
	            continue;
	        }
	        filterStateLabels.add(taskType);
	    }
	    
	    return filterStateLabels;
	    
	}
	
	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getTasksOfOperator(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult){
		/* operator validation */
		validateOperator(userInfo, null);
		Integer opId = userInfo.getUser().getOperatorId();
		List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(userInfo);

		List<Integer> roleIds = roleOperatorRepository.findRoleIdListByOperatorId(opId);
		
		boolean isSytemAdmin = false;

		 if (roleIds.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID)) {
			 isSytemAdmin = true;
	     }

		List<Integer> merchantIds = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(merchList)){
			for(Map<String,String> map :merchList){
				String merchantId = map.get("merchantId");
				if(merchantId != null) {
					merchantIds.add(Integer.parseInt(merchantId));
				}
			}
		}
		List<Integer> taskIds = systemHelperTempRepository.findRejectOrAcceptedHelper(opId, Arrays.asList(1,2));

		return taskRepositoryCustom.getTaskOfOperator(isSytemAdmin ? null : merchantIds, roleIds, pageNo, maxResult, opId, taskIds);
        
	}
	

	@Override
	public Task createMultipleTask(TaskTO taskTO) {
		 if(taskTO.getStateId() == null) {
	            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, "State is null");
	        }

			Merchant merchant = null;

			if(StringUtils.isNotEmpty(taskTO.getMerchantCode())){
				merchant = merchantRepository.findByMerchantCode(taskTO.getMerchantCode());
			}
			
			return saveTaskTo(taskTO, merchant);
	}

	private Task saveTaskTo(TaskTO taskTO, Merchant merchant) {
		Task task = new Task();
		task.setDescription(taskTO.getTaskDescription());
		task.setMerchantId(merchant != null ? merchant.getMerchantId() : null);
		task.setState(stateService.getState(taskTO.getStateId()));
		task.setSubSystemTask(taskTO.getSubsysTaskId());
		task.setUpdateTime(null);
		if (StringTools.isEmptyOrNull(taskTO.getOperatorName())) {
			task.setCreateOperator(TaskConstant.SYSTEM_OPERATOR);
		} else {
			task.setCreateOperator(taskTO.getOperatorName());
		}
			task.setOwner(TaskConstant.NO_OWNER);
			task.setStatus(TaskConstant.OPEN_STATUS);

		task = taskService.saveAndMerge(task);
		generateAndSaveTransaction(task, TaskConstant.NO_OWNER, TaskConstant.TRA_CREATE_STATUS, "", TaskConstant.SYSTEM_OPERATOR);
        return task;
	}

	@Override
	public Map<Integer, Integer> getAllAvailableTaskCount(UserInfo<Operator> userInfo) {

		validateOperator(userInfo, null);
		Integer opId = userInfo.getUser().getOperatorId();

		List<Integer> roleIds = roleOperatorRepository.findRoleIdListByOperatorId(opId);

		Map<Integer, Integer> result = Maps.newHashMap();
		
		if(!roleIds.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID)) {
			List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(userInfo);
			List<Integer> merchantIds = Lists.newLinkedList();
			if(CollectionUtils.isNotEmpty(merchList)){
			    for(Map<String,String> map :merchList){
			    	merchantIds.add(Ints.tryParse(map.get("merchantId")));
				}
			}
			List<Object[]> countInfo = taskRepository.findAvailableTaskCountInfoByMerchant(merchantIds, roleIds);
			for(Object[] record : countInfo) {
				result.put((Integer)record[0], ((Long)record[1]).intValue());
			}
		} else {
			List<Object[]> countInfo = taskRepository.findAvailableTaskCountInfo();
			for(Object[] record : countInfo) {
				result.put((Integer)record[0], ((Long)record[1]).intValue());
			}
		}
		return result;
	}

}
