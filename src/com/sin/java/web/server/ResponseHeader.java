package com.sin.java.web.server;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The response header. ClientThread will add Content-Length field if handler
 * response is not null.
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class ResponseHeader extends BaseHeader {
	private String protocol = "HTTP/1.0";
	private int code = 200;
	private String describe = "OK";

	public ResponseHeader() {
		super();
		this.set("Connection", "Keep-Alive");
		this.set("Content-Type", "text/html; charset=UTF-8");
//		this.set("Date", new Date().toString());
		this.set("Server", "Sin Java WebServer");
	}

	public String getHeaderString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%s %d %s\n", protocol, code, describe));
		Iterator<Entry<String, String>> ir = header.entrySet().iterator();
		while (ir.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<java.lang.String, java.lang.String>) ir.next();
			sb.append(String.format("%s: %s\n", entry.getKey(), entry.getValue()));
		}
		sb.append('\n');
		return sb.toString();
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	
}
