package org.dongq.demo.zk;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.dongq.demo.zk.IndexComposer.SysMenu;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.event.ListDataListener;

public class MenuGridComposer extends GenericForwardComposer<Grid> {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAfterCompose(Grid comp) throws Exception {
		super.doAfterCompose(comp);
		Object sessionId = this.getPage().getDesktop().getSession().getAttribute("sessionId");
		final String uri = "http://192.168.1.100:9527/quickride/html/menu/?m=index";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		Header header = new BasicHeader("Cookie", "JSESSIONID="+sessionId.toString());
		httpGet.setHeader(header);

		HttpResponse resp = httpclient.execute(httpGet);
	    HttpEntity ent = resp.getEntity();
	    final String jsonString = EntityUtils.toString(ent);
	    System.out.println("initMenuTree="+jsonString);
	    EntityUtils.consume(ent);
		
	    JSONParser jsonParser = new JSONParser();
	    JSONObject json = (JSONObject) jsonParser.parse(jsonString);
	    int pgsz = 15;
	    JSONArray result = (JSONArray) json.get("result");
	    
		ListModel<SysMenu> model = new ListModelList<IndexComposer.SysMenu>();
		comp.setModel(model);
		
		comp.setPageSize(pgsz);
	}
	
	class SysMenuListModel implements ListModel<SysMenu> {

		public SysMenu getElementAt(int index) {
			return null;
		}

		public int getSize() {
			return 0;
		}

		public void addListDataListener(ListDataListener l) {
			
		}

		public void removeListDataListener(ListDataListener l) {
			
		}
		
	}
}
