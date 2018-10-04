package com.tcg.admin.to;

import java.io.Serializable;
import java.util.List;

public class QueryOperatorTO implements Serializable {

    private static final long serialVersionUID = -8026121082559409988L;


    private List<Integer> operatorIdList;

    private Integer activeFlag;

    private int pagenumber = 0;

    private int pageSize = 0;

    public List<Integer> getOperatorIdList() {
        return operatorIdList;
    }

    public void setOperatorIdList(List<Integer> operatorIdList) {
        this.operatorIdList = operatorIdList;
    }

    public Integer getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Integer activeFlag) {
        this.activeFlag = activeFlag;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
