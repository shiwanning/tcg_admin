package com.tcg.admin.controller.json;

import java.io.Serializable;
import java.util.List;

public class JsTreeMenu implements Serializable {

	private static final long serialVersionUID = -8043863446575627766L;

	private List<JsMenuNode> treeMenu;

	public List<JsMenuNode> getTreeMenu() {
		return treeMenu;
	}

	public void setTreeMenu(List<JsMenuNode> treeMenu) {
		this.treeMenu = treeMenu;
	}
}
