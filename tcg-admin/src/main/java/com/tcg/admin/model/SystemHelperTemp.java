package com.tcg.admin.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

@Cacheable(value = true)
@Entity
@Table(name = "SYSTEM_HELPER_TEMP")
public class SystemHelperTemp extends BaseEntity {

	private static final long serialVersionUID = 7785206656060099516L;

	/** 選單ID */
	@Id
	@Column(name = "TASK_ID")
	private Integer taskId;
	
	@Column(name = "HELPER_MENU_ID")
	@NotNull
	private Integer menuId;


	@Column(name = "REQUESTER_ID")
	@NotNull
	private Integer requesterId;

	@Column(name = "REQUESTER")
	@NotNull
	private String requester;

	@Column(name = "EN_CONTENT_TEMP")
	private String enContentTemp;

	@Column(name = "CN_CONTENT_TEMP")
	private String cnContentTemp;
	
	@Column(name = "REMARKS")
	private String remarks;
	
	@Column(name = "STATUS")
	private Integer status;
	
	@Column(name = "PROCESSED_BY")
	private String processedBy;
	
	@NotFound(action = NotFoundAction.IGNORE)
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "HELPER_MENU_ID", referencedColumnName = "HELPER_MENU_ID", insertable = false, updatable = false)
	private SystemHelper systemHelper;

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}


	public Integer getRequesterId() {
		return requesterId;
	}

	public void setRequesterId(Integer requesterId) {
		this.requesterId = requesterId;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getEnContentTemp() {
		return enContentTemp;
	}

	public void setEnContentTemp(String enContentTemp) {
		this.enContentTemp = enContentTemp;
	}

	public String getCnContentTemp() {
		return cnContentTemp;
	}

	public void setCnContentTemp(String cnContentTemp) {
		this.cnContentTemp = cnContentTemp;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getProcessedBy() {
		return processedBy;
	}

	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}

	public SystemHelper getSystemHelper() {
		return systemHelper;
	}

	public void setSystemHelper(SystemHelper systemHelper) {
		this.systemHelper = systemHelper;
	}
	
	

	
}
