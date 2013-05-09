package com.sin.java.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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

	public RequestHeader getRequestHeader() {
		return requestHeader;
	}

	public void setRequestHeader(RequestHeader requestHeader) {
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
	protected void responseStream(InputStream inputStream){
		responseStream(inputStream, -1, -1);
	}
	protected void responseStream(InputStream inputStream, String type){
		try {
			responseStream(inputStream, -1, inputStream.available(), type);
		} catch (IOException e) {
			webServer.err(e);
		}
	}
	protected void responseStream(InputStream inputStream, long skip, long len){
		responseStream(inputStream, skip, len, null);
	}
	protected void responseStream(InputStream inputStream, long skip, long len, String type) {
		if(type != null){
			this.setContentType(type);
		}
		if(this.getContentType() == null){
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
				if(count<0)
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
