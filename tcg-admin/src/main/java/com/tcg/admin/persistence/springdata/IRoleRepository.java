package com.tcg.admin.persistence.springdata;


import com.tcg.admin.model.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IRoleRepository extends JpaRepository<Role, Integer>, JpaSpecificationExecutor<Role> {

    /**
     * 依 roleId 查詢所有狀態的 record
     *
     * @param roleId
     *
     * @return
     */
    Role findByRoleId(Integer roleId);

    /**
     * 依 roleName 查詢所有狀態的 record
     *
     * @param roleId
     *
     * @return
     */
    @Query("select r from Role r where LOWER(roleName) = LOWER(?1)")
    Role findByRoleName(String roleName);

    /**
     * 依 roleName 查詢目前狀態為正常的 record 筆數
     *
     * @param roleName
     *
     * @return 筆數
     */
    @Query("select count(r) from Role r where r.activeFlag = 1 and LOWER(roleName) = LOWER(?1)")
    int countByRoleName(String roleName);

    @Query("select r from Role r where r.activeFlag = 1 and roleName in ?1")
    List<Role> findByRoleNames(List<String> roleNameList);

    @Query("select r from Role r where r.roleId in ?1")
    List<Role> findByRoleIds(List<Integer> roleIds);

    @Query("select r.roleId, r.roleName from Role r where r.activeFlag = ?1 order by roleId")
	List<Object[]> findByActiveFlag(int activeFlag);


    @Query("select count(ro) from RoleOperator ro, Role r where ro.roleId=r.roleId AND ro.operatorId=?1 AND r.googleOtpActive = 1 ")
    int countGoogleActiveByOperator(Integer operatorId);

    @Query(value = "SELECT ur.* FROM US_ROLE ur,US_ROLE_OPERATOR uro where uro.role_id = ur.ROLE_ID and uro.OPERATOR_ID =?1 and active_flag = 1",nativeQuery = true)
    List<Role> findOperatorAllRoleByOperatorId(Integer operatorId);
}
