package com.tcg.admin.model;
import java.util.List;

import javax.persistence.*;

/*
 * Role for the authorization mechanism
 * 
 * */
@Cacheable(value = true)
@Entity
@Table(name = "US_ROLE")
@SequenceGenerator(name = "SEQ_ROLE", sequenceName = "SEQ_ROLE", allocationSize = 1)
public class Role extends BaseEntity {

	/**
	 * <code>serialVersionUID</code> 的註解
	 */
	private static final long serialVersionUID = 4514812779199250141L;

	/**
	 * ROLE ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ROLE")
	@Column(name = "ROLE_ID")
	private Integer roleId;

	/**
	 * ROLE NAME
	 */
	@Column(name = "ROLE_NAME")
	private String roleName;

	/**
	 * ROLE's usage description
	 */
	@Column(name = "DESCRIPTION")
	private String description;
	
	/**
	 * ROLE's status
	 */
	@Column(name = "ACTIVE_FLAG")
	private Integer activeFlag;

	/** 顯示順序 */
	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;
	
	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name = "ROLE_ID", insertable = false, updatable = false)
	private List<CategoryRole> categoryRoles;

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
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

	public Integer getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(Integer activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public List<CategoryRole> getCategoryRoles() {
		return categoryRoles;
	}

	public void setCategoryRoles(List<CategoryRole> categoryRoles) {
		this.categoryRoles = categoryRoles;
	}
	
	
}
