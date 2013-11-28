package com.sin.java.web.test;

import com.sin.java.web.server.BaseHandler;
import com.sin.java.web.server.exception.WebException401Unauthorized;


/**
 * A demo handler for HTTP Basic Authorize(401)
 * @author RobinTang
 *         2013-11-28
 */
public class HTTPAuthHandler extends BaseHandler {
	public String handle() throws Exception {
		// get Authorization of request header
		String raws = getRequestHeader().get("Authorization");
		if (raws == null || raws.length() <= 6) {
			throw new WebException401Unauthorized(this, webServer);
		} else {
			// Check, compare Authorization string with authorized string
			// You can use "Basic "+base("name:password") to calculate authorized string
			// Of course, you can decode raws and check the decoded string, but it will cost more time to decode
			// 
			//
			// At here, cm9iaW46MTIzNDU2 == base64("robin:123456"), so you can use robin:123456 to test the handle
			if(raws.equals("Basic cm9iaW46MTIzNDU2") == false)	// not correct
				throw new WebException401Unauthorized(this, webServer);
			
			// do other thing what will do when authorized
			return "success~~~~~~";
		}
	}
}
