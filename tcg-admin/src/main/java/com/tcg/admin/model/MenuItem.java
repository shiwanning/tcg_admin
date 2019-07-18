package com.tcg.admin.model;

import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * <p>Title: com.tcg.admin.model.MenuItem</p>
 * <p>Description: 選單資料表</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * @version 1.0
 */
@Cacheable(value = true)
@Entity
@Table(name = "US_MENU_ITEM")
@SequenceGenerator(name = "SEQ_MENU_ITEM", sequenceName = "SEQ_MENU_ITEM", allocationSize = 1, initialValue = 1)
public class MenuItem extends BaseEntity {

	/**
	 * <code>serialVersionUID</code> 的註解
	 */
	private static final long serialVersionUID = 2160580158346840070L;

	/** 選單ID */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_MENU_ITEM")
	@Column(name = "MENU_ID")
	@NotNull
	private Integer menuId;

	/** 附註 */
	@Column(name = "DESCRIPTION")
	@NotNull
	private String description;

	/** 選單名稱 */
	@Column(name = "MENU_NAME", unique=true, nullable = false, length = 22)
	@NotNull
	private String menuName;

	/** 上層目錄ID */
	@Column(name = "PARENT_ID")
	@NotNull
	private Integer parentId;
	
	/** 是否為根結點選單(0:否   1:是) */
	@Column(name = "IS_LEAF")
	@NotNull
	private Integer isLeaf;
	
	/** 是否要顯示此選單(0:否   1:是) */
	@Column(name = "IS_DISPLAY")
	@NotNull
	private Integer isDisplay;

	/** URL */
	@Column(name = "URL")
	@NotNull
	private String url;

	/** 顯示順序 */
	@Column(name = "DISPLAY_ORDER")
	@NotNull
	private Integer displayOrder;
	
	/** 選單中的階層 */
	@Column(name = "TREE_LEVEL")
	private Integer treeLevel;

	
	/** 是否為按鈕權限項目(0:否   1:是) */
	@Column(name = "IS_BUTTON")
	@NotNull
	private Integer isButton;
	
//	@OneToOne
//	@JoinColumn(name="MENU_ID")
//	private MenuItemAux auxData;
	
	@OneToMany(fetch=FetchType.EAGER)
	@MapKeyColumn(name="LANG_CODE")
	@JoinColumn(name="API_ID")
	private Map<String, ApiLabel> labels;

    @Column(name = "ICON")
    private String icon;

	@Column(name = "ACCESS_TYPE")
	private String accessType;


	/** 選單中的階層 */
	@Column(name = "MENU_TYPE")
	private Integer menuType;

    @OneToMany(fetch= FetchType.EAGER)
    @JoinColumn(name="MENU_ID", referencedColumnName="MENU_ID", insertable = false, updatable = false)
    private List<MenuCategoryMenu> menuCategoryMenu;

    public List<MenuCategoryMenu> getMenuCategoryMenu() {
        return menuCategoryMenu;
    }

    public void setMenuCategoryMenu(List<MenuCategoryMenu> menuCategoryMenu) {
        this.menuCategoryMenu = menuCategoryMenu;
    }

    /**
	 * 取得 meneId
	 * 
	 * @return 傳回 meneId。
	 */
	public Integer getMenuId() {
		return menuId;
	}

	/**
	 * 設定 meneId
	 * 
	 * @param menuId 要設定的 meneId。
	 */
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	/**
	 * 取得 description
	 * 
	 * @return 傳回 description。
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * 設定 description
	 * 
	 * @param description 要設定的 description。
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * 取得 menuName
	 * 
	 * @return 傳回 menuName。
	 */
	public String getMenuName() {
		return menuName;
	}

	/**
	 * 設定 menuName
	 * 
	 * @param menuName 要設定的 menuName。
	 */
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	/**
	 * 取得 parentId
	 * 
	 * @return 傳回 parentId。
	 */
	public Integer getParentId() {
		return parentId;
	}

	/**
	 * 設定 parentId
	 * 
	 * @param parentId 要設定的 parentId。
	 */
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	/**
	 * 取得 isLeaf
	 * 
	 * @return 傳回 isLeaf。
	 */
	public Integer getIsLeaf() {
		return isLeaf;
	}

	/**
	 * 設定 isLeaf
	 * 
	 * @param isLeaf 要設定的 isLeaf。
	 */
	public void setIsLeaf(Integer isLeaf) {
		this.isLeaf = isLeaf;
	}

	/**
	 * 取得 isDisplay
	 * 
	 * @return 傳回 isDisplay。
	 */
	public Integer getIsDisplay() {
		return isDisplay;
	}

	/**
	 * 設定 isDisplay
	 * 
	 * @param isDisplay 要設定的 isDisplay。
	 */
	public void setIsDisplay(Integer isDisplay) {
		this.isDisplay = isDisplay;
	}

	/**
	 * 取得 url
	 * 
	 * @return 傳回 url。
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 設定 url
	 * 
	 * @param url 要設定的 url。
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * 取得 displayOrder
	 * 
	 * @return 傳回 displayOrder。
	 */
	public Integer getDisplayOrder() {
		return displayOrder;
	}

	/**
	 * 設定 displayOrder
	 * 
	 * @param displayOrder 要設定的 displayOrder。
	 */
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

	public Map<String, ApiLabel> getLabels() {
		return labels;
	}

	public void setLabels(Map<String, ApiLabel> labels) {
		this.labels = labels;
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

	public Integer getMenuType() {	return menuType; }

	public void setMenuType(Integer menuType) {	this.menuType = menuType; }

	public MenuItem copy() {
		MenuItem cloneInstance = new MenuItem();
		cloneInstance.setCreateTime(super.getCreateTime());
		cloneInstance.setAccessType(accessType);
		cloneInstance.setDescription(description);
		cloneInstance.setDisplayOrder(displayOrder);
		cloneInstance.setIcon(icon);
		cloneInstance.setIsButton(isButton);
		cloneInstance.setIsDisplay(isDisplay);
		cloneInstance.setIsLeaf(isLeaf);
		cloneInstance.setLabels(Maps.newHashMap(labels));
		cloneInstance.setMenuCategoryMenu(Lists.newLinkedList(menuCategoryMenu));
		cloneInstance.setMenuId(menuId);
		cloneInstance.setMenuName(menuName);
		cloneInstance.setMenuType(menuType);
		cloneInstance.setParentId(parentId);
		cloneInstance.setTreeLevel(treeLevel);
		cloneInstance.setUpdateTime(super.getUpdateTime());
		cloneInstance.setUrl(url);
		return cloneInstance;
	}
	
}
