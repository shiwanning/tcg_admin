package com.tcg.admin.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.tcg.admin.to.Permission;


public class TreeTool {

	private int id;//节点的值
	private String text;//节点显示文本
	private String state = "open";// open,closed，节点状态
	private boolean checked = false;
	private Object attributes;//自定义属性
	private List<TreeTool> children = Lists.newLinkedList();//子节点

	public static List<TreeTool> permissionTree(List<Permission> list, List<Permission> listall, int count,int state){
		List<TreeTool> treelist = Lists.newLinkedList();
		int nodeid=0;
        for (Permission permission : list) {
            List<Permission> list1 = Lists.newLinkedList();//上级节点的子节点
            if(permission.getParentId().equals(count)){
				for(Permission node1 : listall){
					if(node1.getParentId().equals(permission.getMenuId())){
						nodeid= node1.getParentId();
						list1.add(node1);
					}
				}
				TreeTool tree = new TreeTool();
				tree.setId(permission.getMenuId());
				tree.setText(permission.getMenuName());
				tree.setChildren(permissionTree(list1,listall,nodeid,state));
				Map<String, String> attributes = new HashMap<String, String>();
				if(state==1){
					attributes.put("treeLevel", permission.getTreeLevel().toString());
				}else{
					attributes.put("url", permission.getUrl());
				}
				tree.setAttributes(attributes);
				treelist.add(tree);
            }
        }
        return treelist;
	}

    //private String iconCls;//图标
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Object getAttributes() {
        return attributes;
    }

    public void setAttributes(Object attributes) {
        this.attributes = attributes;
    }

    public List<TreeTool> getChildren() {
        return children;
    }

    public void setChildren(List<TreeTool> children) {
        this.children = children;
    }
}
