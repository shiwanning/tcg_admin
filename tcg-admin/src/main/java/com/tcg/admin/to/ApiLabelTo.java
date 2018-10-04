package com.tcg.admin.to;

import java.util.Map;

import com.google.common.collect.Maps;

public class ApiLabelTo {

	private Map<Integer, String> cn = Maps.newHashMap();
	private Map<Integer, String> en = Maps.newHashMap();
	
	public Map<Integer, String> getCn() {
		return cn;
	}
	public void setCn(Map<Integer, String> cn) {
		this.cn = cn;
	}
	public Map<Integer, String> getEn() {
		return en;
	}
	public void setEn(Map<Integer, String> en) {
		this.en = en;
	}
	
}
