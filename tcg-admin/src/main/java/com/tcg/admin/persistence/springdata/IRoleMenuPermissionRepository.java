package com.tcg.admin.persistence.springdata;


import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.RoleMenuPermission;


/**
 * <p>Title: com.yx.us.persistence.IRoleMenuPermissionRepository</p>
 * <p>Description: 選單&角色關聯基本操作DAO</p>
 * <p>Copyright: Copyright (c) UniStar Corp. 2014. All Rights Reserved.</p>
 * <p>Company: UnitStar DEV Team</p>
 *
 * @author Marc
 * @version 1.0
 */
public interface IRoleMenuPermissionRepository extends JpaRepository<RoleMenuPermission, Integer> {

    /**
     * 以roleId與多筆menuId取得對應關係
     *
     * @param roleId
     * @param menuId
     *
     * @return
     */
    @Query("select p from RoleMenuPermission p where p.roleId = ?1 and p.menuId in ?2")
    List<RoleMenuPermission> findByRoleIdAndMenuIdList(Integer roleId, List<Integer> menuIdList);

    /**
     * 取得傳入的roleId list包含的目錄權限
     *
     * @param menuIdList
     *
     * @return
     */
    @Query("SELECT DISTINCT p.menuId FROM RoleMenuPermission p WHERE p.roleId in ?1")
    Set<Integer> findMenuIdListByRoleIdList(List<Integer> roleIdList);

    @Query("SELECT DISTINCT p.menuId FROM MenuItem m, RoleMenuPermission p, RoleOperator r WHERE m.menuId = p.menuId and p.roleId = r.roleId and m.accessType <> 0 and m.menuType = 1 and r.operatorId = (?1) order by p.menuId ")
    List<Integer> findMenuIdListByOperator(Integer operatorId);


    @Modifying
    @Query("DELETE from RoleMenuPermission p where p.roleId = ?1 and p.menuId in ?2")
    void deleteRoleIdAndMenuIdList(Integer roleId, List<Integer> menuIdList);

    @Modifying
    @Query("DELETE from RoleMenuPermission p where p.roleId = ?1")
    void deleteRoleIdAndMenuIdListByRoleId(Integer roleId);
    /**
     * 依roleId來取得對應的資料
     *
     * @param roleId
     *
     * @return
     */
    @Query("select p from RoleMenuPermission p where p.roleId in ?1")
    List<RoleMenuPermission> findByRoleId(Integer roleId);

    /**
     * 依menuId來取得對應的資料
     *
     * @param menuId
     *
     * @return
     */
    @Query("select p from RoleMenuPermission p where p.menuId in ?1")
    List<RoleMenuPermission> findByMenuId(Integer menuId);
    
    @Query("select p from RoleMenuPermission p where p.roleId in (?1) and p.roleId != 1")
    List<RoleMenuPermission> findByRoleIdsWithoutSystem(List<Integer> roleIds);
}
