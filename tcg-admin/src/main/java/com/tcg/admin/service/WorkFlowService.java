package com.tcg.admin.service;

import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Task;
import com.tcg.admin.model.TaskType;
import com.tcg.admin.to.TaskTO;
import com.tcg.admin.to.UserInfo;

import java.util.List;
import java.util.Map;

public interface WorkFlowService {

	public static final String OPEN_STATUS = "O";

	public static final String CLOSED_STATUS = "C";

	public static final String PROCESSING_STATUS = "P";

	public static final String CANCELLED_STATUS = "X";

	public static final String SYSTEM_OPERATOR = "SYS";

	public static final String CLOSED_TASK = "C";

	public static final Integer NO_OWNER = 0;

	public static final Integer C_MERCHANT_PENDING = 200;

	public static final Integer C_MERCHANT_APPROVED = 201;

	public static final Integer C_MERCHANT_REJECTED = 202;

	Map<String, Object> getAllAvailableTask(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult, List<String> stateTypes, List<Integer> stateIds);
	
	Map<String, Object> getAllClaimedTasks(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult);
	
	String undertakeTask(UserInfo<Operator> userInfo, Integer taskId);

	void withdrawTask(UserInfo<Operator> userInfo, Integer taskId);

	void counterClaimTask(UserInfo<Operator> userInfo, Integer taskId);
	
	Task createTask(TaskTO task);
	
	void updateTask(TaskTO task, UserInfo<Operator> operatorInfo);

	Task updateTaskByMerchantId(TaskTO task, UserInfo<Operator> operatorInfo);
	
	void closeTask(TaskTO task, UserInfo<Operator> operatorInfo);

	Task closeTaskByMerchantId(TaskTO task, UserInfo<Operator> operatorInfo);

	void closeTaskWithOutCheck(TaskTO task, UserInfo<Operator> operatorInfo);
	
	void logViewer(UserInfo<Operator> userInfo, Integer taskId);
	
	List<String> getTaskViewers(UserInfo<Operator> operator, Integer taskId);
	
	List<TaskType> getStateLabel();

	Map<String, Object> getTasksOfOperator(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult);
	
	Task createMultipleTask(TaskTO task);

	Map<Integer, Integer> getAllAvailableTaskCount(UserInfo<Operator> user);

}
