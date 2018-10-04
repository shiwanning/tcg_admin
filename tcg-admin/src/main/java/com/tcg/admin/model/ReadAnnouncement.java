package com.tcg.admin.model;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Cacheable(value = true)
@Entity
@Table(name="READ_ANNOUNCEMENT_MAPPING")
@SequenceGenerator(name = "seq_read_announcement", sequenceName = "SEQ_READ_ANNOUNCEMENT", allocationSize = 1)
public class ReadAnnouncement extends BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -7726828549148645271L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_read_announcement")
	@Column(name = "SEQ_ID")
	@NotNull
	private Integer Id;
	
	@Column(name = "OPERATOR_ID")
	private Integer operatorId;
	
	@Column(name = "ANNOUNCEMENT_ID")
	private Integer announcementId;

	public Integer getId() {
		return Id;
	}

	public void setId(Integer id) {
		Id = id;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

	public Integer getAnnouncementId() {
		return announcementId;
	}

	public void setAnnouncementId(Integer announcementId) {
		this.announcementId = announcementId;
	}


	
	

}
