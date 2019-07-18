package com.tcg.admin.model;

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Created by ian.r on 6/7/2017.
 */
@Cacheable(value = true)
@Entity
@Table(name = "ANNOUNCEMENT")
@SequenceGenerator(name = "SEQ_ANNOUNCEMENT", sequenceName = "SEQ_ANNOUNCEMENT", allocationSize = 1, initialValue = 1)
public class Announcement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_ANNOUNCEMENT")
    @Column(name = "ANNOUNCEMENT_ID")
    @NotNull
    private Integer announcementId;

    @Column(name = "EN_SUMMARY")
    private String enSummary;

    @Column(name = "CN_SUMMARY")
    private String cnSummary;

    @Column(name = "FREQUENCY")
    private Integer frequency;

    @Column(name = "STATUS")
    @NotNull
    private Integer status;

    @Column(name = "EN_CONTENT")
    private String enContent;

    @Column(name = "CN_CONTENT")
    private String cnContent;

    @Column(name = "MAINTENANCE_START_TIME")
    @NotNull
    private Date maintenanceStartTime;
    
    @Column(name = "MAINTENANCE_END_TIME")
    private Date maintenanceEndTime;
    
    @Column(name = "START_TIME")
    private Date startTime;

    @Column(name = "MERCHANTS")
    private String merchants;

    @Column(name = "MERCHANT_TYPE")
    private Integer merchantType;
    
    @Column(name = "ANNOUNCEMENT_TYPE")
    private String announcementType;

    @Column(name = "VENDOR")
    private String vendor;
    
    @Column(name = "CN_ATTACH_FILE_NAME")
    private String cnAttachFileName;
    
    @Column(name = "EN_ATTACH_FILE_NAME")
    private String enAttachFileName;
    
    @Column(name = "CN_ATTACH_FILE_URL")
    private String cnAttachFileUrl;
    
    @Column(name = "EN_ATTACH_FILE_Url")
    private String enAttachFileUrl;
    
    @Column(name = "CREATE_OPERATOR_NAME")
    private String createOperatorName;
    
    @Column(name = "UPDATE_OPERATOR_NAME")
    private String updateOperatorName;

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

    public void setCnSummary(String cnSummary) { this.cnSummary = cnSummary; }

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

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) { this.startTime = startTime; }

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

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

	public String getCnAttachFileName() {
		return cnAttachFileName;
	}

	public void setCnAttachFileName(String cnAttachFileName) {
		this.cnAttachFileName = cnAttachFileName;
	}

	public String getEnAttachFileName() {
		return enAttachFileName;
	}

	public void setEnAttachFileName(String enAttachFileName) {
		this.enAttachFileName = enAttachFileName;
	}

	public String getCnAttachFileUrl() {
		return cnAttachFileUrl;
	}

	public void setCnAttachFileUrl(String cnAttachFileUrl) {
		this.cnAttachFileUrl = cnAttachFileUrl;
	}

	public String getEnAttachFileUrl() {
		return enAttachFileUrl;
	}

	public void setEnAttachFileUrl(String enAttachFileUrl) {
		this.enAttachFileUrl = enAttachFileUrl;
	}

	public String getCreateOperatorName() {
		return createOperatorName;
	}

	public void setCreateOperatorName(String createOperatorName) {
		this.createOperatorName = createOperatorName;
	}

	public String getUpdateOperatorName() {
		return updateOperatorName;
	}

	public void setUpdateOperatorName(String updateOperatorName) {
		this.updateOperatorName = updateOperatorName;
	}

	public Date getMaintenanceStartTime() {
		return maintenanceStartTime;
	}

	public void setMaintenanceStartTime(Date maintenanceStartTime) {
		this.maintenanceStartTime = maintenanceStartTime;
	}

	public Date getMaintenanceEndTime() {
		return maintenanceEndTime;
	}

	public void setMaintenanceEndTime(Date maintenanceEndTime) {
		this.maintenanceEndTime = maintenanceEndTime;
	}
	
}
