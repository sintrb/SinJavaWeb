package com.sin.java.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The handler to handle some URL request.
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public abstract class BaseHandler {
	protected WebServer webServer;
	protected RequestHeader requestHeader;
	protected RequestBody requestBody;
	protected ResponseHeader responseHeader;
	protected ResponseBody responseBody;
	protected OutputStream outputStream;
	public boolean responseed = false;

	protected Map<String, String> requestCookies = new Hashtable<String, String>();
	protected Map<String, Cookie> responseCookies = null;

	public String getCookie(String key) {
		Cookie cookie = this.responseCookies.get(key);
		if (cookie != null) {
			return cookie.value;
		} else
			return this.requestCookies.get(key);
	}

	public void setCookie(String key, String value) {
		setCookie(key, value, null, null, null);
	}

	public void setCookie(String key, String value, String path, String domain, Date expires) {
		Cookie cookie = this.responseCookies.get(key);
		if (cookie == null) {
			cookie = new Cookie(key, value);
			this.responseCookies.put(key, cookie);
		}
		cookie.value = value;
		cookie.path = path;
		cookie.domain = domain;
		cookie.expires = expires;
	}

	public RequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(RequestHeader requestHeader) {
		String cookie = requestHeader.get("Cookie");
		if (cookie != null) {
			Pattern pattern = Pattern.compile("([^;,^\\s]+)=([^;,^\\s]+)");
			Matcher matcher = pattern.matcher(cookie);
			while (matcher.find()) {
				requestCookies.put(matcher.group(1), matcher.group(2));
			}
		}
		this.requestHeader = requestHeader;
	}

	public RequestBody getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(RequestBody requestBody) {
		this.requestBody = requestBody;
	}

	public ResponseHeader getResponseHeader() {
		return responseHeader;
	}

	public void setResponseHeader(ResponseHeader responseHeader) {
		this.responseHeader = responseHeader;
		this.responseCookies = responseHeader.responseCookies;
	}

	public ResponseBody getResponseBody() {
		return responseBody;
	}

	public void setResponseBody(ResponseBody responseBody) {
		this.responseBody = responseBody;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public WebServer getWebServer() {
		return webServer;
	}

	public void setWebServer(WebServer webServer) {
		this.webServer = webServer;
	}

	// basic method
	public void setContentType(String type) {
		this.responseHeader.set("Content-Type", type);
	}

	public String getContentType() {
		return this.responseHeader.get("Content-Type");
	}

	public void setContentLength(long len) {
		this.responseHeader.set("Content-Length", "" + len);
	}

	protected void responseStream(InputStream inputStream) {
		try {
			responseStream(inputStream, -1, inputStream.available());
		} catch (Exception e) {
			webServer.err(e);
		}
	}

	protected void responseStream(InputStream inputStream, String type) {
		try {
			responseStream(inputStream, -1, inputStream.available(), type);
		} catch (IOException e) {
			webServer.err(e);
		}
	}

	protected void responseStream(InputStream inputStream, long skip, long len) {
		responseStream(inputStream, skip, len, null);
	}

	protected void responseStream(InputStream inputStream, long skip, long len, String type) {
		if (type != null) {
			this.setContentType(type);
		}
		if (this.getContentType() == null) {
			this.setContentType("application/x-compress");
		}
		try {
			if (skip > 0)
				inputStream.skip(skip);
			if (len > 0)
				setContentLength(len);
			outputStream.write(this.responseHeader.getHeaderString().getBytes());

			int buflen = 1024;
			byte[] buf = new byte[1024];
			long readed = 0;
			int count;
			while (readed < len) {
				count = inputStream.read(buf, 0, len > 0 ? buflen <= (len - readed) ? buflen : (int) (len - readed) : buflen);
				if (count < 0)
					break;
				readed += count;
				outputStream.write(buf, 0, count);
			}
		} catch (IOException e) {
			webServer.err(e);
		}
		this.responseed = true;
	}
}
