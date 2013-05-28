package com.sin.java.web.test;

import com.sin.java.web.server.BaseHandler;

/**
 * A simple hell world!
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class HelloHandler extends BaseHandler {
	public String hello() {
		return 	"<center><h1>Hello World</h1><br />" + 
				"<a href=\"/inject\">Inject Test</a><br />" + 
				"<a href=\"/args/Robin/23\">Arguments Test</a><br />" + 
				"<a href=\"/dl/test.rar\" target=\"None\">Download Test</a><br />"+
				"<a href=\"/cookie/\" target=\"None\">Cookie Test</a><br />"+
				"<a href=\"/index.html\" target=\"None\">Static File, it's a simple site.</a><br />All pages from http://sc.chinaz.com/</center>";
	}
}
