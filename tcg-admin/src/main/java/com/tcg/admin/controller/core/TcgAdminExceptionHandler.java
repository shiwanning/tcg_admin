package com.tcg.admin.controller.core;

import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.exception.SubsystemBaseException;
import com.tcg.admin.to.response.JsonResponse;

@ControllerAdvice
@Priority(1)
public class TcgAdminExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(TcgAdminExceptionHandler.class);

    @ExceptionHandler(value = Throwable.class)
    public ResponseEntity<JsonResponse> toResponse(HttpServletRequest req, Throwable exception) {
        JsonResponse jsonResponse = new JsonResponse(Boolean.valueOf(false));

        boolean isInfoLevel = false;
        if(exception instanceof AdminServiceBaseException) {
            SubsystemBaseException e = (SubsystemBaseException) exception;
            String errorCode = e.getErrorCode();
            String message = e.getMessage();
            if (StringUtils.isNotBlank(errorCode) && StringUtils.isNotBlank(message)) {
                String[] temp = message.split("\\[" + errorCode + "\\] ");
                message = temp[temp.length - 1].trim();
            }
            jsonResponse.setErrorCode(e.getErrorCode());
            jsonResponse.setMessage(message);
            isInfoLevel = isInfoLevelErrorCode(e.getErrorCode());
            if(isInfoLevel) {
    			LOGGER.info("API failed: {}. ErrorCode: {} ErrorMessage: {}.",req.getRequestURI(), errorCode, message);
        	} else {
        		LOGGER.error("API failed: {}. ErrorCode: {} ErrorMessage: {}.",req.getRequestURI(), errorCode, message);
        	}
        } else {
            jsonResponse.setErrorCode("UNKNOWN_ERROR");
            jsonResponse.setMessage(exception.getMessage());
            LOGGER.info("API failed: {}", req.getServletPath());
        }

        if(!isInfoLevel) {
        	LOGGER.error("UI errorCode: {}.",jsonResponse.getErrorCode());
            LOGGER.error(exception.getMessage(), exception);
        }
        return new ResponseEntity<>(jsonResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private boolean isInfoLevelErrorCode(String errorCode) {
    	switch(errorCode) {
    	    case AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR:
    	    case AdminErrorCode.NOT_BIND_GOOGLE_OTP:
    	    case AdminErrorCode.FORBIDDEN_LOGIN_ERROR:
    	    case AdminErrorCode.IP_NOT_ALLOW:
    	    case AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR:
    	    case AdminErrorCode.NEW_PWD_EMPTY_ERROR:
    	    case AdminErrorCode.NEW_PWD_EXISTS_HISTORY_ERROR:
    	    case AdminErrorCode.NEW_OLD_PWD_IS_SAME_ERROR:
    	    case AdminErrorCode.NEW_CONFIRM_PWD_IS_DIFF_ERROR:
    	    case AdminErrorCode.NEW_PWD_FORMAT_ERROR:
    	    case AdminErrorCode.OLD_PWD_EMPTY_ERROR:
    	    case AdminErrorCode.OLD_PWD_FORMAT_ERROR:
    	    case AdminErrorCode.OLD_PWD_ERROR:
    	    case AdminErrorCode.USER_NOT_LOGIN_ERROR:
    	        return true;
    	    default: return false;
    	}
    }
}
