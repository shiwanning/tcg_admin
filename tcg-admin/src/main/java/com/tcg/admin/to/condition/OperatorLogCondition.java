package com.tcg.admin.to.condition;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OperatorLogCondition implements Serializable {

    private static final long serialVersionUID = 6715925367086690317L;
    private String merchant;
    private String userName;
    private String function;
    private List<String> functionList;
    private Date startDateTime;
    private Date endDateTime;
    private List<Integer> menuIds;
    private List<String> merchantCodeList;
    private String editedUserName;

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public List<String> getResourceIdList() {
        return functionList;
    }

    public void setResourceIdList(List<String> functionList) {
        this.functionList = functionList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getEditedUserName() {
        return editedUserName;
    }

    public void setEditedUserName(String editedUserName) {
        this.editedUserName = editedUserName;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
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
