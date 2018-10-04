package com.tcg.admin.utils;

/*
 * Decompiled with CFR 0_118.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTools {
	
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_FORMAT = "default";

    private DateTools() {}
    
    public static Locale getLocale() {
        return Locale.getDefault();
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getDefault();
    }

    public static Date getDate() {
        return DateTools.getCalendar().getTime();
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance(DateTools.getTimeZone(), DateTools.getLocale());
    }

    private static DateFormat getDateFormat(String format, Locale locale, TimeZone timezone) {
        if (format == null) {
            return null;
        }
        DateFormat df = null;
        if (format.endsWith("_date")) {
            String fmt = format.substring(0, format.length() - 5);
            int style = DateTools.getStyleAsInt(fmt);
            df = DateTools.getDateFormat(style, -1, locale, timezone);
        } else if (format.endsWith("_time")) {
            String fmt = format.substring(0, format.length() - 5);
            int style = DateTools.getStyleAsInt(fmt);
            df = DateTools.getDateFormat(-1, style, locale, timezone);
        } else {
            int style = DateTools.getStyleAsInt(format);
            if (style < 0) {
                df = new SimpleDateFormat(format, locale);
                df.setTimeZone(timezone);
            } else {
                df = DateTools.getDateFormat(style, style, locale, timezone);
            }
        }
        return df;
    }

    private static DateFormat getDateFormat(int dateStyle, int timeStyle, Locale locale, TimeZone timezone) {
        try {
            DateFormat df = dateStyle < 0 && timeStyle < 0 ? DateFormat.getInstance() : (timeStyle < 0 ? DateFormat.getDateInstance(dateStyle, locale) : (dateStyle < 0 ? DateFormat.getTimeInstance(timeStyle, locale) : DateFormat.getDateTimeInstance(dateStyle, timeStyle, locale)));
            df.setTimeZone(timezone);
            return df;
        }
        catch (Exception suppressed) {
            return null;
        }
    }

    private static int getStyleAsInt(String style) {
        if (style == null || style.length() < 4 || style.length() > 7) {
            return -1;
        }
        if (style.equalsIgnoreCase("full")) {
            return 0;
        }
        if (style.equalsIgnoreCase("long")) {
            return 1;
        }
        if (style.equalsIgnoreCase("medium")) {
            return 2;
        }
        if (style.equalsIgnoreCase("short")) {
            return 3;
        }
        if (style.equalsIgnoreCase("default")) {
            return 2;
        }
        return -1;
    }

    public static Date toDate(String format, Object obj) {
        return DateTools.toDate(format, obj, DateTools.getLocale(), DateTools.getTimeZone());
    }

    public static Date toDate(String format, Object obj, Locale locale, TimeZone timezone) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date)obj;
        }
        if (obj instanceof Calendar) {
            return ((Calendar)obj).getTime();
        }
        if (obj instanceof Long) {
            Date d = new Date();
            d.setTime((Long)obj);
            return d;
        }
        if (obj instanceof String) {
            try {
                return new SimpleDateFormat(format).parse((String)obj);
            }
            catch (Exception e) {
                return null;
            }
        }
        try {
            DateFormat parser = DateTools.getDateFormat(format, locale, timezone);
            if(parser != null) {
            	return parser.parse(String.valueOf(obj));
            } else {
            	return null;
            }
        }
        catch (Exception e) {
            return null;
        }
    }

    public static Date getNextDate(Date date) {
        return new Date(date.getTime() + 86400000);
    }
    public static long getLastTimeForDay(long currentTime, int count) {
        for (int i = 1; i <= count; ++i) {
            currentTime -= 86400000;
        }
        return currentTime;
    }
}
