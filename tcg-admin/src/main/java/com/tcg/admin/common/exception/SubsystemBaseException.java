package com.tcg.admin.common.exception;

import org.apache.commons.lang3.StringUtils;

import com.tcg.admin.common.constants.IErrorCode;

public class SubsystemBaseException extends RuntimeException implements IErrorBaseException {

    private final String errorCode;

    public SubsystemBaseException(String errorCode, String description, Throwable throwable) {
        super(StringUtils.isBlank(description) ? errorCode :
                      StringUtils.isBlank(errorCode) ? description : "[" + errorCode + "] " + description, throwable);
        this.errorCode = errorCode;
    }

    public SubsystemBaseException(String errorCode, String description) {
        super(StringUtils.isBlank(description) ? errorCode :
                      StringUtils.isBlank(errorCode) ? description : "[" + errorCode + "] " + description);
        this.errorCode = errorCode;
    }

    public SubsystemBaseException(String description) {
        super(description);
        this.errorCode = IErrorCode.UNKNOWN_ERROR;
    }

    public SubsystemBaseException(String errorCode, Throwable throwable) {
        this(errorCode, errorCode, throwable);
    }

    public String getErrorCode() {
        return this.errorCode;
    }


}
