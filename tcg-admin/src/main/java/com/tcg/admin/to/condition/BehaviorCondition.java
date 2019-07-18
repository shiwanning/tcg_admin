package com.tcg.admin.to.condition;

import java.util.List;

public class BehaviorCondition {
	
	private String merchant; 
	private String username; 
	private Integer actionType;
	private List<Integer> resourceIdList; 
	private String keyword;
	private String startDateTime; 
	private String endDateTime;
	private List<Integer> menuIds;
	private List<String> merchantCodeList;
	
	public String getMerchant() {
		return merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Integer getActionType() {
		return actionType;
	}
	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}
	public List<Integer> getResourceIdList() {
		return resourceIdList;
	}
	public void setResourceIdList(List<Integer> resourceIdList) {
		this.resourceIdList = resourceIdList;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
	public List<Integer> getMenuIds() {
		return menuIds;
	}
	public void setMenuIds(List<Integer> menuIds) {
		this.menuIds = menuIds;
	}
	public List<String> getMerchantCodeList() {
		return merchantCodeList;
	}
	public void setMerchantCodeList(List<String> merchantCodeList) {
		this.merchantCodeList = merchantCodeList;
	}
	
}
