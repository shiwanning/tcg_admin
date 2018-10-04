package com.tcg.admin.persistence.springdata;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tcg.admin.model.Operator;

public interface IOperatorRepository extends IUserRepositoryBase<Operator> {


    @Query("select o from Operator o where LOWER(o.operatorName) = LOWER(?1)")
    Operator findByOperatorName(String operatorName);

    @Query("select count(*) from Operator o where LOWER(o.operatorName) = LOWER(?1)")
    int isOperatorExist(String operatorName);

    Operator findByOperatorId(Integer operatorId);

    @Query("select o from Operator o where o.operatorId in (?1)")
    List<Operator> findOperatorIdListByOperatorIdList(List<Integer> operatorIdList);

    @Query("SELECT DISTINCT ro.operatorId FROM CategoryRole b, Role r, RoleOperator ro where 1=1 and r.roleId = b.roleId and ro.roleId=r.roleId and b.categoryId = ?1 and r.activeFlag = ?2 ")
    List<Integer> queryUserFromCategory(Integer categoryId, Integer activeFlag);
    
    @Query("select o.operatorId, o.operatorName from Operator o where o.activeFlag in (?1)")
    List<Object[]> getOperatorsByActiveFlag(List<Integer> operatorIdList);
    
    @Query(value = 
            " SELECT DISTINCT OP.OPERATOR_ID, OP.OPERATOR_NAME" + 
            " FROM US_MENU_ITEM MI" + 
            " INNER JOIN US_ROLE_MENU_PERMISSION MP ON MI.MENU_ID = MP.MENU_ID" + 
            " INNER JOIN US_ROLE_OPERATOR RO ON RO.ROLE_ID = MP.ROLE_ID" + 
            " INNER JOIN US_OPERATOR OP ON OP.OPERATOR_ID = RO.OPERATOR_ID" + 
            " WHERE MI.MENU_ID = :menuId " + 
            " AND" + 
            " ( " + 
            "     EXISTS (" + 
            "         SELECT OPI.OPERATOR_ID" + 
            "         FROM US_MENU_CATEGORY MC" + 
            "         INNER JOIN US_MENU_CATEGORY_MENU MCM ON MC.CATEGORY_NAME = MCM.MENU_CATEGORY_NAME AND MCM.MENU_CATEGORY_NAME != 'SYSTEM'" + 
            "         INNER JOIN US_MERCHANT_MENU_CATEGORY MMC ON MMC.MENU_CATEGORY_NAME = MCM.MENU_CATEGORY_NAME" + 
            "         INNER JOIN US_OPERATOR OPI ON OPI.BASE_MERCHANT_CODE = MMC.MERCHANT_CODE" + 
            "         WHERE MCM.MENU_ID = :menuId AND OPI.OPERATOR_ID = OP.OPERATOR_ID" + 
            "     ) OR " + 
            "     EXISTS (" + 
            "         SELECT MCM.MENU_CATEGORY_NAME" + 
            "         FROM US_MENU_CATEGORY_MENU MCM" + 
            "         WHERE MCM.MENU_CATEGORY_NAME = 'SYSTEM' AND MCM.MENU_ID = :menuId" + 
            "     )" + 
            " )"
            , nativeQuery = true)
    List<Object[]> findByMenuIdPermission(@Param("menuId") Integer menuId);

    List<Operator> findByOperatorIdIn(List<Integer> operatorIds);
}