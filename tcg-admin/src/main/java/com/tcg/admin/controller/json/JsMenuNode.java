package com.tcg.admin.controller.json;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class JsMenuNode implements Serializable {

	private static final long serialVersionUID = 8764894319363408765L;

	private String label;
	private String id;
	private List<JsMenuNode> children;
    private String url;
    private Map<String, String> i18nLabel;
    private String icon;
    private Integer menuId;
    
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public List<JsMenuNode> getChildren() {
		return children;
	}

	public void setChildren(List<JsMenuNode> children) {
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

	public Map<String, String> getI18nLabel() {
		return i18nLabel;
	}

	public void setI18nLabel(Map<String, String> i18nLabel) {
		this.i18nLabel = i18nLabel;
	}

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
    
    
}
