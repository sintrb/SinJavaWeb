package com.sin.java.web.server;

/**
 * The header of HTTP request.
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class RequestHeader extends BaseHeader {
	private String method;
	private String path;
	private String protocol;

	public String getMethod() {
		return method;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getPath() {
		return path;
	}

	public RequestHeader(String method, String path, String protocol) {
		this.method = method;
		this.path = path;
		this.protocol = protocol;
	}

	@Override
	public String toString() {
		return "RequestHeader [method=" + method + ", path=" + path + ", protocol=" + protocol + ", header=" + header + "]";
	}
}
