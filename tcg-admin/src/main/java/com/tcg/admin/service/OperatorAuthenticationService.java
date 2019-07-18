package com.tcg.admin.service;

import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Operator;
import com.tcg.admin.to.UserInfo;

public interface OperatorAuthenticationService {

	/**
	 * <pre>
	 * This is a back-end operator login method, input Operator model with certain fields, 
	 * which will validates in the method. This will check username, password and activeflag status,
	 * also check with password rules, password must be modify over 90 days, 5 times password error lock for 30 mins.
	 * First time logging in will be force to modify the password. Once its success logged into system, 
	 * the operator details will be write into Server cache.
	 * 
	 * 後臺登錄(登入)
	 * </pre>
	 * 
	 * @param username The operator name.
	 * @param password            
	 * @return UserInfo<Operator> write to the server cache
	 * @throws AdminServiceBaseException
	 *             if fields validation fails or certain restricted rules met.
	 */
    UserInfo<Operator> login(String username, String password) throws AdminServiceBaseException;

	/**
	 * <pre>
	 * This will logout back-end operator, check if its existed in database and
	 * then remove from the server cache. 
	 * 
	 * 後臺登出(退出)
	 * </pre>
	 * 
	 * @param String token
	 * @throws AdminServiceBaseException
	 * 
	 */
    void logout(String token) throws AdminServiceBaseException;

    Boolean checkIdle(String token);

	UserInfo<Operator> loginByGoogleOtp(String username, String password) throws AdminServiceBaseException;
}
