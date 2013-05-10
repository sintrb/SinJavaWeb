package com.sin.java.web.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Map;

/**
 * A WebServer
 * <br /><br />
 * <strong>More infomation:</strong><a href="https://github.com/sintrb/SinJavaWeb">https://github.com/sintrb/SinJavaWeb</a>
 * @version 0.1
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class WebServer {
	/**
	 * Web Server Version
	 */
	public static final String VERSION = "0.1";

	public enum Status {
		Stoped, Running, Paused
	}

	private int port;
	private UrlregexMapping urlsMapping;
	private Logger logger;

	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private Status status = Status.Stoped;

	/**
	 * Creates a WebServer by port
	 * 
	 * @param port
	 *            The port of the server.
	 */
	public WebServer(int port) {
		this(port, new UrlregexMapping(), new BaseLogger());
	}

	/**
	 * 
	 * @param port
	 *            The port of the server.
	 * @param urlsMapping
	 *            The UrlregexMapping, it determine the handler of the request
	 *            each request URL.
	 * @param logger
	 *            The Logger for the server.
	 */
	public WebServer(int port, UrlregexMapping urlsMapping, Logger logger) {
		super();
		this.port = port;
		this.urlsMapping = urlsMapping;
		this.logger = logger;
	}

	public boolean start() {
		try {
			this.serverSocket = new ServerSocket(port);
			this.setStatus(Status.Running);
			this.serverThread = new ServerThread(this);
			this.serverThread.start();
			return true;
		} catch (IOException e) {
			this.setStatus(Status.Stoped);
			this.err(e);
		}
		return false;
	}

	public boolean stop() {
		this.setStatus(Status.Stoped);
		try {
			if (this.serverSocket != null)
				this.serverSocket.close();
		} catch (IOException e) {
			this.err(e);
		}
		return true;
	}

	public boolean pause() {
		this.setStatus(Status.Paused);
		return true;
	}

	public boolean resume() {
		this.setStatus(Status.Running);
		return true;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
		log("Server %s", status.toString());
	}

	public void addHandler(String urlregex, String handler) {
		this.addHandler(urlregex, handler, null, "GET");
	}

	public void addHandler(String urlregex, String handler, Map<String, Object> injectMap) {
		this.addHandler(urlregex, handler, injectMap, "GET");
	}

	public void addHandler(String urlregex, String handler, String method) {
		this.addHandler(urlregex, handler, null, method);
	}

	public void addHandler(String urlregex, String handler, Map<String, Object> injectMap, String method) {
		int ix = handler.lastIndexOf('.');
		String handlerClassName = handler.substring(0, ix);
		String handlerMethodName = handler.substring(ix + 1);
		try {
			Class<?> handlerClass = Class.forName(handlerClassName);
			if (BaseHandler.class.isAssignableFrom(handlerClass) == false) {
				throw new Exception(String.format("%s is not extends from %s.", handlerClassName, BaseHandler.class.getName()));
			}
			Method[] handlerMethods = handlerClass.getMethods();
			Method handlerMethod = null;
			for (Method mt : handlerMethods) {
				if (mt.getName().equals(handlerMethodName)) {
					handlerMethod = mt;
				}
			}
			if (handlerMethod == null) {
				throw new Exception(String.format("%s has not method is named %s.", handlerClassName, handlerMethodName));
			}
			if (String.class.isAssignableFrom(handlerMethod.getReturnType()) == false) {
				throw new Exception(String.format("%s return type is not String.", handler));
			}
			UrlregexMappingItem urlHandlerItem = new UrlregexMappingItem(urlregex, method, handler, handlerClassName, handlerMethodName, injectMap);
			this.urlsMapping.add(urlHandlerItem);
		} catch (Exception e) {
			this.err(e);
		}
	}

	public UrlregexMappingItem getHandler(String url, String method) {
		return this.urlsMapping.get(url, method);
	}

	// for Logger
	public void log(String l) {
		if (this.logger != null)
			this.logger.log(l);
	}

	public void log(String format, Object... args) {
		this.log(String.format(format, args));
	}

	public void inf(String i) {
		if (this.logger != null)
			this.logger.inf(i);
	}

	public void inf(String format, Object... args) {
		this.inf(String.format(format, args));
	}

	public void err(String e) {
		if (this.logger != null)
			this.logger.err(e);
	}

	public void err(Exception e) {
		if (this.logger != null)
			this.logger.err(e);
	}

	public void err(String format, Object... args) {
		this.err(String.format(format, args));
	}
}
