package com.tcg.admin.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.json.JsMenuNode;
import com.tcg.admin.controller.json.JsTreeMenu;
import com.tcg.admin.model.ApiLabel;
import com.tcg.admin.model.MenuCategory;
import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.MerchantMenuCategorKey;
import com.tcg.admin.model.MerchantMenuCategory;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.model.RoleMenuPermission;
import com.tcg.admin.persistence.MenuCategoryCustomRepository;
import com.tcg.admin.persistence.RoleOperatorCustomRepository;
import com.tcg.admin.persistence.springdata.IApiLabelRepository;
import com.tcg.admin.persistence.springdata.IMenuCategoryMenuRepository;
import com.tcg.admin.persistence.springdata.IMenuCategoryRepository;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IMerchantMenuCategoryRepository;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.IMerchantRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.persistence.springdata.IRoleOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.specifications.RoleSpecifications;
import com.tcg.admin.to.QueryRoleTO;
import com.tcg.admin.to.TreeTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.OperatorInfoTo;
import com.tcg.admin.utils.NullAwareBeanUtilsBean;
import com.tcg.admin.utils.StringTools;
import com.tcg.admin.utils.ValidatorUtils;
import com.tcg.admin.utils.shiro.ShiroUtils;

import ch.lambdaj.Lambda;

