package com.tcg.admin.common.constants;

public final class SystemHelperConstant {

	public static final Integer ACTIVE = 1;

	public static final Integer INACTIVE = 0;
    
	public static final Integer HELPER_APPROVED = 1;
    
	public static final Integer HELPER_PENDING = 0;
    
    /*FOR TEMP*/
    
	public static final Integer TEMP_PENDING = 0;
    
	public static final Integer TEMP_APPROVED = 1;
    
	public static final Integer TEMP_REJECTED = 2;
    
	public static final Integer TEMP_CLOSED = 3;
    
    /*TASK*/
    
	public static final Integer PENDING_STATE = 300;
    
	public static final Integer APPROVED_STATE = 301;
    
	public static final Integer REJECT_STATE = 302;
    
    private SystemHelperConstant() {
    	throw new IllegalStateException("Constant class");
    }
	
}
