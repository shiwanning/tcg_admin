package com.tcg.admin.common.constants;

public final class BehaviorLogConstant {
	
	public static final Integer VIEW = 0;
	public static final Integer ADD = 1;
	public static final Integer EDIT = 2;
	public static final Integer REMOVE = 3;
	public static final Integer LOGIN = 4;
	public static final Integer LOGOUT = 41;
	public static final Integer EXPORT = 5;
	public static final Integer REJECT = 6;
	public static final Integer APPROVE = 7;
	public static final Integer CHECK = 8;
	public static final Integer MENU = 9;
	public static final Integer TASK = 10;
	public static final Integer OTHER = 11;
	
	private BehaviorLogConstant() {
    	throw new IllegalStateException("Constant class");
    }
}
