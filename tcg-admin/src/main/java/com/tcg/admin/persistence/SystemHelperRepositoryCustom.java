package com.tcg.admin.persistence;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.tcg.admin.common.constants.SystemHelperConstant;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.QApiLabel;
import com.tcg.admin.model.QMenuItem;
import com.tcg.admin.model.QSystemHelper;
import com.tcg.admin.model.SystemHelper;
import com.tcg.admin.utils.QuerydslPageUtil;

@Repository
public class SystemHelperRepositoryCustom {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    private static final Integer ALL = -1;

    public Page<SystemHelper> find(Integer menuId ,List<Integer> menuIdList, Integer status, Pageable pageable) {
        EntityManager em = entityManager;
        JPAQuery query = new JPAQuery(em);
        QSystemHelper systemHelper = QSystemHelper.systemHelper;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        if(CollectionUtils.isNotEmpty(menuIdList)){
            booleanBuilder.and(systemHelper.menuId.in(menuIdList));
        }

        if(status != ALL && status != null) {
            booleanBuilder.and(systemHelper.status.eq(status));
        }
        if(menuId != null) {
            booleanBuilder.and(systemHelper.menuId.eq(menuId));
        }

        booleanBuilder.and(systemHelper.menuItem.isNotNull());

        query.from(systemHelper)
                .where(booleanBuilder)
                .orderBy(systemHelper.menuId.asc());

        return QuerydslPageUtil.pagination(query, systemHelper, pageable);
    }
    
    public List<SystemHelper> search(String queryParam ,String lang) {
        EntityManager em = entityManager;
        JPAQuery query = new JPAQuery(em);
        QSystemHelper systemHelper = QSystemHelper.systemHelper;
        QApiLabel apiLabel = QApiLabel.apiLabel;
        
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        
        if("en_US".equalsIgnoreCase(lang)){
            booleanBuilder.and(systemHelper.enContent.toLowerCase().contains(queryParam.toLowerCase()));
        }else if("zh_CN".equalsIgnoreCase(lang)){
        	booleanBuilder.and(systemHelper.cnContent.toLowerCase().contains(queryParam.toLowerCase()));
        }

        booleanBuilder.or(new JPASubQuery()
			    .from(apiLabel)
			    .where(apiLabel.id.eq(systemHelper.menuId).and(apiLabel.languageCode.eq(lang).and(apiLabel.label.toLowerCase().contains(queryParam.toLowerCase())))).exists());

        booleanBuilder.and(systemHelper.menuItem.isNotNull());

        booleanBuilder.and(systemHelper.state.eq(SystemHelperConstant.HELPER_APPROVED).and(systemHelper.status.eq(SystemHelperConstant.ACTIVE)));
        
        return query.from(systemHelper)
                .where(booleanBuilder).orderBy(systemHelper.menuId.asc()).orderBy(systemHelper.menuItem.treeLevel.asc()).list(systemHelper);
    }
    
    public List<MenuItem> findInMenuItem(String queryParam ,String lang){
    	EntityManager em = entityManager;
        JPAQuery query = new JPAQuery(em);
        QMenuItem menuItemModel = QMenuItem.menuItem;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        
        if("en_US".equalsIgnoreCase(lang)){
            booleanBuilder.and(menuItemModel.menuName.toLowerCase().contains(queryParam.toLowerCase())
            		.or(menuItemModel.description.toLowerCase().contains(queryParam.toLowerCase())
            		.or(menuItemModel.labels.get("en_US").label.toLowerCase().contains(queryParam.toLowerCase()))));
        }else if("zh_CN".equalsIgnoreCase(lang)){
        	booleanBuilder.and(menuItemModel.menuName.toLowerCase().contains(queryParam.toLowerCase())
            		.or(menuItemModel.description.toLowerCase().contains(queryParam.toLowerCase())
            		.or(menuItemModel.labels.get("zh_CN").label.toLowerCase().contains(queryParam.toLowerCase()))));
        }
        
        return query.from(menuItemModel)
                .where(booleanBuilder).orderBy(menuItemModel.menuId.asc()).list(menuItemModel);
    }

    public List<SystemHelper> searchByMenuIds(Collection<Integer> menuIds) {
        EntityManager em = entityManager;
        JPAQuery query = new JPAQuery(em);
        QSystemHelper systemHelper = QSystemHelper.systemHelper;
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(systemHelper.menuId.in(menuIds));
        booleanBuilder.and(systemHelper.state.eq(SystemHelperConstant.HELPER_APPROVED).and(systemHelper.status.eq(SystemHelperConstant.ACTIVE)));
        return query.from(systemHelper)
                .where(booleanBuilder).orderBy(systemHelper.menuId.asc()).orderBy(systemHelper.menuItem.treeLevel.asc()).list(systemHelper);
    }
}
