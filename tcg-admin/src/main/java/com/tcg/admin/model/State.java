package com.tcg.admin.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

@Cacheable(value = true)
@Entity
@Table(name = "WF_STATE")
@SequenceGenerator(name = "seq_state", sequenceName = "SEQ_STATE", allocationSize = 1)
public class State extends BaseEntity {

	private static final long serialVersionUID = -4764995683936533531L;

	/**
	 * State ID
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_state")
	@Column(name = "STATE_ID")
	private Integer stateId;

	/**
	 * State NAME
	 */
	@Column(name = "STATE_NAME")
	@ApiModelProperty(required=true)
	private String stateName;

	@Column(name = "TYPE")
	@ApiModelProperty(required=true)
	private String type;

	/**
	 * State's usage description
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "MENU_ID")
	private Integer menuId;

	@Column(name = "PARENT_ID")
	private Integer parentId;
	
	@Column(name = "VIEW_URL_ID")
	private String viewUrl;

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public String getViewUrl() {
		return viewUrl;
	}

	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	


}
