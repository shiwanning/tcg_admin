package com.tcg.admin.model;

import com.tcg.admin.to.LocalizationPrimaryKey;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@Cacheable
@Entity
@IdClass(LocalizationPrimaryKey.class)
@Table(name = "US_MENU_ITEM_LABEL", uniqueConstraints = @UniqueConstraint(columnNames = { "LANG_CODE", "API_ID" }))
public class ApiLabel implements Serializable {
	
	@Id
	@Column(name="API_ID")
	private Integer id;
	@Id
	@Column(name="LANG_CODE")
	private String languageCode;
	
	@Column(name="LABEL")
	private String label;

	public Integer getMenuId() {
		return id;
	}

	public void setMenuId(Integer menuId) {
		this.id = menuId;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	

}
