package com.tcg.admin.persistence;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.google.common.collect.Maps;


/**
 * <p>Title: com.yx.us.persistence.RoleOperatorRepository</p>
 * <p>Description: 查詢管理員&角色對應資料</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 *
 * @author Marc
 * @version 1.0
 */
@Repository
public class RoleOperatorCustomRepository extends BaseDAORepository {

    @PersistenceContext(unitName = "persistenceUnit")
    private EntityManager entityManager;

    /**
     * 取得對應管理員的角色，其中角色必須為非刪除狀態
     *
     * @param operatorId
     *
     * @return
     */
    public List<Integer> findRoleIdListByOperatorId(Integer operatorId) {
        StringBuilder sb = new StringBuilder(
                "select DISTINCT ro.roleId from RoleOperator ro, Role r where ro.roleId=r.roleId AND ro.operatorId=?1 AND r.activeFlag!=0");
        TypedQuery<Integer> q = entityManager.createQuery(sb.toString(), Integer.class);
        q.setParameter(1, operatorId);

        return q.getResultList();
    }

    public List<Object[]> findRoleIdListOfCategoryByOperatorId(Integer operatorId) {
        Map<String, Object> keyMap = Maps.newHashMap();
        StringBuilder sb = new StringBuilder();

        sb.append(" select ")
        .append(" r.role_Id ") //Bigdec
        .append(" from us_Category c, us_Category_Role cr, us_Role r ")
        .append(" where 1=1 ")
        .append(" and cr.category_Id = c.category_Id ")
        .append(" and cr.role_Id = r.role_Id ")
        .append(" and c.category_Name in  ")
        .append(" ( ")
        .append(" select m.menu_Name from us_Operator o, us_Role_Operator ro, us_Role r,us_Role_Menu_Permission p, us_Menu_Item m  ")
        .append(" where 1=1   ")
        .append(" and o.operator_Id = ro.operator_Id   ")
        .append(" and ro.role_Id = r.role_Id   ")
        .append(" and r.role_Id = p.role_Id   ")
        .append(" and p.menu_Id = m.menu_Id   ");

        if(operatorId != null){
            sb.append(" and o.operator_Id = :operatorId ");
            keyMap.put("operatorId", operatorId);
        }
        sb.append(" and r.active_Flag !=0 ) ");

        return this.findBySql(sb.toString(),keyMap);
    }



}
