package com.tcg.admin.to;

public class SortTo {
	
	private String sortBy;
	private String sortOrder;
	
	private SortTo() {}
	
	public static SortTo of(String sortBy, String sortOrder) {
		SortTo st = new SortTo();
		st.setSortBy(sortBy);
		st.setSortOrder(sortOrder);
		return st;
	}
	
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	public String getSortOrder() {
		return sortOrder;
	}
	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}
}
