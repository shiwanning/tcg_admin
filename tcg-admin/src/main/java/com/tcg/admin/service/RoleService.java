package com.tcg.admin.service;


import java.util.List;
import org.springframework.data.domain.Page;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.model.RoleOperator;
import com.tcg.admin.to.RoleCreateTO;
import com.tcg.admin.to.RoleTo;
import com.tcg.admin.to.SortTo;
import com.tcg.admin.to.UserInfo;

public interface RoleService {


	/**
	 * <pre>
	 * to remove and add the relationships between operator and role.
	 *
	 * </pre>
	 *
	 * @param operatorId, roleList
	 * @throws AdminServiceBaseException
	 */
	void assignRoles(Integer operatorId, List<Integer> roleList);
	
	/**
	 * <pre>
	 * to remove the relationships between operator and role.
	 * 
	 * </pre>
	 * 
	 * @param operator, roleList
	 * 
	 * @throws AdminServiceBaseException
	 */
    void unCorrelateRoles(Integer operatorId, List<Integer> roleList);

    /**
	 * <pre>
	 * Create a user role, for role management.
	 * 
	 * 建立角色
	 * </pre>
	 * 
	 *
	 */
	Role createRole(RoleCreateTO roleTo, String operator);

	/**
	 * <pre>
	 * Update a user role, for role management.
	 *
	 * 修改角色
	 * </pre>
	 *
	 * @param role
	 *            roldId, roleName, roleDescription.
	 * @throws AdminServiceBaseException
	 * @see com.yx.us.model.Role
	 */
    void updateRole(List<Integer> categoryId, RoleCreateTO roleTo, String updateOperator);

    /**
	 * <pre>
	 * Delete a user role, for role management.
	 *  
	 * 刪除角色
	 * </pre>
	 * 
	 * @param roleId
	 * @throws AdminServiceBaseException
	 */
    void toggleActiveFlag(Integer roleId);

	/**
	 * <pre>
	 * Copy a user role, for role management.
	 *  
	 * 複製角色
	 * </pre>
	 *
	 * @param originRoleName
	 * @param newRoleName 
	 * @param description 
	 * @param displayOrder
	 * 
	 * @throws AdminServiceBaseException
	 */
    Role copyRole(String originRoleName, String newRoleName, String description, Integer displayOrder, String updateOperator);



	Role copyRoleAndConnect(RoleCreateTO copyTo, String operator);

    /**
	 * <pre>
	 * Query all roles, for role management.
	 *  
	 * 查詢所有角色
	 * </pre>
	 * 
	 * @param pageNumber 頁數
	 * @param pageSize 每頁筆數
	 * @param pageSize activeFlag
	 * 
	 * @throws AdminServiceBaseException
	 */
    Page<Role> queryAllRoles(int pageNumber, int pageSize);

    Page<Role> queryAllRoles(int pageNumber, int pageSize, Integer activeFlag);

	/***
	 * <pre>
	 * Query the roles of the operator
	 *
	 * </pre>
	 * @param operatorId The id of the operator.
	 */
    List<RoleOperator> queryRoles(Integer operatorId);


	/**
	 * <pre>
	 * Query a operator's role, for role management.
	 *  
	 * 查詢operator擁有角色權限
	 * </pre>
	 * 
	 * @param operatorName
	 * @param pageNumber 頁數
	 * @param pageSize 每頁筆數
	 * 
	 * @throws AdminServiceBaseException
	 */
    Page<Role> queryOperatorRoles(String operatorName, int pageNumber, int pageSize);

    /**
	 * <pre>
	 * Query the role is assign to which operator, for role management.
	 *  
	 * 查詢擁有該角色權限的operator
	 * </pre>
	 * 
	 * @param roleName
	 * @param pageNumber 頁數
	 * @param pageSize 每頁筆數
	 * 
	 * @throws AdminServiceBaseException
	 */
	Page<Operator> queryOperatorsByRole(String roleName, int pageNumber, int pageSize);


    Role queryRoleById(Integer roleId);

    /**
	 * 查詢operator擁有的角色權限
	 * @param operator
	 * @return
	 * @throws AdminServiceBaseException
	 * 包含隐性BUG
	 */
	@Deprecated
    List<Role> queryOperatorAllRoles(Operator operator);
    
    void updateDisplayPermissions(Integer roleId, String[] menuIds);
    
    void updateAuditPermissions(Integer roleId, String[] menuIds);
    
    public Page<Role> queryAllRolesWithParams(int pageNumber, int pageSize, Integer activeFlag, SortTo sortTo, String roleName, String description,Integer categoryId);

    Boolean isSysAdmin(UserInfo<Operator> userInfo);

	List<RoleTo> findAllActiveRoles();

	List<Role> findOperatorAllRoleByOperatorId(Integer operatorId);
}
