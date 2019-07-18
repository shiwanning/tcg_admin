package com.tcg.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.admin.model.Operator;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.to.OperatorCreateTO;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.to.response.JsonResponse;
import com.tcg.admin.to.response.JsonResponseT;
import com.tcg.admin.utils.AuthorizationUtils;

@RestController
@RequestMapping(value = "/resources/operator_sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorSessionResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(OperatorSessionResource.class);
	
    @Autowired
	private OperatorAuthenticationService operatorAuthService;

	/**
	 * Operator login the Business Web Subsystem, the login authentication
	 * is done by apache shiro realm mechanism, once login success , the server
	 * will return a token, the client devices should keep the token content.The
	 * token is used to be the session validation key on each resource
	 * acquiring.
	 * 
	 * 後臺登入
	 */
    @PostMapping
	public UserInfo<Operator> login(@RequestBody OperatorCreateTO operator) {
		UserInfo<Operator> userinfo = operatorAuthService.login(operator.getOperatorName(), operator.getPassword());
        userinfo.getUser().setPassword(null);
		return userinfo;
	}
    
    @PostMapping("/google-otp-login")
	public UserInfo<Operator> googleOtpLogin(@RequestBody OperatorCreateTO operator) {
		UserInfo<Operator> userinfo = operatorAuthService.loginByGoogleOtp(operator.getOperatorName(), operator.getPassword());
        userinfo.getUser().setPassword(null);
		return userinfo;
	}

	/**
	 * This will logout back-end operator, check if its existed in database and
	 * then remove from the server cache. 
	 * 
	 * 後臺登出(退出)
	 */
    @DeleteMapping("{token}")
	public void logout(@PathVariable("token") String token) {
		operatorAuthService.logout(token);
	}

	
	/**
	 * To verify the uuid token which is passed by the client devices is a valid session token or not.
	 * If it's not a valid token, the System will return the result : "SESSION_IS_INVALID".
	 * 查驗Session token是否合法
	 */
    @GetMapping("{token}/islogin")
	public JsonResponse getSession(@PathVariable("token") String token) {
		String result = AuthorizationUtils.verifyToken(token, true);
		JsonResponse jsonResponse = new JsonResponse(true);
		jsonResponse.setMessage(result);
		return jsonResponse;
	}

    @GetMapping("checkIdle/{token}")
    public JsonResponseT<Object> checkIdle(@PathVariable("token") String token) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        Boolean isIdle = operatorAuthService.checkIdle(token);
        response.setValue(isIdle);
        if(isIdle) {
        	LOGGER.info("token {} is idle.", token);
        }
        return response;
    }
}
