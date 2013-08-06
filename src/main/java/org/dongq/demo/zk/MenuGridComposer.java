package org.dongq.demo.zk;

import java.util.Iterator;
import java.util.List;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.ListDataListener;
import org.zkoss.zul.event.PagingEvent;

import com.google.common.collect.Lists;

public class MenuGridComposer extends SelectorComposer<Div> {

	private static final long serialVersionUID = 1L;

	String sessionid;
	JSONParser jsonParser = new JSONParser();
	
	@Wire("#menuPaging")
	Paging menuPaging;
	
	@Wire("menuGrid")
	Grid menuGrid;
	
	@Override
	public void doAfterCompose(Div comp) throws Exception {
		super.doAfterCompose(comp);
	    
		Object session = this.getPage().getDesktop().getSession().getAttribute("sessionId");
		sessionid = session != null ? session.toString() : "";
		
		int pageNo = 1;
		int pageSize = menuPaging.getPageSize();
		Page<SysMenu> page = getMenus(pageSize, pageNo);
		List<SysMenu> menus = page.getResult();
		int pageTotal = page.getTotalCount();

		System.out.println("paging="+menuPaging);
		menuPaging.setTotalSize(pageTotal);
		menuPaging.addEventListener("onPaging", new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				PagingEvent pge = (PagingEvent) event;
				System.out.println("onPagingEvent: activePage="+pge.getActivePage());
				List<SysMenu> menus = getMenus(menuPaging.getPageSize(), pge.getActivePage()+1).getResult();
				menuGrid.setModel(new SysMenuListModel(menus));
			}
		});
		
		ListModel<SysMenu> model = new SysMenuListModel(menus);
		menuGrid.setModel(model);
	}
	
	Page<SysMenu> getMenus(int pageSize, int pageNo) throws Exception {
		Page<SysMenu> page = new Page<SysMenu>(pageSize);
		List<SysMenu> menus = Lists.newArrayList();
		int totalCount = 0;
		
		final String uri = "http://192.168.1.100:9527/quickride/html/menu/?m=index&page_pageSize="+pageSize+"&page_pageNo="+pageNo+"&page_orderBy=id";
	    final String jsonString = HttpClientUtils.getJson(sessionid, uri, HttpClientUtils.METHOD_GET);
		
	    jsonParser = new JSONParser();
	    JSONObject json = (JSONObject) jsonParser.parse(jsonString);
	    totalCount = Integer.valueOf(json.get("totalCount").toString());
	    JSONArray result = (JSONArray) json.get("result");
	    for (Iterator<Object> iter = result.iterator(); iter.hasNext();) {
			JSONObject object = (JSONObject) iter.next();
			SysMenu menu = new SysMenu(Long.valueOf(object.get("id").toString()), object.get("menuName").toString(), Integer.valueOf(object.get("orderNo").toString()), Integer.valueOf(object.get("menuLevelOriginal").toString()));
			if(object.containsKey("parent_menuName")) menu.setParentName(object.get("parent_menuName").toString());
			if(object.containsKey("url")) menu.setUrl(object.get("url").toString());
			menus.add(menu);
		}
		page.setResult(menus);
		page.setPageNo(pageNo);
		page.setTotalCount(totalCount);
		return page;
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
