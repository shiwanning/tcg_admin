package com.tcg.admin.to;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.tcg.admin.common.constants.SystemConstant;
import com.tcg.admin.model.ApiLabel;


public class Permission {

	private List<Permission> children;
	private String state;
	private Integer menuId;
	private String description;
	private String menuName;
	private Integer parentId;
	private Integer isLeaf;
	private Integer isDisplay;
	private String url;
	private Integer displayOrder;
	private Integer treeLevel;
	private Integer isButton;
	private Integer bpm;
	
	private Map<String, ApiLabel> labels;
	private Integer type;
	private Integer family;

    /**
     * 生成treeGrid
     *
     * @param list    父节点拥有的子节点
     * @param listall 全部的list
     * @param count   父节点id
     *
     * @return
     */
    public static List<Permission> permisionTreeGrid(List<Permission> list, List<Permission> listall, int count) {
        List<Permission> treelist = Lists.newLinkedList();
        int nodeid = 0;
        for (Permission resources : list) {
        	if (resources.getParentId().equals(count)) {
        		nodeid = processTreeList(treelist, resources, listall, nodeid);
        	}
        }
        return treelist;
    }

    private static int processTreeList(List<Permission> treelist, Permission resources, List<Permission> listall, int nodeid) {
    	List<Permission> list1 = Lists.newLinkedList();//上级节点的子节点
    	int parentNodeId = nodeid;
        for (Permission res : listall) {
            if (res.getParentId().equals(resources.getMenuId())) {
            	parentNodeId = res.getParentId();
                list1.add(res);
            }
        }
        Permission r = new Permission();
        r.setState("open");
        if (resources.getTreeLevel().equals(SystemConstant.TREELEVEL) && !list1.isEmpty()) {
            r.setState("closed");
        }
        r.setDescription(resources.getDescription());
        r.setIsLeaf(resources.getIsLeaf());
        r.setDisplayOrder(resources.getDisplayOrder());
        r.setParentId(resources.getParentId());
        r.setMenuName(resources.getMenuName());
        r.setTreeLevel(resources.getTreeLevel());
        r.setUrl(resources.getUrl());
        r.setIsButton(resources.getIsButton());
        r.setMenuId(resources.getMenuId());
        r.setChildren(permisionTreeGrid(list1, listall, parentNodeId));
        r.setIsDisplay(resources.getIsDisplay());
        r.setBpm(resources.getBpm());
        r.setType(resources.getType());
        r.setLabels(resources.getLabels());
        treelist.add(r);
        return nodeid;
	}

	public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
	}

	public List<Permission> getChildren() {
		return children;
	}

	public void setChildren(List<Permission> list) {
		this.children = list;
	}

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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


	public Integer getBpm() {
		return bpm;
	}


	public void setBpm(Integer bpm) {
		this.bpm = bpm;
	}

	public Map<String, ApiLabel> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, ApiLabel> labels) {
		this.labels = labels;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getFamily() {
		return family;
	}

	public void setFamily(Integer family) {
		this.family = family;
	}
}
