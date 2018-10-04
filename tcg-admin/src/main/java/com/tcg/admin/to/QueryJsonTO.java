package com.tcg.admin.to;

import java.io.Serializable;
import java.util.List;

public class QueryJsonTO implements Serializable {

    private static final long serialVersionUID = 6865210209348443398L;

    private List<QueryOperatorsTO> queryList;

    private Integer count;

    private Integer totalCount;

    private Integer pageNo;

    private Integer pageSize;

    public List<QueryOperatorsTO> getQueryList() {
        return queryList;
    }

    public void setQueryList(List<QueryOperatorsTO> queryList) {
        this.queryList = queryList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
}
