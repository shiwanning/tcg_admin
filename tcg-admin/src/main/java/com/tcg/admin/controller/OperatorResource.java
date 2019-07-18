package com.tcg.admin.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.tcg.admin.common.annotation.OperationLog;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.OperationFunctionConstant;
import com.tcg.admin.model.Merchant;
import com.tcg.admin.service.BehaviorLogService;
import com.tcg.admin.service.MerchantService;
import com.tcg.admin.service.log.OperatorLogService;
import com.tcg.admin.to.*;
import com.tcg.admin.utils.AuthorizationUtils;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.service.security.OperatorPasswordService;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.to.response.JsonResponseTPage;

@RestController
@RequestMapping(value = "/resources/operators", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorResource {

    @Autowired
	private OperatorService operatorService;

    @Autowired
	private OperatorLoginService operatorLoginService;

    @Autowired
    private OperatorPasswordService operatorPasswordService;

    @Autowired
	private BehaviorLogService behaviorLogService;

    @Autowired
	private MerchantService merchantService;
    @Autowired
	private OperatorLogService operatorLogService;

    /**
     * Combine operatorName, roleId, nickname or departmentId dynamically to query operator record.
     * 7.Search User
     * 搜寻用户:以帳號,角色,暱稱,部門查詢管理員資料
     */
    @GetMapping
    public JsonResponseT<QueryJsonTO> queryOperator(@RequestParam(value = "operatorName", required = false) String operatorName,
            @RequestParam(value = "roleId", required = false) Integer roleId,
            @RequestParam(value = "activeFlag", required = false) Integer activeFlag,
            @RequestParam(value = "merchantId", required = false) Integer merchantId,
            @RequestParam(value = "baseMerchantCode", required = false) String baseMerchantCode,
			@RequestParam(value = "lastLoginIP", required = false) String lastLoginIP,
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder

                                 ) {
        JsonResponseT<QueryJsonTO> response = new JsonResponseT<>(true);
        PageableTO pageableTo = new PageableTO();
        pageableTo.setSortBy(sortBy);
        pageableTo.setSortOrderBy(sortOrder);
        pageableTo.setPageSize(pageSize);
        pageableTo.setPageNo(pageNo);
        QueryJsonTO queryOperatorList = operatorService.queryOperator2(operatorName, roleId, activeFlag, merchantId, baseMerchantCode, lastLoginIP, pageableTo);
        response.setValue(queryOperatorList);
        return response;
    }


    @GetMapping("/querySubscriptionMerchantBy")
	public JsonResponseT querySubscriptionMerchant(
			@RequestParam(value = "userId") Integer userId
	) {
		JsonResponseT response = new JsonResponseT<>(true);
		List<Integer> list = operatorService.querySubscriptionMerchant(userId);
		response.setValue(list);
		return response;
	}

	/**
	 * 2.Create Operator
	 * 创建用户
	 */
	@PostMapping("/addOperator")
	public JsonResponse addOperator(@RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponse response = new JsonResponse(true);
		operatorService.addOperator(operatorCreateTO);
		return response;
	}

	/**
	 * Change back-end operator's active flag status. To inactive or active by operatorName and activeFlag.
	 * operator
	 * 3.用户状态修改
	 */
	@PutMapping("/name/{operatorName}/activeflag/{activeFlag}/_change_activeflag")
	@OperationLog(type = OperationFunctionConstant.MODIFY_STATUS)
	public JsonResponse changeActiveFlag(@PathVariable("operatorName") String operatorName,
	        @PathVariable("activeFlag") int activeFlag) {
		JsonResponse response = new JsonResponse(true);
		Operator operator = operatorService.changeActiveFlag(operatorName, activeFlag);

		if (isForbidStatus(activeFlag)) {
			AuthInfo authInfo = AuthorizationUtils.getSessionAuthInfo(operator.getOperatorId());
			if (null != authInfo && null != authInfo.getToken()) {
				operatorLoginService.logout(authInfo.getToken());
			}
		}

        return response;
	}

	/**
	 * This will set operator's activeflag status to deleted (LoginConstant.ActiveFlagDelete).
	 * Will not actually erase the record from database.
	 * 4.Delete Operator
	 * 刪除用戶
	 */
	@DeleteMapping("/operatorId/{operatorId}")
	@OperationLog(type = OperationFunctionConstant.DELETE_ACCOUNT)
	public JsonResponse deleteOperator(@PathVariable("operatorId") Integer operatorId) {
		JsonResponse response = new JsonResponse(true);
		operatorService.deleteOperator(operatorId);
		return response;
	}

	/**
	 * 5.Reset User Password:
	 * 用户管理 - 重置用户密码
	 * Input a String variable operator name as an parameter to reset operator's
	 * password to default value (123456). Operator name will be check in database,
	 * exception throws when its not found. The new password value will be encrypted with
	 * MD5 mechanism.
	 */
	@PutMapping("/name/{operatorName}/_reset_password")
	@OperationLog(type = OperationFunctionConstant.RESET_PASS_WORD)
	public JsonResponseT<Object> resetPassword(@PathVariable("operatorName") String operatorName) {
		JsonResponseT<Object> response = new JsonResponseT(true);
		Map<String, String> resultMap = operatorService.resetPassword(operatorName);
		response.setValue(resultMap);
		return response;
	}

	/**
	 * Create a user role, for role management.
	 * Assign Role
	 * 6.用户管理 - 分配角色
	 */
	@PutMapping("/operatorId/{operatorId}/_assignRoles")
	@OperationLog(type = OperationFunctionConstant.MODIFY_ROLE)
	public JsonResponse assignRoles(@PathVariable("operatorId") Integer operatorId, @RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponse response = new JsonResponse(true);
		operatorService.assignRoles(operatorId,operatorCreateTO.getRoleIdList());
		return response;
	}

	@GetMapping("/token")
    public JsonResponseT<Operator> getOperatorByToken() {
        return new JsonResponseT<>(true, RequestHelper.getCurrentUser().getUser());
    }

    /**
     * Update Operator Profile
     */
	@PutMapping("/profile")
    @OperationLog(type = OperationFunctionConstant.MODIFY_PROFILE)
    public JsonResponse updateProfile(@RequestBody Operator operator) {
        JsonResponse response = new JsonResponse(true);
        operatorService.updateOperator(operator);
        return response;
    }

    /**
     * Update operator password
     * @param operators
     * @return
     * @throws AdminServiceBaseException
     */
	@PutMapping("/password")
	@OperationLog(type = OperationFunctionConstant.MODIFY_PASS_WORD)
    public JsonResponse changePassword(@RequestBody Map<String, String> operators) {

        operatorService.changeOperatorPassword(operators.get("operatorName"), operators.get("currentPassword"), operators.get("newPassword"),
                                               operators.get("confirmPassword"), false);

        return new JsonResponse(true);
    }
	@PutMapping("/password_origin")
	@OperationLog(type = OperationFunctionConstant.MODIFY_PASS_WORD)
	public JsonResponse changePasswordOrigin(@RequestBody Map<String, String> operators) {

		operatorService.changeOperatorPassword(operators.get("operatorName"), operators.get("currentPassword"), operators.get("newPassword"),
				operators.get("confirmPassword"), true);

		return new JsonResponse(true);
	}
    
	@GetMapping("/activityLog/{pageSize}/{page}")
    public JsonResponseTPage<List<ActivityLog>> activityLog(
                                @PathVariable("page")Integer page,
                                @PathVariable("pageSize")Integer pageSize,
                                @RequestParam(value = "actionType", required = false)Integer actionType,
								@RequestParam(value = "userName")String userName){
    	JsonResponseTPage<List<ActivityLog>> response = new JsonResponseTPage<>(true);

		Map<String, Object> activityLog = operatorService.getActivityLog(userName, page, pageSize, actionType);
		response.setValue((List<ActivityLog>) activityLog.get("activityLogs"));
    	response.setPage(page);
    	response.setTotal((Long) activityLog.get("totalElements"));
    	return response;
    }

	@GetMapping("/getOperatorProfile")
	public JsonResponseT<Object> getOperatorProfile(
			@RequestParam(value = "userName")String userName){
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Map<String, Object> operatorProfile = operatorService.getOperatorProfile(userName);
		response.setValue(operatorProfile);
		return response;
	}



	@GetMapping("/activityLoginLog/{pageSize}/{page}")
	public JsonResponseTPage<List<BehaviorLogTo>> activityLoginLog(
			@PathVariable("page")Integer page,
			@PathVariable("pageSize")Integer pageSize,
			@RequestParam(value = "userName")String userName) {

		JsonResponseTPage<List<BehaviorLogTo>> response = new JsonResponseTPage<>(true);
		List<BehaviorLogTo> rawDataLogin = operatorService.getActivityLoginLog(userName, page, pageSize);
		response.setValue(rawDataLogin);
		response.setPage(page);
		return response;
	}


	@GetMapping("/statisticalLoginStatus/{pageSize}/{page}")
	public JsonResponseT<Object> statisticalLoginStatus(
			@PathVariable("page")Integer page,
			@PathVariable("pageSize")Integer pageSize,
			@RequestParam(value = "userName")String userName,
			@ApiParam(value = "POINT TIME(yyyy-MM-dd HH:mm:ss)",required = true) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @RequestParam(value = "checkDate") Date checkDate) {

		JsonResponseT<Object> response = new JsonResponseT<>(true);
		Map<String, Object> objectMap = behaviorLogService.statisticalLoginStatus(userName, page, pageSize, checkDate);
		response.setValue(objectMap);
		return response;
	}


	/**
     * Query operator's profile include operatorName,operatorId,operatorNickname and merchantCode of the operator
     */
	@GetMapping("/profile")
        public JsonResponseT<Object> getOperatorBySession(@RequestHeader(value = "Authorization", required = false) String token, 
                @RequestParam(value = "flag", defaultValue = "false")boolean flag) {
    	JsonResponseT<Object> response = new JsonResponseT<>(true);
        response.setValue(operatorService.findOperatorBySession(token,flag));
    	return response;
    }

	@PutMapping("{userName}/email/{email}/_reset_password")
	@OperationLog(type = OperationFunctionConstant.RESET_PASS_WORD)
	public Operator resetLoginPassword(@PathVariable("userName")String userName,
            @PathVariable("email")String email) {
        return operatorPasswordService.resetPasswordByEmail(userName, email);
    }

	@PostMapping("/transfer")
    public JsonResponseT<UserInfo<Operator>> transferOperator(@RequestBody OperatorCreateTO operatorTo) {
        JsonResponseT<UserInfo<Operator>> response = new JsonResponseT<>(true);
        operatorService.transferOperator(operatorTo);
        UserInfo<Operator> operator = operatorLoginService.login(operatorTo.getOperatorName(), operatorTo.getPassword(), LoginType.PASSWORD);
        response.setValue(operator);
        return response;
    }
    
	@GetMapping("/getOperatorsByActiveFlag")
	public JsonResponseT<Object> getOperatorsByActiveFlag(@RequestParam(value = "statusCodes", required = false)List<Integer> statusCodes) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		response.setValue(operatorService.getOperatorsByActiveFlag(statusCodes));
		return response;
	}
    
	@GetMapping("/validateOperator")
	public JsonResponseT<Object> isExistOperator(@RequestParam(value = "opName", required = false)String opName) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		response.setValue(operatorService.queryByOperatorName(opName) != null);
		return response;
	}

    /**
     * modify Operator baseMerchant
     */
	@PutMapping("/baseMerchant/{operatorId}")
	@OperationLog(type = OperationFunctionConstant.MODIFY_BELONGS_MERCHANT)
	public JsonResponse updateBaseMerchant(@RequestBody OperatorCreateTO operatorCreateTO,@PathVariable Integer operatorId) {
        JsonResponse response = new JsonResponse(true);
        operatorService.updateBaseMerchant(operatorCreateTO);
        return response;
    }

	/**
	 * check user role is admin.
	 * @return
	 */
	@GetMapping("/is-admin")
	public JsonResponse isAdmin() {
		Map<String,String> resultMap = Maps.newHashMap();
		Boolean isAdmin= merchantService.checkAdmin(false).isAdmin();
		resultMap.put("admin", isAdmin.toString());
        Merchant merchant = merchantService.findMerchantByMerchantCode(RequestHelper.getCurrentUser().getUser().getBaseMerchantCode());
		if (null == merchant) {
			resultMap.put("admin", "false");
		}
		resultMap.put("merchantCode", null == merchant ? null : merchant.getMerchantCode());
		resultMap.put("merchantId", null == merchant ? null : merchant.getMerchantId().toString());
		return new JsonResponseT(true,resultMap);
	}

	private boolean isForbidStatus(int flag) {
		if (LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.equals(LoginConstant.getByStatusCode(flag))) {
			return true;
		}
		if (LoginConstant.ACTIVE_FLAG_SYSTEM_LOGIN_PROHIBITED.equals(LoginConstant.getByStatusCode(flag))) {
			return true;
		}

		return false;
	}

	@GetMapping("/profile/account-name")
	public JsonResponseT<Object> getOperatorByAccountName(@RequestParam(value = "accountName")String accountName) {
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		response.setValue(operatorService.findOperatorByAccountName(accountName));
		return response;
	}
}
