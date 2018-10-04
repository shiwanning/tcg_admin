package com.tcg.admin.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.model.MenuItem;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.State;
import com.tcg.admin.model.Task;
import com.tcg.admin.persistence.RoleOperatorCustomRepository;
import com.tcg.admin.persistence.springdata.IMenuItemRepository;
import com.tcg.admin.persistence.springdata.IMerchantOperatorRepository;
import com.tcg.admin.persistence.springdata.ITaskRepository;
import com.tcg.admin.service.BehaviorLogService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.VerificationService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.VerificationData;

@Service
@Transactional
public class VerificationServiceImpl implements VerificationService {
	
    @Autowired
	private OperatorLoginService operatorLoginService;
	
    @Autowired
	private	RoleMenuPermissionService roleMenuPermissionService;
    @Autowired
	private IMerchantOperatorRepository merchantOperatorRepository;
    @Autowired
	private ITaskRepository taskRepository;
    @Autowired
    private RoleOperatorCustomRepository roleOperatorCustomRepository;
	
    @Autowired
	private IMenuItemRepository menuItemRepository;
	@Autowired
    private MerchantService merchantService;

    @Autowired
	private BehaviorLogService behaviorLogService;

    private static final Integer SUPER_MANAGER_ROLE_ID = 1;

	@Override
	public boolean basic(VerificationData verificationData) {
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(verificationData.getToken());
		MenuItem menuItem= roleMenuPermissionService.queryMenuItemById(verificationData.getMenuId());
		return !(menuItem == null || !roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), menuItem));
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean advanced(VerificationData verificationData) {
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(verificationData.getToken());

		MenuItem menuItem= roleMenuPermissionService.queryMenuItemById(verificationData.getMenuId());
		if(menuItem == null){
			return false;
		}else if(!StringUtils.equals(menuItem.getAccessType(),"9")){
			behaviorLogService.saveBehaviorLog(verificationData, menuItem);
		}

		/* admin does not need to verify permission*/
		if(merchantService.checkAdmin(false).isAdmin()){
			return true;
		}else if(!roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), menuItem)){
			return false;
		}
		return true;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean menuRecord(VerificationData verificationData) {
		MenuItem menuItem= roleMenuPermissionService.queryMenuItemById(verificationData.getMenuId());
		if(menuItem == null){
			return false;
		}else{
			behaviorLogService.saveBehaviorLog(verificationData, menuItem);
		}

		return true;
	}

	@Override
	public boolean task(VerificationData verificationData) {
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(verificationData.getToken());
		Task task = taskRepository.findOne(verificationData.getWorkflowId());
		State state = task.getState();
		MenuItem menuItem= roleMenuPermissionService.queryMenuItemById(state.getMenuId());
		
		return !(merchantOperatorRepository.countByDeptIdAndOperatorId(task.getMerchantId(), userInfo.getUser().getOperatorId()) ==0
				|| !roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), menuItem));
	}

	@Override
	public boolean byResourceUrl(VerificationData verificationData) {
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(verificationData.getToken());

        if(userInfo == null) {
            return false;
        }
		MenuItem menuItem = menuItemRepository.findByUrl(verificationData.getUrl());
		if(menuItem == null){
			return false;
		}

		/* admin does not need to verify permission*/
		if(merchantService.checkAdmin(false).isAdmin()){
			return true;
		}

		return roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), menuItem);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public boolean advancedByResourceUrl(VerificationData verificationData) {
		UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(verificationData.getToken());

		if(userInfo == null) {
			return false;
		}
		MenuItem menuItem = menuItemRepository.findByUrl(verificationData.getUrl());
		if(menuItem == null){
			return false;
		}else if(!StringUtils.equals(menuItem.getAccessType(),"9")){
			behaviorLogService.saveBehaviorLog(verificationData, menuItem);
		}

		/* admin does not need to verify permission*/
		if(merchantService.checkAdmin(false).isAdmin()){
			return true;
		}

		return roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), menuItem);
	}

    @Override
    public boolean merchant(VerificationData verificationData) {
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(verificationData.getToken());

        if(userInfo == null) {
            return false;
        }
        List<Integer> roleIdList = roleOperatorCustomRepository.findRoleIdListByOperatorId(userInfo.getUser().getOperatorId());

        if (roleIdList.isEmpty()) {
            return false;
        }

        for(Integer roleId : roleIdList ) {
            if(roleId == SUPER_MANAGER_ROLE_ID) {
                return true;
            }
        }
        boolean result = false;
        List<Merchant> merchantList = merchantService.queryOperatorMerchants(false);
        for(Merchant merchant : merchantList) {
            if(merchant.getMerchantCode().equalsIgnoreCase(verificationData.getMerchant())){
                result =  true;
            }
        }
        return result;
    }

}
