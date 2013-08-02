package org.dongq.demo.zk;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;
import org.zkoss.zul.Window.Mode;

import com.google.common.collect.Lists;

public class IndexComposer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 1L;

	@Wire("#menu")
	Tree menuTree;

	@Wire("#centerTabbox")
	Tabbox tabbox;

	@Wire("#centerTabs")
	Tabs tabs;
	
	@Wire("#centerTabpanels")
	Tabpanels tabpanels;
	
	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);

		Object realName = this.getPage().getDesktop().getSession().getAttribute("realName");
		System.out.println("index realname= "+realName);
		if(realName == null) {
			Window login = (Window) Executions.createComponents("login.zul", comp, null);
			login.setMode(Mode.MODAL);
		} else {
			initWindow();
		}
		
	}

	void initWindow() {
		SysMenu root = initMenuTree();
		MenuTreeItemRender renderer = new MenuTreeItemRender();
		MenuTreeModel model = new MenuTreeModel(root);
		menuTree.setModel(model);
		menuTree.setItemRenderer(renderer);
		
		menuTree.addEventListener("onSelect", new EventListener<SelectEvent<Tree, SysMenu>>(){
			
			public void onEvent(SelectEvent<Tree, SysMenu> event) throws Exception {
				SysMenu menu = event.getSelectedObjects().iterator().next();
				System.out.println("onSelect=" + event.getName() + ", " + menu.getMenuName());
				if(StringUtils.isNotBlank(menu.getUrl())) {
					String compId = "tab-"+menu.getId();
					boolean hasFellow = tabs.hasFellow(compId);
					if(hasFellow) {
						Tab tab = (Tab) tabs.getFellow(compId);
						tab.setSelected(true);
					} else {
						Tab tab = new Tab(menu.getMenuName());
						tab.setId(compId);
						tab.setClosable(true);
						tab.setSelected(true);
						tabs.appendChild(tab);
						
						Include include = new Include("menu.zul");
						Tabpanel tabpanel = new Tabpanel();
						tabpanel.appendChild(include);
						tabpanels.appendChild(tabpanel);
					}
				}
			}
			
		});
		
	}
	
	SysMenu initMenuTree() {
		JSONParser jsonParser = new JSONParser();
		SysMenu root = new SysMenu();
		root.setMenuLevel(-1);
		
		Object sessionId = this.getPage().getDesktop().getSession().getAttribute("sessionId");
		final String uri = "http://192.168.1.100:9527/quickride/html/menu/?m=indexShow";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		Header header = new BasicHeader("Cookie", "JSESSIONID="+sessionId.toString());
		httpGet.setHeader(header);
		System.out.println("sessionId="+sessionId);
		try {
			HttpResponse response1 = httpclient.execute(httpGet);
		    System.out.println(response1.getStatusLine());
		    HttpEntity entity1 = response1.getEntity();
		    final String jsonString = EntityUtils.toString(entity1);
		    System.out.println("initMenuTree="+jsonString);
		    EntityUtils.consume(entity1);
		    
		    JSONObject json = (JSONObject) jsonParser.parse(jsonString);
		    JSONArray result = (JSONArray) json.get("result");
 			System.out.println("json: "+result.getClass());
 			for (Iterator<Object> iter = result.iterator(); iter.hasNext();) {
				JSONObject menuJson = (JSONObject) iter.next();
				long id = Long.valueOf(menuJson.get("id").toString());
				String menuName = String.valueOf(menuJson.get("menuName"));
				int orderNo = Integer.valueOf(menuJson.get("orderNo").toString());
				int menuLevel = Integer.valueOf(menuJson.get("menuLevelOriginal").toString());
				SysMenu menu = new SysMenu(id, menuName, orderNo, menuLevel);
				root.getChildMenus().add(menu);
				
				if(menuLevel == 1) {
					httpGet = new HttpGet(uri+"&parentId="+id);
					httpGet.setHeader(header);
					HttpResponse resp = httpclient.execute(httpGet);
					
					HttpEntity ent = resp.getEntity();
					String sub = EntityUtils.toString(ent);
					System.out.println("sub menu: "+sub);
					
					JSONObject _json = (JSONObject) jsonParser.parse(sub);
					JSONArray _result = (JSONArray) _json.get("result");
					for (Iterator<Object> _iter = _result.iterator(); _iter.hasNext();) {
						JSONObject subMenuJson = (JSONObject) _iter.next();
						long _id = Long.valueOf(subMenuJson.get("id").toString());
						String _menuName = String.valueOf(subMenuJson.get("menuName"));
						int _orderNo = Integer.valueOf(subMenuJson.get("orderNo").toString());
						int _menuLevel = Integer.valueOf(subMenuJson.get("menuLevelOriginal").toString());
						String _url = String.valueOf(subMenuJson.get("url"));
						SysMenu subMenu = new SysMenu(_id, _menuName, _orderNo, _menuLevel);
						subMenu.setUrl(_url);
						menu.setParent(menu);
						menu.getChildMenus().add(subMenu);
					}
					
					EntityUtils.consume(ent);
				}
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		    httpGet.releaseConnection();
		}
		
		return root;
	}
	
	SysMenu getMenuTree() {
		SysMenu root = new SysMenu();
		root.setMenuLevel(-1);

		List<SysMenu> a = Lists.newArrayList();
		a.add(new SysMenu("用户管理", 2));
		a.add(new SysMenu("角色管理", 2));
		a.add(new SysMenu("权限管理", 2));
		a.add(new SysMenu("菜单管理", 2));
		a.add(new SysMenu("链接管理", 2));
		
		List<SysMenu> b = Lists.newArrayList();
		b.add(new SysMenu("车辆管理", 2));
		b.add(new SysMenu("司机管理", 2));
		b.add(new SysMenu("应用管理", 2));
		
		List<SysMenu> c = Lists.newArrayList();
		c.add(new SysMenu("全局参数设置", 2));
		c.add(new SysMenu("平台日志管理", 2));
		c.add(new SysMenu("调度进程管理", 2));
		
		List<SysMenu> childMenus = Lists.newArrayList();
		childMenus.add(new SysMenu("安全管理", a));
		childMenus.add(new SysMenu("业务管理", b));
		childMenus.add(new SysMenu("平台管理", c));
		root.setChildMenus(childMenus);
		
		return root;
	}

	class MenuTreeModel extends AbstractTreeModel<SysMenu> {
		private static final long serialVersionUID = 1L;

		public MenuTreeModel(SysMenu root) {
			super(root);
		}

		public boolean isLeaf(SysMenu node) {
			return node.getMenuLevel() == 2;
		}

		public SysMenu getChild(SysMenu parent, int index) {
			return parent.getChildMenus().get(index);
		}

		public int getChildCount(SysMenu parent) {
			return parent.getChildMenus().size();
		}

	}

	class MenuTreeItemRender implements TreeitemRenderer<SysMenu> {

		public void render(Treeitem item, SysMenu data, int index) throws Exception {
			item.setLabel(data.getMenuName());
			item.setValue(data);
		}
		
	}
	
	class SysMenu {

		/** 主键 */
		private Long id;

		/** 名称 */
		private String menuName;

		/** url */
		private String url;

		/** 显示顺序 */
		private Integer orderNo;

		/** 菜单级别 */
		private Integer menuLevel = 1;

		/** 父MENU */
		private SysMenu parent;

		/** 包含子MENU */
		private List<SysMenu> childMenus = Lists.newArrayList();

		public SysMenu() {}

		public SysMenu(Long id, String menuName, Integer orderNo, Integer menuLevel) {
			super();
			this.id = id;
			this.menuName = menuName;
			this.orderNo = orderNo;
			this.menuLevel = menuLevel;
		}

		public SysMenu(String menuName, Integer menuLevel) {
			super();
			this.menuName = menuName;
			this.menuLevel = menuLevel;
		}
		
		public SysMenu(String menuName, List<SysMenu> childMenus) {
			super();
			this.menuName = menuName;
			this.childMenus = childMenus;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getMenuName() {
			return menuName;
		}

		public void setMenuName(String menuName) {
			this.menuName = menuName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Integer getOrderNo() {
			return orderNo;
		}

		public void setOrderNo(Integer orderNo) {
			this.orderNo = orderNo;
		}

		public Integer getMenuLevel() {
			return menuLevel;
		}

		public void setMenuLevel(Integer menuLevel) {
			this.menuLevel = menuLevel;
		}

		public SysMenu getParent() {
			return parent;
		}

		public void setParent(SysMenu parent) {
			this.parent = parent;
		}

		public List<SysMenu> getChildMenus() {
			return childMenus;
		}

		public void setChildMenus(List<SysMenu> childMenus) {
			this.childMenus = childMenus;
		}

	}
}
