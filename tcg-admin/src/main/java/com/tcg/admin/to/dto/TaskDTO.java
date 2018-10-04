package com.tcg.admin.to.dto;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by chris.h on 2017/7/21.
 */
public class TaskDTO {

    private Integer taskId;
    private String subSystemTask;
    private String type;
    private String status;
    private String creator;
    private String createTime;
    private String openTime;
    private String closeTime;
    private String ownerName;

    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public TaskDTO(Integer taskId, String subSystemTask ,String type, String status, String creator, Date createTime, Date openTime, Date closeTime, String ownerName) {
        this.taskId = taskId;
        this.subSystemTask = subSystemTask;
        this.type = type;
        this.status = StringUtils.equals(status, "P") ? "Pending" : StringUtils.equals(status, "O") ? "Open":"Close";
        this.creator = creator;
        this.createTime = createTime != null ? dt.format(createTime):"";
        this.openTime = openTime  != null  ? dt.format(openTime):"";
        this.closeTime = closeTime  != null ? dt.format(closeTime):"";
        this.ownerName = closeTime  != null ? ownerName : "";
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
