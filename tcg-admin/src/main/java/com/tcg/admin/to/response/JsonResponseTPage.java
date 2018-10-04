package com.tcg.admin.to.response;

public class JsonResponseTPage<T> extends JsonResponseT<T> {

    /**
     * 目前頁數
     */
    private int page;

    /**
     * 資料總筆數
     */
    private long total;


    public JsonResponseTPage() {
        super(false);
    }


    public JsonResponseTPage(Boolean isSuccess) {
        super(isSuccess);
    }
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

}
