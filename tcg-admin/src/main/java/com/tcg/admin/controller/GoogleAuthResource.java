package com.tcg.admin.controller;

import java.util.Date;

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
import com.tcg.admin.utils.shiro.OtpUtils;

@RestController
@RequestMapping(value = "/resources/auth/google", produces = MediaType.APPLICATION_JSON_VALUE)
public class GoogleAuthResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthResource.class);
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private OperatorLoginService operatorLoginService;
    
    @GetMapping("/info")
    public JsonResponseT<AuthInfoTo> getInfo() {
        JsonResponseT<AuthInfoTo> response = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            response.setValue(new AuthInfoTo(false, userInfo.getOptValidTime()));
        } else {
            response.setValue(new AuthInfoTo(true, userInfo.getOptValidTime()));
        }
        return response;
    }
    
    @GetMapping("/key")
    public ResponseEntity<JsonResponseT<String>> getOtp() {
        JsonResponseT<String> response = new JsonResponseT<>(true);
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
        
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            String key = authService.generateGoogleAuth(userInfo.getUser());
            key = "otpauth://totp/" + userInfo.getUser().getOperatorName() + "?secret=" + key + "&issuer=TCG";
            response.setValue(key);
            return ResponseEntity.ok(response);
        } else if(userInfo.getOptValidTime() != null) {
            String key = authService.createNewGoogleAuthKey();
            userInfo.setGoogleAuthKey(key);
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
    public ResponseEntity<JsonResponse> bindKey(@RequestParam(value = "otp", required = false) String otp) {
        
        if(otp == null || Ints.tryParse(otp) == null) {
            return ResponseEntity.badRequest().body(new JsonResponse(false));
        }
        
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        String key;
        
        if(userInfo.getGoogleAuthKey() != null) {
            key = userInfo.getGoogleAuthKey();
        } else {
            OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
            key = operatorAuth.getAuthKey();
        }
        
        boolean isOtpValid = authService.authorize(key, Ints.tryParse(otp));
        
        if(isOtpValid) {
            authService.activeGoogleAuth(userInfo.getUser().getOperatorId(), key);
            userInfo.setGoogleAuthKey(null);
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
        
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        OperatorAuth operatorAuth = authService.getGoogleAuth(userInfo.getUser().getOperatorId());
        
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new JsonResponse(false));
        }
        
        boolean isOtpValid = authService.authorize(operatorAuth.getAuthKey(), fixedOtp);
        int operatorId = userInfo.getUser().getOperatorId();
        if(isOtpValid) {
            userInfo.setOptValidTime(new Date());
            if(OtpUtils.isNeedOtp(operatorId)) {
                OtpUtils.removeErrorCount(operatorId);
                operatorLoginService.loginFromOtp(userInfo.getUser().getOperatorId());
                OtpUtils.removeOtpData(operatorId);
            }
        } else {
            if(OtpUtils.isNeedOtp(operatorId)) {
                int errorCount = OtpUtils.addLoginErrorCount(operatorId);
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
    public ResponseEntity<JsonResponse> changeOtpStatus(@RequestParam(value = "operatorId", required = false) Integer operatorId) {
        OperatorAuth operatorAuth = authService.getGoogleAuth(operatorId);
        if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
            JsonResponse jr = new JsonResponse(false);
            jr.setErrorCode("NOT_BIND_OTP");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(jr);
        }
        authService.setGoogleAuthStatus(operatorId, false);
        return ResponseEntity.ok().body(new JsonResponse(true));
    }
    
}
