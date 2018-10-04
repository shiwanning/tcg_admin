package com.tcg.admin.to.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class JsonResponse {

	public static final JsonResponse OK = new JsonResponse(true);
	
    private boolean success;
    private String errorCode;
    private String message;

    public JsonResponse() {
        this.success = false;
    }

    public JsonResponse(Boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
}
