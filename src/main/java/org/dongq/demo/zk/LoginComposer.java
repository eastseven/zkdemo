package org.dongq.demo.zk;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class LoginComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;

	@Wire("#username")
	Textbox username;
	
	@Wire("#password")
	Textbox password;
	
	@Wire("#login")
	Window login;
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
	}
	
	@Listen("onClick = button#submit")
	public void submit(Event event) {
		System.out.println("login submit event: " + username.getValue() + "," + password.getValue());
		
		try {
			final String uri = "http://192.168.1.100:9527/quickride/j_spring_security_check";
			DefaultHttpClient httpclient = new DefaultHttpClient();
			HttpPost post = new HttpPost(uri);
			List <NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("j_username", "qi.dong"));
			nvps.add(new BasicNameValuePair("j_password", "000000"));
			nvps.add(new BasicNameValuePair("j_captcha_response", "000000"));
			post.setEntity(new UrlEncodedFormEntity(nvps));
			
			HttpResponse response2 = httpclient.execute(post);

			System.out.println(response2.getStatusLine());
			HttpEntity entity2 = response2.getEntity();
			String jsonString = EntityUtils.toString(entity2);
			System.out.println(jsonString);
			// do something useful with the response body
			// and ensure it is fully consumed
			EntityUtils.consume(entity2);
			
			for (Cookie c : httpclient.getCookieStore().getCookies()) {
				System.out.println("cookie= " + c);
			}
			
			login.onClose();
			
			post.releaseConnection();
			
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
			JSONObject message = (JSONObject) json.get("message");
 			System.out.println("json: "+message.get("realName"));
 			
 			this.getPage().getDesktop().getSession().setAttribute("realName", message.get("realName"));
 			this.getPage().getDesktop().getSession().setAttribute("sessionId", message.get("sessionId"));
 			Executions.sendRedirect("index.zul");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
