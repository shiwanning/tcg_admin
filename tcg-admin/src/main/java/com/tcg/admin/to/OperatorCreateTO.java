package com.tcg.admin.to;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tcg.admin.model.Operator;

public class OperatorCreateTO extends Operator {

    private String taskId;

    private String merchantId;

    private String baseMerchantCode;

    private List<String> menuCategoryNameList;

    private String menuCategoryNames;

    private String operatorIds;

    private String roleIds;

    private List<Integer> merchantIdList;

    private List<Integer> roleIdList;
    
    private String merchantCurrency;

    /**
     * role name from USS.US_ROLE
     */
    private List<String> roleNameList;

    private String token;

    /**
     * from USS
     */

    private String merchantCode;

    private String merchantName;

    private String merchantType;

    private String usMerchantId;

    private String parentId;

    private String status;

    private String customerId;

    private String upline;
    
    /**
     * EDIT MERCHANT
     */
    
    private String newOperatorName;

    private Integer operatorId;


    //Result: Unrecognized field "actionType" (Class com.tcg.admin.to.OperatorCreateTO), not marked as ignorable
    @JsonIgnoreProperties(ignoreUnknown = true)
    private String actionType;
    
    @Override
    public Integer getOperatorId() {
        return operatorId;
    }

    @Override
    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getUpline() {
        return upline;
    }

    public void setUpline(String upline) {
        this.upline = upline;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }

    public List<Integer> getMerchantIdList() {
        return merchantIdList;
    }

    public void setMerchantIdList(List<Integer> merchantIdList) {
        this.merchantIdList = merchantIdList;
    }

    public List<Integer> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Integer> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getOperatorIds() {
        return operatorIds;
    }

    public void setOperatorIds(String operatorIds) {
        this.operatorIds = operatorIds;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getUsMerchantId() {
        return usMerchantId;
    }

    public void setUsMerchantId(String usMerchantId) {
        this.usMerchantId = usMerchantId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public String getMerchantType() {
        return merchantType;
    }

    public void setMerchantType(String merchantType) {
        this.merchantType = merchantType;
    }

	public String getMerchantCurrency() {
		return merchantCurrency;
	}

	public void setMerchantCurrency(String merchantCurrency) {
		this.merchantCurrency = merchantCurrency;
	}

	public String getNewOperatorName() {
		return newOperatorName;
	}

	public void setNewOperatorName(String newOperatorName) {
		this.newOperatorName = newOperatorName;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	@Override
    public String getBaseMerchantCode() {
        return baseMerchantCode;
    }

	@Override
    public void setBaseMerchantCode(String baseMerchantCode) {
        this.baseMerchantCode = baseMerchantCode;
    }

    public List<String> getMenuCategoryNameList() {
        return menuCategoryNameList;
    }

    public void setMenuCategoryNameList(List<String> menuCategoryNameList) {
        this.menuCategoryNameList = menuCategoryNameList;
    }

    public String getMenuCategoryNames() {
        return menuCategoryNames;
    }

    public void setMenuCategoryNames(String menuCategoryNames) {
        this.menuCategoryNames = menuCategoryNames;
    }
}
