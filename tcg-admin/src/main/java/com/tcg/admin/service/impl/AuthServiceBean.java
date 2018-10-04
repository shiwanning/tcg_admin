package com.tcg.admin.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorAuth;
import com.tcg.admin.persistence.springdata.IOperatorAuthRepository;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.to.UserInfo;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

@Service
@Transactional
public class AuthServiceBean implements AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceBean.class);
    
    @Autowired
    private IOperatorAuthRepository operatorAuthRepository;
    
    @Autowired
    private OperatorLoginService operatorLoginService;
    
    @Override
    public OperatorAuth getGoogleAuth(Integer operatorId) {
        return operatorAuthRepository.findByOperatorId(operatorId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String generateGoogleAuth(Operator operator) {
        
        OperatorAuth entity = getGoogleAuth(operator.getOperatorId());
        if(entity == null) {
            entity = createNewOperatorAuth(operator, operator.getOperatorId());
        } else {
            entity.setAuthKey(createNewGoogleAuthKey());
            entity.setUpdateOperatorName(operator.getOperatorName());
        }
        
        operatorAuthRepository.save(entity);
        
        return entity.getAuthKey();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean setGoogleAuthStatus(Integer operatorId, Boolean status) {
        OperatorAuth entity = getGoogleAuth(operatorId);
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        if(entity == null) {
            entity = createNewOperatorAuth(userInfo.getUser(), operatorId);
        }
        
        if(status) {
            entity.setLastPassTime(null);
        }
        
        entity.setStatus(status ? OperatorAuth.Status.ACTIVE : OperatorAuth.Status.INACTIVE);
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        
        operatorAuthRepository.save(entity);
        return true;
    }

    private OperatorAuth createNewOperatorAuth(Operator operator, Integer operatorId) {
        OperatorAuth entity = new OperatorAuth();
        entity.setOperatorId(operatorId);
        entity.setAuthType("GOOGLE_OTP");
        entity.setStatus(OperatorAuth.Status.INACTIVE);
        entity.setCreateOperatorName(operator.getOperatorName());
        entity.setUpdateOperatorName(operator.getOperatorName());
        entity.setAuthKey(createNewGoogleAuthKey());
        
        return entity;
    }
    
    @Override
    public String createNewGoogleAuthKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    @Override
    public void activeGoogleAuth(Integer operatorId, String authKey) {
        OperatorAuth entity = getGoogleAuth(operatorId);
        UserInfo<Operator> userInfo = operatorLoginService.getSessionUser(RequestHelper.getToken());
        
        if(entity == null) {
            entity = createNewOperatorAuth(userInfo.getUser(), operatorId);
        }

        entity.setStatus(OperatorAuth.Status.ACTIVE);
        entity.setAuthKey(authKey);
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        entity.setLastPassTime(null);
        
        operatorAuthRepository.save(entity);
    }

    @Override
    public boolean authorize(String key, Integer otp) {
        GoogleAuthenticator xAuth = new GoogleAuthenticator();
        return xAuth.authorize(key, otp);
    }

    @Override
    public void saveLastPassTime(Integer operatorId) {
        OperatorAuth entity = getGoogleAuth(operatorId);
        if(entity != null) {
            entity.setLastPassTime(new Date());
            operatorAuthRepository.save(entity);
        }
    }
    
}
