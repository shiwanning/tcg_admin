package com.tcg.admin.model;

import java.util.List;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * 
 * <p>Title: com.tcg.admin.model.menuCategory</p>
 * <p>Description: 選單資料表</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2018. All Rights Reserved.</p>
 * @version 1.0
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_MENU_CATEGORY")
//@SequenceGenerator(name = "SEQ_US_MENU_CATEGORY", sequenceName = "SEQ_US_MENU_CATEGORY", allocationSize = 1, initialValue = 1)
public class MenuCategory extends BaseEntity {

	private static final long serialVersionUID = 1805077371612525647L;

	/**
	 * 附註
	 */
	@Id
	@Column(name = "CATEGORY_NAME")
	@NotNull
	private String categoryName;


	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "MENU_CATEGORY_NAME", insertable = false, updatable = false)
	private List<MenuCategoryMenu> menuCategoryMenu;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "MENU_CATEGORY_NAME", insertable = false, updatable = false)
	private Set<MerchantMenuCategory> merchantMenuCategories;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
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

	public List<MenuCategoryMenu> getMenuCategoryMenu() {
		return menuCategoryMenu;
	}

	public void setMenuCategoryMenu(List<MenuCategoryMenu> menuCategoryMenu) {
		this.menuCategoryMenu = menuCategoryMenu;
	}

	public Set<MerchantMenuCategory> getMerchantMenuCategories() {
		return merchantMenuCategories;
	}

	public void setMerchantMenuCategories(Set<MerchantMenuCategory> merchantMenuCategories) {
		this.merchantMenuCategories = merchantMenuCategories;
	}
}
