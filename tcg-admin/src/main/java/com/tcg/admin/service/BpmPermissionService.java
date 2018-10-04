package com.tcg.admin.service;


import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.BpmGroup;
import com.tcg.admin.model.Operator;

import java.util.List;
import java.util.Set;

/**
 * Platform_2.0 : com.yx.us.service.BpmPermissionService
 * ***
 * Description :
 * Author : giggs
 * Date : 2014/12/5 16:51
 */
public interface BpmPermissionService {

    List<String> getGroupKeys(Integer roleId);

    List<BpmGroup> findAllBpmGroup();

	/**
	 * 變更審核權限
	 *
	 * @param roleId
	 * @param menuIdList
	 * @throws AdminServiceBaseException
	 */
    void updatePermissionRoleGroup(Integer roleId, List<Integer> menuIdList);

    /**
	 * 取得有審核權限的MenuItem id list
	 * @param roleId
	 * @return
	 * @throws AdminServiceBaseException
	 */
    List<Integer> getBpmCheckListByRole(Integer roleId);
    
    List<?> generatePermissionTree(Integer roleId);

    Set<String> getBpmGroupKeysByOperator(Operator operator);
}
