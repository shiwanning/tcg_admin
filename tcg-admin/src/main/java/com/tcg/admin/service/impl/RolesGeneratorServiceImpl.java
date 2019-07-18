package com.tcg.admin.service.impl;

import static ch.lambdaj.Lambda.by;
import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.group;
import static ch.lambdaj.Lambda.on;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.CategoryRole;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.model.RoleMenuPermission;
import com.tcg.admin.persistence.RoleCustomRepository;
import com.tcg.admin.persistence.springdata.ICategoryRoleRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.persistence.springdata.IRoleOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;
import com.tcg.admin.service.CommonMenuService;
import com.tcg.admin.service.MenuItemService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.RolesGeneratorService;
import com.tcg.admin.to.RoleGeneratorTO;
import com.tcg.admin.to.TreeTo;
import com.tcg.admin.to.UserInfo;
import com.yx.commons.utils.DateUtils;

import ch.lambdaj.group.Group;


@Service
@Transactional
public class RolesGeneratorServiceImpl implements RolesGeneratorService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RolesGeneratorServiceImpl.class);
	
	@Autowired
	private RoleMenuPermissionService roleMenuPermission;
	
	@Autowired
	private IRoleRepository roleRepository;
	
	@Autowired
	private IRoleMenuPermissionRepository roleMenuRepository;
	
	@Autowired
	private CommonMenuService commonMenuService;
	
	@Autowired 
	private OperatorLoginService operatorLoginService;
	
	@Autowired
	private IRoleOperatorRepository roleOperatorRepository;
	
	@Autowired
	private ICategoryRoleRepository categoryRoleRepository;
	
	@Autowired
	private RoleCustomRepository roleCustomRepository;
	
	@Autowired
	private MenuItemService menuItemService;
	
	private static final String PREFIX = "roleId_";
	private static final String MENU_NAME = "menuName";
	private static final String MENU_ID = "menuId";
	private static final String TREE_LEVEL = "treeLevel";
	private static final String PARENT_ID = "parentId";
	private static final String HAS_CHILDREN = "hasChildren";
	private static final String SHOW = "show";
	private static final String ICON = "icon";
	private static final String HAS_PERMISSION = "hasPermission";
	private static final String CHILD_EXPAND = "childrenExpand";
	
	private static final String ROLE_ID = "roleId";
	private static final String ROLE_NAME = "roleName";
	
	/**
	 * @Description: Build the response transfer object
	 * */
	@Override
	public RoleGeneratorTO buildPermission(List<Integer> rolesId) {
		RoleGeneratorTO response = new RoleGeneratorTO();
		response.setPermissions(this.generatePermissionByRoles(rolesId));
		response.setGenericVariables(this.createGenericVariables(rolesId));
		return response;
	}

	/**
	 * @Description: get the permission by provided roles from UI
	 * */
	private List<Map<String, Object>> generatePermissionByRoles(List<Integer> rolesIds) {
		List<Map<String, Object>> responseValue = Lists.newLinkedList();
		
		LOGGER.info("1: " + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		
		/*Build the generic response with all the permission is set to FALSE*/
		Map<String, Object> defaultMap = this.defaultMap(rolesIds);
		
		//TODO: if system should get all menuId
		/*Fetch all the permissions of each roles and create a group of menu ID to see what is the roles under by each menu ID*/
		List<RoleMenuPermission> permissions = roleMenuRepository.findByRoleIdsWithoutSystem(rolesIds);
		
		if(rolesIds.contains(1)) {
			permissions.addAll(commonMenuService.getAdminRoleMenuPermission());
		}
		
		Group<RoleMenuPermission> groupByMenuId = group(permissions, by(on(RoleMenuPermission.class).getMenuId()));
		
		LOGGER.info("2: " + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		/*Fetch the operator permissions and exist in base merchant menu*/
		List<TreeTo> operatorPermission = roleMenuPermission.findPermissionOfOperator();
		LOGGER.info("3: " + DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
		if(CollectionUtils.isEmpty(operatorPermission)){
			throw new AdminServiceBaseException(AdminErrorCode.ROLES_NOT_EXIST_ERROR,
					"Service type has empty pemission");
		}
		
		/*time to assign which role has a permission in each menu*/
		return this.generateGenericVariable(responseValue, operatorPermission, groupByMenuId, defaultMap);
	}
	
	/**
	 * @Description: Assign the roles in each menu
	 * */
	private List<Map<String, Object>> generateGenericVariable(List<Map<String, Object>> responseValue, List<TreeTo> operatorPermission, Group<RoleMenuPermission> groupByMenuId, Map<String, Object> defaultMap){
		if(CollectionUtils.isNotEmpty(operatorPermission)){
			for(TreeTo tree: operatorPermission){
				if(tree.getIsInGroup()) {
					continue;
				}
				/*Set up the base objects*/
				Map<String, Object> value = Maps.newHashMap();
				value.put(MENU_ID, tree.getMenuId());
				value.put(TREE_LEVEL, tree.getTreeLevel());
				value.put(PARENT_ID, tree.getParentId());
				value.put(HAS_CHILDREN, CollectionUtils.isNotEmpty(tree.getList()));
				value.put(SHOW, tree.getParentId() == 0);
				value.put(ICON, tree.getIcon());
				value.put(CHILD_EXPAND, false);
				
				/*Using the default map will do the changes*/
				 Map<String, Object> mapObject = this.findRoleIdsByMenuId(groupByMenuId, defaultMap, tree.getMenuId());
				value.putAll(mapObject);
				
				/*Add only the menu ID id has even one permission in roles parameter*/
				if(!mapObject.isEmpty()){
					responseValue.add(value);
				}
				
				if(CollectionUtils.isNotEmpty(tree.getList())){
					this.generateGenericVariable(responseValue, tree.getList(), groupByMenuId, defaultMap);
				}
			}
		}
		return responseValue;
	}
	
	/**
	 * @Description: Will search the menu ID in group and get all the roles that has a permission to menu ID
	 * */
	private Map<String, Object> findRoleIdsByMenuId(Group<RoleMenuPermission> groupByMenuId, Map<String, Object> defaultMap, Integer menuId){
		
		Map<String, Object> clonedMap = Maps.newHashMap();
		/* Search in sub group to get the roles that has a permission */
		List<Integer> rolesWithPermissionInMenuId = extract(groupByMenuId.find(menuId), on(RoleMenuPermission.class).getRoleId()); 
		if(CollectionUtils.isEmpty(rolesWithPermissionInMenuId)){
			return Collections.emptyMap();
		}
		
		/* Use all roles to replace the map with the has permission is to true */
		if(CollectionUtils.isNotEmpty(rolesWithPermissionInMenuId)){
			for(Integer role : rolesWithPermissionInMenuId){
				Map<String, Object> defVal = Maps.newLinkedHashMap();
				defVal.put(HAS_PERMISSION, true);
				clonedMap.put(PREFIX+role, true);
			}
		}
		return clonedMap;
		
	}
	
	/**
	 * @Description: use to create what roles is included in list
	 * */
	private List<Map<String, Object>> createGenericVariables(List<Integer> rolesIds){
		List<Map<String, Object>> genericVars = Lists.newLinkedList();
		 List<Role> roles = roleRepository.findByRoleIds(rolesIds);
		

		if(CollectionUtils.isEmpty(roles)){
			throw new AdminServiceBaseException(AdminErrorCode.ROLES_NOT_EXIST_ERROR,
					"Roles not exist");
		}
		
		for(Role role : roles){
			Map<String, Object> builder = Maps.newHashMap();
			builder.put(ROLE_ID, PREFIX+role.getRoleId());
			builder.put(ROLE_NAME, role.getRoleName());
			genericVars.add(builder);
		}
		
		return genericVars;
		
	}
	
	/**
	 * @Description: Initialize the permission to FALSE
	 * */
	private Map<String, Object> defaultMap(List<Integer> rolesIds){
		Map<String, Object> defaultMap = Maps.newHashMap();
		
		for(Integer role: rolesIds){
			defaultMap.put(PREFIX+role, this.hasPermissionDefault(false));
		}
		
		return defaultMap;
	}
	
	/**
	 * @Description: Fetch all roles with in the category roles of user
	 * */
	@Override
	public List<Role> getRolesByUserCategory(){
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		Integer operatorId = userInfo.getUser().getOperatorId();
		
		List<Integer> operatorRoles = roleOperatorRepository.findRoleIdListByOperatorId(operatorId);
		
		Group<CategoryRole> groupByCategoryId = group(categoryRoleRepository.findCategoryRoleByRoleId(operatorRoles), by(on(CategoryRole.class).getCategoryId()));
		
		Set<String> keys = groupByCategoryId.keySet();
		List<Integer> categoryIds = Lists.newLinkedList();
		for(String key : keys){
			categoryIds.add(Integer.valueOf(key));
		}
		
		return roleCustomRepository.getRolesByCategoryId(categoryIds);
		
		
	}
	
	private Map<String, Object> hasPermissionDefault(boolean val){
		Map<String, Object> defVal = Maps.newHashMap();
		defVal.put(HAS_PERMISSION, val);
		
		return defVal;
	}
	
	/**
	 * @description Will create new reference for object/Deep clone :
	 * @returns new instance and reference of Object
	 */
	  private <K, V> Map<K,V> deepCopy(Map<K,V> oldObj,  Type type) {
		  ObjectMapper objectMapper = new ObjectMapper();
		  Gson gson = new Gson();
		  /*GET THE TYPE OF CLASS*/
          String json = "";
		try {
			/*CONVERT THE LIST OBJECT TO JSON STRING*/
			json = objectMapper.writeValueAsString(oldObj);
		} catch (IOException e) {
			LOGGER.error("Deep copy error", e);
		}
		/*CONVERT BACK FROM JSON STRING TO ACTUAL LIST OBJECT*/
          return  gson.fromJson(json, type);

	  }

}
