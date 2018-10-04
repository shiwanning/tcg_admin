package com.tcg.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.service.security.OperatorPasswordService;
import com.tcg.admin.to.ActivityLog;
import com.tcg.admin.to.OperatorCreateTO;
import com.tcg.admin.to.QueryJsonTO;
import com.tcg.admin.to.UserInfo;
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
            @RequestParam(value = "pageNo", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "sortOrder", required = false) String sortOrder
                                 ) {
        JsonResponseT<QueryJsonTO> response = new JsonResponseT<>(true);
        QueryJsonTO queryOperatorList = operatorService.queryOperator2(operatorName, roleId, activeFlag, merchantId, baseMerchantCode,pageNo, pageSize, sortBy, sortOrder );
        response.setValue(queryOperatorList);
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
	public JsonResponse changeActiveFlag(@PathVariable("operatorName") String operatorName,
	        @PathVariable("activeFlag") int activeFlag) {
		JsonResponse response = new JsonResponse(true);
		operatorService.changeActiveFlag(operatorName, activeFlag);

		return response;
	}

	/**
	 * This will set operator's activeflag status to deleted (LoginConstant.ActiveFlagDelete).
	 * Will not actually erase the record from database.
	 * 4.Delete Operator
	 * 刪除用戶
	 */
	@DeleteMapping("/operatorId/{operatorId}")
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
	public JsonResponse resetPassword(@PathVariable("operatorName") String operatorName) {
		JsonResponse response = new JsonResponse(true);
		operatorService.resetPassword(operatorName);
		return response;
	}

	/**
	 * Create a user role, for role management.
	 * Assign Role
	 * 6.用户管理 - 分配角色
	 */
	@PutMapping("/operatorId/{operatorId}/_assignRoles")
	public JsonResponse assignRoles(@PathVariable("operatorId") Integer operatorId, @RequestBody OperatorCreateTO operatorCreateTO) {
		JsonResponse response = new JsonResponse(true);
		operatorService.assignRoles(operatorId,operatorCreateTO.getRoleIdList());
		return response;
	}

	@GetMapping("/token")
    public JsonResponseT<Object> getOperatorByToken(@RequestHeader(value = "Authorization", required = false) String token) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        response.setValue( operatorService.findOperatorByToken(token));
        return response;
    }

    /**
     * Update Operator Profile
     */
	@PutMapping("/profile")
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
    public JsonResponse changePassword(@RequestBody Map<String, String> operators) {

        operatorService.changeOperatorPassword(operators.get("operatorName"), operators.get("currentPassword"), operators.get("newPassword"),
                                               operators.get("confirmPassword"));

        return new JsonResponse(true);
    }
    
	@GetMapping("/activityLog/{pageSize}/{page}")
    public JsonResponseTPage<List<ActivityLog>> activityLog(@RequestHeader(value = "Authorization", required = false)String token,
                                @PathVariable("page")Integer page,
                                @PathVariable("pageSize")Integer pageSize,
                                @RequestParam(value = "actionType", required = false)Integer actionType) {
    	JsonResponseTPage<List<ActivityLog>> response = new JsonResponseTPage<>(true);
        List<ActivityLog> rawData = operatorService.getActivityLog(operatorLoginService.getSessionUser(token), page, pageSize, actionType);
    	response.setValue(rawData);
    	response.setPage(page);
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
    public Operator resetLoginPassword(@PathVariable("userName")String userName, 
            @PathVariable("email")String email) {
        Operator operator = operatorPasswordService.resetPasswordByEmail(userName, email);
        return operator;
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
		response.setValue(operatorService.queryByOperatorName(opName) != null ? true : false);
		return response;
	}

    /**
     * modify Operator baseMerchant
     */
	@PutMapping("/baseMerchant")
    public JsonResponse updateBaseMerchant(@RequestBody OperatorCreateTO operatorCreateTO) {
        JsonResponse response = new JsonResponse(true);
        operatorService.updateBaseMerchant(operatorCreateTO);
        return response;
    }

}
