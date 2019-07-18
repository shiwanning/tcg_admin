package com.tcg.admin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.constants.SessionStatusConstant;
import com.tcg.admin.model.Operator;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.AuthorizationUtils;

@Service
@Transactional
public class OperatorAuthenticationServiceImpl implements OperatorAuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorAuthenticationServiceImpl.class);

    @Autowired
    private OperatorLoginService operatorLoginService;


    // ------------------------------------------------------------------------------------------------------------------------


    @Override
    public void logout(String token) {
        operatorLoginService.logout(token);
    }

    @Override
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<Operator> login(String username, String password) {
        return operatorLoginService.login(username, password, LoginType.PASSWORD);
    }
    
    @Override
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<Operator> loginByGoogleOtp(String username, String password) {
        return operatorLoginService.login(username, password, LoginType.GOOGLE_OTP);
    }

    @Override
    public Boolean checkIdle(String token) {
    	return AuthorizationUtils.verifyToken(token, false) == SessionStatusConstant.INVALID.getType();
    }

}
