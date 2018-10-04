package com.tcg.admin.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Cacheable(value = true)
@Entity
@Table(name = "US_CATEGORY")
@SequenceGenerator(name = "seq_category", sequenceName = "SEQ_CATEGORY", allocationSize = 1)
public class Category extends BaseEntity {

	private static final long serialVersionUID = -3977486572087713499L;

	/**
	 * CATEGORY ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_category")
	@Column(name = "CATEGORY_ID")
	@NotNull
	private Integer categoryId;

	/**
	 * Category Name
	 */
	@Column(name = "CATEGORY_NAME")
	@NotNull
	private String categoryName;

	/**
	 * Category description
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;

	@OneToMany(fetch=FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID", insertable = false, updatable = false)
	private List<CategoryRole> categoryRoles;


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

	public List<CategoryRole> getCategoryRoles() {
		return categoryRoles;
	}

	public void setCategoryRoles(List<CategoryRole> categoryRoles) {
		this.categoryRoles = categoryRoles;
	}
}
