package com.sin.java.web.server;

import java.util.Date;

import com.sin.java.web.server.util.DateUtils;

/**
 * Server Cookie Class
 * 
 * @author RobinTang
 * 
 *         2013-5-28
 */
public class Cookie {
	public String key;
	public String value;
	public String path;
	public String domain;
	public Date expires;

	public Cookie(String key, String value) {
		this(key, value, null, null, null);
	}

	public Cookie(String key, String value, String path, String domain, Date expires) {
		this.key = key;
		this.value = value;
		this.path = path;
		this.domain = domain;
		this.expires = expires;
	}

	public String toSetString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%s=%s; ", key, value));
		if (path != null)
			sb.append(String.format("path=%s;", path));
		if (domain != null)
			sb.append(String.format("domain=%s;", domain));
		if (expires != null)
			sb.append(String.format("expires=%s;", DateUtils.toGMTString(expires)));
		return sb.toString();
	}
}
