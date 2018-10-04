package com.tcg.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

//@Cacheable(value = true)
@Deprecated
@Entity
@Table(name = "US_MENU_ITEM_AUX", uniqueConstraints = @UniqueConstraint(columnNames = { "MENU_ID" }))
public class MenuItemAux {
	
	@Id
	@Column(name="MENU_ID")
	private Integer menuId;
	
	/**
	 * 0: menu; 1: button; 2: notification; 3: task step; 4: tab
	 */
	@Column(name="API_TYPE")
	private Integer apiType;
	
	/**
	 * 0: view only; 1: add; 2: edit; 3: remove; 4: login
	 */
	@Column(name="API_FAMILY")
	private Integer apiFamily;

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getApiType() {
		return apiType;
	}

	public void setApiType(Integer apiType) {
		this.apiType = apiType;
	}

	public Integer getApiFamily() {
		return apiFamily;
	}

	public void setApiFamily(Integer apiFamily) {
		this.apiFamily = apiFamily;
	}

	
}
