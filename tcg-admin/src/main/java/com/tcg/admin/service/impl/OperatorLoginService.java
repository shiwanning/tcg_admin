package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.constants.SessionConstants;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.controller.core.session.SessionAttribute;
import com.tcg.admin.model.DomainProperties;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorPasswordHistory;
import com.tcg.admin.model.OperatorProfile;
import com.tcg.admin.persistence.OperatorPasswordHistoryRepositoryCustom;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.service.DomainService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.shiro.ShiroUtils;

@Service
@Transactional
public class OperatorLoginService extends CommonLoginService<Operator> {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorLoginService.class);

    protected Map<String, UserInfo<Operator>> userInfoMap = new HashMap<>();

    @Autowired
    private OperatorPasswordHistoryRepositoryCustom operatorPasswordHistoryRepositoryCustom;

    @Autowired
    private RoleMenuPermissionService roleMenuPermissionService;

    @Autowired
    private AuthService authService;
    
    @Autowired
    private DomainService domainService;
    
    @Autowired
    protected void initRepository(IOperatorRepository operatorRepository) {
        super.userRepositoryBase = operatorRepository;
    }

    // --------------------------------------------------------------------------------------------------------------
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<Operator> login(String operatorName, String password, LoginType loginType) {

        UserInfo<Operator> newOperatorInfo;

        try {

            // 登入主流程在父類別

        	DomainProperties dp = domainService.getDomainProperties(RequestHelper.getHost());
        	
        	if( (!dp.getLoginPassword() && loginType == LoginType.PASSWORD) ||
        		(!dp.getLoginGoogleOtp() && loginType == LoginType.GOOGLE_OTP)) {
        		throw new AdminServiceBaseException(AdminErrorCode.FORBIDDEN_LOGIN_TYPE);
        	}
        	
            newOperatorInfo = super.login(operatorName, password, loginType);
            String token = ShiroUtils.doOperatorLogin(operatorName, newOperatorInfo.getUser().getPassword());
            newOperatorInfo.setToken(token);
            List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(newOperatorInfo);
            List<Map<String,String>> companies = roleMenuPermissionService.getCompanies(newOperatorInfo);
            List<Map<String,String>> allMerchants = roleMenuPermissionService.getAllMerchant();
            List<Map<String,String>> allCompanies = roleMenuPermissionService.getAllCompanies();
            
            super.setSessionValue(newOperatorInfo.getToken(), SessionConstants.SESSION_KEY_USER, newOperatorInfo);
            super.setSessionValue(newOperatorInfo.getToken(), SessionConstants.OPERATOR_MERCHANTS,
                                  merchList);
            super.setSessionValue(newOperatorInfo.getToken(), SessionAttribute.TOKEN, newOperatorInfo.getToken());
            newOperatorInfo.setCompanies(objectMapper.writeValueAsString(companies));
            newOperatorInfo.setMerchants(objectMapper.writeValueAsString(merchList));
            newOperatorInfo.setAllMerchants(objectMapper.writeValueAsString(allMerchants));
            newOperatorInfo.setAllCompanies(objectMapper.writeValueAsString(allCompanies));
            LOGGER.info("Loggin Operator success!!");

            return newOperatorInfo;

        } catch (AdminServiceBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }

    // --------------------------------------------------------------------------------------------------------------

    @Override
    protected String getUserNameFormatErrorDescription() {
        return "operator name can't be empty.";
    }

    @Override
    protected String getUserNameNotExistErrorCode() {
        return AdminErrorCode.OPERATOR_NOT_EXIST_ERROR;
    }

    @Override
    protected String getUserNameNotExistErrorDescription() {
        return "this operator does not exist.";
    }

    @Override
    protected Date getPasswdLastModifyDate(Operator userRecord) {
        OperatorPasswordHistory operatorPasswordHistory =
                operatorPasswordHistoryRepositoryCustom.findLastPasswordHistory(userRecord.getOperatorName());
        return operatorPasswordHistory != null ? operatorPasswordHistory.getCreateTime() : null;
    }

    @Override
    protected void removeUserInfo(String userName) {
        userInfoMap.remove(userName);
    }

    @Override
    public UserInfo<Operator> getUserInfo(String userName) {
        return userInfoMap.get(userName);
    }

    @Override
    public UserInfo<Operator> initUserInfo(String userName, Operator userRecord) {
        userInfoMap.remove(userName);
        UserInfo<Operator> userInfo = new UserInfo<>();
        userInfo.setCount(0);
        userInfo.setPaymentCount(0);
        userInfo.setUser(userRecord);
        userInfo.setUserName(userName);
        userInfo.setType(userRecord.getActiveFlag());
        userInfo.setErrorTime(null);
        userInfoMap.put(userName, userInfo);
        return userInfo;
    }

    @Override
    public void setUserInfo(String userName, UserInfo<Operator> userInfo) {
        userInfoMap.put(userName, userInfo);
    }

    public void loginFromOtp(Integer operatorId) {
        Operator operator = userRepositoryBase.findOne(operatorId);
        OperatorProfile operatorProfile = operatorProfileRepository.findOne(operatorId);
        
        operator.setErrorCount(0);
        operator.setErrorTime(null);
        
        operatorProfile.setLastLoginTime(new Date());
        
        saveLoginLog(operatorProfile, RequestHelper.getIp());
        updateUserRecord(operator);
        authService.saveLastPassTime(operatorId);
    }

    public void lockUser(UserInfo<Operator> userInfo) {
        Operator newOp = userRepositoryBase.findOne(userInfo.getUser().getOperatorId());
        newOp.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.getStatusCode());
        userInfo.setType(LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.getStatusCode());
        super.updateUserRecord(newOp);
    }


}
