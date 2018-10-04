package com.tcg.admin.service.impl;

import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.model.Operator;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.service.OperatorAuthenticationService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.shiro.ShiroUtils;

@Service
@Transactional
public class OperatorAuthenticationServiceImpl implements OperatorAuthenticationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorAuthenticationServiceImpl.class);

    @Autowired
    private OperatorLoginService operatorLoginService;

    @Autowired
    private IOperatorRepository operatorRepository;


    // ------------------------------------------------------------------------------------------------------------------------


    @Override
    public void logout(String token) {
        try {

            operatorLoginService.logout(token);
        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }


    // ------------------------------------------------------------------------------------------------------------------------


    @Override
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<Operator> login(String username, String password) {
        try {
            return operatorLoginService.login(username, password, LoginType.PASSWORD);
        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<Operator> loginByGoogleOtp(String username, String password) {
        try {
            return operatorLoginService.login(username, password, LoginType.GOOGLE_OTP);
        } catch (AdminServiceBaseException usbe) {
            throw usbe;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }


    // ------------------------------------------------------------------------------------------------------------------------


    public boolean isOperatorNameExist(String operatorName) {
        try {
            Operator operatorRecord = operatorRepository.findByOperatorName(operatorName);
            return operatorRecord != null && operatorRecord.getActiveFlag() != LoginConstant.ACTIVE_FLAG_DELETE.getStatusCode();
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }


    // ------------------------------------------------------------------------------------------------------------------------


    @Override
    public String verifyToken(String token) {
        return ShiroUtils.verifyToken(token, true);
    }


    // ------------------------------------------------------------------------------------------------------------------------


    @Override
    public Object getSessionValue(String token, String key) {
        return operatorLoginService.getSessionValue(token, key);
    }


    // ------------------------------------------------------------------------------------------------------------------------


    @Override
    public void setSessionValue(String token, String key, Object value) {
        operatorLoginService.setSessionValue(token, key, value);

    }

    @Override
    public UserInfo<Operator> getOperatorByToken(String token) {
        return operatorLoginService.getSessionUser(token);
    }

    @Override
    public Boolean checkIdle(String token) {
        Long maxIdleTime = 72000001L; //120 mins
        Session session = ShiroUtils.getSession(token);
        if(session == null) {
        	LOGGER.info("{} is not in session, idle", token);
        	return true; 
        }
        Long lastAccessTime = session.getLastAccessTime().getTime();
        Long currentTime = System.currentTimeMillis();
        Long idleTime = currentTime - lastAccessTime;
        LOGGER.debug("{}: idle {} s", token, idleTime/1000.);
        return idleTime.compareTo(maxIdleTime) > 0;
    }

}
