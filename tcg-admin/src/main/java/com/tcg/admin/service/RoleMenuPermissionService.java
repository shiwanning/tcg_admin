package com.tcg.admin.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.controller.json.JsTreeMenu;
import com.tcg.admin.model.ApiLabel;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.to.TreeTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.OperatorInfoTo;

public interface RoleMenuPermissionService {

	List<TreeTo> getAllTreeTo();
	
	/**
	 * <pre>
	 * Create correlation between role and menu. Use roleId and menuIdList to link the relationship.
	 * 
	 * 建立角色與權限資源的關聯
	 * </pre>
	 * 
	 * @param roleId
	 * @param menuIdList
	 * @throws AdminServiceBaseException
	 */
    void correlatePermission(Integer roleId, List<Integer> menuIdList);

	/**
	 * <pre>
	 * Delete correlation between role and menuIdList.
	 * 
	 * 刪除角色與選單的關聯
	 * </pre>
	 * 
	 * @param Integer roleId
	 * @param menuIdList
	 * @throws AdminServiceBaseException
	 */
    void unCorrelatePermission(Integer roleId, List<Integer> menuIdList);

    /**
	 * <pre>
	 * Update a menuItem.
	 *
	 * 更新權限資源內容
	 * </pre>
	 *
	 * @param menuItem
	 * @throws AdminServiceBaseException
	 * @see com.yx.us.model.MenuItem
	 */
    void updateMenuItem(MenuItem menuItem);

	/**
	 * 以ID查詢權限資源
	 * @param menuId
	 * @return
	 * @throws AdminServiceBaseException
	 */
    MenuItem queryMenuItemById(Integer menuId);

	/**
	 * 查詢角色擁有的權限資源
	 * @param roleId
	 * @return
	 * @throws AdminServiceBaseException
	 */
    List<MenuItem> queryMenuItemsByRole(Integer roleId);

	/**
	 * 查詢使用者擁有的權限資源
	 * @param operatorId
	 * @return
	 * @throws AdminServiceBaseException
	 */
	List<MenuItem> queryMenuItemsByOperatorId(Integer operatorId);

	/**
	 * 驗證使用者是否擁有此權限
	 * @param operatorId
	 * @param menuItem
	 * @return
	 * @throws AdminServiceBaseException
	 */
    boolean verifyMenuItemPermission(Integer operatorId, MenuItem menuItem);

    /**
	 * 查詢完整的目錄選單結構
	 * query full menu tree structure
	 * 
	 * example: menuId 0 mapped to highest menuItem 1
	 * 			menuId 1 mapped to list of MenuItem 2,3,4
	 * 	        menuId 2 map to list of MenuItem 5
	 *          menuId 3 map to list MenuItem 6
	 * 
	 * @return
	 * @throws AdminServiceBaseException
	 */
    Map<Integer, List<MenuItem>> queryAllMenuTree();

    /**
	 * 查詢管理員所具有的目錄權限結構
	 * query menu tree structure of a specific operator
	 * 
	 * example: menuId 0 mapped to highest menuItem 1
	 * 			menuId 1 mapped to list of MenuItem 2,3,4 
	 * 	        menuId 2 map to list of MenuItem 5
	 *          menuId 3 map to list MenuItem 6
	 * @param operatorId
	 * @return
	 * @throws AdminServiceBaseException
	 */
    Map<String, List<MenuItem>> queryAllMenuTreeByOperator(Integer operatorId);

	List<TreeTo> queryAllMenuTreeByMenuCategoryName(List<String> menuCategoryNameList);


    /**
	 * 查詢擁有此menuId功能的角色有哪些
	 * @param menuId
	 * @return
	 * @throws AdminServiceBaseException
	 */
    List<Integer> queryRolesByMenuId(Integer menuId);

	Page<Role> getRolesByMenuId(Integer menuId, Integer activeFlag, int pageNumber, int pageSize, String sortOrder, String sortColumn);

	/**
	 * 查詢管理員所具有的目錄權限, 並組成目錄樹
	 * @return
	 * @throws AdminServiceBaseException
	 */
    JsTreeMenu getOperatorJsMenuTree(Operator operator);

    /**
	 * 取得管理員所有按鈕權限
	 * query all menu permission of an operator
	 * @param operatorName
	 * @return
	 * @throws AdminServiceBaseException
	 */
	List<Integer> getAllButtonList(String operatorName);

	List<MenuItem> getAllButtonListForService(String operatorName);

    List<Map<String,String>> getMerchants(UserInfo<Operator> op);
    
    void saveOrUpdateLabel(ApiLabel apiLabel);
    
    List<TreeTo> findPermissionOfOperator();

	List<Integer> menuIdsByBaseMerchant(Operator op);

    List<Map<String,String>> getAllMerchant();
    
    List<Map<String,String>> getAllCompanies();

    List<TreeTo> findPermissionOfOperator(Integer opreatorId, UserInfo<Operator> userInfo);

    /**
     * 将所有权限组绑定到 merchant
     * @param merchantCode
     */
    void createAllCategoryRelationFromMerchant(String merchantCode);

    List<Map<String, String>> getCompanies(UserInfo<Operator> newOperatorInfo);

    List<OperatorInfoTo> findByMenuIdPermission(Integer menuId);

	List<TreeTo> getAllTreeTo(Integer operatorId);

	List<Map<String, String>> getMerchants(Integer operatorId);
}
