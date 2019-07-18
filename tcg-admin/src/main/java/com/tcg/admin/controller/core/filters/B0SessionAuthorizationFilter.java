package com.tcg.admin.controller.core.filters;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.tcg.admin.utils.AuthorizationUtils;

@WebFilter(filterName = "authFilter", urlPatterns = "/resources/*")
@Component
public class B0SessionAuthorizationFilter implements Filter {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private static final Logger LOGGER = LoggerFactory.getLogger(B0SessionAuthorizationFilter.class);

    //MCS 电话系统需要调用/resources/operators/profile/account-name接口验证用户名，
    // 所以拿掉对于这个接口的token验证，此接口存在数据安全泄漏风险，已跟PM沟通
    private final List<String> ignoreValidateTokenList = Arrays.asList(
    		"/randy",
            "/_reset_password",
            "/operator_sessions",
            "transfer",
            "/verification",
            "/subsystem",
            "/system/cache",
            "/version",
            "/auth/google/valid-otp",
            "/domain/properties",
            "/operators/password_origin",
            "/auth/google/bind-key-auto",
            "/operators/profile/account-name");

    private Boolean isAutowired = false;
    
    @Override
    public void destroy() {
    	// do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        
        if(!isAutowired) {
            SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
            isAutowired = true;
        }
        
        HttpServletRequest requestWrapper = (HttpServletRequest) request;
        HttpServletResponse responseWrapper = (HttpServletResponse) response;

        final String requestUri = requestWrapper.getRequestURI();

        if (checkIgnoreList(requestUri, ignoreValidateTokenList)) {
            filterChain.doFilter(request, response);
        } else {
            String token = requestWrapper.getHeader(AUTHORIZATION_HEADER);
            try {
                if (AuthorizationUtils.isLogin(token)) {
                	AuthorizationUtils.touch(token);
                    filterChain.doFilter(request, response);
                } else {
                	String responseBody = AuthorizationUtils.isOtherLogin(token) ? "{\"errorCode\":" + "\"KICKED_TOKEN\"}" : "{\"errorCode\":" + "\"INVALID_TOKEN\"}";
                    LOGGER.debug("Login fail [{}] - {}", token, requestUri);
                    responseWrapper.setContentType("application/json");
                    responseWrapper.setStatus(401);
                    responseWrapper.getWriter().write(responseBody);
                    responseWrapper.flushBuffer();
                }
            } catch (Exception e) {
                LOGGER.error("login filter error", e);
            }
        }
    }

    private boolean checkIgnoreList(final String uri, List<String> pIgnoreList) {
        Object returnObject = CollectionUtils.find(pIgnoreList, new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                if (null == uri || object == null) {
                    return false;
                }
                return uri.contains(String.valueOf(object));
            }
        });
        return returnObject != null;
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
    	// do nothing
    }
}
