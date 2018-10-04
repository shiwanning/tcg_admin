package com.tcg.admin.common.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.tcg.admin.controller.core.HeaderNames;
import com.tcg.admin.utils.HostAddressUtils;

public class RequestHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHelper.class);
    
    private RequestHelper() {
    	
    }
    
    public static String getLanguage() {
        return getRequest().getHeader(HeaderNames.LANGUAGE);
    }

    public static String getMerchantCode() {
        return getRequest().getHeader(HeaderNames.MERCHANT);
    }

    public static String getToken() {
        return getRequest().getHeader(HeaderNames.AUTHORIZATION_HEADER);
    }
    
    public static HttpServletRequest getRequest(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }
    
    public static Map<String, String> getMapFromUrlFormat(String query) throws UnsupportedEncodingException {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf('=');
            if(idx < 0) {
                continue;
            }
            queryPairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return queryPairs;
    }
    
    public static String getIp() {
        HttpServletRequest request = getRequest();      
        return HostAddressUtils.getClientIpAddr(request);
    }
    
    public static String getHost() {
    	String referer = getRequest().getHeader("Referer");
        return refererToHost(referer);
    }

    
    private static String refererToHost(String referer) {
		String host = referer == null ? "" : referer;
		if(host.startsWith("http://")) {
			host = host.substring(7);
		} else if(host.startsWith("https://")) {
			host = host.substring(8);
		}
		Integer hostEnd1 = host.indexOf(':');
		Integer hostEnd2 = host.indexOf('/');
		if(hostEnd1 < 0 && hostEnd2 < 0) {
			return host;
		}
		
		if(hostEnd1 < 0) {
			return host.substring(0, hostEnd2);
		}
		
		if(hostEnd2 < 0) {
			return host.substring(0, hostEnd1);
		}

		return host.substring(0, Math.min(hostEnd1, hostEnd2));
	}
}