@Service
@Transactional
public class RoleMenuPermissionServiceImpl implements RoleMenuPermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleMenuPermissionServiceImpl.class);
	private static final Integer SUPER_MANAGER_ROLE_ID = 1;

	@Autowired
	private CommonMenuService menuCacher;// 選單的Cache服務

	@Autowired
	private MerchantService merchantServiceBean;

	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private IRoleMenuPermissionRepository roleMenuRepository;

	@Autowired
	private IOperatorRepository operatorRepository;

	@Autowired
	private RoleOperatorCustomRepository roleOperatorCustomRepository;

	@Autowired
	private IApiLabelRepository apiLabelRepository;

	@Autowired
	private IMerchantOperatorRepository merchantOperatorRepository;

	@Autowired
	private IRoleOperatorRepository roleOperatorRepository;

	@Autowired
	private IMenuCategoryMenuRepository menuCategoryMenuRepository;

	@Autowired
	private OperatorAuthenticationService operatorAuthenticationService;

	@Autowired
	private RoleMenuPermissionService roleMenuPermissionService;

	@Autowired
	IMenuCategoryMenuRepository menuCategoryRepo;

	@Autowired
	IMenuItemRepository menuRepository;

	@Autowired
	IMerchantRepository merchantRepo;

	@Autowired
	IMerchantMenuCategoryRepository merchantMenuCategoryRepo;

	@Autowired
	MenuCategoryCustomRepository menuCategoryCustomRepo;

	@Autowired
	IMenuCategoryRepository menuCategoryRepository;

	@Override
	public void correlatePermission(Integer roleId, List<Integer> menuIdList) {
		try {

			// 1.確認role是否存在____________________________________________________________________
			Role role = roleRepository.findOne(roleId);
			if (role == null) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR,
						"can't find the corresponding role");
			}

			List<MenuItem> menuItems = this.queryMenuItemsByRole(roleId);

			// REMOVE NULL

			List<Integer> currentRolePermissions = Lambda.extract(menuItems, Lambda.on(MenuItem.class).getMenuId());

			// 2.每個menuId都要存在__________________________________________________________________
			for (Integer menuId : menuIdList) {
				MenuItem menu = menuCacher.getMenuMap().get(menuId);
				if (menu == null) {
					throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR,
							"can't find the corresponding menu");
				}
			}

			if (CollectionUtils.isNotEmpty(currentRolePermissions)) {
				this.unCorrelatePermission(roleId, currentRolePermissions);
			}

			// 3.關聯關係___________________________________________________________________________
			List<RoleMenuPermission> roleMenuList = Lists.newLinkedList();

			for (Integer menuId : menuIdList) {
				RoleMenuPermission roleMenu = new RoleMenuPermission();
				roleMenu.setRoleId(roleId);
				roleMenu.setMenuId(menuId);
				roleMenuList.add(roleMenu);
			}

			roleMenuRepository.save(roleMenuList);

		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
		}
	}

	@Override
	public void unCorrelatePermission(Integer roleId, List<Integer> menuIdList) {

		try {
			roleMenuRepository.deleteRoleIdAndMenuIdList(roleId, menuIdList);
		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
		}

	}

	@Override
	public void createMenuItem(MenuItem menu) {
		try {
			// 已存在則不能新增
			if (menuCacher.getMenuMap().get(menu.getMenuId()) != null) {
				throw new AdminServiceBaseException(AdminErrorCode.MENU_ALREADY_EXIST, "This menu has already exsited");
			}

			// menu 名稱不能重複
			for (MenuItem menuListItem : menuCacher.getMenuList()) {
				if (menu.getMenuName().equals(menuListItem.getMenuName())) {
					throw new AdminServiceBaseException(AdminErrorCode.MENU_ALREADY_EXIST,
							"This menu name has already used");
				}
			}

			checkParentAndSetTreeLevel(menu);

			// 執行新增
			menuCacher.addMenu(menu);

		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
		}
	}

	@Override
	public void deleteMenuItem(Integer menuItemId) {
		try {

			// 有role已分配此選單的權限則不可刪除
			if (!roleMenuRepository.findByMenuId(menuItemId).isEmpty()) {
				throw new AdminServiceBaseException(AdminErrorCode.MENU_BELONG_TO_ROLE_ERROR,
						"can't remove menu which is belong to Roles");
			}

			// 選單不存在則不能刪除
			MenuItem menu = menuCacher.getMenuMap().get(menuItemId);
			if (menu == null) {
				throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "can't remove unexist menu");
			}

			menuCacher.removeMenu(menu);

		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
		}

	}

	@Override
	public void updateMenuItem(MenuItem menuItem) {
		try {

			MenuItem dbMenuItem = menuCacher.getMenuMap().get(menuItem.getMenuId());

			if (dbMenuItem == null) {
				throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "This menu does not exsited");
			}

			// menu 名稱不能重複
			for (MenuItem menuListItem : menuCacher.getMenuList()) {
				if (!menuItem.getMenuId().equals(menuListItem.getMenuId())
						&& menuItem.getMenuName().equals(menuListItem.getMenuName())) {
					throw new AdminServiceBaseException(AdminErrorCode.MENU_ALREADY_EXIST,
							"This menu name has already used");
				}
			}

			checkParentAndSetTreeLevel(menuItem);
			BeanUtilsBean beanUtilsBean = new NullAwareBeanUtilsBean();
			beanUtilsBean.getConvertUtils().register(false, false, 0); // tell BeanUtils to ignore null variables
			beanUtilsBean.copyProperties(dbMenuItem, menuItem);

			menuCacher.updateMenu(dbMenuItem);
		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_EXCEPTION, ex.getMessage(), ex);
		}
	}

	@Override
	public Map<Integer, List<MenuItem>> queryAllMenuTree() {
		try {
			return menuCacher.getWholeMenuTree();
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	@Override
	@Cacheable(value= "cache-ap-admin", key = "'queryAllMenuTreeByOperator:' + #operatorId")
	public Map<Integer, List<MenuItem>> queryAllMenuTreeByOperator(Integer operatorId) {
		try {

			// 1.確認此管理員存在(無存在拋ex)__________________________________________________________
			Operator op = operatorRepository.findOne(operatorId);
			if (op == null || op.getActiveFlag().equals(LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode())) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR,
						"This operator is not exist");
			}

			// 2.取得此管理員的隸屬角色(無角色拋ex)_____________________________________________________
			List<Integer> roleIdList = roleOperatorCustomRepository.findRoleIdListByOperatorId(operatorId);
			if (roleIdList.isEmpty()) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_WITHOUT_ROLE_ERROR,
						"This operator is not in any Role");
			}

			// 3.取得這些角色具有那些選單權限(無任何menu權限拋ex)________________________________________
			Set<Integer> menuIdList = roleMenuRepository.findMenuIdListByRoleIdList(roleIdList);
			if (menuIdList.isEmpty()) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_WITHOUT_MENU_PRIVILEGE_ERROR,
						"This operator dosen't have menu privilege");
			}
			menuIdList.add(0);// 加入最頂層目錄

			// 4.組該operator專屬選單_________________________________________________________________
			return generateOperatorMenuTree(roleIdList.contains(SUPER_MANAGER_ROLE_ID), menuIdList);
		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	private Map<Integer, List<MenuItem>> generateOperatorMenuTree(boolean isAdmin, Set<Integer> menuIdList) {
		Map<Integer, List<MenuItem>> allMenuTree = this.queryAllMenuTree();

		Map<Integer, List<MenuItem>> operatorMenuTree = Maps.newHashMap();
		for (Entry<Integer, List<MenuItem>> entry : allMenuTree.entrySet()) {

			Integer menuId = entry.getKey();
			List<MenuItem> menus = entry.getValue();

			// 該role權限包含此層節點才需要判斷是否要加入map...............
			List<MenuItem> eachMenuList = null;
			if (isAdmin) {
				eachMenuList = Lists.newLinkedList(menus);
			} else if (menuIdList.contains(menuId)) {
				eachMenuList = Lists.newLinkedList();
				for (MenuItem subMenu : menus) {
					if (menuIdList.contains(subMenu.getMenuId())) {
						eachMenuList.add(subMenu);
					}
				}
			}
			if (eachMenuList != null && !eachMenuList.isEmpty()) {
				operatorMenuTree.put(menuId, eachMenuList);
			}
		}
		return operatorMenuTree;
	}

	@Override
	public List<TreeTo> queryAllMenuTreeByMenuCategoryName(List<String> menuCategoryNameList) {
		Set<Integer> menuIdList = menuCategoryMenuRepository.findMenuIdByMenuCategoryName(menuCategoryNameList);
		menuIdList.add(0);// 加入最頂層目錄

		Map<Integer, List<MenuItem>> allMenuTree = this.queryAllMenuTree();
		Map<Integer, List<MenuItem>> result = Maps.newHashMap();
		for (Entry<Integer, List<MenuItem>> menuEntry : allMenuTree.entrySet()) {
			Integer menuId = menuEntry.getKey();
			List<MenuItem> menus = menuEntry.getValue();
			putEachMenuList(result, menuIdList, menuId, menus);
		}

		// 取得第一階的父Tree
		List<MenuItem> firstParentTree = Lists.newLinkedList();

		if (!result.isEmpty()) {
			firstParentTree = result.get(0);
		}

		return grenateTreeTo(firstParentTree, result);

	}

	private void putEachMenuList(Map<Integer, List<MenuItem>> result, Set<Integer> menuIdList, Integer menuId, List<MenuItem> menus) {
		if (menuIdList.contains(menuId)) {
			List<MenuItem> eachMenuList = Lists.newLinkedList();
			for (MenuItem subMenu : menus) {
				if (menuIdList.contains(subMenu.getMenuId())) {
					eachMenuList.add(subMenu);
				}
			}
			// 此層目錄向下有直屬節點才加入map中......................
			if (!eachMenuList.isEmpty()) {
				result.put(menuId, eachMenuList);
			}
		}
	}

	private List<TreeTo> grenateTreeTo(List<MenuItem> firstParentTree, Map<Integer, List<MenuItem>> result) {
		List<TreeTo> finList = Lists.newLinkedList();
		for (MenuItem firstMenuItem : firstParentTree) {
			TreeTo cloneTree = new TreeTo();
			copyProperties(cloneTree, firstMenuItem);
			Integer menuId = firstMenuItem.getMenuId();

			// 代表有子節點
			List<MenuItem> childList = result.get(menuId);
			if (childList != null && !childList.isEmpty()) {
				cloneTree.setList(this.returnChildList(result, childList));
			}
			finList.add(cloneTree);
		}
		return finList;
	}

	private List<TreeTo> returnChildList(Map<Integer, List<MenuItem>> result, List<MenuItem> childList) {
		List<TreeTo> menuItemLists = Lists.newLinkedList();
		for (int i = 0; i < childList.size(); i++) {
			MenuItem menuItem = childList.get(i);
			Integer menuId = menuItem.getMenuId();

			// 代表有子節點
			List<MenuItem> subList = result.get(menuId);
			TreeTo cloneTree = new TreeTo();
			copyProperties(cloneTree, menuItem);
			if (subList != null && !subList.isEmpty()) {
				cloneTree.setList(this.returnChildList(result, subList));
			}
			menuItemLists.add(cloneTree);
		}

		return menuItemLists;
	}

	private void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new AdminServiceBaseException("BEAN_COPY_ERROR", e);
		}
	}
	
	@Override
	public MenuItem queryMenuItemById(Integer menuId) {
		return menuCacher.getMenuMap().get(menuId);
	}

	@Override
	public List<MenuItem> queryMenuItemsByRole(Integer roleId) {
		try {
			List<Integer> roleIdList = Lists.newLinkedList();
			roleIdList.add(roleId);

			Set<Integer> menuIdList = roleMenuRepository.findMenuIdListByRoleIdList(roleIdList);

			List<MenuItem> menuItemList = Lists.newLinkedList();
			for (Integer menuId : menuIdList) {
				MenuItem menu = menuCacher.getMenuMap().get(menuId);
				if (menu != null) {
					menuItemList.add(menuCacher.getMenuMap().get(menuId));
				}
			}

			return menuItemList;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	@Override
	public List<MenuItem> queryMenuItemsByOperatorId(Integer operatorId) {
		List<Integer> menuIdList = roleMenuRepository.findMenuIdListByOperator(operatorId);
		return this.getMenuItemList(menuIdList);
	}

	@Override
	public boolean verifyMenuItemPermission(Integer operatorId, MenuItem menuItem) {
		// 檢查operatorId和menuItem
		Operator op = operatorRepository.findOne(operatorId);
		if (op == null || op.getActiveFlag().equals(LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode())) {
			throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR,
					"This operator is not exist");
		}
		MenuItem existedMenuItem = menuCacher.getMenuMap().get(menuItem.getMenuId());
		if (existedMenuItem == null || !existedMenuItem.getUrl().equals(menuItem.getUrl())) {
			throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR,
					"can't find the corresponding menu : " + menuItem.getMenuId());
		}

		// 找出operator的所有role
		List<Integer> roleIdList = roleOperatorCustomRepository.findRoleIdListByOperatorId(operatorId);
		if (roleIdList.isEmpty()) {
			return false;
		}

		if(roleIdList.contains(SUPER_MANAGER_ROLE_ID)) {
			if (existedMenuItem.getTreeLevel() == 2) {
				LOGGER.info("super loggin menu access");
			}
			return true;
		}

		// 檢查傳入的menuItem是否在有權限清單中
		Set<Integer> menuIdList = roleMenuRepository.findMenuIdListByRoleIdList(roleIdList);
		
		if(menuIdList.contains(menuItem.getMenuId())) {
			if (existedMenuItem.getTreeLevel() == 2) {
				LOGGER.info("loggin menu access");
			}
			return true;
		}
		
		return false;
	}

	@Override
	public List<Integer> queryRolesByMenuId(Integer menuId) {
		List<RoleMenuPermission> roleMenuList = roleMenuRepository.findByMenuId(menuId);
		List<Integer> roleIdList = Lists.newLinkedList();
		for (RoleMenuPermission roleMenu : roleMenuList) {
			roleIdList.add(roleMenu.getRoleId());
		}

		return roleIdList;
	}

	@Override
	public Page<Role> getRolesByMenuId(Integer menuId, Integer activeFlag, int pageNumber, int pageSize,
			String sortOrder, String sortColumn) {
		Page<Role> page = null;
		try {
			List<Integer> roleIdList = this.queryRolesByMenuId(menuId);

			// 查詢 US_ROLE 設定條件所需的物件
			QueryRoleTO queryRoleTO = new QueryRoleTO();

			queryRoleTO.setRoleIdList(roleIdList);

			queryRoleTO.setActiveFlag(1); // 只找啟用的Role

			// 頁數
			queryRoleTO.setPagenumber(pageNumber);
			// 每頁筆數
			queryRoleTO.setPageSize(pageSize);

			// 組成where condition 查詢條件
			Specification<Role> role = RoleSpecifications.queryByConditions(queryRoleTO);

			Pageable request = new PageRequest(pageNumber - 1, pageSize,
					"asc".equalsIgnoreCase(sortOrder) ? Sort.Direction.ASC : Sort.Direction.DESC,
					sortColumn != null ? sortColumn : "roleId");
			// 由 roleRepository 做查詢
			page = roleRepository.findAll(role, request);

		} catch (Exception e) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}
		return page;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public JsTreeMenu getOperatorJsMenuTree(Operator operator) {

		try {
			if (operator == null) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "operator not exist");
			}

			Map<Integer, List<MenuItem>> menuTree = this.queryAllMenuTreeByOperator(operator.getOperatorId());

			JsTreeMenu treeMenu = new JsTreeMenu();
			List<JsMenuNode> operatorMenuTree = Lists.newLinkedList();
			UserInfo<Operator> userInfo = operatorAuthenticationService.getOperatorByToken(RequestHelper.getToken());
			List<Integer> menuItemsOfBaseMerchant = this.menuIdsByBaseMerchant(userInfo.getUser());
			boolean isAdmin = this.isAdmin(userInfo.getUser().getOperatorId());
			List<JsMenuNode> categoryList = setSubJsMenuList(userInfo, 0, menuTree, menuItemsOfBaseMerchant, isAdmin);
			operatorMenuTree.addAll(categoryList);
			treeMenu.setTreeMenu(operatorMenuTree);

			return treeMenu;

		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, "fail to get menu tree", ex);
		}
	}

	@Override
	public void submitEditPermissionForm(MenuItem menuItem) {
		/**
		 * Check for the violations of @NotNull annotation in entity
		 */

		menuItem.setUpdateTime(new Date());
		Set<ConstraintViolation<MenuItem>> violations = ValidatorUtils.validate(menuItem);

		if (CollectionUtils.isNotEmpty(violations)) {
			throw new AdminServiceBaseException(AdminErrorCode.PARAMETER_IS_REQUIRED, "request param error");
		}

		/**
		 * Proceed update MenuItem
		 */
		this.updateMenuItem(menuItem);

	}

	@Override
	@CacheEvict(value = "apiLabel", key = "'translateMenu'")
	public void saveOrUpdateLabel(ApiLabel apiLabel) {
		MenuItem dbMenuItem = menuCacher.getMenuMap().get(apiLabel.getMenuId());
		if (dbMenuItem == null) {
			throw new AdminServiceBaseException(AdminErrorCode.MENU_NOT_EXIST_ERROR, "This menu does not exsited");
		}
		ApiLabel currLabel = apiLabelRepository.findByIdAndLanguageCode(apiLabel.getMenuId(),
				apiLabel.getLanguageCode());
		if (currLabel == null) {
			apiLabelRepository.saveAndFlush(apiLabel);
		} else {
			copyProperties(currLabel, apiLabel);
			apiLabelRepository.saveAndFlush(currLabel);
		}
		menuCacher.refresh();
	}

	@Override
	public List<MenuItem> getAllButtonList(String operatorName) {
		try {

			// 先確認operator存在
			Operator operator = operatorRepository.findByOperatorName(operatorName);
			if (operator == null) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "operator not exist");
			}

			// 由cache取得選單並且過濾僅保留按鈕權限
			Map<Integer, List<MenuItem>> menuTree = this.queryAllMenuTreeByOperator(operator.getOperatorId());

			List<MenuItem> buttonList = Lists.newLinkedList();
			for (Entry<Integer, List<MenuItem>> entry : menuTree.entrySet()) {
				List<MenuItem> menuList = entry.getValue();
				for (MenuItem eachMenu : menuList) {
					buttonList.add(eachMenu);
				}
			}

			return buttonList;
		} catch (AdminServiceBaseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, ex);
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<Map<String, String>> getMerchants(UserInfo<Operator> op) {
		Integer operatorId = op.getUser().getOperatorId();
		List<Integer> roleIds = roleOperatorRepository.findRoleIdListByOperatorId(operatorId);
		List<Merchant> tempList = Lists.newLinkedList();
		// set values in session, only request one time per session
		String merchantList = "merchantList";
		List<Merchant> merchants = ShiroUtils.getSessionValue(op.getToken(), merchantList);

		if (merchants == null || merchants.isEmpty()) {
			merchants = merchantServiceBean.checkAdmMerchantList();
			ShiroUtils.setSessionValue(op.getToken(), merchantList, merchants);
		}
		// if role is super admin, is will return all merchant list, else query
		// merchantList in the mapping table by operatorId
		if (roleIds.contains(SUPER_MANAGER_ROLE_ID)) {
			tempList = merchants;
		} else {
			List<Integer> merchantIds = merchantOperatorRepository.findMerchantIdListByOperatorId(operatorId);
			for (Merchant merchant : merchants) {
				for (Integer merchantId : merchantIds) {
					if (merchant.getMerchantId().equals(merchantId)) {
						tempList.add(merchant);
					}
				}
			}
		}

		return generateMerchantCodeList(tempList);
	}

	private List<Map<String, String>> generateMerchantCodeList(List<Merchant> tempList) {
		List<Map<String, String>> merchantCodeList = Lists.newLinkedList();
		for (Merchant merchant : tempList) {
			Map<String, String> merchantTO = Maps.newHashMap();
			if (merchant.getStatus() == 1 && !"9".equals(merchant.getMerchantType())) {
				merchantTO.put("merchantId", merchant.getMerchantId().toString());
				merchantTO.put("merchantCode", merchant.getMerchantCode());
				merchantTO.put("merchantName", merchant.getMerchantName());
				merchantTO.put("merchantCurrency", merchant.getCurrency());
				merchantTO.put("merchantType", merchant.getMerchantType());
				merchantCodeList.add(merchantTO);
			}
		}
		return merchantCodeList;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<Map<String, String>> getCompanies(UserInfo<Operator> op) {
		Integer operatorId = op.getUser().getOperatorId();
		List<Integer> roleIds = roleOperatorRepository.findRoleIdListByOperatorId(operatorId);

		// set values in session, only request one time per session
		String merchantList = "merchantList";
		List<Merchant> merchants = ShiroUtils.getSessionValue(op.getToken(), merchantList);

		if (merchants == null || merchants.isEmpty()) {
			merchants = merchantServiceBean.checkAdmMerchantList();
			ShiroUtils.setSessionValue(op.getToken(), merchantList, merchants);
		}
		// if role is super admin, is will return all merchant list, else query
		// merchantList in the mapping table by operatorId
		List<Merchant> tempList = Lists.newLinkedList();
		if (roleIds.contains(SUPER_MANAGER_ROLE_ID)) {
			tempList = merchants;
		} else {
			List<Integer> merchantIds = merchantOperatorRepository.findMerchantIdListByOperatorId(operatorId);
			for (Merchant merchant : merchants) {
				for (Integer merchantId : merchantIds) {
					if (merchant.getMerchantId().equals(merchantId)) {
						tempList.add(merchant);
					}
				}
			}
		}

		return generateMerchantCodeList(tempList);
	}

	private List<MenuItem> getMenuItemList(List<Integer> menuIdList) {
		List<MenuItem> menuItemList = Lists.newLinkedList();
		for (Integer menuId : menuIdList) {
			MenuItem menu = menuCacher.getMenuMap().get(menuId);
			if (menu != null) {
				menuItemList.add(menuCacher.getMenuMap().get(menuId));
			}
		}

		return menuItemList;
	}

	/**
	 * 組成此目錄下面的選單清單
	 * 
	 * @param userInfo
	 *
	 * @param menuId                  目錄的menuId
	 * @param menuTree
	 * @param menuItemsOfBaseMerchant
	 * @param isAdmin 
	 *
	 * @return
	 */
	private List<JsMenuNode> setSubJsMenuList(UserInfo<Operator> userInfo, Integer menuId,
			Map<Integer, List<MenuItem>> menuTree, List<Integer> menuItemsOfBaseMerchant, boolean isAdmin) {
		if (menuTree.get(menuId) == null || menuTree.get(menuId).isEmpty()) {
			return Lists.newLinkedList();
		}

		List<JsMenuNode> subJsMenuList = Lists.newLinkedList();
		for (MenuItem subMenu : menuTree.get(menuId)) {
			if (!isMenuType(subMenu)) {
				continue;
			}
			JsMenuNode subNode = generatrSubNode(subMenu);
			// 如果還有下層, 把下層選單清單組出來 //zero will always fall here
			if (subMenu.getIsLeaf().equals(0)) {
				subNode.setChildren(
						setSubJsMenuList(userInfo, subMenu.getMenuId(), menuTree, menuItemsOfBaseMerchant, isAdmin));
			}
			/*
			 * Will add only if root has a children || the submenu of root that has
			 * permission and in the base merchant menu id's
			 */
			if ((subMenu.getParentId() == 0 && CollectionUtils.isNotEmpty(subNode.getChildren()))
					|| (subMenu.getParentId() != 0 && (menuItemsOfBaseMerchant.contains(subMenu.getMenuId())
					|| CollectionUtils.isEmpty(menuItemsOfBaseMerchant)))
					|| isAdmin) {
				subJsMenuList.add(subNode);
			}
		
		}

		return subJsMenuList;
	}

	private boolean isMenuType(MenuItem subMenu) {
		return subMenu.getIsDisplay().intValue() > 0 && ((StringTools.equals(subMenu.getAccessType(), "0"))
				|| (StringTools.equals(subMenu.getAccessType(), "9")));
	}

	private JsMenuNode generatrSubNode(MenuItem subMenu) {
		JsMenuNode subNode = new JsMenuNode();
		subNode.setId(subMenu.getUrl() == null ? null : subMenu.getUrl().replaceAll("/", "_"));
		subNode.setLabel(subMenu.getDescription());
		subNode.setUrl(subMenu.getUrl());
		subNode.setIcon(subMenu.getIcon());
		subNode.setMenuId(subMenu.getMenuId());
		if (subMenu.getLabels() != null) {
			Map<String, String> localLabel = Maps.newHashMap();
			for (Map.Entry<String, ApiLabel> i18nLabel : subMenu.getLabels().entrySet()) {
				localLabel.put(i18nLabel.getKey(), i18nLabel.getValue().getLabel());
			}
			subNode.setI18nLabel(localLabel);
		}
		return subNode;
	}

	private MenuItem checkParentAndSetTreeLevel(MenuItem menuItem) {
		if (menuItem.getParentId() != null && menuItem.getParentId().intValue() > 0) {
			MenuItem parentMenuItem = menuCacher.getMenuMap().get(menuItem.getParentId());
			if (parentMenuItem == null) {
				throw new AdminServiceBaseException(AdminErrorCode.MENU_PARENT_NOT_EXIST_ERROR,
						"parent menu is not existed");
			}
			menuItem.setTreeLevel(parentMenuItem.getTreeLevel() + 1);
		} else {
			menuItem.setTreeLevel(1);
			menuItem.setParentId(0);
		}
		return menuItem;
	}

	@Override
	public List<TreeTo> findPermissionOfOperator() {
		UserInfo<Operator> userInfo = operatorAuthenticationService.getOperatorByToken(RequestHelper.getToken());
		return findPermissionOfOperator(userInfo.getUser().getOperatorId(), userInfo);
	}

	@Override
	@Cacheable(value = "cache-ap-admin", key = "'findPermissionOfOperator:' + #opreatorId")
	public List<TreeTo> findPermissionOfOperator(Integer opreatorId, UserInfo<Operator> userInfo) {
		Map<Integer, List<MenuItem>> result = roleMenuPermissionService
				.queryAllMenuTreeByOperator(userInfo.getUser().getOperatorId());

		List<Integer> menuItemsOfBaseMerchant = this.menuIdsByBaseMerchant(userInfo.getUser());
		boolean isAdmin = this.isAdmin(userInfo.getUser().getOperatorId());

		List<TreeTo> finList = Lists.newLinkedList();
		// 取得第一階的父Tree
		List<MenuItem> firstParentTree = result.get(0);
		try {
			for (MenuItem firstMenuItem : firstParentTree) {
				TreeTo cloneTree = new TreeTo();
				BeanUtils.copyProperties(cloneTree, firstMenuItem);
				finList.add(cloneTree);
				
				// 代表有子節點
				List<MenuItem> childList = result.get(firstMenuItem.getMenuId());
				if (!CollectionUtils.isEmpty(childList)) {
					cloneTree.setList(
							this.returnChildListWithBaseMerchant(result, childList, menuItemsOfBaseMerchant, isAdmin));
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error("findPermissionOfOperator fail", e);
		}

		return finList;
	}

	private List<TreeTo> returnChildListWithBaseMerchant(Map<Integer, List<MenuItem>> result, List<MenuItem> childList,
			List<Integer> menuItemsOfBaseMerchant, boolean isAdmin)
			throws InvocationTargetException, IllegalAccessException {

		List<TreeTo> menuItemLists = Lists.newLinkedList();
		for (int i = 0; i < childList.size(); i++) {
			MenuItem menuItem = childList.get(i);
			Integer menuId = menuItem.getMenuId();

			/* This is the filter of permissions in base merchant menu ids */
			if ((menuItemsOfBaseMerchant.contains(menuId) || CollectionUtils.isEmpty(menuItemsOfBaseMerchant))
					|| isAdmin) {
				// 代表有子節點
				List<MenuItem> subList = result.get(menuId);
				TreeTo cloneTree = new TreeTo();
				copyProperties(cloneTree, menuItem);
				if (subList != null && !subList.isEmpty()) {
					cloneTree.setList(
							this.returnChildListWithBaseMerchant(result, subList, menuItemsOfBaseMerchant, isAdmin));
				}

				menuItemLists.add(cloneTree);
			}

		}

		return menuItemLists;
	}

	private boolean isAdmin(Integer operatorId) {
		return roleOperatorCustomRepository.findRoleIdListByOperatorId(operatorId).contains(SUPER_MANAGER_ROLE_ID);
	}

	@Override
	public List<Integer> menuIdsByBaseMerchant(Operator op) {
		String baseMerchant = op.getBaseMerchantCode(); // FIND BASE MERCHANT OF USER
		if (baseMerchant == null) {
			return Collections.emptyList();
		}
		/* Collect all the service type of merchants */
		List<String> categoryName = merchantMenuCategoryRepo.findMerchantCategory(baseMerchant);

		if (CollectionUtils.isEmpty(categoryName)) {
			return Collections.emptyList();
		}

		return menuCategoryCustomRepo.menuIdsByBaseMerchant(baseMerchant);
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<Map<String, String>> getAllMerchant() {

		List<Map<String, String>> merchantCodeList = Lists.newLinkedList();

		List<Merchant> merchants = merchantServiceBean.checkAdmMerchantList();

		for (Merchant merchant : merchants) {
			if (merchant.getStatus() == 1 && !"9".equalsIgnoreCase(merchant.getMerchantType())) {
				merchantCodeList.add(generageMapFromMerchant(merchant));
			}
		}
		return merchantCodeList;
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<Map<String, String>> getAllCompanies() {

		List<Map<String, String>> companies = Lists.newLinkedList();

		List<Merchant> merchants = merchantServiceBean.checkAdmMerchantList();

		for (Merchant merchant : merchants) {
			if (merchant.getStatus() == 1 && "9".equals(merchant.getMerchantType())) {
				companies.add(generageMapFromMerchant(merchant));
			}
		}
		return companies;
	}

	private Map<String, String> generageMapFromMerchant(Merchant merchant) {
		Map<String, String> resultMap = Maps.newHashMap();
		resultMap.put("merchantId", merchant.getMerchantId().toString());
		resultMap.put("merchantCode", merchant.getMerchantCode());
		resultMap.put("merchantName", merchant.getMerchantName());
		resultMap.put("merchantCurrency", merchant.getCurrency());
		resultMap.put("merchantType", merchant.getMerchantType());
		return resultMap;
	}

	@Override
	public void createAllCategoryRelationFromMerchant(String merchantCode) {

		List<MenuCategory> menuCategories = menuCategoryRepository.findByCategoryNameNot("SYSTEM");

		List<MerchantMenuCategory> merchantMenuCategories = Lists.newLinkedList();

		for (MenuCategory menuCategory : menuCategories) {
			MerchantMenuCategory merchantMenuCategory = new MerchantMenuCategory();
			merchantMenuCategory.setKey(new MerchantMenuCategorKey(merchantCode, menuCategory.getCategoryName()));
			merchantMenuCategories.add(merchantMenuCategory);
		}

		merchantMenuCategoryRepo.save(merchantMenuCategories);
	}

	@Override
	@Cacheable(value = "findByMenuIdPermission", key = "#menuId")
	public List<OperatorInfoTo> findByMenuIdPermission(Integer menuId) {
		List<Object[]> operators = operatorRepository.findByMenuIdPermission(menuId);
		List<OperatorInfoTo> results = Lists.newLinkedList();
		for (Object[] op : operators) {
			OperatorInfoTo to = new OperatorInfoTo();
			to.setOperatorId(((BigDecimal) op[0]).intValue());
			to.setOperatorName((String) op[1]);
			results.add(to);
		}
		return results;
	}

	@Override
	public List<TreeTo> getAllTreeTo() {
		Map<Integer, List<MenuItem>> result = roleMenuPermissionService.queryAllMenuTree();
        return getAllTreeTo(result);
	}

	@Override
	public List<TreeTo> getAllTreeTo(Integer operatorId) {
		Map<Integer, List<MenuItem>> result = roleMenuPermissionService.queryAllMenuTreeByOperator(operatorId);
		return getAllTreeTo(result);
	}
	
	private List<TreeTo> getAllTreeTo(Map<Integer, List<MenuItem>> result) {
		List<TreeTo> finList = Lists.newLinkedList();
        // 取得第一階的父Tree
        List<MenuItem> firstParentTree = result.get(0);
        try {
        	for (MenuItem firstMenuItem : firstParentTree) {
                TreeTo cloneTree = new TreeTo();
                BeanUtils.copyProperties(cloneTree, firstMenuItem);
                finList.add(cloneTree);
 
                // 代表有子節點
                List<MenuItem> childList = result.get(firstMenuItem.getMenuId());
                if (!CollectionUtils.isEmpty(childList)){
                    cloneTree.setList(this.returnChildList(result, childList));
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.error("getAllTreeTo error", e);
        }
        return finList;
	}

}
