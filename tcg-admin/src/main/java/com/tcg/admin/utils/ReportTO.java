package com.tcg.admin.utils;

import net.sf.json.JSONArray;

/**
 * Created by ian.r on 6/9/2017.
 */
public class ReportTO {

    private String title;
    private String subTitle;
    private String[] tableTile;
    private String[] attrName;
    private JSONArray array;

    public String[] getTableTile() {
        return tableTile;
    }

    public void setTableTile(String[] tableTile) {
        this.tableTile = tableTile;
    }

    public String[] getAttrName() {
        return attrName;
    }

    public void setAttrName(String[] attrName) {
        this.attrName = attrName;
    }

    public JSONArray getArray() {
        return array;
    }

    public void setArray(JSONArray array) {
        this.array = array;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
