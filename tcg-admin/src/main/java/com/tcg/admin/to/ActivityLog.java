package com.tcg.admin.to;

import com.tcg.admin.model.ApiLabel;

import java.util.Date;
import java.util.List;

public class ActivityLog {
	
	private BehaviorLogTo behaviorLog;
	private List<ApiLabel> labels;
	
	public ActivityLog(){
		super();
	}
	public ActivityLog(BehaviorLogTo behaviorLog,List<ApiLabel> apiLabels){
		this.behaviorLog = behaviorLog;
		this.labels=apiLabels;
	}
	public List<ApiLabel> getLabels() {
		return labels;
	}
	public void setLabels(List<ApiLabel> labels) {
		this.labels = labels;
	}
	public Integer getType(){
		return behaviorLog.getResourceType();
	}
	public Date getActivityTime(){
		return behaviorLog.getStartProcessDate();
	}
	
}
