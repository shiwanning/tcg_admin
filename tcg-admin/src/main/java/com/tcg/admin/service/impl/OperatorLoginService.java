package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tcg.admin.model.*;
import com.tcg.admin.persistence.springdata.IBehaviorLogRepository;
import com.tcg.admin.service.OperatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.persistence.OperatorPasswordHistoryRepositoryCustom;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.service.DomainService;
import com.tcg.admin.service.RoleMenuPermissionService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.AuthorizationUtils;

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
    private OperatorService operatorService;

    @Autowired
    private IBehaviorLogRepository behaviorLogRepository;

    
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
            AuthorizationUtils.doOperatorLogin(newOperatorInfo);
            
            List<Map<String,String>> merchList = roleMenuPermissionService.getMerchants(newOperatorInfo);
            List<Map<String,String>> companies = roleMenuPermissionService.getCompanies(newOperatorInfo);
            List<Map<String,String>> allMerchants = roleMenuPermissionService.getAllMerchant();
            List<Map<String,String>> allCompanies = roleMenuPermissionService.getAllCompanies();
            
            newOperatorInfo.setCompanies(objectMapper.writeValueAsString(companies));
            newOperatorInfo.setMerchants(objectMapper.writeValueAsString(merchList));
            newOperatorInfo.setAllMerchants(objectMapper.writeValueAsString(allMerchants));
            newOperatorInfo.setAllCompanies(objectMapper.writeValueAsString(allCompanies));
            
            LOGGER.info("Loggin Operator success!!");

            if(!AuthorizationUtils.isNeedOperation(newOperatorInfo.getUser().getOperatorId())){
                updateBehaviorLogLogSuccess(operatorName, newOperatorInfo.getUser().getOperatorId());
            }

            return newOperatorInfo;

        } catch (AdminServiceBaseException e) {
            throw e;
        } catch (Exception e) {
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
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

    public void removeAllUser(){
        userInfoMap.clear();
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

    public Date loginFromOtp(Integer operatorId) {
        Operator operator = userRepositoryBase.findOne(operatorId);
        OperatorProfile operatorProfile = operator.getProfile();
        
        operator.setErrorCount(0);
        operator.setGoogleErrorCount(0);
        operator.setErrorTime(null);
        
        operatorProfile.setLastLoginTime(new Date());

        operatorProfile.setLastLoginIP(RequestHelper.getIp());
        operatorProfile.setLastLoginTime(new Date());
        operatorProfile.setUpdateTime(new Date());

        updateUserRecord(operator);
        authService.saveLastPassTime(operatorId);
        return  operatorProfile.getLastLoginTime();
    }

    public void lockUser(UserInfo<Operator> userInfo) {
        Operator newOp = userRepositoryBase.findOne(userInfo.getUser().getOperatorId());
        newOp.setActiveFlag(LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.getStatusCode());
        userInfo.setType(LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.getStatusCode());

        initUserInfo(newOp.getOperatorName(), newOp);
        super.updateUserRecord(newOp);
    }

    private void updateBehaviorLogLogSuccess(String userName, Integer operatorId){

        Operator newOp = userRepositoryBase.findOne(operatorId);

        BehaviorLog currentLoginBehaviorLog = operatorService.getCurrentLoginBehaviorLog(userName);

        if(currentLoginBehaviorLog != null){
            currentLoginBehaviorLog.setRemark("SUCCESS");
            currentLoginBehaviorLog.setEndProcessDate(newOp.getProfile().getLastLoginTime());
            behaviorLogRepository.saveAndFlush(currentLoginBehaviorLog);
        }
    }





}
