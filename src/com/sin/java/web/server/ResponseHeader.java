package com.sin.java.web.server;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.sin.java.web.server.util.DateUtils;

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
	protected Map<String, Cookie> responseCookies = new Hashtable<String, Cookie>();

	public ResponseHeader() {
		super();
		this.set("Connection", "Keep-Alive");
		this.set("Date", DateUtils.toGMTString());
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
		if (this.responseCookies.size() > 0) {

			Iterator<Entry<String, Cookie>> ic = responseCookies.entrySet().iterator();
			Entry<String, Cookie> sc = null;
			
			while (ic.hasNext()) {
				sc = ic.next();
				String ln = String.format("Set-Cookie: %s\n", sc.getValue().toSetString());
				sb.append(ln);
			}
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
