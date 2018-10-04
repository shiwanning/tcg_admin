package com.tcg.admin.to;

/**
 * Created by chris.h on 2017/10/5.
 */
public class PageableTO {

    private Integer pageNo;

    private Integer pageSize;

    private String sortBy;

    private String sortOrderBy;

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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortOrderBy() {
        return sortOrderBy;
    }

    public void setSortOrderBy(String sortOrderBy) {
        this.sortOrderBy = sortOrderBy;
    }
}
