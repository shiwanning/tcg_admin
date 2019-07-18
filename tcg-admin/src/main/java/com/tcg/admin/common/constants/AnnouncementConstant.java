package com.tcg.admin.common.constants;

public final class AnnouncementConstant {
	
	public static final Integer ACTIVE = 1;
	
	public static final String ALL = "0";
	
	public static final String EMERGENCY = "1";
	
	public static final String MAINTENANCE = "2";
	
	public static final String UPDATE = "3";
	
    private AnnouncementConstant() {
    	throw new IllegalStateException("Constant class");
    }
}
