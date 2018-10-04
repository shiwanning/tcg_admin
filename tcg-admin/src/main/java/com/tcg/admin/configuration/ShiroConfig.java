package com.tcg.admin.configuration;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.SessionListener;
import org.apache.shiro.session.mgt.eis.CachingSessionDAO;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tcg.admin.controller.core.session.TcgAdminSessionListener;
import com.tcg.admin.controller.core.session.TcgAdminUniquePrincipalSecurityManager;
import com.tcg.admin.controller.core.session.TcgSessionIdGenerator;

@Configuration
public class ShiroConfig {
    
    @Bean
    public DefaultWebSessionManager webSessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdCookieEnabled(false);
        Collection<SessionListener> listeners = new LinkedList<>();
        listeners.add(new TcgAdminSessionListener());
        sessionManager.setSessionListeners(listeners);
        
        CachingSessionDAO sessionDao = new EnterpriseCacheSessionDAO();
        sessionDao.setSessionIdGenerator(new TcgSessionIdGenerator());
        
        sessionManager.setSessionDAO(sessionDao);
        sessionManager.setGlobalSessionTimeout(72000000);
        
        return sessionManager;
    }
    
    @Bean
    public RealmSecurityManager securityManager(@Qualifier("shiroCacheManager") EhCacheManager shiroCacheManager, 
            DefaultWebSessionManager sessionManager, Realm realm) {
        DefaultSecurityManager sm = new TcgAdminUniquePrincipalSecurityManager();
        sm.setCacheManager(shiroCacheManager);
        sm.setSessionManager(sessionManager);
        sm.setRealm(realm);
        return sm;
    }
    
    @Bean
    public Object shiroInitializer(RealmSecurityManager securityManager) {
        MethodInvokingFactoryBean returnVal = new MethodInvokingFactoryBean();
        returnVal.setStaticMethod("org.apache.shiro.SecurityUtils.setSecurityManager");
        Object[] args = {securityManager};
        returnVal.setArguments(args);
        return returnVal;
    }
}
