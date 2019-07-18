package com.tcg.admin.common.constants;

public class TaskConstant {

	public static final String OPEN_STATUS = "O";

	public static final String CLOSED_STATUS = "C";

	public static final String PROCESSING_STATUS = "P";

	public static final String CANCELLED_STATUS = "X";
	
	public static final String SYSTEM_OPERATOR = "SYS";
	
	public static final String TRA_CLAIM_STATUS = "claim";
	
	public static final String TRA_UNCLAIM_STATUS = "unclaim";
	
	public static final String TRA_CREATE_STATUS = "create";
	
	public static final String TRA_PROCESS_STATUS = "process";
	
	public static final String TRA_CLOSE_STATUS = "close";
	
	public static final Integer NO_OWNER = 0;
	
	private TaskConstant() {
	    throw new IllegalStateException("Constant class");
	}
}
