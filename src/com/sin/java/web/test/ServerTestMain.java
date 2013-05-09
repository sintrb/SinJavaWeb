package com.sin.java.web.test;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.sin.java.web.server.WebServer;

public class ServerTestMain {

	public static void main(String[] args) {
		WebServer server = new WebServer(9011); // the listen port is 9011

		server.addHandler("/", "com.sin.java.web.test.HelloHandler.hello"); // hello
																			// page,
																			// use
																			// GET
																			// method

		Map<String, Object> injectMap = new HashMap<String, Object>();
		injectMap.put("name", "Robin");
		server.addHandler("/inject", "com.sin.java.web.test.HighlevelHandler.inject", injectMap); // inject
																									// page
		server.addHandler("/args/([^/]*)/([^/]*).*", "com.sin.java.web.test.HighlevelHandler.urlargs"); // argument
																										// page
		server.addHandler("/dl/(.*)", "com.sin.java.web.test.HighlevelHandler.download"); // download
																							// page

		server.addHandler("/.*", "com.sin.java.web.test.HelloHandler.hello"); // default
																				// page
		server.start();

		Date date = new Date();
		String s = toGMTString(date);
		Date date2 = fromGTMString(s);
		System.out.println(s);
		System.out.println(date.getTime());
		System.out.println(date2.getTime());
		System.out.println(date2.compareTo(date));
		String s2 = toGMTString(date2);
		System.out.println(s2);
		// Now you can test the Server by follow URLs:
		// http://127.0.0.1:9011 hello page
		// http://127.0.0.1:9011/inject inject test page
		// http://127.0.0.1:9011/args/Robin/23 arguments test page
		// http://127.0.0.1:9011/dl/test.rar download test page, download from
		// files folder
	}

	public static final String FORMAT_GTM = "EEE,d MMM yyyy hh:mm:ss z";
	public static final String toGMTString(Date date) {
		Locale aLocale = Locale.ENGLISH;
		DateFormat fmt = new SimpleDateFormat(FORMAT_GTM, new DateFormatSymbols(aLocale));
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return fmt.format(date);
	}
	public static final Date fromGTMString(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_GTM, Locale.ENGLISH);
		Date date = null;
		try {
			date = sdf.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}
