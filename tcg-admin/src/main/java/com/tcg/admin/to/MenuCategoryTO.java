package com.tcg.admin.to;

import java.util.List;

/**
 * Created by chris.h on 3/6/2018.
 */
public class MenuCategoryTO extends PageableTO{

    private Integer menuCategoryId;
    private String categoryName;
    private List<Integer> menuIdList;

    public Integer getMenuCategoryId() {
        return menuCategoryId;
    }

    public void setMenuCategoryId(Integer menuCategoryId) {
        this.menuCategoryId = menuCategoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public List<Integer> getMenuIdList() {
        return menuIdList;
    }

    public void setMenuIdList(List<Integer> menuIdList) {
        this.menuIdList = menuIdList;
    }
}
