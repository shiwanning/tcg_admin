package com.tcg.admin.to;

import java.util.Date;

public class BehaviorLogTo {

    private Long id;
    private String ip;
    private String operatorName;
    private String url;
    private Integer resourceType;
    private String parameters;
    private String browser;
    private Integer resourceId;
    private Date startProcessDate;
    private Date endProcessDate;
    private String merchantCode;
    private String remark;
    private String area;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getResourceType() {
        return resourceType;
    }

    public void setResourceType(Integer resourceType) {
        this.resourceType = resourceType;
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

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
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

    public String getMerchantCode() {
        return merchantCode;
    }

    public void setMerchantCode(String merchantCode) {
        this.merchantCode = merchantCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
