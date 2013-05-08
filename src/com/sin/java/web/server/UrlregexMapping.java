package com.sin.java.web.server;

import java.util.ArrayList;
import java.util.List;

/**
 * URL to RequestHandler mapping
 * 
 * @author RobinTang
 * 
 *         2013-5-7
 */
public class UrlregexMapping {
	private List<UrlregexMappingItem> mapitems = new ArrayList<UrlregexMappingItem>();
	public void add(UrlregexMappingItem item) throws Exception {
		if (!mapitems.contains(item)) {
			this.mapitems.add(item);
		} else {
			throw new Exception("Already exist urlregex: " + item.toString());
		}
	}

	public UrlregexMappingItem get(String url, String method) {
		int count = size();
		for (int i = 0; i < count; ++i) {
			UrlregexMappingItem item = this.mapitems.get(i);
			if (item.method.equals(method) && url.matches(item.urlregex)) {
				return this.mapitems.get(i);
			}
		}
		return null;
	}

	public int size() {
		return this.mapitems.size();
	}
}
