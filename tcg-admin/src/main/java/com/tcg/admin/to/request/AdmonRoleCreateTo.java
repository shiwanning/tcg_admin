package com.tcg.admin.to.request;

import java.util.List;

public class AdmonRoleCreateTo {

	private String merchantCode;
	private List<Integer> roles;

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public List<Integer> getRoles() {
		return roles;
	}

	public void setRoles(List<Integer> roles) {
		this.roles = roles;
	}
	
}
