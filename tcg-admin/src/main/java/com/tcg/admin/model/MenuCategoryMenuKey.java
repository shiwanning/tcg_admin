package com.tcg.admin.model;

import java.io.Serializable;


/**
 * Created by James on 2016/11/11.
 */
public class MenuCategoryMenuKey implements Serializable {

    private String menuCategoryName;
    private Integer menuId;

    public String getMenuCategoryName() {
        return menuCategoryName;
    }

    public void setMenuCategoryName(String menuCategoryName) {
        this.menuCategoryName = menuCategoryName;
    }

    public Integer getMenuId() {
        return menuId;
    }

    public void setMenuId(Integer menuId) {
        this.menuId = menuId;
    }

    public boolean equals(Object obj) {
        if(obj instanceof MenuCategoryMenuKey){
            MenuCategoryMenuKey menuCategoryMenuKey2 = (MenuCategoryMenuKey) obj;

            if(!menuCategoryMenuKey2.getMenuCategoryName().equals(menuCategoryName)){
                return false;
            }

            if(!menuCategoryMenuKey2.getMenuId().equals(menuId)){
                return false;
            }

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return menuCategoryName.hashCode() + menuId.hashCode();
    }

}
