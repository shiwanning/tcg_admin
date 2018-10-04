package com.tcg.admin.to;

import org.springframework.data.domain.Pageable;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * Created by jerry.b on 2016/12/28.
 */
@ApiModel
public class PageActivityLogRes extends TcgPageImpl<ActivityLog> {
    public PageActivityLogRes(List<ActivityLog> datalist, Pageable pageable, long totalElements) {
        super(datalist, pageable, totalElements);
    }

    public PageActivityLogRes(List<ActivityLog> list) {
        super(list);
    }
}
