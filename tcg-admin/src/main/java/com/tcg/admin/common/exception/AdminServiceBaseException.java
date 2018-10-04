package com.tcg.admin.common.exception;

public class AdminServiceBaseException extends SubsystemBaseException {

    public AdminServiceBaseException(String errorCode) {
        super(errorCode, "");
    }

	public AdminServiceBaseException(String errorCode, String description, Throwable throwable) {
		super(errorCode, description,throwable);
	}
	
	public AdminServiceBaseException(String errorCode, String description) {
		super(errorCode, description);
	}

    public AdminServiceBaseException(String errorCode, Throwable throwable) {
        super(errorCode, throwable);
    }
}
