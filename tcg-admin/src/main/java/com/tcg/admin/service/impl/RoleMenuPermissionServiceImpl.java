package com.tcg.admin.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.Sets;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
import com.tcg.admin.cache.MerchantCacheable;
import com.tcg.admin.cache.RedisCacheEvict;
import com.tcg.admin.cache.RedisCacheable;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.RoleIdConstant;
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
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.specifications.RoleSpecifications;
import com.tcg.admin.to.QueryRoleTO;
import com.tcg.admin.to.TreeTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.OperatorInfoTo;
import com.tcg.admin.utils.NullAwareBeanUtilsBean;
import com.tcg.admin.utils.StringTools;

import ch.lambdaj.Lambda;

@Service
@Transactional
public class RoleMenuPermissionServiceImpl implements RoleMenuPermissionService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RoleMenuPermissionServiceImpl.class);

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
	@RedisCacheEvict(allEntries = true)
	public void correlatePermission(Integer roleId, List<Integer> menuIdList) {
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
	}

	@Override
	public void unCorrelatePermission(Integer roleId, List<Integer> menuIdList) {
        roleMenuRepository.deleteRoleIdAndMenuIdListByRoleId(roleId);
        deleteRoleIdAndMenuIdList(roleId, menuIdList);
	}
	
	private void deleteRoleIdAndMenuIdList(Integer roleId, List<Integer> menuIdList) {
		for(List<Integer> partition : Lists.partition(menuIdList, 850)) {
			roleMenuRepository.deleteRoleIdAndMenuIdList(roleId, partition);
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
		} catch (IllegalAccessException | InvocationTargetException ex) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	@Override
	public Map<Integer, List<MenuItem>> queryAllMenuTree() {
		try {
			return menuCacher.getWholeMenuTree();
		} catch (Exception ex) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	@Override
	@RedisCacheable(key = "'queryAllMenuTreeByOperator:' + #operatorId")
	public Map<String, List<MenuItem>> queryAllMenuTreeByOperator(Integer operatorId) {
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
		return generateOperatorMenuTree(roleIdList.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID), menuIdList);
	}

	private Map<String, List<MenuItem>> generateOperatorMenuTree(boolean isAdmin, Set<Integer> menuIdList) {
		Map<Integer, List<MenuItem>> allMenuTree = this.queryAllMenuTree();

		Map<String, List<MenuItem>> operatorMenuTree = Maps.newHashMap();
		for (Entry<Integer, List<MenuItem>> entry : allMenuTree.entrySet()) {

			Integer menuId = entry.getKey();
			List<MenuItem> menus = entry.getValue();

			// 該role權限包含此層節點才需要判斷是否要加入map...............
			List<MenuItem> eachMenuList = null;
			if (isAdmin) {
				eachMenuList = Lists.newLinkedList(menus);
			} else if (menuIdList.contains(menuId)) {
				// 取交集
				eachMenuList = intersectionMenu(menuIdList, menus);
			}
			if (eachMenuList != null && !eachMenuList.isEmpty()) {
				operatorMenuTree.put(menuId.toString(), eachMenuList);
			}
		}
		return operatorMenuTree;
	}

	private List<MenuItem> intersectionMenu(Set<Integer> menuIdList, List<MenuItem> menus) {
		List<MenuItem> eachMenuList = Lists.newLinkedList();
		for (MenuItem subMenu : menus) {
			if (menuIdList.contains(subMenu.getMenuId())) {
				eachMenuList.add(subMenu);
			}
		}
		return eachMenuList;
	}

	@Override
	public List<TreeTo> queryAllMenuTreeByMenuCategoryName(List<String> menuCategoryNameList) {
		Set<Integer> menuIdList = menuCategoryMenuRepository.findMenuIdByMenuCategoryName(menuCategoryNameList);
		menuIdList.add(0);// 加入最頂層目錄

		List<Integer> topTreeNode = menuRepository.findTopTreeNode();

		if("SYSTEM".equalsIgnoreCase(menuCategoryNameList.get(0))){
           menuIdList.addAll(topTreeNode);
		}
		Map<Integer, List<MenuItem>> allMenuTree = this.queryAllMenuTree();
		Map<String, List<MenuItem>> result = Maps.newHashMap();
		for (Entry<Integer, List<MenuItem>> menuEntry : allMenuTree.entrySet()) {
			Integer menuId = menuEntry.getKey();
			List<MenuItem> menus = menuEntry.getValue();
			putEachMenuList(result, menuIdList, menuId, menus);
		}

		// 取得第一階的父Tree
		List<MenuItem> firstParentTree = Lists.newLinkedList();

		if (!result.isEmpty()) {
			firstParentTree = getFirstParamTree(result);
		}

		return grenateTreeTo(firstParentTree, result);

	}

	private void putEachMenuList(Map<String, List<MenuItem>> result, Set<Integer> menuIdList, Integer menuId, List<MenuItem> menus) {
		if (menuIdList.contains(menuId)) {
			List<MenuItem> eachMenuList = Lists.newLinkedList();
			for (MenuItem subMenu : menus) {
				if (menuIdList.contains(subMenu.getMenuId())) {
					eachMenuList.add(subMenu);
				}
			}
			// 此層目錄向下有直屬節點才加入map中......................
			if (!eachMenuList.isEmpty()) {
				result.put(String.valueOf(menuId), eachMenuList);
			}
		}
	}

	private List<TreeTo> grenateTreeTo(List<MenuItem> firstParentTree, Map<String, List<MenuItem>> result) {
		List<TreeTo> finList = Lists.newLinkedList();
		for (MenuItem firstMenuItem : firstParentTree) {
			TreeTo cloneTree = new TreeTo();
			copyProperties(cloneTree, firstMenuItem);
			Integer menuId = firstMenuItem.getMenuId();

			// 代表有子節點
			List<MenuItem> childList = result.get(String.valueOf(menuId));
			if (childList != null && !childList.isEmpty()) {
				cloneTree.setList(this.returnChildList(result, childList));
			}
			finList.add(cloneTree);
		}
		return finList;
	}

	private List<TreeTo> returnChildList(Map<? extends Object, List<MenuItem>> result, List<MenuItem> childList) {
		List<TreeTo> menuItemLists = Lists.newLinkedList();
		for (int i = 0; i < childList.size(); i++) {
			MenuItem menuItem = childList.get(i);
			Integer menuId = menuItem.getMenuId();

			// 代表有子節點
			List<MenuItem> subList = result.get(String.valueOf(menuId));
			if(subList == null) {
				subList = result.get(menuId);
			}
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
		return menuId == null ? null : menuCacher.getMenuMap().get(menuId);
	}

	@Override
	public List<MenuItem> queryMenuItemsByRole(Integer roleId) {
		List<Integer> roleIdList = Lists.newLinkedList();
		roleIdList.add(roleId);

		Set<Integer> menuIdList = findMenuIdListByRoleIdList(roleIdList);

		List<MenuItem> menuItemList = Lists.newLinkedList();
		for (Integer menuId : menuIdList) {
			MenuItem menu = menuCacher.getMenuMap().get(menuId);
			if (menu != null) {
				menuItemList.add(menuCacher.getMenuMap().get(menuId));
			}
		}

		return menuItemList;
	}

	private Set<Integer> findMenuIdListByRoleIdList(List<Integer> roleIdList) {
		Set<Integer> menuIdList = Sets.newHashSet();
		for(List<Integer> partition : Lists.partition(roleIdList, 999)) {
			menuIdList.addAll(roleMenuRepository.findMenuIdListByRoleIdList(partition));
		}
		return menuIdList;
	}

	@Override
	public List<MenuItem> queryMenuItemsByOperatorId(Integer operatorId) {
		List<Integer> menuIdList = roleMenuRepository.findMenuIdListByOperator(operatorId);
		return this.getMenuItemList(menuIdList);
	}

	@Override
	@RedisCacheable(key = "'verifyMenuItemPermission:' + #menuItem.menuId + ':' + #operatorId")
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

		if(roleIdList.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID)) {
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

		return roleRepository.findAll(role, request);
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public JsTreeMenu getOperatorJsMenuTree(Operator operator) {
		Map<String, List<MenuItem>> menuTree = this.queryAllMenuTreeByOperator(operator.getOperatorId());

		JsTreeMenu treeMenu = new JsTreeMenu();
		List<JsMenuNode> operatorMenuTree = Lists.newLinkedList();
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		List<Integer> menuItemsOfBaseMerchant = this.menuIdsByBaseMerchant(userInfo.getUser());
		boolean isAdmin = this.isAdmin(userInfo.getUser().getOperatorId());
		List<JsMenuNode> categoryList = setSubJsMenuList(userInfo, "0", menuTree, menuItemsOfBaseMerchant, isAdmin);
		operatorMenuTree.addAll(categoryList);
		treeMenu.setTreeMenu(operatorMenuTree);

		return treeMenu;
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
	public List<MenuItem> getAllButtonListForService(String operatorName) {
		// 先確認operator存在
		Operator operator = operatorRepository.findByOperatorName(operatorName);
		if (operator == null) {
			throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "operator not exist");
		}

		// 由cache取得選單並且過濾僅保留按鈕權限
		Map<String, List<MenuItem>> menuTree = this.queryAllMenuTreeByOperator(operator.getOperatorId());

		List<MenuItem> buttonList = Lists.newLinkedList();
		for (Entry<String, List<MenuItem>> entry : menuTree.entrySet()) {
			List<MenuItem> menuList = entry.getValue();
			for (MenuItem eachMenu : menuList) {
				buttonList.add(eachMenu);
			}
		}

		return buttonList;
	}
	@Override
	public List<Integer> getAllButtonList(String operatorName) {
		// 先確認operator存在

		Operator operator = operatorRepository.findByOperatorName(operatorName);
		if (operator == null) {
			throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "operator not exist");
		}
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();

		// 由cache取得選單並且過濾僅保留按鈕權限
		Map<String, List<MenuItem>> menuTree = this.queryAllMenuTreeByOperator(operator.getOperatorId());

		List<Integer> menuItemsOfBaseMerchant = this.menuIdsByBaseMerchant(userInfo.getUser());


		boolean isAdmin = this.isAdmin(userInfo.getUser().getOperatorId());

		List<TreeTo> finList = Lists.newLinkedList();
		// 取得第一階的父Tree
		List<MenuItem> firstParentTree = getFirstParamTree(menuTree);
		try {
			for (MenuItem firstMenuItem : firstParentTree) {
				TreeTo cloneTree = new TreeTo();
				BeanUtils.copyProperties(cloneTree, firstMenuItem);
				finList.add(cloneTree);

				// 代表有子節點
				List<MenuItem> childList = menuTree.get(String.valueOf(firstMenuItem.getMenuId()));
				if (!CollectionUtils.isEmpty(childList)) {
					cloneTree.setList(
							this.returnChildListWithBaseMerchant2(menuTree, childList, menuItemsOfBaseMerchant, isAdmin));
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error("findPermissionOfOperator fail", e);
		}
		List<Integer> permissons = new ArrayList<Integer>();

		for(TreeTo treeTo : finList){
			//添加最外层树Id
			permissons.add(treeTo.getMenuId());
			//遍历每一层下list
			this.getPermissions(permissons, treeTo.getList());
		}
		return permissons;
	}

	private void getPermissions(List<Integer> permissons, List<TreeTo> childList){
		if(!CollectionUtils.isEmpty(childList)){
			for(TreeTo treeChild : childList){
				permissons.add(treeChild.getMenuId());
				this.getPermissions(permissons, treeChild.getList());
			}
		}
	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	@MerchantCacheable(key="'getMerchants:' + #userInfo.user.operatorId")
	public List<Map<String, String>> getMerchants(UserInfo<Operator> userInfo) {
		return getMerchants(userInfo.getUser().getOperatorId());
	}
	
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	@MerchantCacheable(key="'getMerchants:' + #operatorId")
	public List<Map<String, String>> getMerchants(Integer operatorId) {
		List<Integer> roleIds = roleOperatorRepository.findRoleIdListByOperatorId(operatorId);
		List<Merchant> tempList = Lists.newLinkedList();
		// set values in session, only request one time per session
		List<Merchant> merchants = merchantServiceBean.checkAdmMerchantList();

		// if role is super admin, is will return all merchant list, else query
		// merchantList in the mapping table by operatorId
		if (roleIds.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID)) {
			tempList = merchants;
		} else {
			List<Integer> merchantIds = merchantOperatorRepository.findMerchantIdListByOperatorId(operatorId);
			
			for (Merchant merchant : merchants) {
				if(merchantIds.contains(merchant.getMerchantId())) {
					tempList.add(merchant);
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
		List<Merchant> merchants = merchantServiceBean.checkAdmMerchantList();

		// if role is super admin, is will return all merchant list, else query
		// merchantList in the mapping table by operatorId
		List<Merchant> tempList = Lists.newLinkedList();
		if (roleIds.contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID)) {
			tempList = merchants;
		} else {
			List<Integer> merchantIds = merchantOperatorRepository.findMerchantIdListByOperatorId(operatorId);
			for (Merchant merchant : merchants) {
				if(merchantIds.contains(merchant.getMerchantId())) {
					tempList.add(merchant);
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
	private List<JsMenuNode> setSubJsMenuList(UserInfo<Operator> userInfo, String menuId,
			Map<String, List<MenuItem>> menuTree, List<Integer> menuItemsOfBaseMerchant, boolean isAdmin) {
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
						setSubJsMenuList(userInfo, String.valueOf(subMenu.getMenuId()), menuTree, menuItemsOfBaseMerchant, isAdmin));
			}
			/*
			 * Will add only if root has a children || the submenu of root that has
			 * permission and in the base merchant menu id's
			 */
			if (isAdmin || isRootMenu(subMenu, subNode) || isMerchantMenu(subMenu, menuItemsOfBaseMerchant)) {
				subJsMenuList.add(subNode);
			}
		}

		return subJsMenuList;
	}

	private boolean isMerchantMenu(MenuItem subMenu, List<Integer> menuItemsOfBaseMerchant) {
		return subMenu.getParentId() != 0 && (menuItemsOfBaseMerchant.contains(subMenu.getMenuId())
				|| CollectionUtils.isEmpty(menuItemsOfBaseMerchant));
	}

	private boolean isRootMenu(MenuItem subMenu, JsMenuNode subNode) {
		return subMenu.getParentId() == 0 && CollectionUtils.isNotEmpty(subNode.getChildren());
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
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		return findPermissionOfOperator(userInfo.getUser().getOperatorId(), userInfo);
	}

	@Override
	//@RedisCacheable(key = "'findPermissionOfOperator:' + #opreatorId")
	public List<TreeTo> findPermissionOfOperator(Integer opreatorId, UserInfo<Operator> userInfo) {
		Map<String, List<MenuItem>> result = roleMenuPermissionService
				.queryAllMenuTreeByOperator(userInfo.getUser().getOperatorId());

		List<Integer> menuItemsOfBaseMerchant = this.menuIdsByBaseMerchant(userInfo.getUser());
		boolean isAdmin = this.isAdmin(userInfo.getUser().getOperatorId());

		List<TreeTo> finList = Lists.newLinkedList();
		// 取得第一階的父Tree
		List<MenuItem> firstParentTree = getFirstParamTree(result);
		try {
			for (MenuItem firstMenuItem : firstParentTree) {
				TreeTo cloneTree = new TreeTo();
				BeanUtils.copyProperties(cloneTree, firstMenuItem);
				finList.add(cloneTree);
				
				// 代表有子節點
				List<MenuItem> childList = result.get(String.valueOf(firstMenuItem.getMenuId()));
				if (!CollectionUtils.isEmpty(childList)) {
					cloneTree.setList(
							this.returnChildListWithBaseMerchant(cloneTree, result, childList, menuItemsOfBaseMerchant, isAdmin));
				}
			}
		} catch (InvocationTargetException | IllegalAccessException e) {
			LOGGER.error("findPermissionOfOperator fail", e);
		}

		return finList;
	}

	private List<TreeTo> returnChildListWithBaseMerchant(TreeTo parentMenu, Map<String, List<MenuItem>> result, List<MenuItem> childList,
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
				List<MenuItem> subList = result.get(String.valueOf(menuId));
				TreeTo cloneTree = new TreeTo();
				copyProperties(cloneTree, menuItem);
				cloneTree.getParents().addAll(parentMenu.getParents());
                cloneTree.getParents().add(parentMenu.getMenuId());
				if (subList != null && !subList.isEmpty()) {
					cloneTree.setList(
							this.returnChildListWithBaseMerchant(cloneTree, result, subList, menuItemsOfBaseMerchant, isAdmin));
				}

				menuItemLists.add(cloneTree);
			}

		}

		return menuItemLists;
	}

	private List<TreeTo> returnChildListWithBaseMerchant2(Map<String, List<MenuItem>> result, List<MenuItem> childList,
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
				List<MenuItem> subList = result.get(String.valueOf(menuId));
				TreeTo cloneTree = new TreeTo();
				copyProperties(cloneTree, menuItem);
				if (subList != null && !subList.isEmpty()) {
					cloneTree.setList(
							this.returnChildListWithBaseMerchant2(result, subList, menuItemsOfBaseMerchant, isAdmin));
				}

				menuItemLists.add(cloneTree);
			}

		}

		return menuItemLists;
	}

	private boolean isAdmin(Integer operatorId) {
		return roleOperatorCustomRepository.findRoleIdListByOperatorId(operatorId).contains(RoleIdConstant.SUPER_MANAGER_ROLE_ID);
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
	@RedisCacheable(key = "findByMenuIdPermission:#menuId")
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
	@RedisCacheable(key = "'getAllTreeTo'")
	public List<TreeTo> getAllTreeTo() {
		Map<Integer, List<MenuItem>> result = roleMenuPermissionService.queryAllMenuTree();
		return getAllTreeTo(result);
	}

	@Override
	public List<TreeTo> getAllTreeTo(Integer operatorId) {
		Map<String, List<MenuItem>> result = roleMenuPermissionService.queryAllMenuTreeByOperator(operatorId);
		return getAllTreeTo(result);
	}
	
	private List<TreeTo> getAllTreeTo(Map<? extends Object, List<MenuItem>> result) {
		List<TreeTo> finList = Lists.newLinkedList();
        // 取得第一階的父Tree
		
		List<MenuItem> firstParentTree = getFirstParamTree(result);
        try {
        	for (MenuItem firstMenuItem : firstParentTree) {
                TreeTo cloneTree = new TreeTo();
                BeanUtils.copyProperties(cloneTree, firstMenuItem);
                finList.add(cloneTree);
 
                // 代表有子節點
                List<MenuItem> childList = result.get(String.valueOf(firstMenuItem.getMenuId()));
                if(CollectionUtils.isEmpty(childList)) {
                	childList = result.get(firstMenuItem.getMenuId());
                }
                if (childList == null || !CollectionUtils.isEmpty(childList)){
                    cloneTree.setList(this.returnChildList(result, childList));
                }
            }
        } catch (Exception e) {
            LOGGER.error("getAllTreeTo error", e);
        }
        return finList;
	}

	private List<MenuItem> getFirstParamTree(Map<? extends Object, List<MenuItem>> result) {
		List<MenuItem> firstParentTree = result.get("0");
        if(firstParentTree == null) {
        	firstParentTree = result.get(0);
        }
		return firstParentTree;
	}

}
