package com.tcg.admin.utils;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateTools {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DateTools.class);
	
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_FORMAT = "default";

    private DateTools() {}
    
    public static Date parseDate(String date, String pattern) {
    	try {
			return DateUtils.parseDate(date, pattern);
		} catch (ParseException e) {
			LOGGER.error("parser date error", e);
			return null;
		}
    }

    public static Date getNextDate(Date date) {
        return new Date(date.getTime() + 86400000);
    }

    public static String format(Date date, String pattern) {
        return date == null ? " " : new SimpleDateFormat(pattern).format(date);
    }
}
