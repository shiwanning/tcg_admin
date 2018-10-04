package com.tcg.admin.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Cacheable(value = true)
@Entity
@Table(name = "US_CATEGORY_ROLE")
@SequenceGenerator(name = "seq_category_role", sequenceName = "SEQ_CATEGORY_ROLE", allocationSize = 1)
public class CategoryRole extends BaseEntity {

	private static final long serialVersionUID = 7750942523587507356L;

	/**
	 * CATEGORY ROLE ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_category_role")
	@Column(name = "CATEGORY_ROLE_ID")
	private Integer categoryRoleId;

	@Column(name = "CATEGORY_ID")
	private Integer categoryId;

	@Column(name = "ROLE_ID")
	@NotNull
	private Integer roleId;

	/**
	 *  CATEGORY ROLE description
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;
	


	public Integer getCategoryRoleId() {
		return categoryRoleId;
	}

	public void setCategoryRoleId(Integer categoryRoleId) {
		this.categoryRoleId = categoryRoleId;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(String createOperator) {
		this.createOperator = createOperator;
	}

	public String getUpdateOperator() {
		return updateOperator;
	}

	public void setUpdateOperator(String updateOperator) {
		this.updateOperator = updateOperator;
	}

}
