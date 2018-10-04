package com.tcg.admin.to.dto;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chris.h on 2017/7/21.
 */
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

    public TaskTransactionDTO(Integer taskId, String transactionType, String description, String subSystemTask, String status, String stateName, String ownerName, Date createTime, Date openTime, Date closeTime, Date updateTime, String updator) {
        this.taskId = taskId;
        this.transactionType = transactionType;
        this.description = description;
        this.subSystemTask = subSystemTask;
        this.status = StringUtils.equals(status, "P") ? "Pending" : StringUtils.equals(status, "O") ? "Open":"Close";
        this.stateName = stateName;
        this.ownerName = openTime  != null ? ownerName : "";
        this.createTime = createTime != null ? dt.format(createTime):"";
        this.openTime = openTime  != null  ? dt.format(openTime):"";
        this.closeTime = closeTime  != null ? dt.format(closeTime):"";
        this.updateTime = updateTime != null ? dt.format(updateTime):"";
        this.updator = updator;
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
