package com.tcg.admin.persistence;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;


/**
 * <p>Title: com.yx.us.persistence.MenuCategoryMenuRepository</p>
 * <p>Description: 查詢MenuCategory-Menu對應資料</p>
 *
 * @author Chris.H
 * @version 1.0
 */

@Repository
public class MenuCategoryCustomRepository extends BaseDAORepository implements Serializable {

    private static final long serialVersionUID = 5348979046106047087L;

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    /**
     *
     SELECT m.menu_Id FROM us_Menu_Item m where m.menu_Id NOT IN (
     select distinct c.menu_Id from us_menu_category_menu c where c.menu_category_name != 'SYSTEM'  and c.menu_Id not in
     (select b.menu_id from us_menu_Category_menu b where b.menu_Category_Name in (
     select a.menu_Category_Name from us_Merchant_Menu_Category a where a.merchant_Code = 'chris402'
     )));
     * @param merchantCode
     * @return
     */
    public List<Integer> menuIdsByBaseMerchant(String merchantCode){
        StringBuilder jpql=new StringBuilder();
        jpql.append("select m.menuId FROM MenuItem m where m.menuId not in (")
                .append("select distinct c.menuId from MenuCategoryMenu c where c.menuCategoryName != 'SYSTEM'  and c.menuId not in (")
                .append("select b.menuId from MenuCategoryMenu b where b.menuCategoryName in (")
                .append("select a.key.menuCategoryName from MerchantMenuCategory a where a.key.merchantCode =:merchantCode")
                .append(")")
                .append(")")
                .append(")");


        Map<String, Object> params = new HashMap<>();
        params.put("merchantCode", merchantCode);
        return this.findByHql(jpql.toString(), params);
    }

}
