package com.tcg.admin.service;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.CategoryRole;

import java.util.List;

public interface CategoryRoleService {


	List<CategoryRole> queryCategoryRoleByRoleId(Integer roleId);


	/**
	 * <pre>
	 * to build the relationships between Category and role.
	 * (correlateCategory or unCorrelateCategory)
	 * 
	 * </pre>
	 * 
	 * @param categoryId, roleList
	 * @throws AdminServiceBaseException
	 */
	void correlateCategory(List<Integer> categoryId, List<Integer> roleList, String updateOperator) throws AdminServiceBaseException;


}
