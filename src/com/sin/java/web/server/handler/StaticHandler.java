package com.sin.java.web.server.handler;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import com.sin.java.web.server.BaseHandler;

/**
 * A handler for static files. It handle for: .html .css .js .png .jpg .ico etc
 * 
 * @author RobinTang
 * 
 *         2013-5-10
 */
public class StaticHandler extends BaseHandler {
	public String docroot = null; // the root path of static file
	public static Map<String, String> suffixToTypeMap = new HashMap<String, String>();

	public StaticHandler() {
		super();
		suffixToTypeMap.put(".js", "application/x-javascript");
		suffixToTypeMap.put(".css", "text/css");
		suffixToTypeMap.put(".html", "text/html; charset=utf-8");
		suffixToTypeMap.put(".png", "image/png");
		suffixToTypeMap.put(".ico", "image/x-icon");
		suffixToTypeMap.put(".jpg", "image/jpeg");
		suffixToTypeMap.put(".gif", "image/gif");
	}

	public static void addSuffixTypeMap(String suffix, String type) {
		suffixToTypeMap.put(suffix, type);
	}

	public String handle(String filename) throws Exception {
		String res = null;
		int ix = filename.lastIndexOf('.');
		String type = null;
		if (ix >= 0 && (type = suffixToTypeMap.get(filename.substring(ix))) != null) {
			String abspath = (docroot != null ? docroot + filename : filename);
			File file = new File(abspath);
			if (file.exists() && file.canRead()) {

				String etag = requestHeader.get("If-None-Match");
				String ntag = "" + file.lastModified();
				if (etag != null && etag.equals(ntag)) {
					// not modify
					responseHeader.setCode(304);
					responseHeader.setDescribe("Not Modified");
					return res = "<center><strong>403 Not Modified.</strong></center>";
				} else {
					responseHeader.set("ETag", ntag);
					FileInputStream fileInputStream = new FileInputStream(abspath);
					responseStream(fileInputStream, type);
					fileInputStream.close();
				}
			} else {
				responseHeader.setCode(404);
				responseHeader.setDescribe("Not Found");
				res = "<center><strong>404 Not Found.</strong></center>";
				webServer.log(res);
			}
		} else {
			throw new Exception("Unknown file type when handle: " + filename);
		}
		return res;
	}
}
