package org.dongq.demo.zk;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Tabbox;
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
	Tabbox centerTabbox;

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
		initMenuTree();
		MenuTreeItemRender renderer = new MenuTreeItemRender();
		MenuTreeModel model = new MenuTreeModel(getMenuTree());
		menuTree.setModel(model);
		menuTree.setItemRenderer(renderer);
		
		menuTree.addEventListener("onSelect", new EventListener<SelectEvent<Tree, SysMenu>>(){
			
			public void onEvent(SelectEvent<Tree, SysMenu> event) throws Exception {
				System.out.println("onSelect=" + event.getName() + ", " + event.getSelectedObjects().iterator().next().getMenuName());
			}
			
		});
		
	}
	
	void initMenuTree() {
		final String uri = "http://192.168.1.100:9527/quickride/html/menu/?m=indexShow";
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(uri);
		try {
			HttpResponse response1 = httpclient.execute(httpGet);
		    System.out.println(response1.getStatusLine());
		    HttpEntity entity1 = response1.getEntity();
		    System.out.println("initMenuTree="+EntityUtils.toString(entity1));
		    // do something useful with the response body
		    // and ensure it is fully consumed
		    EntityUtils.consume(entity1);
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
		    httpGet.releaseConnection();
		}
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
