package com.tcg.admin.to;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 紀錄登入回覆資訊
 *
 * @author sammy
 */
public class NoneAdminInfo implements Serializable {

    private static final long serialVersionUID = 8638104108521396178L;

    private boolean isAdmin;

    private Integer operatorId;

    private Integer categoryId;

    private String categoryName;

    private List<Integer> roleIds;

    private List<Integer> assingRoleIdList;

    private List<Map<String,String>> merchantCodeList;

    private List<Integer> merchantIdList;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }


    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Integer> getAssingRoleIdList() {
        return assingRoleIdList;
    }

    public void setAssingRoleIdList(List<Integer> assingRoleIdList) {
        this.assingRoleIdList = assingRoleIdList;
    }

    public List<Map<String, String>> getMerchantCodeList() {
        return merchantCodeList;
    }

    public void setMerchantCodeList(List<Map<String, String>> merchantCodeList) {
        this.merchantCodeList = merchantCodeList;
    }

    public List<Integer> getMerchantIdList() {
        return merchantIdList;
    }

    public void setMerchantIdList(List<Integer> merchantIdList) {
        this.merchantIdList = merchantIdList;
    }
}
