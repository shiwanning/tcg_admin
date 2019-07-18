package com.tcg.admin.to;

import com.tcg.admin.model.OperatorAuth;

public class OperatorsTO {
	
	private Integer operatorId;
	private String operatorName;
	private OperatorAuth.Status status;
	
	public OperatorsTO() {
		
	}
	
	public OperatorsTO(Integer operatorId, String operatorName) {
		this.operatorId = operatorId;
		this.operatorName = operatorName;
	}
	
	public Integer getOperatorId() {
		return operatorId;
	}
	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public OperatorAuth.Status getStatus() {
		return status;
	}

	public void setStatus(OperatorAuth.Status status) {
		this.status = status;
	}
}
