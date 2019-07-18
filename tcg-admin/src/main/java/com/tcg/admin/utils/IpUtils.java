package com.tcg.admin.utils;

import com.tcg.common.util.IPSeeker;
import com.tcg.common.util.qqwry.IPEntity;

import java.util.regex.Pattern;

public class IpUtils {
    
    private static final String IP_4_REGEX = "^(((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5])|(\\*))\\.){3}((\\d{1,2})|(1\\d{2})|(2[0-4]\\d)|(25[0-5])|(\\*))$";
    private static final String IP_6_REGEX = "^(?:([0-9a-fA-F]{1,4}:)|(\\*)){7}(([0-9a-fA-F]{1,4})|(\\*))$";
    private static final String IP_6_REGEX_COMPRESS = "^((?:(([0-9A-Fa-f]{1,4})|(\\*))(?::(([0-9A-Fa-f]{1,4})|(\\*)))*)?)::((?:([0-9A-Fa-f]{1,4}|(\\*))(?::([0-9A-Fa-f]{1,4}|(\\*)))*)?)$";

    private static final Pattern ipv4Pattern = Pattern.compile(IP_4_REGEX);
    private static final Pattern ipv6Pattern = Pattern.compile(IP_6_REGEX);
    private static final Pattern ipv6CompressPattern = Pattern.compile(IP_6_REGEX_COMPRESS);
    
    private IpUtils() {
        throw new IllegalStateException("Utility class");
    }
    
    public static boolean validIpv4(String ipv4) {
        return ipv4Pattern.matcher(ipv4).find();
    }
    
    public static boolean validIpv6(String ipv6) {
        return ipv6Pattern.matcher(ipv6).find() || ipv6CompressPattern.matcher(ipv6).find();
    }
    
    /**
     * @param pattern ipv4 or ipv6 and star pattern ex: 
     *   127.0.0.*
     *   FE80::0202:B3FF:*:8329
     * @param ip
     * @return
     */
    public static boolean contain(String pattern, String ip) {
        
    	String localPattern = pattern.replace(".", "\\.");
    	localPattern = localPattern.replace("*", ".*");
    	localPattern = localPattern.replace("::", ":(0000:){1,7}");
    	localPattern = "^" + localPattern + "$";
        
        return Pattern.compile(localPattern).matcher(ip).find();
    }

    public static String getArea(String ip){
        IPEntity seek = IPSeeker.seek(ip);
        if(seek.getNation() == null && seek.getProvince()!= null && seek.getCity()!= null){
            return  seek.getCity();
        }else {
            return  seek.getNation();
        }
    }
}
