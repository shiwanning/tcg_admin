package com.tcg.admin.cache;

import com.tcg.admin.model.Operator;
import com.tcg.admin.to.AuthInfo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.AuthorizationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Date;

import javax.annotation.PostConstruct;

@Component
public class RedisLoginUserManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisLoginUserManager.class);
	
	private static final String TOKEN_NAMESPACE = "authorize:token:";
	private static final String USER_NAMESPACE = "authorize:user:";
	
	@Autowired
	private CacheManager redisCacheManager;

	@PostConstruct
	private void init() {
		AuthorizationUtils.init(this);
	}
	
	public UserInfo<Operator> update(UserInfo<Operator> userInfo) {
		this.saveSession(userInfo, false);
		return userInfo;
	}
	
	public void initSession(UserInfo<Operator> userInfo) {
		AuthInfo authInfo = doReadAuth(userInfo.getUser().getOperatorId());
		
		if(authInfo != null && authInfo.getToken() != null) {
			LOGGER.info("user login and kick user: {}", userInfo.getUser().getOperatorName());
			
			UserInfo<Operator> oldLoginUser = doReadSession(authInfo.getToken());
			if(oldLoginUser != null) {
				oldLoginUser.setValid(false);
				getCache().put(getTokenKey(oldLoginUser.getToken()), oldLoginUser);
			}
		}
		this.saveSession(userInfo, true);
	}

	private Cache getCache() {
		return redisCacheManager.getCache("tac-auth-redis");
	}
	
	private void saveSession(UserInfo<Operator> userInfo, boolean updateToken) {
		if(userInfo == null || userInfo.getUser() == null){
			LOGGER.info("user is null");
			return;
		}
		AuthInfo authInfo = doReadAuth(userInfo.getUser().getOperatorId());
		
		if(authInfo == null) {
			authInfo = new AuthInfo();
		}
		
		if(updateToken) {
			authInfo.setToken(userInfo.getToken());
		}
		
		authInfo.setLastAccessTime(new Date());
		
		getCache().put(getUserKey(userInfo.getUser().getOperatorId()), authInfo);
		getCache().put(getTokenKey(userInfo.getToken()), userInfo);
	}

	public void delete(UserInfo<Operator> userInfo) {
		if(userInfo == null || userInfo.getUser() == null){
			LOGGER.error("user is null");
			return;
		}

		getCache().evict(getTokenKey(userInfo.getToken()));
		getCache().evict(getUserKey(userInfo.getUser().getOperatorId()));
	}
	
	private String getTokenKey(String token) {
		return TOKEN_NAMESPACE + token;
	}
	
	private String getUserKey(Integer userId) {
		return USER_NAMESPACE + userId;
	}

	public UserInfo<Operator> doReadSession(String token) {
		if(token == null){
			LOGGER.warn("token is null");
			return null;
		}
		return getCache().get(getTokenKey(token), UserInfo.class);
	}

	public void delete(String token) {
		getCache().evict(getTokenKey(token));
	}

	public AuthInfo doReadAuth(Integer userId) {
		return getCache().get(getUserKey(userId), AuthInfo.class);
	}
	
	public void doPutAuth(Integer userId, AuthInfo authInfo) {
		getCache().put(getUserKey(userId), authInfo);
	}

	public void updateOtp(Integer userId, boolean isNeedOtp) {
		AuthInfo existAuthInfo = doReadAuth(userId);
		if(existAuthInfo == null) {
			existAuthInfo = new AuthInfo();
		}
		existAuthInfo.setNeetOtp(isNeedOtp);
		doPutAuth(userId, existAuthInfo);
	}

	public void updateIsNeedReset(Integer userId, boolean isNeedReset) {
		AuthInfo existAuthInfo = doReadAuth(userId);
		if(existAuthInfo == null) {
			existAuthInfo = new AuthInfo();
		}
		existAuthInfo.setNeedReset(isNeedReset);
		doPutAuth(userId, existAuthInfo);
	}
	public void updateIsAuthOrigin(Integer userId, boolean isAuthOrigin) {
		AuthInfo existAuthInfo = doReadAuth(userId);
		if(existAuthInfo == null) {
			existAuthInfo = new AuthInfo();
		}
		existAuthInfo.setAuthOrigin(isAuthOrigin);
		doPutAuth(userId, existAuthInfo);
	}


	
	public int addOtpErrorCount(Integer userId) {
		AuthInfo existAuthInfo = doReadAuth(userId);
		if(existAuthInfo == null) {
			existAuthInfo = new AuthInfo();
		}
		int errorCount = existAuthInfo.getOtpValidErrorCount() + 1;
		existAuthInfo.setOtpValidErrorCount(errorCount);
		doPutAuth(userId, existAuthInfo);
		return errorCount;
	}

	public void removeOtpErrorCount(int userId) {
		AuthInfo existAuthInfo = doReadAuth(userId);
		if(existAuthInfo == null) {
			existAuthInfo = new AuthInfo();
		}
		existAuthInfo.setOtpValidErrorCount(0);
		doPutAuth(userId, existAuthInfo);
	}
	
}
