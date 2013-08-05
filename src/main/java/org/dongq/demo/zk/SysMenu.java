package org.dongq.demo.zk;

import java.util.List;

import com.google.common.collect.Lists;

public class SysMenu {

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

	private String parentName = "";

	/** 包含子MENU */
	private List<SysMenu> childMenus = Lists.newArrayList();

	public SysMenu() {
	}

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

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public List<SysMenu> getChildMenus() {
		return childMenus;
	}

	public void setChildMenus(List<SysMenu> childMenus) {
		this.childMenus = childMenus;
	}

}
