package com.sin.java.web.server;

/**
 * The Logger for Web Server
 * 
 * @author RobinTang
 * 
 *         2013-5-9
 */
public interface Logger {
	public void log(String l);
	public void inf(String i);
	public void err(String e);
	public void err(Exception e);
}
