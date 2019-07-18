package com.tcg.admin.controller;

import com.google.common.collect.Lists;
import com.tcg.admin.common.annotation.OperationLog;
import com.tcg.admin.common.constants.OperationFunctionConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.RoleCreateTO;
import com.tcg.admin.to.SortTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/resources/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleResource {

    @Autowired
	private RoleService roleService;
    @Autowired
	private OperatorLoginService operatorLoginService;

	@Autowired
	private RoleMenuPermissionService roleMenuPermissionService;

	/**
	 * Create a user role, for role management.
	 * 
	 * 取消管理員-角色關聯
	 */
    @DeleteMapping("/operator/relations/{operatorId}")
	@OperationLog(type = OperationFunctionConstant.MODIFY_ROLE)
	public JsonResponse uncorrelateRoles(@PathVariable("operatorId") Integer operatorId,
			@RequestParam(value = "roles", required = false) String roles)
			{
		JsonResponse response = new JsonResponse(true);
		String[] rolesStrArr = roles.split(",");
		List<Integer> roleIdList = Lists.newLinkedList();
		for (String roleId : rolesStrArr) {
			if (StringUtils.isNumeric(roleId)) {
				roleIdList.add(Integer.valueOf(roleId));
			} else {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "roleId not exists");
			}
		}
		roleService.unCorrelateRoles(operatorId, roleIdList);
		return response;
	}

	/**
	 * Create a user role, for role management.
	 * 
	 * 建立角色
	 */
	@PostMapping("/_createRole")
	public JsonResponseT<Role> createRole(@RequestBody RoleCreateTO roleTo) {
		JsonResponseT<Role> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String operator = userInfo.getUser().getOperatorId().toString();
		Role roleCreated = roleService.createRole(roleTo, operator);
		response.setValue(roleCreated);
		return response;
	}

	/**
	 * Update a user role, for role management.
	 *
	 * 修改角色
	 */
	@PutMapping("/_updateRole")
	public JsonResponse updateRole(@RequestBody RoleCreateTO roleTo) {
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String updateOperator = userInfo.getUser().getOperatorId().toString();
		roleService.updateRole(roleTo.getCategoryId(), roleTo, updateOperator);
		return new JsonResponse(true);
	}

	@PostMapping("/_viewCopyRole")
	public JsonResponse viewCopyRole(@RequestBody RoleCreateTO roleCopyTO){
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String operator = userInfo.getUser().getOperatorId().toString();
		Role roleCreated = roleService.copyRoleAndConnect(roleCopyTO, operator);
		if(roleCreated != null){
			return  new JsonResponse(true);
		}else {
			return  new JsonResponse(false);
		}
	}

	/**
	 * Delete a user role, for role management.
	 *
	 * 刪除角色
	 */
	@DeleteMapping("/{roleId}")
	public JsonResponse roleToggleActiveFlag(@PathVariable("roleId") Integer roleId) {
		roleService.toggleActiveFlag(roleId);

		return new JsonResponse(true);
	}

	/**
	 * Copy a user role, for role management.
	 *
	 * 複製角色
	 */
	@PutMapping("/copy")
	public JsonResponseT<Role> copyRole(@RequestParam(value = "originRoleName", required = false) String originRoleName,
	        @RequestParam(value = "newRoleName", required = false) String newRoleName, 
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "displayOrder", required = false) Integer displayOrder) {
		JsonResponseT<Role> response = new JsonResponseT<>(true);
		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
		String updateOperator = userInfo.getUser().getOperatorId().toString();
		Role roleCopied = roleService.copyRole(originRoleName, newRoleName, description, displayOrder,updateOperator);
		response.setValue(roleCopied);
		return response;
	}

	/**
	 * Query all roles, for role management.
	 *
	 * 查詢所有角色
	 */
	@GetMapping("/all/{pageNumber}/{pageSize}")
	public JsonResponseT<Object> queryAllRoles(
	        @PathVariable("pageNumber") Integer pageNumber, 
	        @PathVariable("pageSize") Integer pageSize,
	        @RequestParam(value = "activeFlag", required = false) Integer activeFlag) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Page<Role> queryResult = roleService.queryAllRoles(pageNumber, pageSize, activeFlag);
		response.setValue(queryResult);
		return response;
	}

	/**
	 * Query a operator's role, for role management.
	 *
	 * 查詢operator擁有角色權限
	 */
	@GetMapping("/operator/{pageNumber}/{pageSize}")
	@OperationLog(type = OperationFunctionConstant.WATCH_PERMISSIONS)
	public JsonResponseT<Object> queryOperatorRoles(
	        @RequestParam("operatorName") String operatorName,
	        @PathVariable("pageNumber") Integer pageNumber, 
	        @PathVariable("pageSize") Integer pageSize)
			{
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Page<Role> queryResult = roleService.queryOperatorRoles(operatorName, pageNumber, pageSize);
		response.setValue(queryResult);
		return response;
	}

	/**
	 * Query the role is assign to which operator, for role management.
	 *
	 * 查詢擁有該角色權限的operator
	 */
	@GetMapping(value = {"/operators/{pageNumber}/{pageSize}", "/permissions/operators/{pageNumber}/{pageSize}"})
	public JsonResponseT<Object> queryOperatorsByRole(
	        @RequestParam(value = "roleName", required = false) String roleName,
	        @PathVariable("pageNumber") Integer pageNumber, 
	        @PathVariable("pageSize") Integer pageSize)
			{
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Page<Operator> operators = roleService.queryOperatorsByRole(roleName, pageNumber, pageSize);
		response.setValue(operators);
		return response;
	}

	@PostMapping("/permission/display/{roleId}")
	public JsonResponse updateDisplayPermissions(@PathVariable("roleId") Integer roleId,
	        @RequestParam(value = "menuIds", required = false) String menuIds) {
		JsonResponse response = new JsonResponse(true);
		roleService.updateDisplayPermissions(roleId, menuIds.split(","));
		return response;
	}

	@PostMapping("/permission/audit/{roleId}")
	public JsonResponse updateAuditPermissions(@PathVariable("roleId") Integer roleId, 
	        @RequestParam(value = "menuIds", required = false) String menuIds) {
		JsonResponse response = new JsonResponse(true);
		roleService.updateAuditPermissions(roleId, menuIds.split(","));
		return response;
	}
	
	@GetMapping("/allRoles/{pageNumber}/{pageSize}")
	public JsonResponseT<Object> queryAllRolesParams(
	        @PathVariable("pageNumber") Integer pageNumber, @PathVariable("pageSize") Integer pageSize,
	        @RequestParam(value = "activeFlag", required = false) Integer activeFlag, 
	        @RequestParam(value = "sortOrder", required = false) String sortOrder, 
	        @RequestParam(value = "sortColumn", required = false) String sortColumn,
	        @RequestParam(value = "roleName", required = false) String roleName, 
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "categoryId", required = false) Integer categoryId) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		SortTo sortTo = SortTo.of(sortColumn, sortOrder);
		Page<Role> queryResult = roleService.queryAllRolesWithParams(pageNumber, pageSize, activeFlag, sortTo, roleName, description, categoryId );
		response.setValue(queryResult);
		return response;
	}
	
	@GetMapping("/allRoles")
	public JsonResponseT<Object> queryAllRolesParams(
	        @RequestParam(value = "activeFlag", required = false) Integer activeFlag, 
	        @RequestParam(value = "sortOrder", required = false) String sortOrder, 
	        @RequestParam(value = "sortColumn", required = false) String sortColumn,
	        @RequestParam(value = "roleName", required = false) String roleName, 
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "categoryId", required = false) Integer categoryId,
	        @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
	        @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		SortTo sortTo = SortTo.of(sortColumn, sortOrder);
		Page<Role> queryResult = roleService.queryAllRolesWithParams(pageNumber, pageSize, activeFlag, sortTo, roleName, description, categoryId );
		response.setValue(queryResult);
		return response;
	}

}
