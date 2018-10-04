package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Cacheable(value = true)
@JsonIgnoreProperties({"stateId" })
@Entity
@Table(name = "WF_TASK")
public class TaskSm extends BaseEntity {

	private static final long serialVersionUID = -3637051537477454997L;

	/**
	 * Task ID
	 */
	@Id
	@Column(name = "TASK_ID")
//	@NotNull
	private Integer taskId;

	/**
	 * Task's usage description
	 */
	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "MERCHANT_ID")
	private Integer merchantId;

	@Column(name = "OWNER")
	private Integer owner;

    @Column(name = "STATE_ID", insertable = false, updatable = false)
    private Integer stateId;

	@Column(name = "SUB_SYSTEM_TASK")
	private String subSystemTask;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;

	@Column(name = "OWNER_NAME")
	private String ownerName;


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OPEN_TIME")
	protected Date openTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CLOSE_TIME")
	protected Date closeTime;

	@ManyToOne
	@JoinColumn(name = "STATE_ID")
	private State state;
	
	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

    public Integer getStateId() {
        return stateId;
    }

    public void setStateId(Integer stateId) {
        this.stateId = stateId;
    }

    public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

	public String getSubSystemTask() {
		return subSystemTask;
	}

	public void setSubSystemTask(String subSystemTask) {
		this.subSystemTask = subSystemTask;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
	}

	public Date getCloseTime() {
		return closeTime;
	}

	public void setCloseTime(Date closeTime) {
		this.closeTime = closeTime;
	}

}
