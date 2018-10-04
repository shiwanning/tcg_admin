package com.tcg.admin.to;

import com.tcg.admin.model.MenuItem;

import java.io.Serializable;
import java.util.List;

public class TreeTo extends MenuItem implements Serializable {

    private static final long serialVersionUID = 708474815561108958L;

    private List<TreeTo> list;

    public java.util.List<TreeTo> getList() {
        return list;
    }

    public void setList(java.util.List<TreeTo> list) {
        this.list = list;
    }
}
