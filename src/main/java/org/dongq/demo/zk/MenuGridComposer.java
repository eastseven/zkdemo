package org.dongq.demo.zk;

import java.util.Iterator;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.event.ListDataListener;

import com.google.common.collect.Lists;

public class MenuGridComposer extends GenericForwardComposer<Grid> {

	private static final long serialVersionUID = 1L;

	@Override
	public void doAfterCompose(Grid comp) throws Exception {
		super.doAfterCompose(comp);
		Object sessionId = this.getPage().getDesktop().getSession().getAttribute("sessionId");
		final String uri = "http://192.168.1.100:9527/quickride/html/menu/?m=index&page_pageSize=150&page_pageNo=1&page_orderBy=id";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		Header header = new BasicHeader("Cookie", "JSESSIONID="+sessionId.toString());
		httpGet.setHeader(header);

		HttpResponse resp = httpclient.execute(httpGet);
	    HttpEntity ent = resp.getEntity();
	    final String jsonString = EntityUtils.toString(ent);
	    System.out.println("initMenuTree="+jsonString);
	    EntityUtils.consume(ent);
		
	    List<SysMenu> menus = Lists.newArrayList();
	    JSONParser jsonParser = new JSONParser();
	    JSONObject json = (JSONObject) jsonParser.parse(jsonString);
	    int pgsz = 15;
	    JSONArray result = (JSONArray) json.get("result");
	    for (Iterator<Object> iter = result.iterator(); iter.hasNext();) {
			JSONObject object = (JSONObject) iter.next();
			SysMenu menu = new SysMenu(Long.valueOf(object.get("id").toString()), object.get("menuName").toString(), Integer.valueOf(object.get("orderNo").toString()), Integer.valueOf(object.get("menuLevelOriginal").toString()));
			if(object.containsKey("parent_menuName")) menu.setParentName(object.get("parent_menuName").toString());
			if(object.containsKey("url")) menu.setUrl(object.get("url").toString());
			menus.add(menu);
		}
	    
		ListModel<SysMenu> model = new SysMenuListModel(menus);
		comp.setModel(model);
		
		comp.setPageSize(pgsz);
	}
	
	class SysMenuListModel implements ListModel<SysMenu> {

		List<SysMenu> menus;
		
		public SysMenuListModel(List<SysMenu> menus) {
			this.menus = menus;
		}
		
		public SysMenu getElementAt(int index) {
			return menus.get(index);
		}

		public int getSize() {
			return menus.size();
		}

		public void addListDataListener(ListDataListener l) {
			
		}

		public void removeListDataListener(ListDataListener l) {
			
		}
		
	}
}
