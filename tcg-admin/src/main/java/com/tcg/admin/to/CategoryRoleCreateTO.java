package com.tcg.admin.to;

import com.tcg.admin.model.CategoryRole;

import java.io.Serializable;
import java.util.List;

public class CategoryRoleCreateTO extends CategoryRole implements Serializable {

    private static final long serialVersionUID = -1527899409798001222L;

    private List<Integer> categoryIdList;

    private List<Integer> roleList;

    public List<Integer> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Integer> roleList) {
        this.roleList = roleList;
    }

	public List<Integer> getCategoryIdList() {
		return categoryIdList;
	}

	public void setCategoryIdList(List<Integer> categoryIdList) {
		this.categoryIdList = categoryIdList;
	}


}
