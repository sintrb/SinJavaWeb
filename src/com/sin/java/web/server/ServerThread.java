package com.sin.java.web.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.sin.java.web.server.WebServer.Status;

/**
 * A thread to handle socket connect request.
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class ServerThread extends Thread {
	private WebServer webServer;
	private ServerSocket serverSocket;

	public ServerThread(WebServer webServer) {
		super();
		this.webServer = webServer;
		this.serverSocket = webServer.getServerSocket();
	}

	@Override
	public void run() {
		while (webServer.getStatus() != Status.Stoped) {
			try {
				Socket clientSocket = serverSocket.accept();
				if (webServer.getStatus() == Status.Running) {
					ClientRunner clientRunner = new ClientRunner(webServer, clientSocket);
					if (webServer.getThreadPool() != null) {
						// using thread pool
						webServer.getThreadPool().execute(clientRunner);
					} else {
						// not use thread pool
						new Thread(clientRunner).start();
					}
					// webServer.log("request from:%s",
					// clientSocket.getRemoteSocketAddress().toString());
				} else if (webServer.getStatus() == Status.Paused) {
					clientSocket.close();
				}
			} catch (IOException e) {
				if (webServer.getStatus() == Status.Stoped) {
					break;
				} else {
					webServer.err(e);
				}
			}
		}
	}
}
