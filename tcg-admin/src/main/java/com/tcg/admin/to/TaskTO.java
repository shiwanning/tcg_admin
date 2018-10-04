package com.tcg.admin.to;

public class TaskTO {
	
	private Integer taskId;
	private String subsysTaskId;
	private Integer stateId;
	private String taskDescription;
	private String merchantCode;
	private String token;
	private String operatorName;
	
	public String getSubsysTaskId() {
		return subsysTaskId;
	}
	public void setSubsysTaskId(String subsysTaskId) {
		this.subsysTaskId = subsysTaskId;
	}
	public Integer getStateId() {
		return stateId;
	}
	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}
	public String getTaskDescription() {
		return taskDescription;
	}
	public void setTaskDescription(String taskDescription) {
		this.taskDescription = taskDescription;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public Integer getTaskId() {
		return taskId;
	}
	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

}
