package com.tcg.admin.utils;

import java.util.Date;
import java.util.UUID;

import com.tcg.admin.persistence.springdata.IOperatorAuthRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcg.admin.cache.RedisLoginUserManager;
import com.tcg.admin.common.constants.SessionStatusConstant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.to.AuthInfo;
import com.tcg.admin.to.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthorizationUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationUtils.class);
    
    private static final Long MAX_IDLE_TIME = 72000001L; //120 mins
    
    private static RedisLoginUserManager redisLoginUserManager;

    private AuthorizationUtils() {}
    
    public static void init(RedisLoginUserManager manager) {
    	if(redisLoginUserManager != null) {
    		LOGGER.warn("already init, ignore.");
    		return;
    	}
    	redisLoginUserManager = manager;
    }
    
	public static String verifyToken(String token, boolean isTouch) {
		UserInfo<Operator> userInfo = isTouch ? touch(token) : getSessionUser(token);
        if(userInfo == null) {
        	LOGGER.info("{} is not in session, idle", token);
        	return SessionStatusConstant.INVALID.getType(); 
        }
        
        AuthInfo authInfo = getSessionAuthInfo(userInfo.getUser().getOperatorId());
        if(authInfo == null) {
        	LOGGER.info("{} is not in authInfo, idle", token);
        	return SessionStatusConstant.INVALID.getType(); 
        }
        Long lastAccessTime = authInfo.getLastAccessTime().getTime();
        Long idleTime = System.currentTimeMillis() - lastAccessTime;
		return idleTime <= MAX_IDLE_TIME && userInfo.getValid() ? SessionStatusConstant.VALID.getType() : SessionStatusConstant.INVALID.getType();
	}
	
	public static void doOperatorLogin(UserInfo<Operator> userInfo) {
		String token = UUID.randomUUID().toString();
		userInfo.setToken(token);
		redisLoginUserManager.initSession(userInfo);
	}

	public static void kickSessionViaToken(String token) {
		redisLoginUserManager.delete(token);
	}

    public static boolean isLogin(String token) {
    	UserInfo<Operator> userInfo = token == null ? null : getSessionUser(token);
		if(userInfo == null || isNeedOtpToLogin(userInfo.getUser().getOperatorId()) || IsNeedReset(userInfo.getUser().getOperatorId()) || IsAuthOrigin(userInfo.getUser().getOperatorId())) {
			return false;
    	}
    	AuthInfo authInfo = getSessionAuthInfo(userInfo.getUser().getOperatorId());
    	
        return authInfo != null && authInfo.getToken().equals(userInfo.getToken());
    }

    
    // ，是否有其他登入
    public static boolean isOtherLogin(String token) {
    	if(token == null) {
    		return false;
    	}
    	UserInfo<Operator> userInfo = redisLoginUserManager.doReadSession(token);
    	if(userInfo == null) {
    		return false;
    	}
    	
    	AuthInfo authInfo = redisLoginUserManager.doReadAuth(userInfo.getUser().getOperatorId());
    	
        return authInfo != null && !authInfo.getToken().equals(userInfo.getToken());
    }

    public static final UserInfo<Operator> touch(String token) {
    	UserInfo<Operator> userInfo = redisLoginUserManager.doReadSession(token);
    	if(userInfo != null && userInfo.getValid()) {
    		redisLoginUserManager.update(userInfo);
    	}
    	return userInfo;
    }
    
    public static final UserInfo<Operator> getSessionUser(String token) {
    	UserInfo<Operator> userInfo = redisLoginUserManager.doReadSession(token);
    	return userInfo == null || !userInfo.getValid() ? null : redisLoginUserManager.doReadSession(token);
    }

	public static AuthInfo getSessionAuthInfo(Integer operatorId) {
		return redisLoginUserManager.doReadAuth(operatorId);
	}
	
	public static void removeSession(String token) {
    	redisLoginUserManager.delete(token);
    }

	public static void putNeedOtp(Integer operatorId) {
		redisLoginUserManager.updateOtp(operatorId, true);
	}
	public static void putIsNeedReset(Integer operatorId, Boolean onOff){
		redisLoginUserManager.updateIsNeedReset(operatorId, onOff);
	}
	public static void putIsAuthOrigin(Integer operatorId, Boolean onOff){
		redisLoginUserManager.updateIsAuthOrigin(operatorId, onOff);
	}
	public static boolean IsNeedReset(Integer operatorId) {
		AuthInfo authInfo = AuthorizationUtils.getSessionAuthInfo(operatorId);
		return authInfo != null && authInfo.isNeedReset();
	}
	public static boolean IsAuthOrigin(Integer operatorId) {
		AuthInfo authInfo = AuthorizationUtils.getSessionAuthInfo(operatorId);
		return authInfo != null && authInfo.isAuthOrigin();
	}

	public static void putNotNeedOtp(Integer operatorId) {
		redisLoginUserManager.updateOtp(operatorId, false);
	}
    
	public static int addOtpErrorCount(Integer operatorId) {
		return redisLoginUserManager.addOtpErrorCount(operatorId);
	}

	public static void removeOtpErrorCount(int operatorId) {
		redisLoginUserManager.removeOtpErrorCount(operatorId);
	}

	public static boolean isNeedOtpToLogin(Integer operatorId) {
		AuthInfo authInfo = AuthorizationUtils.getSessionAuthInfo(operatorId);
        return authInfo != null && authInfo.isNeetOtp();
	}


	public static void setOptValidTime(UserInfo<Operator> userInfo, Date date) {
		userInfo.setOptValidTime(date);
		redisLoginUserManager.update(userInfo);
	}

	public static void setGoogleAuthKey(UserInfo<Operator> userInfo, String key) {
		userInfo.setGoogleAuthKey(key);
		redisLoginUserManager.update(userInfo);
	}

	public static boolean isNeedOperation(Integer operatorId){
		return isNeedOtpToLogin(operatorId) || IsNeedReset(operatorId) || IsAuthOrigin(operatorId);
	}
}