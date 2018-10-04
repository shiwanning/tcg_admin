package com.tcg.admin.utils.shiro;

import java.io.Serializable;
import java.util.Collection;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tcg.admin.common.constants.SessionStatusConstant;
import com.tcg.admin.common.constants.UsLoginUserTypeConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;

public class ShiroUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShiroUtils.class);
    
    private ShiroUtils() {}
    
	public static String verifyToken(String token, boolean isTouch) {
        Subject.Builder builder = new Subject.Builder(SecurityUtils.getSecurityManager());
		builder.sessionId(token);
		Subject subject = builder.buildSubject();

		if (subject.isAuthenticated() && isTouch) {
			subject.getSession().touch();
		}


		return subject.isAuthenticated() ? SessionStatusConstant.VALID.getType() : SessionStatusConstant.INVALID.getType();
	}

	public static Session getSession(String token) {
		Subject.Builder builder = new Subject.Builder(SecurityUtils.getSecurityManager());
		builder.sessionId(token);
		Subject subject = builder.buildSubject();

		if (subject.isAuthenticated()) {
			return subject.getSession();
		} else {
			return null;
		}
	}

    public static void setSessionValue(String token, String key, Object value) throws AdminServiceBaseException {

        Session session = getSession(token);

        if (session == null) {
            throw new AdminServiceBaseException(AdminErrorCode.USER_NOT_LOGIN_ERROR, "The token is invalid.");
        }

        session.setAttribute(key, value);
    }

    public static <V> V getSessionValue(String token, String key) throws AdminServiceBaseException {

        Session session = getSession(token);

        if (session == null) {
            throw new AdminServiceBaseException(AdminErrorCode.USER_NOT_LOGIN_ERROR, "The token is invalid.");
        }

        return (V) session.getAttribute(key);
    }
	public static String doOperatorLogin(String username, String password) throws AdminServiceBaseException {

		Subject subject = (new Subject.Builder(SecurityUtils.getSecurityManager())).buildSubject();
		CustomizedToken token = new CustomizedToken(username, password);
		token.setLoginUserType(UsLoginUserTypeConstant.OPERATOR.getType());

		try {
			subject.login(token);
			Serializable sessionId = subject.getSession().getId();

			// Set Session never expired.

			return sessionId.toString();
		} catch (Exception ex) {
		    LOGGER.error("doOperatorLogin exception.", ex);
			throw new AdminServiceBaseException(AdminErrorCode.LOGIN_FAIL, "Login fail");
		}
	}

	public static void kickSessionViaToken(Serializable token) {
		Subject.Builder builder = new Subject.Builder(SecurityUtils.getSecurityManager());
		builder.sessionId(token);
		Subject subject = builder.buildSubject();
		subject.logout();

		DefaultSecurityManager securityManager = (DefaultSecurityManager) SecurityUtils.getSecurityManager();
		DefaultSessionManager sessionManager = (DefaultSessionManager) securityManager.getSessionManager();
		Collection<Session> activeSessions = sessionManager.getSessionDAO().getActiveSessions();
		for (Session session : activeSessions) {
			if (token.equals(session.getId())) {
				session.stop();
				break;
			}
		}
	}


    /**
     * check login token session
     *
     * @param token
     *
     * @return
     */
    public static boolean isLogin(String token) {
        return getSubject(token).isAuthenticated();
    }

    /**
     * keep session alive
     *
     * @param token
     */
    public static final void touch(String token) {
        getSubject(token).getSession().touch();
    }

    public static final Subject getSubject(String token) {
        Subject.Builder builder = new Subject.Builder();
        builder.sessionId(token);
        return builder.buildSubject();
    }
}