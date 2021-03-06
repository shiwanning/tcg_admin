package com.tcg.admin.to;

import com.tcg.admin.model.Role;

import java.io.Serializable;
import java.util.List;

public class RoleCreateTO implements Serializable{

    private static final long serialVersionUID = 6649762885172375917L;
    private List<Integer> categoryId;
	private String roleName;
	private String description;
	private Integer displayOrder;
	private Integer roleId;
	private List<Integer> menuIdList;
	private Role.Status googleOtpActive;

	public List<Integer> getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(List<Integer> categoryId) {
		this.categoryId = categoryId;
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
	public Integer getDisplayOrder() {
		return displayOrder;
	}
	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}
	public Integer getRoleId() {
		return roleId;
	}
	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public List<Integer> getMenuIdList() {
		return menuIdList;
	}

	public void setMenuIdList(List<Integer> menuIdList) {
		this.menuIdList = menuIdList;
	}
	public Role.Status getGoogleOtpActive() {
		return googleOtpActive;
	}
	public void setGoogleOtpActive(Role.Status googleOtpActive) {
		this.googleOtpActive = googleOtpActive;
	}
}
