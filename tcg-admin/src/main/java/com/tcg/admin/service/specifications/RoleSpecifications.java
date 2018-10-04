package com.tcg.admin.service.specifications;


import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.springframework.data.jpa.domain.Specification;

import com.google.common.collect.Lists;
import com.tcg.admin.model.CategoryRole;
import com.tcg.admin.model.Role;
import com.tcg.admin.to.QueryRoleTO;

public class RoleSpecifications {
 
	private RoleSpecifications() {}
	
    public static Specification<Role> queryByConditions(final QueryRoleTO queryRoleTO) {

        return new Specification<Role>() {
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicatesList = Lists.newLinkedList();
                
                //如果查詢條件的 roleIdList 用不到，則維持為 null, 如果傳入空的List，會用 isNull
                if (queryRoleTO.getRoleIdList() != null) {

                    //如果傳入的不是空的List，代表有設 roleId in (xx,yy,zz)
                    if (!queryRoleTO.getRoleIdList().isEmpty()) {
                        Expression<Integer> exp = root.get("roleId");
                        Predicate pred = exp.in(queryRoleTO.getRoleIdList());
                        predicatesList.add(pred);

                        //如果傳入空的List，會用 代表 roleId 是 null
                    } else {
                        Expression<Integer> exp = root.get("roleId");
                        Predicate pred = exp.isNull();
                        predicatesList.add(pred);
                    }

                }

                if (queryRoleTO.getActiveFlag() != null) {
                    Path<Integer> namePath = root.get("activeFlag");
                    Predicate pred = cb.equal(namePath, queryRoleTO.getActiveFlag());
                    predicatesList.add(pred);
                }
                
                if (queryRoleTO.getRoleName() != null) {
                    Path<String> namePath = root.get("roleName");
                    Predicate pred = cb.like(cb.upper(namePath),"%"+queryRoleTO.getRoleName().toUpperCase()+"%");
                    predicatesList.add(pred);
                }
                
                if (queryRoleTO.getDescription() != null) {
                    Path<String> namePath = root.get("description");
                    Predicate pred = cb.like(cb.upper(namePath),"%"+queryRoleTO.getDescription().toUpperCase()+"%");
                    predicatesList.add(pred);
                }
                
                if (queryRoleTO.getCategoryRoleId() != null) {      
                    Subquery<CategoryRole> categorySubQuery = query.subquery(CategoryRole.class);
                    Root<CategoryRole> subQueryRoot = categorySubQuery.from(CategoryRole.class);
                    categorySubQuery.select(subQueryRoot);
                    
                    Predicate predRole = cb.equal(subQueryRoot.get("roleId"), root.get("roleId"));
                    Predicate predCat = cb.equal(subQueryRoot.get("categoryId"), queryRoleTO.getCategoryRoleId());
                	
                    categorySubQuery.where(
                            cb.and(predRole, predCat)
                    );
                    
                    predicatesList.add(cb.exists(categorySubQuery));
                }

                query.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
                return query.getGroupRestriction();
            }

        };

    }

}
