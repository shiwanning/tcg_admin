package com.tcg.admin.common.constants;

public final class SystemConstant {

	public static final int TREELEVEL = 3;//权限的目录阶层

    public static final String TYPE_JSON = "JSON";
    public static final String TYPE_POST = "POST";
    public static final String JSON_UTF_8 = "application/json;charset=utf-8";
    public static final String X_WWW_FORM_UTF_8 = "application/x-www-form-urlencoded;charset=utf-8";
	
	private SystemConstant() {
		throw new IllegalStateException("Constant class");
	}

}
