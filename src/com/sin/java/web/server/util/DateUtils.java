package com.sin.java.web.server.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * @author RobinTang
 *
 * 2013-5-9
 */
public class DateUtils {
	public static final String FORMAT_GMT = "EEE,d MMM yyyy hh:mm:ss z";	// GTM string format
	public static final Locale LOCALE_GMT = Locale.ENGLISH;	// The GTM locale London 
	public static final String toGMTString(){
		return toGMTString(new Date());
	}
	public static final String toGMTString(Date date) {
		DateFormat fmt = new SimpleDateFormat(FORMAT_GMT, new DateFormatSymbols(LOCALE_GMT));
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return fmt.format(date);
	}
	public static final Date fromGMTString(String str) {
		if(str==null)
			return null;
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_GMT, LOCALE_GMT);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	public static final Date getGMTDateTime(){
		return fromGMTString(toGMTString());
	}
}
