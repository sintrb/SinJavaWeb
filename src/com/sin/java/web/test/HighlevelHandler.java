package com.sin.java.web.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.sin.java.web.server.BaseHandler;

/**
 * High level test.
 * 
 * @author RobinTang
 * 
 *         2013-5-8
 */
public class HighlevelHandler extends BaseHandler {
	public String name;
	public String inject() {
		return "<h1>Your name is "+this.name+"</h1>";
	}

	public String urlargs(String name, String age) {
		return String.format("<p>Name: %s Arg: %s<br></p>", name, age);
	}
	
	public String download(String filename) {
		String res = null;
		try {
			File file = new File("./files/"+filename);
			InputStream inputStream = new FileInputStream(file);
			this.responseHeader.set("Content-Disposition", "attachment; filename=\""+filename+"\"");
			this.responseStream(inputStream, -1, file.length());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			responseHeader.setCode(404);
			responseHeader.setDescribe("Not Found");
			res = "<h>404 Not Found</h>";
		}
		return res;
	}
}
