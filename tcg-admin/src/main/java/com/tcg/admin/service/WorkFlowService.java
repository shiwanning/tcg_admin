package com.tcg.admin.service;

import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Task;
import com.tcg.admin.model.TaskType;
import com.tcg.admin.to.TaskTO;
import com.tcg.admin.to.UpdateTaskResult;
import com.tcg.admin.to.UserInfo;

import java.util.List;
import java.util.Map;

public interface WorkFlowService {
	
	Map<String, Object> getAllAvailableTask(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult, List<String> stateTypes, List<Integer> stateIds);
	
	Map<String, Object> getAllClaimedTasks(UserInfo<Operator> userInfo, Integer pageNo, Integer maxResult);
	
	UpdateTaskResult undertakeTask(UserInfo<Operator> userInfo, Integer taskId);

	void withdrawTask(UserInfo<Operator> userInfo, Integer taskId);

	void counterClaimTask(UserInfo<Operator> userInfo, Integer taskId);

	Map<String, Object> supportMcsTaskInfo(Integer taskId);
	
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
