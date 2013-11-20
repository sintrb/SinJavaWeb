package com.sin.java.web.test;

import java.util.HashMap;
import java.util.Map;

import com.sin.java.web.server.WebServer;

public class ServerTestMain {

	public static void main(String[] args) {

		// create a web server on port 9011
		WebServer server = new WebServer(9011);

		// hello page, use GET method
		server.addHandler("/", "com.sin.java.web.test.HelloHandler.hello");

		Map<String, Object> injectMap = new HashMap<String, Object>();
		injectMap.put("name", "Robin");

		// inject test page
		server.addHandler("/inject", "com.sin.java.web.test.HighlevelHandler.inject", injectMap);

		// argument test page
		server.addHandler("/args/([^/]*)/([^/]*).*", "com.sin.java.web.test.HighlevelHandler.urlargs");

		// download test page
		server.addHandler("/dl/(.*)", "com.sin.java.web.test.HighlevelHandler.download");

		// static file
		Map<String, Object> injStatic = new HashMap<String, Object>();
		injStatic.put("docroot", "./web");
		// handle static file
		server.addHandler("(/.*[.html|.js|.css|.png|.jpg|.gif|.ico])", "com.sin.java.web.server.handler.StaticHandler.handle", injStatic);
		
		
		// cookie test page
		server.addHandler("/cookie/.*", "com.sin.java.web.test.CookieSessionHandler.cookietest");
		
		// default test page
		server.addHandler("/.*", "com.sin.java.web.test.HelloHandler.hello");
		
		
		// start the server
		server.start();

		// Now you can test the Server by follow URLs:
		// http://127.0.0.1:9011 hello page
		// http://127.0.0.1:9011/inject inject test page
		// http://127.0.0.1:9011/args/Robin/23 arguments test page
		// http://127.0.0.1:9011/dl/test.rar download test page, download from
		// http://127.0.0.1:9011/index.html open a simple website
		// files folder
	}
}















