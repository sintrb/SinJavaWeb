package com.sin.java.web.server.exception;

import com.sin.java.web.server.BaseWebException;

/**
 * HTTP 304 Not Modified <br/>
 * 
 * @author RobinTang
 * @date 2013-11-20
 */
public class WebException304NotModified extends BaseWebException {
	private static final long serialVersionUID = 5347862129979078112L;

	public static final int CODE = 304;
	public static final String DESCRIBE = "Not Modified";
	public static final String RESPONSE = null;// "<center><strong>304 Not Modified.</strong></center>";

	public WebException304NotModified() {
		super(CODE, DESCRIBE, RESPONSE);
	}
}
