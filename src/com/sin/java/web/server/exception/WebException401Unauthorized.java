package com.sin.java.web.server.exception;

import com.sin.java.web.server.BaseHandler;
import com.sin.java.web.server.BaseWebException;
import com.sin.java.web.server.WebServer;

/**
 * HTTP 401 Unauthorized <br/>
 * 
 * @author RobinTang
 * @date 2013-11-28
 */
public class WebException401Unauthorized extends BaseWebException {
	private static final long serialVersionUID = 5347862129979078113L;

	public static final int CODE = 401;
	public static final String DESCRIBE = "Unauthozied";
	public static final String RESPONSE = "<center><strong>401 Unauthozied</strong></center>";

	public WebException401Unauthorized(BaseHandler handler, WebServer server) {
		super(CODE, DESCRIBE, RESPONSE);
		handler.getResponseHeader().set("WWW-Authenticate", "Basic realm=\"" + server.getName() + "\"");
	}
}
