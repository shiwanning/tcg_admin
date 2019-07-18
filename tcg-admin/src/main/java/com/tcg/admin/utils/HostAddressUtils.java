package com.tcg.admin.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class HostAddressUtils {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HostAddressUtils.class);
    
    private static final String LOCAL_IP = "127.0.01";
    
    private static final List<String> ipHeaders = Lists.newArrayList("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR", "X-Real-IP");
    
    private HostAddressUtils() {
    	throw new IllegalStateException("Utility class");
    }

    /**
     * get remote client ip
     * 
     * Nginx must setup as below  
     * 
     * proxy_set_header Host $host; <br/>
     * proxy_set_header Proxy-Client-IP $http_x_real_ip; <br/>
     * proxy_set_header X-Real-IP $remote_addr;
     * proxy_set_header X-Forwarded-For $http_x_forwarded_for;
     * 
     * @param request
     * @return
     */
    public static String getClientIpAddr(HttpServletRequest request) {
                
        String ip = null;
        
        for(String ipHeader : ipHeaders) {
        	ip = request.getHeader(ipHeader);
        	if (ip != null && ip.length() > 0 && !"unknown".equalsIgnoreCase(ip)) {
        		break;
        	}
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip == null || LOCAL_IP.equals(ip) || ip.startsWith("fe80") || "0:0:0:0:0:0:0:1".equals(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = LOCAL_IP;
        }
        
        try {    
            String[] realIP = ip.split(",");
            
            ip = realIP[0];
        } catch(Exception e) {
            LOGGER.error("Cant get real ip. forward IP : {}", ip , e);
            return LOCAL_IP;
        }

        return ip;
    }

}
