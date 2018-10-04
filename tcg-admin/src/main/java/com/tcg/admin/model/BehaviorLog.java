package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


@Entity
//@Cache(usage= CacheConcurrencyStrategy.READ_ONLY, region="BehaviorLog")
@Table(name = "BEHAVIOR_LOG")
@SequenceGenerator(name = "SEQ_BEHAVIOR_LOG", sequenceName = "SEQ_BEHAVIOR_LOG", allocationSize = 1, initialValue = 1)
public class BehaviorLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_BEHAVIOR_LOG")
	@Column(name = "SYSID")
	private Long id;
	
	@Column(name = "IP_ADDRESS")
	private String ip;
	@Column(name = "OPERATOR_NAME")
	private String operatorName;
	@Column(name = "URL")
	@NotNull
	private String url;
	@Column(name="RESOURCE_TYPE")
	private Integer resourceType;
	@Column(name = "PARAMS")
	private String parameters;
	@Column(name = "BROWSER")
	private String browser;
	@Column(name = "RESOURCE_ID")
	private Integer resourceId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_PROC_DATE")
	private Date startProcessDate;
	@Column(name = "END_PROC_DATE")
	private Date endProcessDate;
	@Column (name = "MERCHANT_CODE")
	private String merchantCode;
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOperatorName() {
		return operatorName;
	}
	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getParameters() {
		return parameters;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	
	public Date getStartProcessDate() {
		return startProcessDate;
	}
	public void setStartProcessDate(Date startProcessDate) {
		this.startProcessDate = startProcessDate;
	}
	public Date getEndProcessDate() {
		return endProcessDate;
	}
	public void setEndProcessDate(Date endProcessDate) {
		this.endProcessDate = endProcessDate;
	}
	public Integer getResourceType() {
		return resourceType;
	}
	public void setResourceType(Integer resourceType) {
		this.resourceType = resourceType;
	}
	public Integer getResourceId() {
		return resourceId;
	}
	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}
	public String getMerchantCode() { return merchantCode; }
	public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }
}
