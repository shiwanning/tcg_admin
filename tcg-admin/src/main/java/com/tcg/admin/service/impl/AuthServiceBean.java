package com.tcg.admin.service.impl;

import java.util.Date;

import com.tcg.admin.model.BehaviorLog;
import com.tcg.admin.persistence.springdata.IBehaviorLogRepository;
import com.tcg.admin.persistence.springdata.IRoleRepository;
import com.tcg.admin.service.OperatorService;
import com.tcg.admin.utils.AuthorizationUtils;
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

    @Autowired
    private IRoleRepository iRoleRepository;

    @Autowired
    private AuthService authService;
    @Autowired
    private OperatorService operatorService;

    @Autowired
    private IBehaviorLogRepository behaviorLogRepository;
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
    public void activeGoogleAuth(Integer operatorId, String authKey, boolean isAuto) {
        OperatorAuth entity = getGoogleAuth(operatorId);
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();

        if(entity == null) {
            entity = createNewOperatorAuth(userInfo.getUser(), operatorId);
        }

        entity.setStatus(OperatorAuth.Status.ACTIVE);
        entity.setAuthKey(authKey);
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        entity.setLastPassTime(null);
        entity.setInputType(isAuto ? OperatorAuth.IsAuto.AUTO : OperatorAuth.IsAuto.MANUAL );

        operatorAuthRepository.save(entity);
    }
    @Override
    public void generateGoogleAuthBindRole(UserInfo<Operator> userInfo, Integer operatorId) {
        OperatorAuth entity = new OperatorAuth();
        entity.setOperatorId(operatorId);
        entity.setAuthType("GOOGLE_OTP");
        entity.setStatus(OperatorAuth.Status.ORIGIN);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        entity.setCreateOperatorName(userInfo.getUser().getOperatorName());
        entity.setUpdateOperatorName(userInfo.getUser().getOperatorName());
        entity.setAuthKey(createNewGoogleAuthKey());
        entity.setInputType(OperatorAuth.IsAuto.AUTO);
        operatorAuthRepository.save(entity);
    }

    @Override
    public void deleteByOperatorId(Integer operatorId) {

        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        operatorAuthRepository.deleteByOperatorId(operatorId);
        AuthorizationUtils.putIsAuthOrigin(operatorId, false);
        if(iRoleRepository.countGoogleActiveByOperator(operatorId) > 0){
            authService.generateGoogleAuthBindRole(userInfo, operatorId);
            AuthorizationUtils.putIsAuthOrigin(operatorId, true);
        }

    }

    @Override
    public void logSuccessLogin(Integer operatorId, String userName, Date lastLoginTime) {
        if(!AuthorizationUtils.isNeedOperation(operatorId)){
            updateBehaviorLogLogSuccess(userName, lastLoginTime);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Boolean setGoogleAuthStatus(Integer operatorId, Boolean status, boolean isAuto) {
        OperatorAuth entity = getGoogleAuth(operatorId);
        UserInfo<Operator> userInfo = RequestHelper.getCurrentUser();
        
        if(entity == null) {
            entity = createNewOperatorAuth(userInfo.getUser(), operatorId);
        }

        entity.setLastPassTime(null);
        entity.setInputType(isAuto ? OperatorAuth.IsAuto.AUTO : OperatorAuth.IsAuto.MANUAL);
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
        entity.setInputType(OperatorAuth.IsAuto.MANUAL);
        return entity;
    }
    
    @Override
    public String createNewGoogleAuthKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    @Override
    public void activeGoogleAuthName(Integer operatorId, String authKey, boolean isAuto, String userName) {
        OperatorAuth entity = getGoogleAuth(operatorId);

        entity.setStatus(OperatorAuth.Status.ACTIVE);
        entity.setAuthKey(authKey);
        entity.setUpdateOperatorName(userName);
        entity.setLastPassTime(null);
        entity.setInputType(isAuto ? OperatorAuth.IsAuto.AUTO : OperatorAuth.IsAuto.MANUAL );
        
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
    private void updateBehaviorLogLogSuccess(String userName, Date lastLoginTime){
        BehaviorLog currentLoginBehaviorLog = operatorService.getCurrentLoginBehaviorLog(userName);
        if(currentLoginBehaviorLog != null){
            currentLoginBehaviorLog.setRemark("SUCCESS");
            currentLoginBehaviorLog.setEndProcessDate(lastLoginTime);
            behaviorLogRepository.saveAndFlush(currentLoginBehaviorLog);
        }
    }
}
