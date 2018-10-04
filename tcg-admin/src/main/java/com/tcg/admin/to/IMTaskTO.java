package com.tcg.admin.to;

import java.util.List;

import com.tcg.admin.model.StateRelationship;

public class IMTaskTO {
	
	private Integer merchantId;
	private String type;
	private Boolean notSound;
	
	private List<StateRelationship> nextStates;
	
	public Integer getMerchantId() {
		return merchantId;
	}
	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}
	public List<StateRelationship> getNextStates() {
		return nextStates;
	}
	public void setNextStates(List<StateRelationship> nextStates) {
		this.nextStates = nextStates;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Boolean getNotSound() {
		return notSound;
	}
	public void setNotSound(Boolean notSound) {
		this.notSound = notSound;
	}
}
