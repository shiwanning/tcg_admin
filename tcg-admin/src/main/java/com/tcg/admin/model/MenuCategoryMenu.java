package com.tcg.admin.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 */
@Entity
@Table(name = "US_MENU_CATEGORY_MENU")
@IdClass(value=MenuCategoryMenuKey.class)
public class MenuCategoryMenu extends BaseEntity {

    private static final long serialVersionUID = -1022703500142624282L;

//    @EmbeddedId
//    private MenuCategoryMenuKey key;
//
//    public MenuCategoryMenuKey getKey() {
//        return key;
//    }
    @Id
    @Column(name = "MENU_CATEGORY_NAME")
    private String menuCategoryName;

    @Id
    @Column(name = "MENU_ID")
    private Integer menuId;

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public String getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }


//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "MENU_ID", insertable = false, updatable = false)
//    private MenuItem menuItem;

//    public MenuItem getMenuItem() {
//        return menuItem;
//    }
//
//    public void setMenuItem(MenuItem menuItem) {
//        this.menuItem = menuItem;
//    }

//    public void setKey(MenuCategoryMenuKey key) {
//        this.key = key;
//    }
//    @Column(name = "MENU_CATEGORY_ID")
//    private Integer menuCategoryId;
//
//    @Column(name = "MENU_ID")
//    private Integer menuId;
//
//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "MENU_ID", insertable = false, updatable = false)
//    private MenuItem menuItem;


//    public Integer getMenuCategoryId() {
//        return menuCategoryId;
//    }
//
//    public void setMenuCategoryId(Integer menuCategoryId) {
//        this.menuCategoryId = menuCategoryId;
//    }
//
//    public MenuItem getMenuItem() {
//        return menuItem;
//    }
//
//    public void setMenuItem(MenuItem menuItem) {
//        this.menuItem = menuItem;
//    }
//
//    public Integer getMenuId() {
//        return menuId;
//    }
//
//    public void setMenuId(Integer menuId) {
//        this.menuId = menuId;
//    }
}
