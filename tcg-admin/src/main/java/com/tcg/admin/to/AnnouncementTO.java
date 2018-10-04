package com.tcg.admin.to;

import java.util.List;

/**
 * Created by ian.r on 6/9/2017.
 */
public class AnnouncementTO {

    private Integer announcementId;

    private String enSummary;

    private String cnSummary;

    private Integer frequency;

    private Integer status;

    private String enContent;

    private String cnContent;

    private String startTime;

    private String merchants;

    private Integer merchantType;
    
    private String announcementType;
    
    private String endDate;
    
    private List<Integer> idsToRead;

    private String vendor;

    public Integer getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(Integer announcementId) {
        this.announcementId = announcementId;
    }

    public String getEnSummary() {
        return enSummary;
    }

    public void setEnSummary(String enSummary) {
        this.enSummary = enSummary;
    }

    public String getCnSummary() {
        return cnSummary;
    }

    public void setCnSummary(String cnSummary) {
        this.cnSummary = cnSummary;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getMerchants() { return merchants; }

    public void setMerchants(String merchants) { this.merchants = merchants; }

    public Integer getMerchantType() { return merchantType; }

    public void setMerchantType(Integer merchantType) { this.merchantType = merchantType; }

	public String getAnnouncementType() {
		return announcementType;
	}

	public void setAnnouncementType(String announcementType) {
		this.announcementType = announcementType;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public List<Integer> getIdsToRead() {
		return idsToRead;
	}

	public void setIdsToRead(List<Integer> idsToRead) {
		this.idsToRead = idsToRead;
	}

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
}
