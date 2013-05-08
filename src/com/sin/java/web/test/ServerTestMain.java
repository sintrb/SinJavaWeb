package com.sin.java.web.test;

import java.util.HashMap;
import java.util.Map;

import com.sin.java.web.server.WebServer;

public class ServerTestMain {

	public static void main(String[] args) {
		WebServer server = new WebServer(9011);	// the listen port is 9011
		
		server.addHandler("/", "com.sin.java.web.test.HelloHandler.hello");	// hello page, use GET method
		
		Map<String, Object> injectMap = new HashMap<String, Object>();
		injectMap.put("name", "Robin");
		server.addHandler("/inject", "com.sin.java.web.test.HighlevelHandler.inject", injectMap);	// inject page
		server.addHandler("/args/([^/]*)/([^/]*).*", "com.sin.java.web.test.HighlevelHandler.urlargs"); // argument page
		server.addHandler("/dl/(.*)", "com.sin.java.web.test.HighlevelHandler.download");	// download page
		
		server.addHandler("/.*", "com.sin.java.web.test.HelloHandler.hello"); // default page
		server.start();
		
		
		// Now you can test the Server by follow URLs:
		// http://127.0.0.1:9011					hello page
		// http://127.0.0.1:9011/inject				inject test page
		// http://127.0.0.1:9011/args/Robin/23		arguments test page
		// http://127.0.0.1:9011/dl/test.rar		download test page, download from files folder
	}
}
