package com.tcg.admin.persistence.springdata;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tcg.admin.model.BpmGroup;

import java.util.List;

/**
 * Platform_2.0 : com.yx.us.persistence.IBpmGroupRepository
 * ***
 * Description :
 * Author : giggs
 * Date : 2014/12/9 12:42
 */
public interface IBpmGroupRepository extends JpaRepository<BpmGroup, Integer> {

    @Query("SELECT x FROM BpmGroup x WHERE x.menuId in ?1")
    public List<BpmGroup> findByMenuId(List<Integer> menuIdList);

    @Query("SELECT x.groupName FROM BpmGroup x WHERE x.menuId in ?1")
    public List<String> findGroupNameByMenuId(List<Integer> menuIdList);

	@Query("SELECT x.menuId FROM BpmGroup x WHERE x.groupName in (SELECT DISTINCT o.groupName FROM BpmRoleGroup o WHERE o.roleId = ?1)")
	public List<Integer> findBpmCheckListByRole(Integer roleId);
}
