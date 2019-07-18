package com.tcg.admin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "OPERATOR_LOG")
public class OperatorLog implements Serializable{

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "SEQ_OPERATOR_LOG", sequenceName = "SEQ_OPERATOR_LOG", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_OPERATOR_LOG")
	private Long id;
	@Column(name = "OPERATOR_NAME" ,length = 20)
	private String operatorName;
	@Column(name = "OPERATE_FUNCTION",length = 40)
	private String operateFunction;
	@Column(name = "EDITED_OPERATOR_NAME",length = 20)
	private String editedOperatorName;
	@Column(name = "MERCHANT_CODE",length = 20)
	private String merchantCode;
	@Column(name = "OLD_DATA")
	private String oldData;
	@Column(name = "NEW_DATA")
	private String newData;
	@Column(name = "LOG_CONTENT")
	private String logContent;
	@Column(name = "CREATE_DATE")
	private Date createDate;

	public void setId(Long value) {
		this.id = value;
	}
	
	public Long getId() {
		return this.id;
	}
	public void setOperatorName(String value) {
		this.operatorName = value;
	}
	
	public String getOperatorName() {
		return this.operatorName;
	}
	public void setOperateFunction(String value) {
		this.operateFunction = value;
	}
	
	public String getOperateFunction() {
		return this.operateFunction;
	}
	public void setEditedOperatorName(String value) {
		this.editedOperatorName = value;
	}
	
	public String getEditedOperatorName() {
		return this.editedOperatorName;
	}
	public void setOldData(String value) {
		this.oldData = value;
	}
	
	public String getOldData() {
		return this.oldData;
	}
	public void setNewData(String value) {
		this.newData = value;
	}
	
	public String getNewData() {
		return this.newData;
	}
	public void setLogContent(String value) {
		this.logContent = value;
	}
	
	public String getLogContent() {
		return this.logContent;
	}
	public void setCreateDate(Date value) {
		this.createDate = value;
	}
	
	public Date getCreateDate() {
		return this.createDate;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
}
