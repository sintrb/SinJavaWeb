package com.sin.java.web.server;

import java.util.Map;

/**
 * The Item model if URL regular 
 * @author RobinTang
 *
 * 2013-5-8
 */
public class UrlregexMappingItem {
	public String urlregex;
	public String method;
	public String handler;

	public String handlerClassName;
	public String handlerMethodName;

	public Map<String, Object> injectMap;
	
	@Override
	public boolean equals(Object obj) {
		UrlregexMappingItem item = (UrlregexMappingItem) obj;
		return item.urlregex.equals(this.urlregex);
	}

	public UrlregexMappingItem(String urlregex, String method, String handler, String handlerClassName, String handlerMethodName, Map<String, Object> injectMap) {
		super();
		this.urlregex = urlregex;
		this.method = method;
		this.handler = handler;
		this.handlerClassName = handlerClassName;
		this.handlerMethodName = handlerMethodName;
		this.injectMap = injectMap;
	}
}
