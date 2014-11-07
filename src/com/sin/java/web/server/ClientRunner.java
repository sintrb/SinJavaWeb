package com.sin.java.web.server;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
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
public class ClientRunner implements Runnable {
	private WebServer webServer;
	private Socket clientSocket;

	public ClientRunner(WebServer webServer, Socket clientSocket) {
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

	private String readLineFromStream(InputStream is) {
		ArrayList<Byte> bts = new ArrayList<Byte>();
		try {
			while (true) {
				int r = is.read();
				if (r == -1)
					break;
				else if (r == '\n')
					break;
				else if (r != '\r')
					bts.add((byte) r);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (bts.size() == 0) {
			return null;
		} else {
			byte[] dats = new byte[bts.size()];
			for (int i = 0; i < dats.length; i++) {
				dats[i] = bts.get(i);
			}
			return new String(dats);
		}
	}

	@Override
	public void run() {
		RequestHeader requestHeader = null;
		try {
			InputStream inputStream = this.clientSocket.getInputStream();
			String l = readLineFromStream(inputStream); // method, path,
														// protocol
			if (l == null)
				return; // connect break
			String[] ss = l.split(" ");
			if (ss.length != 3)
				return; // not a HTTP request
			requestHeader = new RequestHeader(ss[0], ss[1], ss[2]);
			UrlregexMappingItem urlsMapItem = this.webServer.getHandler(requestHeader.getPath(), requestHeader.getMethod());
			if (urlsMapItem != null) {
				while ((l = readLineFromStream(inputStream)) != null && l.length() > 0) {
					int i = l.indexOf(':');
					requestHeader.set(l.substring(0, i).trim(), l.substring(i + 1).trim());
				}

				RequestBody requestBody = new RequestBody();
				if (requestHeader.containsKey("Content-Length")) {
					int len = Integer.parseInt(requestHeader.get("Content-Length"));
					byte[] buf = new byte[len];
					int ix = 0;
					while (ix < len) {
						ix += inputStream.read(buf, ix, len - ix);
					}
					requestBody.data = buf;
				}
				String handlerClassName = urlsMapItem.handlerClassName;
				String handlerMethodName = urlsMapItem.handlerMethodName;

				Class<?> handlerClass = Class.forName(handlerClassName);

				BaseHandler handler = (BaseHandler) handlerClass.newInstance();

				// base member fields
				handler.setRequestHeader(requestHeader);
				handler.setRequestBody(requestBody);
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
				String res = null;
				try {
					res = (String) handlerMethod.invoke(handler, args);
				} catch (InvocationTargetException e) {
					if (BaseWebException.class.isInstance(e.getTargetException())) {
						BaseWebException we = (BaseWebException) e.getTargetException();
						handler.getResponseHeader().setCode(we.getCode());
						handler.getResponseHeader().setDescribe(we.getDescribe());
						res = we.getResponse();
					} else if (e.getCause() instanceof Exception) {
						throw (Exception) e.getCause();
					} else {
						webServer.err(e.getMessage());
					}
				}
				if (handler.responseed == false) {
					if (res != null && handler.getResponseHeader().get("Content-Length") == null) {
						byte[] bts = res.getBytes();
						handler.setContentLength(bts.length);
						if (handler.getContentType() == null) {
							handler.setContentType("text/html; charset=UTF-8");
						}
						this.clientSocket.getOutputStream().write(handler.getResponseHeader().getHeaderString().getBytes());
						this.clientSocket.getOutputStream().write(bts);
					} else {
						this.clientSocket.getOutputStream().write(handler.getResponseHeader().getHeaderString().getBytes());
					}
				}
				webServer.log("%s %s %s %d %s", requestHeader.getMethod(), requestHeader.getPath(), requestHeader.getProtocol(), handler.getResponseHeader().getCode(), handler.getResponseHeader().getDescribe());

				// close stream
				inputStream.close();
			} else {
				throw new Exception(String.format("Unknow how to handle url(%s): %s ", requestHeader.getMethod(), requestHeader.getPath()));
			}
		} catch (Exception e) {
			webServer.err(e);
			if (requestHeader != null)
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
