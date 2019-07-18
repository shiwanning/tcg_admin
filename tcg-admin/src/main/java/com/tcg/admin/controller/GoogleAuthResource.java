package com.tcg.admin.controller;

import java.util.Date;

import com.tcg.admin.common.annotation.OperationLog;
import com.tcg.admin.common.constants.OperationFunctionConstant;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.SystemHelperTemp;
import com.tcg.admin.service.OperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.primitives.Ints;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorAuth;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.service.impl.OperatorLoginService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.AuthInfoTo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.AuthorizationUtils;

@RestController
@RequestMapping(value = "/resources/auth/google", produces = MediaType.APPLICATION_JSON_VALUE)
public class GoogleAuthResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthResource.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private OperatorLoginService operatorLoginService;

    @Autowired
    private OperatorService operatorService;
    
    @GetMapping("/info")
    public JsonResponseT<AuthInfoTo> getInfo() {
        JsonResponseT<AuthInfoTo> response = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            response.setValue(new AuthInfoTo(false, userInfo.getOptValidTime()));
        } else {
            response.setValue(new AuthInfoTo(true, userInfo.getOptValidTime()));
        }
        return response;
    }
    @PutMapping("/removeGoogle")
    @OperationLog(type = OperationFunctionConstant.REMOVE_GOOGLE_VERIFICATION)
    public JsonResponseT<SystemHelperTemp> removeGoogle( @RequestParam(value = "operatorId", required = false) Integer operatorId) {
        JsonResponseT<SystemHelperTemp> response = new JsonResponseT<>(true);

        authService.setGoogleAuthStatus(operatorId, false, false);
        return response;
    }
    @GetMapping("/key")
    public ResponseEntity<JsonResponseT<String>> getOtp() {
        JsonResponseT<String> response = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        
        OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
        
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            String key = authService.generateGoogleAuth(userInfo.getUser());
            key = "otpauth://totp/" + userInfo.getUser().getOperatorName() + "?secret=" + key + "&issuer=TCG";
            response.setValue(key);
            return ResponseEntity.ok(response);
        } else if(userInfo.getOptValidTime() != null) {
            String key = authService.createNewGoogleAuthKey();
            AuthorizationUtils.setGoogleAuthKey(userInfo, key);
            key = "otpauth://totp/" + userInfo.getUser().getOperatorName() + "?secret=" + key + "&issuer=TCG";
            response.setValue(key);
            return ResponseEntity.ok(response);
        } else {
            response.setErrorCode(AdminErrorCode.ALERADY_BIND);
            response.setMessage("Otp already bind.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
    }
    
    @PostMapping("/bind-key")
    @OperationLog(type = OperationFunctionConstant.ADD_GOOGLE_VERIFICATION)
    public ResponseEntity<JsonResponse> bindKey(
            @RequestParam(value = "otp", required = false) String otp,
            @RequestParam(value = "isAuto", required = false) boolean isAuto
            ) {
        
        if(otp == null || Ints.tryParse(otp) == null) {
            return ResponseEntity.badRequest().body(new JsonResponse(false));
        }
        
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        
        String key;
        
        if(userInfo.getGoogleAuthKey() != null) {
            key = userInfo.getGoogleAuthKey();
        } else {
            OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
            key = operatorAuth.getAuthKey();
        }
        
        boolean isOtpValid = authService.authorize(key, Ints.tryParse(otp));
        
        if(isOtpValid) {
            authService.activeGoogleAuth(userInfo.getUser().getOperatorId(), key, isAuto);
            userInfo.setGoogleAuthKey(null);
        }
        
        return ResponseEntity.ok().body(new JsonResponse(isOtpValid));
    }


    @PostMapping("/bind-key-auto")
    @OperationLog(type = OperationFunctionConstant.ADD_GOOGLE_VERIFICATION)
    public ResponseEntity<JsonResponse> bindKeyAuto(
            @RequestParam(value = "otp", required = false) String otp,
            @RequestParam(value = "isAuto", required = false) boolean isAuto,
            @RequestParam(value = "userName", required = false) String userName
    ) {

        if(otp == null || Ints.tryParse(otp) == null) {
            return ResponseEntity.badRequest().body(new JsonResponse(false));
        }

        Operator operator = operatorService.findOperatorByName(userName);
        if(operator == null){
            throw new AdminServiceBaseException(AdminErrorCode.CUSTOMER_NOT_EXIST_ERROR, "Username is not found");
        }

        OperatorAuth googleAuth = authService.getGoogleAuth(operator.getOperatorId());
        if(googleAuth == null){
            throw new AdminServiceBaseException(AdminErrorCode.NOT_BIND_GOOGLE_OTP, "NOT_BIND_GOOGLE_OTP");
        }
        String key = googleAuth.getAuthKey();
        boolean isOtpValid = authService.authorize(key, Ints.tryParse(otp));


        if(isOtpValid) {
            authService.activeGoogleAuthName(operator.getOperatorId(), key, isAuto, userName);
            AuthorizationUtils.putIsAuthOrigin(operator.getOperatorId(), false);
            authService.logSuccessLogin(operator.getOperatorId(), userName, operator.getProfile().getLastLoginTime());
        }
        return ResponseEntity.ok().body(new JsonResponse(isOtpValid));
    }
    
    
    @PostMapping("/valid-otp")
    public ResponseEntity<JsonResponse> validOtp(@RequestParam(value = "otp", required = false) String otp) {
    	Integer fixedOtp;
        if(otp == null || Ints.tryParse(otp) == null) {
        	fixedOtp = -1;
        } else {
        	fixedOtp = Ints.tryParse(otp);
        }
        
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        
        OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
        
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponse(false));
        }
        
        boolean isOtpValid = authService.authorize(operatorAuth.getAuthKey(), fixedOtp);
        int operatorId = userInfo.getUser().getOperatorId();
        if(isOtpValid) {
            AuthorizationUtils.setOptValidTime(userInfo, new Date());
            if(AuthorizationUtils.isNeedOtpToLogin(operatorId)) {
            	AuthorizationUtils.removeOtpErrorCount(operatorId);
                operatorLoginService.loginFromOtp(userInfo.getUser().getOperatorId());
                AuthorizationUtils.putNotNeedOtp(operatorId);
            }
            //获取当前登录时间
            Operator operator = operatorService.findOperatorByName(userInfo.getUser().getOperatorName());
            authService.logSuccessLogin(operatorId, userInfo.getUser().getOperatorName(), operator.getProfile().getLastLoginTime());
        } else {
            if(AuthorizationUtils.isNeedOtpToLogin(operatorId)) {
                int errorCount = AuthorizationUtils.addOtpErrorCount(operatorId);
                if(errorCount >= 3) {
                    operatorLoginService.lockUser(userInfo);
                    JsonResponse response = new JsonResponse(false);
                    response.setErrorCode("LOCK_USER");
                    return ResponseEntity.ok().body(response);
                }
            }
        }
        
        return ResponseEntity.ok().body(new JsonResponse(isOtpValid));
    }
    
    @PutMapping("/reset-key")
    @OperationLog(type = OperationFunctionConstant.REMOVE_GOOGLE_VERIFICATION)
    public ResponseEntity<JsonResponse> changeOtpStatus(@RequestParam(value = "operatorId", required = false) Integer operatorId) {
        OperatorAuth operatorAuth = authService.getGoogleAuth(operatorId);
        if(operatorAuth == null) {
            JsonResponse jr = new JsonResponse(false);
            jr.setErrorCode("NOT_BIND_OTP");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jr);
        }
        authService.deleteByOperatorId(operatorId);
        return ResponseEntity.ok().body(new JsonResponse(true));
    }
    
}
