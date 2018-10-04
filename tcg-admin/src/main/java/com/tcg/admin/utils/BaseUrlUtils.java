package com.tcg.admin.utils;

import org.apache.commons.collections.MapUtils;

import java.util.Map;

public final class BaseUrlUtils {

	private BaseUrlUtils() {}
	
    private static String appendParams(Map<String, String> params, StringBuilder sb) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue() + "&");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public static String buildURL(String apiURL, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(apiURL);
 
        if (MapUtils.isNotEmpty(params)) {
            sb.append("?");
            return appendParams(params,sb);
        }
        return sb.toString();
        
        
    }
}
