package com.sin.java.web.server.exception;

import com.sin.java.web.server.BaseWebException;

/**
 * HTTP 404 Not Found <br/>
 * 
 * @author trb
 * @date 2013-11-20
 */
public class WebException404NotFound extends BaseWebException {
	private static final long serialVersionUID = 5347862129979078111L;

	public static final int CODE = 404;
	public static final String DESCRIBE = "Not Found";
	public static final String RESPONSE = "<center><strong>404 Not Found.</strong></center>";

	public WebException404NotFound() {
		super(CODE, DESCRIBE, RESPONSE);
	}
}
