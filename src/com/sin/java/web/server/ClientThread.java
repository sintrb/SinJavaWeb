package com.sin.java.web.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The thread to handle socket connect. Depend the request URL, it will dispatch
 * to Handler.
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class ClientThread extends Thread {
	private WebServer webServer;
	private Socket clientSocket;

	public ClientThread(WebServer webServer, Socket clientSocket) {
		super();
		this.webServer = webServer;
		this.clientSocket = clientSocket;
	}

	private void injectObject(Object obj, Map<String, Object> injectMap) {
		if (injectMap == null)
			return;

		Iterator<Entry<String, Object>> ir = injectMap.entrySet().iterator();
		Class<?> cls = obj.getClass();
		while (ir.hasNext()) {
			Entry<String, Object> entry = (Map.Entry<String, Object>) ir.next();
			try {
				Field field = cls.getField(entry.getKey());
				field.setAccessible(true);
				field.set(obj, entry.getValue());
			} catch (SecurityException e) {
				webServer.err(e);
			} catch (NoSuchFieldException e) {
				webServer.err(e);
			} catch (IllegalArgumentException e) {
				webServer.err(e);
			} catch (IllegalAccessException e) {
				webServer.err(e);
			}

		}
	}

	@Override
	public void run() {
		RequestHeader requestHeader = null;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			String l = br.readLine(); // method, path, protocol
			if(l == null)
				return; // connect break
			String[] ss = l.split(" ");
			if(ss.length != 3)
				return; // not a HTTP request
			requestHeader = new RequestHeader(ss[0], ss[1], ss[2]);
			UrlregexMappingItem urlsMapItem = this.webServer.getHandler(requestHeader.getPath(), requestHeader.getMethod());
			if (urlsMapItem != null) {
				while ((l = br.readLine()) != null && l.length() > 0) {
					int i = l.indexOf(':');
					requestHeader.set(l.substring(0, i).trim(), l.substring(i + 1).trim());
				}
				String handlerClassName = urlsMapItem.handlerClassName;
				String handlerMethodName = urlsMapItem.handlerMethodName;

				Class<?> handlerClass = Class.forName(handlerClassName);

				BaseHandler handler = (BaseHandler) handlerClass.newInstance();

				// base member fields
				handler.setRequestHeader(requestHeader);
				handler.setRequestBody(new RequestBody());
				handler.setResponseHeader(new ResponseHeader());
				handler.setResponseBody(new ResponseBody());
				handler.setOutputStream(this.clientSocket.getOutputStream());
				handler.setWebServer(webServer);
				// inject fields
				injectObject(handler, urlsMapItem.injectMap);

				// create arguments for method
				// urlsMapItem.urlregex;
				Pattern pattern = Pattern.compile(urlsMapItem.urlregex);
				Matcher matcher = pattern.matcher(requestHeader.getPath());
				int argscount = matcher.groupCount();
				Method handlerMethod = null;
				Object[] args = null;
				if (argscount > 0 && matcher.find()) {
					Class<?>[] parameterTypes = new Class<?>[argscount];
					for (int i = 0; i < argscount; ++i) {
						parameterTypes[i] = String.class;
					}
					handlerMethod = handlerClass.getMethod(handlerMethodName, parameterTypes);
					args = new Object[argscount];
					for (int i = 0; i < argscount; ++i) {
						args[i] = matcher.group(i + 1);
					}
				} else {
					handlerMethod = handlerClass.getMethod(handlerMethodName);
					args = new Object[0];
				}
				// call handle
				webServer.log("Method=%s Path=%s Protocol=%s", requestHeader.getMethod(), requestHeader.getPath(), requestHeader.getProtocol());
				String res = (String) handlerMethod.invoke(handler, args);

				if (handler.responseed == false) {
					if (res != null && handler.getResponseHeader().get("Content-Length") == null) {
						byte[] bts = res.getBytes();
						handler.setContentLength(bts.length);
						if(handler.getContentType()==null){
							handler.setContentType("text/html; charset=UTF-8");
						}
						this.clientSocket.getOutputStream().write(handler.getResponseHeader().getHeaderString().getBytes());
						this.clientSocket.getOutputStream().write(bts);
					} else {
						this.clientSocket.getOutputStream().write(handler.getResponseHeader().getHeaderString().getBytes());
					}
				}
			} else {
				throw new Exception(String.format("Unknow how to handle url(%s): %s ", requestHeader.getMethod(), requestHeader.getPath()));
			}
		} catch (Exception e) {
			webServer.err(e);
			if(requestHeader != null)
				webServer.log(requestHeader.toString());
		} finally {
			try {
				this.clientSocket.close();
			} catch (IOException e) {
				webServer.err(e);
			}
		}
	}
}
