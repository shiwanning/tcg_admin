package com.tcg.admin.to.dto;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;

import com.tcg.admin.model.Transaction;

public class TaskTransactionDTO {

    private Integer taskId;
    private String transactionType;
    private String description;
    private String subSystemTask;
    private String status;
    private String stateName;
    private String ownerName;
    private String createTime;
    private String openTime;
    private String closeTime;
    private String updateTime;
    private String updator;

    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public TaskTransactionDTO(Transaction transaction) {
    	this.taskId = transaction.getTaskId();
        this.transactionType = transaction.getTransactionType();
        this.description = transaction.getDescription();
        this.subSystemTask = transaction.getSubSystemTask();
        if(StringUtils.equals(transaction.getStatus(), "P")) {
        	this.status = "Pending";
        } else {
        	this.status = StringUtils.equals(transaction.getStatus(), "O") ? "Open":"Close";
        }
        this.stateName = transaction.getStateName();
        this.ownerName = transaction.getOpenTime()  != null ? transaction.getOwnerName() : "";
        this.createTime = transaction.getCreateTime() != null ? dt.format(transaction.getCreateTime()):"";
        this.openTime = transaction.getOpenTime()  != null  ? dt.format(transaction.getOpenTime()):"";
        this.closeTime = transaction.getCloseTime()  != null ? dt.format(transaction.getCloseTime()):"";
        this.updateTime = transaction.getUpdateTime() != null ? dt.format(transaction.getUpdateTime()):"";
        this.updator = transaction.getUpdateOperator();
    }
    
    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdator() {
        return updator;
    }

    public void setUpdator(String updator) {
        this.updator = updator;
    }


}
