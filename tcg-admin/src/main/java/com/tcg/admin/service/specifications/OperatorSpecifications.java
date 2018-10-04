package com.tcg.admin.service.specifications;


import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.google.common.collect.Lists;
import com.tcg.admin.model.Operator;
import com.tcg.admin.to.QueryOperatorTO;

public class OperatorSpecifications {

	private OperatorSpecifications() {}
	
    public static Specification<Operator> queryByConditions(final QueryOperatorTO queryOperatorTO) {

        return new Specification<Operator>() {

            @Override
            public Predicate toPredicate(Root<Operator> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicatesList = Lists.newLinkedList();

                //如果查詢條件的 operatorIdList 用不到，則維持為 null, 如果傳入空的List，會用 isNull
                if (queryOperatorTO.getOperatorIdList() != null) {

                    //如果傳入的不是空的List，代表有設 operatorId in (xx,yy,zz)
                    if (!queryOperatorTO.getOperatorIdList().isEmpty()) {
                        Expression<Integer> exp = root.get("operatorId");
                        Predicate pred = exp.in(queryOperatorTO.getOperatorIdList());
                        predicatesList.add(pred);

                        //如果傳入空的List，會用 代表 operatorId 是 null
                    } else {
                        Expression<Integer> exp = root.get("operatorId");
                        Predicate pred = exp.isNull();
                        predicatesList.add(pred);
                    }

                }

                if (queryOperatorTO.getActiveFlag() != null) {
                    Path<Integer> namePath = root.get("activeFlag");
                    Predicate pred = cb.equal(namePath, queryOperatorTO.getActiveFlag());
                    predicatesList.add(pred);
                }

                query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
                return query.getGroupRestriction();
            }
        };

    }

}
