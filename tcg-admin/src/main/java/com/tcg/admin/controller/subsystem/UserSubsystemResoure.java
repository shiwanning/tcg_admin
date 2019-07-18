package com.tcg.admin.controller.subsystem;

import java.util.List;
import java.util.Map;

import com.tcg.admin.to.request.SubscribeMerchantTo;
import com.tcg.admin.to.response.JsonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.AdminRoles;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.AdminRolesService;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.service.RoleService;
import com.tcg.admin.to.OperatorsTO;
import com.tcg.admin.to.RoleTo;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.to.response.OperatorInfoTo;
import com.tcg.admin.to.response.OperatorMerchantTo;

@RestController
@RequestMapping(value = "/resources/subsystem/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserSubsystemResoure {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserSubsystemResoure.class);
	
    @Autowired
    private OperatorService operatorService;
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private AdminRolesService adminRolesService;

    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;
    
    @GetMapping
    public JsonResponseT<List<Operator>> getOperator(@RequestParam(value = "operatorIds", required = false) List<Integer> operatorIds) {
        JsonResponseT<List<Operator>> jp = new JsonResponseT<>(true);
        List<Operator> operators = operatorService.findOperatorByIds(operatorIds);
        jp.setValue(operators);
        return jp;
    }
    
    @GetMapping("all-active-operators")
    public JsonResponseT<List<OperatorsTO>> getOperators() {
        JsonResponseT<List<OperatorsTO>> jp = new JsonResponseT<>(true);
        List<Integer> activeFlag = Lists.newArrayList(0, 1, 2);
        jp.setValue(operatorService.getOperatorsByActiveFlag(activeFlag));
        return jp;
    }

    @PostMapping("/updateUsOperatorByMisMerchantStatus")
    public JsonResponse updateUsOperatorByMisMerchantStatus(@RequestBody Map<String, String> params) {

        String merchantCode = params.get("merchantCode");
        String status = params.get("status");

        operatorService.updateUsOperatorByMisMerchantStatus(Integer.parseInt(status), merchantCode);
        return JsonResponse.OK;
    }
    
    @GetMapping("all-active-admin-roles")
    public JsonResponseT<List<AdminRoles>> getAdminRoles() {
        JsonResponseT<List<AdminRoles>> jp = new JsonResponseT<>(true);
        jp.setValue(adminRolesService.getAll());
        return jp;
    }
    
    @GetMapping("permission-list")
    public JsonResponseT<List<OperatorInfoTo>> getPermissionList(@RequestParam(value = "menuId", required = false) Integer menuId) {
        JsonResponseT<List<OperatorInfoTo>> jp = new JsonResponseT<>(true);
        jp.setValue(roleMenuPermissionService.findByMenuIdPermission(menuId));
        return jp;
    }
    
    @GetMapping("merchants")
    public JsonResponseT<List<OperatorMerchantTo>> getMerchantsForCurrentOperator() {
        JsonResponseT<List<OperatorMerchantTo>> jp = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        
        if(userInfo == null) {
        	LOGGER.info("getMerchantsForCurrentOperator token invalid: ${}", RequestHelper.getToken() );
        	jp.setErrorCode(AdminErrorCode.TOKEN_IS_INVALID);
        	jp.setSuccess(false);
        	return jp;
        }
        List<OperatorMerchantTo> targetList = Lists.newLinkedList();
        jp.setValue(targetList);
        List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(userInfo);
        
        for(Map<String,String> map : merchList) {
        	OperatorMerchantTo record = new OperatorMerchantTo();
        	record.setMerchantCode(map.get("merchantCode"));
        	record.setMerchantName(map.get("merchantName"));
        	targetList.add(record);
        }
        
        return jp;
    }

}
