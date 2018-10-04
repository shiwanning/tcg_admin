package com.tcg.admin.to;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Created by chris.h on 2017/10/5.
 */
public class TaskQueryTO extends PageableTO{

    private List<Integer> merchantIds = Lists.newLinkedList();

    private Integer taskId;

    private Integer stateId;

    private String startDate;

    private String endDate;

    private Integer owner;

    private String status;

    private String ownerName;

    private String stateType;
    
    private String taskType;
    
    private List<String> defStat;
    
    public List<Integer> getMerchantIds() {
        return merchantIds;
    }

    public void setMerchantIds(List<Integer> merchantIds) {
        this.merchantIds = merchantIds;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOwner() {
        return owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getStateType() {
        return stateType;
    }

    public void setStateType(String stateType) {
        this.stateType = stateType;
    }

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public List<String> getDefStat() {
		return defStat;
	}

	public void setDefStat(List<String> defStat) {
		this.defStat = defStat;
	}


    
}
