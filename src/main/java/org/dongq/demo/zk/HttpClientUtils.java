package org.dongq.demo.zk;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

public final class HttpClientUtils {

	static DefaultHttpClient client = new DefaultHttpClient();
	
	final static String METHOD_GET  = "GET";
	final static String METHOD_POST = "POST";
	
	public static final String getJson(String sessionId, String uri, String method) {
		String jsonString = null;
		
		try {
			
			if(method.equals(METHOD_GET)) {
				HttpGet httpGet = new HttpGet(uri);
				Header header = new BasicHeader("Cookie", "JSESSIONID="+sessionId);
				httpGet.setHeader(header);
				HttpResponse resp = client.execute(httpGet);
			    HttpEntity ent = resp.getEntity();
			    jsonString = EntityUtils.toString(ent);
			    System.out.println("jsonString="+jsonString);
			    EntityUtils.consume(ent);
			} else if(method.equals(METHOD_POST)) {
				HttpPost httpPost = new HttpPost(uri);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return jsonString;
	}
}
