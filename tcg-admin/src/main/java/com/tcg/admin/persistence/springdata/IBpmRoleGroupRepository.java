package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.BpmRoleGroup;

import java.util.List;

/**
 * Platform_2.0 : com.yx.us.persistence.IBpmRoleMenuRepository
 * ***
 * Description :
 * Author : giggs
 * Date : 2014/12/5 17:04
 */
public interface IBpmRoleGroupRepository extends JpaRepository<BpmRoleGroup, Integer> {

	@Query("SELECT o.groupName FROM BpmRoleGroup o WHERE o.roleId = ?1")
	public List<String> findBpmGroupKeyByRoleId(Integer roleId);

	@Modifying
	@Query("DELETE FROM BpmRoleGroup x WHERE x.roleId=?1 AND x.groupName in?2")
	public void deleteByRoleIdAndGroupName(Integer roleId, List<String> groupNameList);

    @Modifying
    @Query("DELETE FROM BpmRoleGroup x WHERE x.roleId=?1")
    public void deleteByRoleId(Integer roleId);

}
