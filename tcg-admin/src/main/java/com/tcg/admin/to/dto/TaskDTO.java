package com.tcg.admin.to.dto;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.tcg.admin.model.Task;

public class TaskDto {

    private Integer taskId;
    private String subSystemTask;
    private String type;
    private String status;
    private String creator;
    private String createTime;
    private String openTime;
    private String closeTime;
    private String description;
    private String ownerName;

    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public TaskDto(Task task) {
    	this.taskId = task.getTaskId();
        this.subSystemTask = task.getSubSystemTask();
        this.type = task.getState().getType();
        if(StringUtils.equals(task.getStatus(), "P")) {
        	this.status = "Pending";
        } else {
        	this.status = StringUtils.equals(task.getStatus(), "O") ? "Open":"Close";
        }
        this.creator = task.getCreateOperator();
        this.createTime = task.getCreateTime() != null ? dt.format(task.getCreateTime()):"";
        this.openTime = task.getOpenTime() != null  ? dt.format(task.getOpenTime()):"";
        this.closeTime = task.getCloseTime() != null ? dt.format(task.getCloseTime()):"";
        this.ownerName = task.getOwnerName() != null ? task.getOwnerName() : "";
        this.description = task.getDescription() != null ? task.getDescription() : "";
    }
    
    
    public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getSubSystemTask() {
        return subSystemTask;
    }

    public void setSubSystemTask(String subSystemTask) {
        this.subSystemTask = subSystemTask;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

}
