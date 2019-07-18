package com.tcg.admin.service.impl;

import java.util.*;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.*;
import com.tcg.admin.service.*;
import com.tcg.admin.to.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.cache.RedisCacheEvict;
import com.tcg.admin.common.constants.RoleConstant;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.persistence.RoleOperatorCustomRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleMenuPermissionRepository;
import com.tcg.admin.persistence.springdata.IRoleOperatorRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;
import com.tcg.admin.service.specifications.OperatorSpecifications;
import com.tcg.admin.service.specifications.RoleSpecifications;
import com.tcg.admin.utils.StringTools;

import ch.lambdaj.Lambda;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleMenuPermissionService roleMenuPermissionService;
	
	@Autowired
	private BpmPermissionService bpmPermissionService;

	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private IOperatorRepository operatorRepository;

	@Autowired
	private IRoleOperatorRepository roleOperatorRepository;

	@Autowired
	private RoleOperatorCustomRepository roleOperatorCustomRepository;

	@Autowired
	private IRoleMenuPermissionRepository roleMenuPermissionRespository;

	@Autowired
	private CategoryRoleService categoryRoleService;

	@Autowired
	private MerchantService merchantService;


	@Autowired
	private AuthService authService;
	
	private static final Integer SUPER_MANAGER_ROLE_ID = 1;
	
	/**
	 * Assign Role
	 * 配置角色
	 * @param operatorId
	 * @param roleList
	 * @throws AdminServiceBaseException
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	@RedisCacheEvict(allEntries = true)
	public void assignRoles(Integer operatorId, List<Integer> roleList) {
		try {
            /* operator 是否存在 */
			if (operatorRepository.findOne(operatorId) == null) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "Operator not found!");
			}

            /* role 是否存在  */
			if(roleList.size() != roleRepository.findAll(roleList).size()){
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "role not found!");
			}

            /* 解除全部Operator與role的連結關係 */
			List<RoleOperator> oriRoleOperatorList = roleOperatorRepository.findByOperatorId(operatorId);
			if(oriRoleOperatorList != null && !oriRoleOperatorList.isEmpty() ) {
				roleOperatorRepository.delete(oriRoleOperatorList);
			}

			OperatorAuth googleAuth = authService.getGoogleAuth(operatorId);


            /* 重新設定Operator與role連結關係 */
			List<RoleOperator> reSetupRoleOperatorList = new ArrayList<>();
			if(!roleList.isEmpty() ) {
				for (Integer roleId : roleList) {
					RoleOperator roleOp;
					roleOp = new RoleOperator();
					roleOp.setOperatorId(operatorId);
					roleOp.setRoleId(roleId);
					reSetupRoleOperatorList.add(roleOp);
				}
				roleOperatorRepository.save(reSetupRoleOperatorList);

				setGoogleActive(roleList, googleAuth, operatorId);
			}
			Operator one = operatorRepository.findOne(operatorId);
			one.setUpdateTime(new Date());
			one.setUpdateOperator(RequestHelper.getCurrentUser().getUser().getOperatorName());
			operatorRepository.saveAndFlush(one);

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}
	}

	private void setGoogleActive(List<Integer> roleList, OperatorAuth googleAuth, Integer operatorId){

		UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
//		if(
//				(!checkRoleListGoogleActive(roleList))
//						&& googleAuth !=null
//						&& (googleAuth.getStatus() == OperatorAuth.Status.ACTIVE || googleAuth.getStatus() == OperatorAuth.Status.ORIGIN)
//						&& googleAuth.getInputType() == OperatorAuth.IsAuto.AUTO ){
//			if(googleAuth.getStatus() == OperatorAuth.Status.ORIGIN){
//                authService.deleteByOperatorId(operatorId);
//			}else{
//				authService.setGoogleAuthStatus(operatorId, false, true);
//			}
//		}else {
		for (Integer roleId : roleList) {
			Role byRoleId = roleRepository.findByRoleId(roleId);
			//
			if(googleAuth !=null && byRoleId.getGoogleOtpActive() == Role.Status.ACTIVE && googleAuth.getStatus() == OperatorAuth.Status.INACTIVE){
				authService.setGoogleAuthStatus(operatorId, true, true);
				break;
			}else if(googleAuth == null && byRoleId.getGoogleOtpActive() == Role.Status.ACTIVE){
				authService.generateGoogleAuthBindRole(userInfo, operatorId);
				break;
			}
		}
//		}
	}
	private boolean checkRoleListGoogleActive(List<Integer> roleList){
		boolean onOff = false;
		for(Integer roleId : roleList){
			if(roleRepository.findByRoleId(roleId).getGoogleOtpActive() == Role.Status.ACTIVE){
				onOff = true;
				break;
			}
		}
		return  onOff;
	}

	@Override
	public void unCorrelateRoles(Integer operatorId, List<Integer> roleList) {

		try {
			List<RoleOperator> roleOperatorList = roleOperatorRepository.findByOperatorIdAndRoleIdList(operatorId,
					roleList);
			if (roleOperatorList == null || roleOperatorList.size() != roleList.size()) {
				throw new AdminServiceBaseException(AdminErrorCode.CORRELATION_NOT_EXIST_ERROR,
						"can't find the correlation between operator and role");
			}

			roleOperatorRepository.delete(roleOperatorList);

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}
	}

	@Override
	public Role createRole(RoleCreateTO roleTo, String updateOperator) {

		Role result = null;

		try {

			// 依照 roleName 查詢
			int countByRoleName = roleRepository.countByRoleName(roleTo.getRoleName());

			// 如果有該 roleName 的 record, 則拋Exception, 因為不允許重複的 roleName
			if (countByRoleName > 0) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NAME_ALREADY_EXIST_ERROR,
						"role name already exists");
			}

			// 先查詢所有狀態的Role, 因為之前已經有 select count
			// 過狀態為Normal的紀錄，如果有紀錄就會拋Exception
			// 所以這裡查詢出來的紀錄一定會是狀態註記刪除的紀錄或者是查不到
			result = roleRepository.findByRoleName(roleTo.getRoleName());

			// 如果查詢不到，代表DB裡面沒有該名稱的紀錄，就宣告一個新的 Role
			if (result == null) {
				result = new Role();
				result.setRoleName(roleTo.getRoleName());
			}
			result.setDescription(roleTo.getDescription());
			result.setActiveFlag(RoleConstant.ACTIVE_FLAG_NORMAL.getActiveFlagType());
			result.setDisplayOrder(roleTo.getDisplayOrder());
			result.setGoogleOtpActive(roleTo.getGoogleOtpActive());
			Role role = roleRepository.saveAndFlush(result);

			//設定Role所屬的category
			List<Integer> roleList = new ArrayList<>();
			roleList.add(role.getRoleId());
			categoryRoleService.correlateCategory(roleTo.getCategoryId(), roleList, updateOperator);

			return role;

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	@Override
	public void updateRole(List<Integer> categoryId,RoleCreateTO roleTo, String updateOperator) {
		try {

			// 依照 role 裡面的 roleId 查詢
			Role roleFound = roleRepository.findByRoleId(roleTo.getRoleId());

			// 如果沒有該 rolId 的 record, 則拋Exception
			if (roleFound == null) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "roleId not exists");
			}

			// 如果 rolId 查出來 record 的 activeFlag不是Normal, 則拋Exception
			if (roleFound.getActiveFlag() != RoleConstant.ACTIVE_FLAG_NORMAL.getActiveFlagType()) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_STATUS_ABNORMAL_ERROR,
						"role activeFlag not normal");
			}

			String trimmedRoleName = roleTo.getRoleName().trim();
			UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
			// 如果傳入 role 的 roleName 和 role 的 roleId 查詢到的名稱相同，代表角色名稱沒有要變更
			if (trimmedRoleName.equalsIgnoreCase(roleFound.getRoleName().trim())) {

				// 把原本要變更的 description 設定
				roleFound.setDescription(roleTo.getDescription());
				roleFound.setDisplayOrder(roleTo.getDisplayOrder());
				roleFound.setGoogleOtpActive(roleTo.getGoogleOtpActive());

				//先查询出与该角色相关的用户
				List<RoleOperator> byRoleIdOpList = roleOperatorRepository.findByRoleId(roleTo.getRoleId());

				if(byRoleIdOpList.size() > 0){
					Iterator<RoleOperator> iterator = byRoleIdOpList.iterator();
					while(iterator.hasNext()){
						Integer operatorId = iterator.next().getOperatorId();
						OperatorAuth googleAuth = authService.getGoogleAuth(operatorId);

						//开启谷歌验证
						if(roleTo.getGoogleOtpActive() == Role.Status.ACTIVE){
							if(googleAuth == null){
								//没有配置谷歌验证，生成谷歌验证，记录为初次创建

								authService.generateGoogleAuthBindRole(userInfo, operatorId);
							}else if(googleAuth != null && googleAuth.getStatus() == OperatorAuth.Status.INACTIVE){
								//已经配置了谷歌验证，但是没有开启

								authService.setGoogleAuthStatus(operatorId, true, true);
							}
						}
						else {
							//关闭谷歌验证
							//需要看该用户下面的其他角色是否有开启谷歌验证，如果没有开启谷歌验证，则关闭，反之，则不变
							//未配置，已关闭不管

							if(googleAuth != null && googleAuth.getStatus() == OperatorAuth.Status.ORIGIN){
								//除了当前角色之外的角色有开启谷歌验证，则不变
								authService.deleteByOperatorId(operatorId);
							}
						}
					}
				}
				roleRepository.saveAndFlush(roleFound);

				// 如果傳入的角色名稱和DB查詢到的名稱不相同，代表角色名稱有要變更
			} else {

				// 要換的名字，是否已經在DB裡面有紀錄
				Role findByNewName = roleRepository.findByRoleName(trimmedRoleName);

				// 如果依新名稱查詢，DB有找到
				if (findByNewName != null) {

					// 如果有該 foundByNewName 的 record, 且 狀態一樣為Normal 而且還不同 roleId
					// (因為不得存在狀態都是 Normal 的相同名稱Role)
					validRole(findByNewName, roleTo);

					// 因為之前曾經檢核過，DB是否有相同名稱且狀態為Normal紀錄的存在
					// 所以就裡在DB裡面的紀錄，一定是狀態不為Normal，只要把 description設定.
					// activeFlag設為Normal
					findByNewName.setDescription(roleTo.getDescription());
					findByNewName.setActiveFlag(RoleConstant.ACTIVE_FLAG_NORMAL.getActiveFlagType());
					findByNewName.setDisplayOrder(roleTo.getDisplayOrder());
					roleRepository.saveAndFlush(findByNewName);

					// 如果 DB裡面有 findByNewName 的紀錄，且 roleId 不一樣
					// 要把原本在 input 的 role 底下的人員全都搬過去到 findByNewName 底下
					// 先查詢 US_ROLE_OPERATOR 紀錄
					setNewRoleId(roleTo.getRoleId(), findByNewName.getRoleId());

					// 把原本依 department 裡面的 deptId 查詢的紀錄，狀態設為刪除
					roleFound.setActiveFlag(RoleConstant.ACTIVE_FLAG_DELETED.getActiveFlagType());
					roleRepository.saveAndFlush(roleFound);

					// 如果依新名稱查詢，DB沒有找到
				} else {

					// 要變更的 roleName. description 設定
					roleFound.setRoleName(roleTo.getRoleName());
					roleFound.setDescription(roleTo.getDescription());
					roleFound.setDisplayOrder(roleTo.getDisplayOrder());
					roleRepository.saveAndFlush(roleFound);

				}

			}

			//設定Role所屬的category
			List<Integer> roleList = new ArrayList<>();
			roleList.add(roleFound.getRoleId());
			categoryRoleService.correlateCategory(categoryId, roleList, updateOperator);

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	private void validRole(Role findByNewName, RoleCreateTO roleTo) {
		if (findByNewName.getActiveFlag() == RoleConstant.ACTIVE_FLAG_NORMAL.getActiveFlagType()
				&& !findByNewName.getRoleId().equals(roleTo.getRoleId())) {
			throw new AdminServiceBaseException(AdminErrorCode.ROLE_NAME_ALREADY_EXIST_ERROR,
					"new role name already exists");
		}
	}

	private void setNewRoleId(Integer oldRoleId, Integer newRoleId) {
		List<RoleOperator> havingRoleOperators = roleOperatorRepository.findByRoleId(oldRoleId);

		// 每筆 US_ROLE_OPERATOR 記錄拿出來更改 roleId
		for (RoleOperator roleOp : havingRoleOperators) {
			roleOp.setRoleId(newRoleId);
			roleOperatorRepository.saveAndFlush(roleOp);
		}
	}

	@Override
	public void toggleActiveFlag(Integer roleId) {
		try {

			// 依照 roleId 查詢
			Role roleFound = roleRepository.findByRoleId(roleId);

			// 如果沒有該 rolId 的 record, 則拋Exception
			if (roleFound == null) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "roleId not exists");
			}

			// 依 roleId 刪除角色時，該角色必須要沒有 permission 關聯,否則拋Exception
			List<RoleMenuPermission> permissions = roleMenuPermissionRespository.findByRoleId(roleId);
			if (!permissions.isEmpty()) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR,
						"roleId still has permissions");
			}

			// 依 roleId 刪除角色時，該角色必須要沒有 operator 關聯,否則拋Exception
			List<RoleOperator> operators = roleOperatorRepository.findByRoleId(roleId);
			if (!operators.isEmpty()) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_MUST_BE_UNCORELATED_WHEN_DELETING_ERROR,
						"roleId still has operators");
			}

			Integer flag = roleFound.getActiveFlag().equals(0) ? RoleConstant.ACTIVE_FLAG_NORMAL.getActiveFlagType() : RoleConstant.ACTIVE_FLAG_DELETED.getActiveFlagType();
			roleFound.setActiveFlag(flag);

			roleRepository.saveAndFlush(roleFound);

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}
	}

	@Override
	public Role copyRoleAndConnect(RoleCreateTO copyTo, String operator) {
		try {
			Role roleCreated = this.createRole(copyTo,operator);
			roleMenuPermissionService.correlatePermission(roleCreated.getRoleId(), copyTo.getMenuIdList());
			return  roleCreated;
		} catch (Exception e) {
			e.printStackTrace();
			return  null;
		}
	}

	@Override
	public Role copyRole(String originRoleName, String newRoleName, String description, Integer displayOrder, String updateOperator)
			{

		try {

			// 依照 originRoleName 查詢
			Role roleFound = roleRepository.findByRoleName(originRoleName);

			// 如果沒有該 originRoleName 的 record, 則拋Exception
			if (roleFound == null) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "roleName not exists");
			}

			// originRoleName 查出來的 record, activeFlag 必須要是 Normal 才可複製
			if (RoleConstant.ACTIVE_FLAG_NORMAL.getActiveFlagType() != roleFound.getActiveFlag()) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_ACTIVE_FLAG_OF_INPUT_MUST_BE_NORMAL_ERROR,
						"roleName's activeFlag abnormal");
			}

			//查出原本Role的categoryId與RoleId,以提供新的categoryRole設定
			List<CategoryRole> categoryRoleList = categoryRoleService.queryCategoryRoleByRoleId(roleFound.getRoleId());
			
			List<Integer> categoryIds = Lambda.extract(categoryRoleList, Lambda.on(CategoryRole.class).getCategoryId());

			// 叫 createRole 新增一個 Role, create時各種判斷邏輯也交由 createRole 處理
			RoleCreateTO roleCreateTO = new RoleCreateTO();
			roleCreateTO.setCategoryId(categoryIds);
			roleCreateTO.setRoleName(newRoleName);
			roleCreateTO.setDescription(description);
			roleCreateTO.setDisplayOrder(displayOrder);

			Role roleCreated = this.createRole(roleCreateTO, updateOperator);

			// originRoleName 擁有的權限
			List<MenuItem> permissions = roleMenuPermissionService.queryMenuItemsByRole(roleFound.getRoleId());

			// 把權限的 menuId 取出
			List<Integer> permissionsToGrant = Lists.newLinkedList();
			for (MenuItem menuItem : permissions) {
				permissionsToGrant.add(menuItem.getMenuId());
			}

			// 由 roleMenuPermissionService 把權限加上去到新的 Role
			roleMenuPermissionService.correlatePermission(roleCreated.getRoleId(), permissionsToGrant);

			return roleCreated;

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception ex) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, ex.getMessage(), ex);
		}

	}

	@Override
	public Page<Role> queryAllRoles(int pageNumber, int pageSize) {
		return queryAllRoles(pageNumber, pageSize, null);
	}

	@Override
	public Page<Role> queryAllRoles(int pageNumber, int pageSize, Integer activeFlag) {

		Page<Role> page = null;
		try {

			// 查詢 US_ROLE 設定條件所需的物件
			QueryRoleTO queryRoleTO = new QueryRoleTO();

			queryRoleTO.setActiveFlag(activeFlag);

			// 頁數
			queryRoleTO.setPagenumber(pageNumber);
			// 每頁筆數
			queryRoleTO.setPageSize(pageSize);

			// 組成where condition 查詢條件
			Specification<Role> role = RoleSpecifications.queryByConditions(queryRoleTO);

			Pageable request = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "displayOrder");

			// 僅能查詢自己所被付與的Role,如果該Role有category的權限,可查詢或Assign該category的role給User
			// Admin能查詢全部的Role以及Assign全部的Role給User
			NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);

			if(!noneAdminInfo.isAdmin()) {
				queryRoleTO.setRoleIdList(noneAdminInfo.getRoleIds());
			}

			page = roleRepository.findAll(role, request);

		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}

		return page;

	}

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	@Override
	public List<RoleOperator> queryRoles(Integer operatorId) {

		return roleOperatorRepository.findByOperatorId(operatorId);
	}

	@Override
	public Page<Role> queryOperatorRoles(String operatorName, int pageNumber, int pageSize) {
		Page<Role> result = null;
		try {
			// 用 operatorName 查詢 Operator
			Operator operator = operatorRepository.findByOperatorName(operatorName);

			// 如果查不到, 拋Exception
			if (operator == null) {
				throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_NOT_EXIST_ERROR, "operatorName not exists");
			}

			// 用 operatorId 查詢 US_ROLE_OPERATOR 的 roleId
			List<Integer> roleIds = roleOperatorCustomRepository.findRoleIdListByOperatorId(operator.getOperatorId());

			// 查詢 US_ROLE 設定條件所需的物件
			QueryRoleTO queryRoleTO = new QueryRoleTO();

			// 頁數
			queryRoleTO.setPagenumber(pageNumber);
			// 每頁筆數
			queryRoleTO.setPageSize(pageSize);
			// roleId的List
			queryRoleTO.setRoleIdList(roleIds);

			// 組成where condition 查詢條件
			Specification<Role> role = RoleSpecifications.queryByConditions(queryRoleTO);

			Pageable request = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "displayOrder");

			// 由 roleRepository 做查詢
			result = roleRepository.findAll(role, request);

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}
		return result;
	}

	@Override
	public Page<Operator> queryOperatorsByRole(String roleName, int pageNumber, int pageSize) {
		Page<Operator> result = null;
		try {

			// 用 roleName 查詢 Role
			Role roleFound = roleRepository.findByRoleName(roleName);

			// 如果查不到, 拋Exception
			if (roleFound == null) {
				throw new AdminServiceBaseException(AdminErrorCode.ROLE_NOT_EXIST_ERROR, "roleName not exists");
			}

			// 查詢 operatorId 的 List
			List<RoleOperator> roleOperators = roleOperatorRepository.findByRoleId(roleFound.getRoleId());
			List<Integer> operatorIds = Lists.newLinkedList();
			for (RoleOperator roleOperator : roleOperators) {
				operatorIds.add(roleOperator.getOperatorId());
			}

			// 查詢 US_OPERATOR 設定條件所需的物件
			QueryOperatorTO queryOperatorTO = new QueryOperatorTO();

			// 頁數
			queryOperatorTO.setPagenumber(pageNumber);
			// 每頁筆數
			queryOperatorTO.setPageSize(pageSize);
			// operatorId的List
			queryOperatorTO.setOperatorIdList(operatorIds);

			queryOperatorTO.setNotIncludeActiveFlag(7);

			// 組成where condition 查詢條件
			Specification<Operator> operator = OperatorSpecifications.queryByConditions(queryOperatorTO);

			Pageable request = new PageRequest(pageNumber - 1, pageSize, Sort.Direction.ASC, "operatorId");

			// 由 operatorRepository 做查詢
			result = operatorRepository.findAll(operator, request);

		} catch (AdminServiceBaseException usbe) {
			throw usbe;
		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}
		return result;
	}

	@Override
	public Role queryRoleById(Integer roleId) {
		return roleRepository.findOne(roleId);
	}

	@Override
	@Deprecated
	public List<Role> queryOperatorAllRoles(Operator operator) {
		List<Role> result = null;
		try {
			// 用 operatorId 查詢 US_ROLE_OPERATOR 的 roleId
			List<Integer> roleIds = roleOperatorCustomRepository.findRoleIdListByOperatorId(operator.getOperatorId());

			// 由 roleRepository 做查詢
			result = roleRepository.findByRoleIds(roleIds);

		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}
		return result;
	}

	@Override
	public void updateDisplayPermissions(Integer roleId, String[] menuIds) {
		List<MenuItem> menuItems = roleMenuPermissionService.queryMenuItemsByRole(roleId);
		List<Integer> newMenus = new ArrayList<>();
		List<Integer> removedMenus = new ArrayList<>();
		for (String s : Arrays.asList(menuIds)) {
			newMenus.add(Integer.valueOf(s));
		}

		for (MenuItem menuItem : menuItems) {
			if (!newMenus.contains(menuItem.getMenuId())) {
				removedMenus.add(menuItem.getMenuId());
			} else {
				newMenus.remove(menuItem.getMenuId());
			}
		}
		if (!newMenus.isEmpty()) {
			roleMenuPermissionService.correlatePermission(roleId, newMenus);
		}
		if (!removedMenus.isEmpty()) {
			roleMenuPermissionService.unCorrelatePermission(roleId, removedMenus);
		}
	}

	@Override
	public void updateAuditPermissions(Integer roleId, String[] menuIds) {
		List<Integer> menuList = new ArrayList<>();
		for (String s : Arrays.asList(menuIds)) {
			if (!StringTools.isEmptyOrNull(s)) {
				menuList.add(Integer.valueOf(s));
			}
		}
		bpmPermissionService.updatePermissionRoleGroup(roleId, menuList);
	}

	@Override
	public Page<Role> queryAllRolesWithParams(int pageNumber, int pageSize, Integer activeFlag, SortTo sortTo, String roleName, String description,Integer categoryId) {
		Page<Role> page = null;
		try {
			// 查詢 US_ROLE 設定條件所需的物件
			QueryRoleTO queryRoleTO = new QueryRoleTO();

			queryRoleTO.setActiveFlag(activeFlag);

			// 頁數
			queryRoleTO.setPagenumber(pageNumber);
			// 每頁筆數
			queryRoleTO.setPageSize(pageSize);
			
			queryRoleTO.setRoleName(roleName);

			queryRoleTO.setDescription(description);
			
			queryRoleTO.setCategoryRoleId(categoryId);

			// 組成where condition 查詢條件
			Specification<Role> role = RoleSpecifications.queryByConditions(queryRoleTO);

			Pageable request = new PageRequest(pageNumber - 1, pageSize, "asc".equalsIgnoreCase(sortTo.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC, sortTo.getSortBy() != null ? sortTo.getSortBy() : "roleName");

			// 由 roleRepository 做查詢
			page = roleRepository.findAll(role, request);

		} catch (Exception e) {
			throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
		}

		return page;
	}
	
    @Override
    public Boolean isSysAdmin(UserInfo<Operator> userInfo) {
        Integer opId = userInfo.getUser().getOperatorId();
        
        RoleOperator role = roleOperatorRepository.findByOperatorIdAndRoleId(opId, SUPER_MANAGER_ROLE_ID);
        
        return role != null;
    }

	@Override
	public List<RoleTo> findAllActiveRoles() {
		List<Object[]> list = roleRepository.findByActiveFlag(1);
		List<RoleTo> result = Lists.newLinkedList(); 
		for(Object[] r : list) {
			RoleTo rt = new RoleTo();
			
			rt.setRoleId((Integer) r[0]);
			rt.setRoleName((String) r[1]);
			
			result.add(rt);
		}
		
		return result;
	}

	@Override
	public List<Role> findOperatorAllRoleByOperatorId(Integer operatorId) {
		return roleRepository.findOperatorAllRoleByOperatorId(operatorId);
	}
}
