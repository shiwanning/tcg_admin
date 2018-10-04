package com.tcg.admin.controller.core.session;


import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.ops4j.pax.shiro.cdi.ShiroIni;

/**
 * Description: Override login, check principal is unique.<br/>
 *
 * @author Wei.Lin
 */
@ShiroIni
public class TcgAdminUniquePrincipalSecurityManager extends DefaultWebSecurityManager {

    @Override
    public Subject login(Subject subject, AuthenticationToken token) {

		String loginPrincipal = (String) token.getPrincipal();
		DefaultSessionManager manager = (DefaultSessionManager) getSessionManager();
		
		for (Session session : manager.getSessionDAO().getActiveSessions()) {
			SimplePrincipalCollection principal = (SimplePrincipalCollection) session
					.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
			if (principal != null && loginPrincipal.equals(principal.getPrimaryPrincipal())) {
				Object previousToken = session.getAttribute(SessionAttribute.TOKEN);
				if (previousToken != null) {
					logout(previousToken.toString());
				}
				break;
			}

		}
    	
        return super.login(subject, token);
    }

    private void logout(String token) {
        Subject subject = getSubject(token);
        subject.logout();
    }

    static final Subject getSubject(String token) {
        Subject.Builder builder = new Subject.Builder();
        builder.sessionId(token);
        return builder.buildSubject();
    }
}
