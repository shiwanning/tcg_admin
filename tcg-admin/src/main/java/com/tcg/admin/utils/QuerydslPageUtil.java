package com.tcg.admin.utils;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.Expression;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

/**
 * Description: Page util for QueryDsl. <br/>
 *
 * @author Eddie
 */
public class QuerydslPageUtil {

	private QuerydslPageUtil() {}
	
    public static <T> Page<T> pagination(JPQLQuery query, Expression projection, Pageable pageable) {

        Long total = query.count();

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<T> content = total > pageable.getOffset() ?
                query.list(projection) : Collections.emptyList();

        return new PageImpl<>(content, pageable, total);
    }

    public static <T> Page<T> pagination(JPQLQuery query, Pageable pageable, Expression... projection) {

        Long total = query.count();

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<T> content = (List<T>) (total > pageable.getOffset() ?
                query.list(projection) : Collections.emptyList());

        return new PageImpl<>(content, pageable, total);
    }
}
