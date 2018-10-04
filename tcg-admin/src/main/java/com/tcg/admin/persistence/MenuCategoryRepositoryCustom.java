package com.tcg.admin.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.model.MenuCategory;
import com.tcg.admin.model.QMenuCategory;
import com.tcg.admin.to.MenuCategoryTO;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class MenuCategoryRepositoryCustom extends BaseDAORepository {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

	public Page<MenuCategory> queryMenuCategory(MenuCategoryTO to, Pageable pageable){
		EntityManager em = entityManager;
		JPAQuery query = new JPAQuery(em);
		QMenuCategory model = QMenuCategory.menuCategory;

		String categoryName = to.getCategoryName();
		BooleanBuilder booleanBuilder = new BooleanBuilder();

		if(StringUtils.isNotEmpty(categoryName)) {
			booleanBuilder.and(model.categoryName.like("%"+categoryName.toUpperCase()+"%"));
		}

		query.from(model)
				.where(booleanBuilder)
				.orderBy(model.createTime.asc());

		return QuerydslPageUtil.pagination(query, model, pageable);
	}

}
