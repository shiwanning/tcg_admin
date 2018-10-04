package com.tcg.admin.controller;

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

@RestController
@RequestMapping(value = "/resources/operator_sessions", produces = MediaType.APPLICATION_JSON_VALUE)
public class OperatorSessionResource {

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
		String result = operatorAuthService.verifyToken(token);
		JsonResponse jsonResponse = new JsonResponse(true);
		jsonResponse.setMessage(result);
		return jsonResponse;
	}
	
	/**
	 * Get value in the session by the key.
	 */
    @GetMapping("{token}/{key}/value")
	public JsonResponseT<Object> getSessionValue(@PathVariable("token") String token, @PathVariable("key") String key) {

		Object obj = operatorAuthService.getSessionValue(token, key);
		JsonResponseT<Object> response = new JsonResponseT<>(true);
		response.setValue(obj);
		return response;
	}

    @GetMapping("checkIdle/{token}")
    public JsonResponseT<Object> checkIdle(@PathVariable("token") String token) {
        JsonResponseT<Object> response = new JsonResponseT<>(true);
        response.setValue(operatorAuthService.checkIdle(token));
        return response;
    }
}
