package com.sin.java.web.server;

import java.util.Hashtable;
import java.util.Map;

/**
 * The header, in fact it's a map.
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class BaseHeader {
	protected Map<String, String> header = new Hashtable<String, String>();

	public void set(String k, String v) {
		this.header.put(k, v);
	}

	public String get(String k) {
		return this.header.get(k);
	}
	
	public boolean containsKey(String k){
		return this.header.containsKey(k);
	}
}
