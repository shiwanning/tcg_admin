package com.tcg.admin.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.model.QCategoryRole;
import com.tcg.admin.model.QRole;
import com.tcg.admin.model.Role;

@Repository
public class RoleCustomRepository {
	
	private static final Integer ACTIVE = 1;
	
	@PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;
	
	public List<Role> getRolesByCategoryId(List<Integer> categoryIds){
		EntityManager em = entityManager;
        JPAQuery query = new JPAQuery(em);
        QRole role = QRole.role;
        QCategoryRole categoryRole = QCategoryRole.categoryRole;
        
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        
        booleanBuilder.and(role.activeFlag.eq(ACTIVE));
        booleanBuilder.and(new JPASubQuery()
			    .from(categoryRole)
			    .where(role.roleId.eq(categoryRole.roleId).and(categoryRole.categoryId.in(categoryIds))).exists());
        
        return query.from(role)
                .where(booleanBuilder).list(role);
	}

}
