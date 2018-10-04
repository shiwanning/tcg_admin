package com.tcg.admin.common.constants;

public class TaskStatus {

	public static final String OPEN_STATUS = "O";

	public static final String CLOSED_STATUS = "C";

	public static final String PROCESSING_STATUS = "P";

	public static final String CANCELLED_STATUS = "X";
	
	private TaskStatus() {
	    throw new IllegalStateException("Constant class");
	}
}
