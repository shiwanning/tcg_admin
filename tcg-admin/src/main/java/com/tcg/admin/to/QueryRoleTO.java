package com.tcg.admin.to;

import java.io.Serializable;
import java.util.List;

public class QueryRoleTO implements Serializable {

    private static final long serialVersionUID = 5769091254951355661L;

    private List<Integer> roleIdList;

    private Integer activeFlag;

    private int pagenumber = 0;

    private int pageSize = 0;
    
    private String roleName;
    
    private String description;
    
    private Integer categoryRoleId;

    public List<Integer> getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(List<Integer> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCategoryRoleId() {
		return categoryRoleId;
	}

	public void setCategoryRoleId(Integer categoryRoleId) {
		this.categoryRoleId = categoryRoleId;
	}
	
	
    
    

}
