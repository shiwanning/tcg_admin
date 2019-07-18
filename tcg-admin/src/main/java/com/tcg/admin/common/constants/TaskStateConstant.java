package com.tcg.admin.common.constants;

public final class TaskStateConstant {

	public static final Integer CREATE_MERCHANT_STATE_ID = 200;

	public static final Integer C_MERCHANT_PENDING = 200;

	public static final Integer C_MERCHANT_APPROVED = 201;

	public static final Integer C_MERCHANT_REJECTED = 202;
	
	public static final Integer CREATE_MERCHANT_ERROR = 202;
	
    private TaskStateConstant() {
    	throw new IllegalStateException("Constant class");
    }
}
