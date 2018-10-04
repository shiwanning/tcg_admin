package com.tcg.admin.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.Role;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.RoleCreateTO;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;

@RestController
@RequestMapping(value = "/resources/roles", produces = MediaType.APPLICATION_JSON_VALUE)
public class RoleResource {

    @Autowired
	private RoleService roleService;
    @Autowired
	private OperatorLoginService operatorLoginService;

	/**
	 * Create a user role, for role management.
	 * 
	 * 建立管理員-角色關聯
	 */
    @PutMapping("/operator/relations/{operatorId}")
	@Deprecated
	public JsonResponse correlateRoles(@PathVariable("operatorId") Integer operatorId,
			@RequestParam(value = "roles", required = false) String roles)
			{
		JsonResponse response = new JsonResponse(true);
		String[] rolesStrArr = roles.split(",");
		List<Integer> roleIdList = new ArrayList<>();
		for (String roleId : rolesStrArr) {
			if (StringUtils.isNumeric(roleId)) {
				roleIdList.add(Integer.valueOf(roleId));
			} else {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "roleId not exists");
			}
		}
		roleService.correlateRoles(operatorId, roleIdList);
		return response;
	}

	/**
	 * Create a user role, for role management.
	 * 
	 * 取消管理員-角色關聯
	 */
    @DeleteMapping("/operator/relations/{operatorId}")
	public JsonResponse uncorrelateRoles(@PathVariable("operatorId") Integer operatorId,
			@RequestParam(value = "roles", required = false) String roles)
			{
		JsonResponse response = new JsonResponse(true);
		String[] rolesStrArr = roles.split(",");
		List<Integer> roleIdList = new ArrayList<Integer>();
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
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
		String operator = userInfo.getUser().getOperatorId().toString();
		Role roleCreated = roleService.createRole(roleTo.getCategoryId(),roleTo.getRoleName(), roleTo.getDescription(),roleTo.getDisplayOrder(), operator);
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
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
		String updateOperator = userInfo.getUser().getOperatorId().toString();
		roleService.updateRole(roleTo.getCategoryId(), roleTo, updateOperator);
		return new JsonResponse(true);
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
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
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
	@GetMapping("/operators/{pageNumber}/{pageSize}")
	public JsonResponseT<Object> queryOperatorsByRole(
	        @RequestParam(value = "roleName", required = false) String roleName,
	        @PathVariable("pageNumber") Integer pageNumber, 
	        @PathVariable("pageSize") Integer pageSize)
			{
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Page<Operator> queryResult = roleService.queryOperatorsByRole(roleName, pageNumber, pageSize);
		response.setValue(queryResult);
		return response;
	}

	@GetMapping("/permissions/operators/{pageNumber}/{pageSize}")
	public JsonResponseT<Object> queryOperatorsByRoleOfPermission(
	        @RequestParam(value = "roleName", required = false) String roleName,
	        @PathVariable("pageNumber") Integer pageNumber, 
	        @PathVariable("pageSize") Integer pageSize)
			{
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Page<Operator> queryResult = roleService.queryOperatorsByRole(roleName, pageNumber, pageSize);
		response.setValue(queryResult);
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
		Page<Role> queryResult = roleService.queryAllRolesWithParams(pageNumber, pageSize, activeFlag,sortOrder, sortColumn, roleName, description, categoryId );
		response.setValue(queryResult);
		return response;
	}

}
