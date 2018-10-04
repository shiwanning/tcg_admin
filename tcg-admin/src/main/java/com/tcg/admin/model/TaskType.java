package com.tcg.admin.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "WF_TASK_TYPE_LABEL")
public class TaskType implements Serializable {
	
	private static final long serialVersionUID = 7830309216895869544L;
	
	@Id
	@Column(name = "TYPE_CODE")
	private String typeCode;
	
	@Id
	@Column(name = "LANG_CODE")
	private String langCode;
	
	@Column(name = "LABEL")
	private String label;
	
	@Column(name = "DESCRIPTION")
	private String description;
	
	public String getTypeCode() {
		return typeCode;
	}
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}
	public String getLangCode() {
		return langCode;
	}
	public void setLangCode(String langCode) {
		this.langCode = langCode;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	

}
