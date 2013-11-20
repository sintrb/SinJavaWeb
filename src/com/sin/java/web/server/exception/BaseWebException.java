package com.sin.java.web.server.exception;


/**
 * Base Web Exception
 * <br/>
 * @author trb
 * @date 2013-11-20
 */
public class BaseWebException extends Exception {
	private static final long serialVersionUID = 846074876870941523L;
	
	private int code;
	private String describe;
	private String response;
	public BaseWebException(int code, String describe, String response) {
		super();
		this.code = code;
		this.describe = describe;
		this.response = response;
	}
	public BaseWebException(int code, String describe) {
		super();
		this.code = code;
		this.describe = describe;
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
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
