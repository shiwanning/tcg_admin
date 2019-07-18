package com.tcg.admin.to;

import java.util.List;

public class TemplateCacheTo {

    private Long rid;
    private Integer type;
    private String name;
    private List<String> detailStringValues;

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDetailStringValues() {
        return detailStringValues;
    }

    public void setDetailStringValues(List<String> detailStringValues) {
        this.detailStringValues = detailStringValues;
    }
}
