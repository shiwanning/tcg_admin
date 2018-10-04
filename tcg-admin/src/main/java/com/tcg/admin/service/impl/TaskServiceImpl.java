package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.constants.TaskStatus;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Task;
import com.tcg.admin.persistence.BaseDAORepository;
import com.tcg.admin.persistence.springdata.ITaskRepository;
import com.tcg.admin.service.TaskService;
import com.tcg.admin.utils.ValidatorUtils;

@Service
@Transactional
public class TaskServiceImpl extends BaseDAORepository implements TaskService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    private ITaskRepository taskRepository;
    
    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public Task createTask(Task task) {
        task.setStatus(TaskStatus.OPEN_STATUS);
        return taskRepository.saveAndFlush(task);
    }

    @Override
    public void updateTask(Task task) {
        Task insertModel = taskRepository.findOne(task.getTaskId());
        insertModel.setStatus(task.getStatus());
        insertModel.setMerchantId(task.getMerchantId());
        insertModel.setDescription(task.getDescription());
        insertModel.setOwner(task.getOwner());
        insertModel.setSubSystemTask(task.getSubSystemTask());
        insertModel.setUpdateOperator(task.getUpdateOperator());
        taskRepository.saveAndFlush(insertModel);
    }

    @Override
    public void cancelTask(Integer taskId) {
        Task task = taskRepository.findOne(taskId);
        task.setStatus(TaskStatus.CANCELLED_STATUS);
        taskRepository.saveAndFlush(task);
    }
    
    /**
     * @description : save or update task
     * @param task: object that contains a data for saving
     * */
    @Override
    public void saveOrUpdateTask(Task task){
    	/*Violations container*/
    	Set<ConstraintViolation<Task>> violations = ValidatorUtils.validate(task);
		
    	/*check for violations*/
		if (CollectionUtils.isNotEmpty(violations)) {
			throw new AdminServiceBaseException(AdminErrorCode.PARAMETER_IS_REQUIRED,"Entity has required fields error");
		}
		
		/*call the repository*/
    	 taskRepository.saveAndFlush(task);
    }
    
	
    /**
     * @description : pull the task in database using the taskId
     * @param taskId: id used to get the Task
     * @return Task Object
     * */
	@Override
	public Task getTask(Integer taskId){
		Task task = taskRepository.findTask(taskId);
		if (task == null) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, "Task Not Found");
		}
		return task;
	}

    @Override
    public Task getTaskByMerchantId(String subSystemTask){
        Task task = taskRepository.findBySubSystemTaskAndStateId(subSystemTask, CREATE_MERCHANT_ERROR);
        if (task == null) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, "Task Not Found");
        }
        return task;
    }

	@Override
	public Task saveAndMerge(Task task) {
		/*Violations container*/
    	Set<ConstraintViolation<Task>> violations = ValidatorUtils.validate(task);

    	/*check for violations*/
		if (CollectionUtils.isNotEmpty(violations)) {
			throw new AdminServiceBaseException(AdminErrorCode.PARAMETER_IS_REQUIRED,"Entity has required fields error");
		}
		
		/*call the repository*/
		return taskRepository.saveAndFlush(task);
	}

    @Override
    public Task getSubsysTask(String subSysTaskId, Integer stateId, String status) {
        return taskRepository.findBySubSystemTaskAndStateId(subSysTaskId, stateId, status);
    }

    @Override
    public void closeTask(Date endDate, List<String> stateType) {
        List<String> status = Lists.newArrayList("O", "P");
        int updateRows = taskRepository.updateStatusByOpenTimeBeforeAndStateTypeAndStatusIn("C", endDate, stateType, status);
        LOGGER.info("closed {} task for stateType: {}", updateRows, stateType);
    }

}
