package com.tcg.admin.to;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TcgPageImpl<T> extends PageImpl<T> {

    public TcgPageImpl(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public TcgPageImpl(List<T> content) {
        super(content);
    }

    public TcgPageImpl() {
        super(new ArrayList<T>());
    }
}
