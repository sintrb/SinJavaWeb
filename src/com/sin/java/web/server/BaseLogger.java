package com.sin.java.web.server;


/**
 * A logger implemented by System.out and System.err.
 * @author RobinTang
 *
 * 2013-5-9
 */
public class BaseLogger implements Logger {

	@Override
	public void log(String l) {
		System.out.println("log>"+l);
	}

	@Override
	public void inf(String i) {
		System.out.println("inf>"+i);
	}

	@Override
	public void err(String e) {
		System.err.println("err>"+e);
	}

	@Override
	public void err(Exception e) {
		this.err(e.getMessage());
		e.printStackTrace();
	}
}
