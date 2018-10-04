package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.RoleOperator;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.List;

import javax.persistence.QueryHint;

/**
 * <p>Title: com.yx.us.persistence.IRoleOperatorRepository</p>
 * <p>Description: 角色與與管理員的對應DAO</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 *
 * @author Marc
 * @version 1.0
 */
public interface IRoleOperatorRepository extends JpaRepository<RoleOperator, Integer> {

    /**
     * 取得某個role下有哪些對應的operator
     *
     * @param roleId
     *
     * @return
     */
	 @Query("SELECT a FROM RoleOperator a WHERE roleId = ?1")
    List<RoleOperator> findByRoleId(Integer roleId);

    /**
     * 取得operator屬於那些role
     *
     * @param operatorId
     *
     * @return
     */
    @Query("SELECT a FROM RoleOperator a WHERE operatorId = ?1")
    List<RoleOperator> findByOperatorId(Integer operatorId);

    /**
     * 以roleId與多筆menuId取得對應關係
     *
     * @param roleId
     * @param menuId
     *
     * @return
     */
    @Query("select p from RoleOperator p where p.operatorId = ?1 and p.roleId in ?2")
    List<RoleOperator> findByOperatorIdAndRoleIdList(Integer operatorId, List<Integer> roleIdList);

    /**
     * 以roleId與多筆menuId取得對應關係
     *
     * @param roleId
     * @param menuId
     *
     * @return
     */
    @Query("select p from RoleOperator p where p.operatorId = ?1 and p.roleId = ?2")
    RoleOperator findByOperatorIdAndRoleId(Integer operatorId, Integer roleId);


    /**
     * 依 operatorId 刪除擁有 roleId 的 record
     *
     * @param operatorId
     */
    @Modifying()
    @Query("delete from RoleOperator ro where ro.operatorId = ?1")
    void deleteByOperatorId(Integer operatorId);

    @Query("select DISTINCT ro.roleId from RoleOperator ro, Role r where ro.roleId=r.roleId AND ro.operatorId=?1 AND r.activeFlag!=0")
    @QueryHints(value={@QueryHint(name=org.hibernate.annotations.QueryHints.CACHEABLE, value="true")})
    List<Integer> findRoleIdListByOperatorId(Integer operatorId);

}
