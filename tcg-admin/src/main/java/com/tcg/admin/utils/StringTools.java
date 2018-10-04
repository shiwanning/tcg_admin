package com.tcg.admin.utils;

import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 字符串辅助类
 */
public class StringTools extends StringUtils {

	/**
	 * 判断字符串是否为null或者""或者"null"
	 * @param source
	 * @return
	 */
	public static boolean isEmptyOrNull(String source) {

		return StringUtils.isBlank(source) || ("null".equalsIgnoreCase(source));
	}
	
	/**
	 * 把一个Object转化成字符串
	 * @param obj
	 * @return
	 */
	public static String objectToString(Object obj) {
		if (null == obj) {
			return "";
		} else {
			return String.valueOf(obj).trim();
		}
	}
	/**
	 * 中文字符串URL解码
	 * @param source
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String strDencode(String source)
			throws UnsupportedEncodingException {
		if (null == source || "".equals(source) || "null".equals(source)) {
			return "";
		} else {
			return URLDecoder.decode(source.trim(), "UTF-8");
		}
	}
	/**
	 * 根据会员ID生产订单编号
	 * @param customerid
	 * @return
	 */
	public static String createOrderNumber(Integer customerid){
		long nowTime = new Date().getTime();
		return customerid+"_"+Long.toString(nowTime, 32);
	}

	public static String appendMerchantCodeUsername(String merchantCode, String username) {
		return merchantCode.toLowerCase() + "@" + username;
	}
    
    public static String maskLast(String str, int stars) {
        if (str == null) {
            return null;
        }
        if (stars < 0) {
            throw new IllegalArgumentException("stars < 0");
        }
        if (str.length() <= stars) {
            return getMask(stars);
        }
        return showFirst(str, str.length() - stars, stars);
    }

    private static String getMask(int stars) {
        if (stars <= 0) {
            return "";
        }
        char[] c = new char[stars];
        for (int i = 0; i < c.length; i++) {
            c[i] = '*';
        }
        return new String(c);
    }
    
    public static String showFirst(String str, int firstToShow, int stars) {
        if (str == null) {
            return null;
        }
        if (firstToShow < 0 || stars < 0) {
            throw new IllegalArgumentException("lastToShow < 0 || stars < 0");
        }       
        String firstX = str.substring(0, firstToShow);
        return new StringBuilder().append(firstX).append(getMask(stars)).toString();
    }

    public static List<Integer> transStringToList(String splitString){
        List<Integer> returnList = new ArrayList<>();
        List<String> splitArray = Arrays.asList(splitString.split(","));
        for(String str: splitArray){
            returnList.add(Integer.parseInt(str));
        }
        return returnList;
    }
    
    public static String sliceHtml(String htmlString){
    	return StringTools.isNotEmpty(htmlString) ? htmlString.replaceAll("<[^>]*>", "") : "";
    }

    public static String listToString(List<String> list) {
        StringBuilder sb = new StringBuilder();
        int listSize = list.size();

        for(int i=1 ; i <= listSize ; i++){
            sb.append(list.get(i-1));
            if(i != listSize){
                sb.append(",");
            }
        }

        return sb.toString();
    }
}
