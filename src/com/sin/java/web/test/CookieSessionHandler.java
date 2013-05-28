package com.sin.java.web.test;

import com.sin.java.web.server.BaseHandler;

/**
 * Cookie and Session Test
 * 
 * @author RobinTang
 * 
 *         2013-5-28
 */
public class CookieSessionHandler extends BaseHandler {
	public String cookietest() {
		int count = 0;
		try{
			count = Integer.parseInt(getCookie("count"));
		}
		catch(Exception e){
			count = 0;
		}
		++count;
		setCookie("count", ""+count);
		setCookie("name", "robin");
		
		return 	"<center><h1>Cookie Test</h1><br />" + 
				"<p>You are visited this page <font color=\"RED\">"+count+"</font> count!</p>" +
				"<p><a href=\"\">Please refresh</a></p>" +
//				"<form method=\"POST\">" + 
//				"CookieKey:" + 
//				"<input type=\"text\" name=\"key\" />" + 
//				"<br />" + 
//				"CookieValue:" + 
//				"<input type=\"text\" name=\"value\" />" +
//				"<br />" + 
//				"<input type=\"submit\" value=\"Submit\" />" + 
//				"</form>" +
				"</center>";
	}
}
