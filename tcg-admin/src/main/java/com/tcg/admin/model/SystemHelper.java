package com.tcg.admin.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Cacheable(value = true)
@Entity
@Table(name = "SYSTEM_HELPER")
public class SystemHelper extends BaseEntity {

	private static final long serialVersionUID = -1361779563274277751L;

	/** 選單ID */
	@Id
	@Column(name = "HELPER_MENU_ID")
	@NotNull
	private Integer menuId;

	@Column(name = "EN_CONTENT")
	private String enContent;

	@Column(name = "CN_CONTENT")
	private String cnContent;

	/**
	 * 0:INACTIVE 1:ACTIVE
	 */
	@Column(name = "STATUS")
	@NotNull
	private Integer status;

	@Column(name = "CREATE_OPERATOR")
	private Integer createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private Integer updateOperator;

	@Column(name = "CREATE_OPERATOR_NAME")
	private String createOperatorName;

	@Column(name = "UPDATE_OPERATOR_NAME")
	private String updateOperatorName;
	
	
	@Column(name = "STATE")
	private Integer state;

/*	@OneToMany(fetch=FetchType.EAGER)
	@MapKeyColumn(name="LANG_CODE")
	@JoinColumn(name = "API_ID", referencedColumnName = "HELPER_MENU_ID", insertable = false, updatable = false)
	private Map<String, ApiLabel> labels;*/
	
	@OneToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "HELPER_MENU_ID", referencedColumnName = "MENU_ID", insertable = false, updatable = false)
	private MenuItem menuItem;
	
/*	@NotFound(action = NotFoundAction.IGNORE)
	@OneToMany(fetch=FetchType.EAGER,  mappedBy="systemHelper")
	private List<SystemHelperTemp> systemHelperTemp;*/

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}


	public String getEnContent() {
		return enContent;
	}

	public void setEnContent(String enContent) {
		this.enContent = enContent;
	}

	public String getCnContent() {
		return cnContent;
	}

	public void setCnContent(String cnContent) {
		this.cnContent = cnContent;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCreateOperator() {
		return createOperator;
	}

	public void setCreateOperator(Integer createOperator) {
		this.createOperator = createOperator;
	}



	public String getCreateOperatorName() {
		return createOperatorName;
	}

	public void setCreateOperatorName(String createOperatorName) {
		this.createOperatorName = createOperatorName;
	}


/*

	public Map<String, ApiLabel> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, ApiLabel> labels) {
		this.labels = labels;
	}*/


	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}



/*	public List<SystemHelperTemp> getSystemHelperTemp() {
		return systemHelperTemp;
	}

	public void setSystemHelperTemp(List<SystemHelperTemp> systemHelperTemp) {
		this.systemHelperTemp = systemHelperTemp;
	}*/

	public Integer getUpdateOperator() {
		return updateOperator;
	}

	public void setUpdateOperator(Integer updateOperator) {
		this.updateOperator = updateOperator;
	}

	public String getUpdateOperatorName() {
		return updateOperatorName;
	}

	public void setUpdateOperatorName(String updateOperatorName) {
		this.updateOperatorName = updateOperatorName;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
	}
	
	

	
}
