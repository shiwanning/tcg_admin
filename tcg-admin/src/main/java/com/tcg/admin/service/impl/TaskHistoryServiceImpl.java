package com.tcg.admin.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.State;
import com.tcg.admin.model.Task;
import com.tcg.admin.persistence.StateRepositoryCustom;
import com.tcg.admin.persistence.TaskRepositoryCustom;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.TaskHistoryService;
import com.tcg.admin.to.NoneAdminInfo;
import com.tcg.admin.to.TaskQueryTO;
import com.tcg.admin.to.UserInfo;


@Service
@Transactional
public class TaskHistoryServiceImpl implements TaskHistoryService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TaskHistoryServiceImpl.class);

	@Autowired
	TaskRepositoryCustom taskRepositoryCustom;

	@Autowired
	private StateRepositoryCustom stateRepositoryCustom;

	@Autowired
    private RoleMenuPermissionService roleMenuPermissionService;

	@Autowired
	private MerchantService merchantService;
	
	@Autowired
    private OperatorAuthenticationService operatorAuthService;

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Page<Task> getAll(TaskQueryTO taskQueryTO) {
		NoneAdminInfo noneAdminInfo = merchantService.checkAdmin(true);
		
		UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
		
		boolean isViewSysHelper = roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), roleMenuPermissionService.queryMenuItemById(10800));
		boolean isViewMerchant = roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), roleMenuPermissionService.queryMenuItemById(10400));
		
		List<Integer> excludeStateIds = Lists.newLinkedList();
		
		if(!isViewSysHelper) {
		    excludeStateIds.add(300);
		    excludeStateIds.add(301);
		    excludeStateIds.add(302);
		}
		
		if(!isViewMerchant) {
            excludeStateIds.add(200);
            excludeStateIds.add(201);
            excludeStateIds.add(202);
        }
		
        return taskRepositoryCustom.getAll(noneAdminInfo.isAdmin(), taskQueryTO, excludeStateIds);
	}

	public List<State> getStateList(String type) {
	    
	    UserInfo<Operator> userInfo = operatorAuthService.getOperatorByToken(RequestHelper.getToken());
	    
	    boolean isViewSysHelper = roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), roleMenuPermissionService.queryMenuItemById(10800));
	    boolean isViewMerchant = roleMenuPermissionService.verifyMenuItemPermission(userInfo.getUser().getOperatorId(), roleMenuPermissionService.queryMenuItemById(10400));
	    
	    List<State> states = stateRepositoryCustom.getStateListByType(type);
	    
	    List<State> result = Lists.newLinkedList();
	    
	    for(State state : states) {
	        if((!isViewSysHelper && "SYS".equals(state.getType()))
	                || (!isViewMerchant && "MER".equals(state.getType()))) {
	            continue;
	        }
	        result.add(state);
	    }
	    
		return result;
	}

}
