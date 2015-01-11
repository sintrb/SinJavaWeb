package com.sin.java.web.server;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * A WebServer <br />
 * <br />
 * <strong>More infomation:</strong><a
 * href="https://github.com/sintrb/SinJavaWeb"
 * >https://github.com/sintrb/SinJavaWeb</a>
 * 
 * @version 1.1.1
 * @author RobinTang
 *         Change remove response header "Connection: Keep-Alive"
 *         2014-11-02
 *         
 * @version 1.1
 * @author RobinTang
 *         HTTP Basic Authorize(401)
 *         2013-11-28
 * 
 * @version 1.0
 * @author RobinTang
 *         Using thread pool
 *         2013-11-26
 *         
 * @version 0.2
 * @author RobinTang
 *         HTTP 304, 404
 *         2013-11-20
 * 
 * 
 * @version 0.1
 * @author RobinTang
 *         Base version
 *         2013-5-7
 */
public class WebServer {
	/**
	 * Web Server Version
	 */
	public static final String VERSION = "1.1";
	public static final String DATE = "2013-11-28";

	// public static final String VERSION = "1.0";
	// public static final String DATE = "2013-11-26";

	// public static final String VERSION = "0.2";
	// public static final String DATE = "2013-11-20";

	// public static final String VERSION = "0.1";

	public enum Status {
		Stoped, Running, Paused
	}

	private int port;
	private String name = "Sin WebServer V" + VERSION;
	private UrlregexMapping urlsMapping;
	private Logger logger;

	private ServerSocket serverSocket;
	private ServerThread serverThread;
	private Status status = Status.Stoped;

	private Executor threadPool = null;

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

	/**
	 * Start server
	 * 
	 * @return return true when start success, otherwise return false(maybe open
	 *         socket fail)
	 */
	public boolean start() {
		try {
			log("Starting %s at port %d ...", name, port);
			this.serverSocket = new ServerSocket(port);
			this.setStatus(Status.Running);
			this.serverThread = new ServerThread(this);
			this.serverThread.start();
			return true;
		} catch (IOException e) {
			this.setStatus(Status.Stoped);
			log("Start server fail!", name, port);
			this.err(e);
		}
		return false;
	}

	/**
	 * Stop server and close server socket
	 * 
	 * @return always return true
	 */
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

	/**
	 * Suspend server
	 * 
	 * @return always return true
	 */
	public boolean pause() {
		this.setStatus(Status.Paused);
		return true;
	}

	/**
	 * Resume server
	 * 
	 * @return always return true
	 */
	public boolean resume() {
		this.setStatus(Status.Running);
		return true;
	}

	/**
	 * Get the server main socket, in fact it is unnecessary to call this method
	 * 
	 * @return the server socket
	 */
	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	/**
	 * Get the server status
	 * 
	 * @return server status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * Set the server status
	 */
	public void setStatus(Status status) {
		this.status = status;
		log("Server %s", status.toString());
	}

	/**
	 * Set thread pool to handle client request
	 * 
	 * @param threadPool
	 *            Thread pool, if pool is null the server will create a new
	 *            thread to handle each client request
	 */
	public void setThreadpool(Executor threadPool) {
		this.threadPool = threadPool;
	}

	/**
	 * Get thread pool, it will called by ServerThread
	 * 
	 * @return ThreadPool
	 */
	public Executor getThreadPool() {
		return threadPool;
	}

	/**
	 * Add a request handler
	 * 
	 * @param urlregex
	 *            request path pattern
	 * @param handler
	 *            request handler, contain class and method, f.e.
	 *            "com.sin.web.ApiHanlder.getuser"
	 */
	public void addHandler(String urlregex, String handler) {
		this.addHandler(urlregex, handler, null, null);
	}

	/**
	 * Add a request handler
	 * 
	 * @param urlregex
	 *            request path pattern
	 * @param handler
	 *            request handler, contain class and method, f.e.
	 *            "com.sin.web.ApiHanlder.getuser"
	 * @param injectMap
	 *            inject variable mapping
	 */
	public void addHandler(String urlregex, String handler, Map<String, Object> injectMap) {
		this.addHandler(urlregex, handler, injectMap, null);
	}

	/**
	 * Add a request handler
	 * 
	 * @param urlregex
	 *            request path pattern
	 * @param handler
	 *            request handler, contain class and method, f.e.
	 *            "com.sin.web.ApiHanlder.getuser"
	 * @param injectMap
	 *            inject variable mapping
	 * @param method
	 *            HTTP method, f.e: GET POST PUT etc, default is GET
	 */
	public void addHandler(String urlregex, String handler, String method) {
		this.addHandler(urlregex, handler, null, method);
	}

	/**
	 * Add a request handler
	 * 
	 * @param urlregex
	 *            request path pattern
	 * @param handler
	 *            request handler, contain class and method, f.e.
	 *            "com.sin.web.ApiHanlder.getuser"
	 * @param method
	 *            HTTP method, f.e: GET POST PUT etc, default is All
	 */
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

	/**
	 * Find and get a UrlregexMappingItem by request path and method
	 * 
	 * @param url
	 *            request path
	 * @param method
	 *            HTTP method
	 * @return
	 */
	public UrlregexMappingItem getHandler(String url, String method) {
		return this.urlsMapping.get(url, method);
	}

	/**
	 * Get server name
	 * 
	 * @return Server Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set server name
	 * 
	 * @param name
	 *            server name
	 */
	public void setName(String name) {
		this.name = name;
	}

	// for Logger
	/**
	 * Log a string
	 * 
	 * @param l
	 *            log string
	 */
	public void log(String l) {
		if (this.logger != null)
			this.logger.log(l);
	}

	/**
	 * Log by format
	 * 
	 * @param format
	 *            format string
	 * @param args
	 *            arguments
	 */
	public void log(String format, Object... args) {
		this.log(String.format(format, args));
	}

	/**
	 * Log a information string
	 * 
	 * @param i
	 *            information string
	 */
	public void inf(String i) {
		if (this.logger != null)
			this.logger.inf(i);
	}

	/**
	 * Log information by format
	 * 
	 * @param format
	 *            format string
	 * @param args
	 *            arguments
	 */
	public void inf(String format, Object... args) {
		this.inf(String.format(format, args));
	}

	/**
	 * Log a error string
	 * 
	 * @param e
	 *            error string
	 */
	public void err(String e) {
		if (this.logger != null)
			this.logger.err(e);
	}

	/**
	 * Log a Exception
	 * 
	 * @param e
	 *            Exception
	 */
	public void err(Exception e) {
		if (this.logger != null)
			this.logger.err(e);
	}

	/**
	 * Log error by format
	 * 
	 * @param format
	 *            format string
	 * @param args
	 *            arguments
	 */
	public void err(String format, Object... args) {
		this.err(String.format(format, args));
	}
}
