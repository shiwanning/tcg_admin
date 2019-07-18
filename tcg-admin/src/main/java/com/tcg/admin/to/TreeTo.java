package com.tcg.admin.to;

import java.util.List;

import com.google.common.collect.Lists;
import com.tcg.admin.model.MenuCategoryMenu;

public class TreeTo {

	private Integer menuId;
	private String menuName;
	private Integer parentId;
	private Integer isLeaf;
	private Integer isDisplay;
	private Boolean isInGroup = false;
	private String url;
	private Integer displayOrder;
	private Integer treeLevel;
	private Integer isButton;
    private String icon;
	private String accessType;
	private Integer menuType;
	private List<Integer> parents = Lists.newLinkedList();
	
	private List<MenuCategoryMenu> menuCategoryMenu;
	
	private List<TreeTo> list;

    public List<MenuCategoryMenu> getMenuCategoryMenu() {
        return menuCategoryMenu;
    }
    
    public void setMenuCategoryMenu(List<MenuCategoryMenu> menuCategoryMenu) {
        this.menuCategoryMenu = menuCategoryMenu;
    }

    public java.util.List<TreeTo> getList() {
        return list;
    }

    public void setList(java.util.List<TreeTo> list) {
        this.list = list;
    }

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Integer getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(Integer isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
	}

	public Integer getTreeLevel() {
		return treeLevel;
	}

	public void setTreeLevel(Integer treeLevel) {
		this.treeLevel = treeLevel;
	}

	public Integer getIsButton() {
		return isButton;
	}

	public void setIsButton(Integer isButton) {
		this.isButton = isButton;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getAccessType() {
		return accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public Integer getMenuType() {
		return menuType;
	}

	public void setMenuType(Integer menuType) {
		this.menuType = menuType;
	}

	public List<Integer> getParents() {
		return parents;
	}

	public void setParents(List<Integer> parents) {
		this.parents = parents;
	}

	public Boolean getIsInGroup() {
		return isInGroup;
	}

	public void setIsInGroup(Boolean isInGroup) {
		this.isInGroup = isInGroup;
	}
	
}
