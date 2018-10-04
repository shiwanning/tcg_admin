package com.tcg.admin.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Cacheable(value = true)
@Entity
@Table(name = "WF_TRANSACTION")
@SequenceGenerator(name = "seq_transaction", sequenceName = "SEQ_TRANSACTION", allocationSize = 1)
public class Transaction extends BaseEntity {

	private static final long serialVersionUID = -1161376949741008547L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_transaction")
	@Column(name = "TRANS_ID")
	private Integer transId;

	@Column(name = "TASK_ID")
	@NotNull
	private Integer taskId;

	@Column(name = "OWNER")
	private Integer owner;

	@Column(name = "STATE_ID")
	@NotNull
	private Integer stateId;

	@Column(name = "STATE_NAME")
	@NotNull
	private String stateName;

	@Column(name = "MERCHANT_ID")
	private Integer merchantId;

	@Column(name = "CREATE_OPERATOR")
	private String createOperator;

	@Column(name = "UPDATE_OPERATOR")
	private String updateOperator;

	@Column(name = "COMMENTS")
	private String comments;

	@Column(name = "TRANSACTION_TYPE")
	@NotNull
	private String transactionType;

	@Column(name = "OWNER_NAME")
	private String ownerName;

	@Column(name = "SUB_SYSTEM_TASK")
	private String subSystemTask;

	@Column(name = "STATUS")
	private String status;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "OPEN_TIME")
	protected Date openTime;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CLOSE_TIME")
	protected Date closeTime;

	@Column(name = "DESCRIPTION")
	private String description;

	public Integer getTransId() {
		return transId;
	}

	public void setTransId(Integer transId) {
		this.transId = transId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public Integer getOwner() {
		return owner;
	}

	public void setOwner(Integer owner) {
		this.owner = owner;
	}

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

	public Integer getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Integer merchantId) {
		this.merchantId = merchantId;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
