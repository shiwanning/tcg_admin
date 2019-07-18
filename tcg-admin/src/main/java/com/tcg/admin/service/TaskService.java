package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Task;

import java.util.Date;
import java.util.List;

public interface TaskService {

	List<Task> findAll();

	/**
	 * <pre>
	 * Create a task
	 * 
	 * 建立任務
	 * </pre>
	 * 
	 * @param task
	 *            taskId,taskName,description,stateId,departmentId,owner,subSystemTask,status,createOperator,updateOperator
	 * @return Department
	 * @throws AdminServiceBaseException
	 */
    Task createTask(Task task);

	/**
	 * <pre>
	 * Update a task
	 * 
	 * 修改任務
	 * </pre>
	 * 
	 * @param task
	 *            taskId,taskName,description,stateId,departmentId,owner,subSystemTask,status,updateOperator
	 * @throws AdminServiceBaseException
	 */
    void updateTask(Task task);
    
    /**
	 * <pre>
	 * save or update task
	 * 
	 * 修改任務
	 * </pre>
	 * 
	 * @param task
	 *            taskId,taskName,description,stateId,departmentId,owner,subSystemTask,status,updateOperator
	 * @throws AdminServiceBaseException
	 */
    public void saveOrUpdateTask(Task task);

	/**
     * <pre>
	 * cancel a task
	 *  
	 * 取消任務
	 * </pre>
	 * 
	 * @param taskId
	 * @throws AdminServiceBaseException
	 */
    void cancelTask(Integer taskId);

	Task getTask(Integer taskId);

	Task getTaskByMerchantId(String subSystemTask);

	Task saveAndMerge(Task task);

    Task getSubsysTask(String subSysTaskId, Integer stateId, String status);

    void closeTask(Date ednDate, List<String> string);

}
