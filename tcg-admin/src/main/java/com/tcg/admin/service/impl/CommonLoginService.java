package com.tcg.admin.service.impl;

import java.util.Date;
import java.util.List;

import com.tcg.admin.model.*;
import com.tcg.admin.persistence.springdata.*;
import com.tcg.admin.service.OperatorService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.primitives.Ints;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.constants.SessionConstants;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.AuthorizationUtils;
import com.tcg.admin.utils.IpUtils;
import com.tcg.admin.utils.MD5Utils;

/**
 * Only OperatorLoginService extends the CommonLoginService.
 */
public abstract class CommonLoginService<T extends UserPrincipal> {

    public static final String SESSION_KEY_USER = SessionConstants.SESSION_KEY_USER;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonLoginService.class);
    
    @Autowired
    protected IUserRepositoryBase<T> userRepositoryBase;
    
    @Autowired
    private ISystemParameterRepository systemParameterRepository;
    
    @Autowired
    private IMerchantPropertiesRepository merchantPropertiesRepository;
    
    @Autowired
    private IMerchantWhitelistIpRepository merchantWhitelistIpRepository;

    @Autowired
    protected IOperatorProfileRepository operatorProfileRepository;

    @Autowired
    private IBehaviorLogRepository behaviorLogRepository;
    
    @Autowired
    private AuthService authService;

    @Autowired
    private  IOperatorPasswordHistoryRepository iOperatorPasswordHistoryRepository;

    @Autowired
    private  OperatorService operatorService;
    
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<T> login(String userName, String password, LoginType loginType) {

        validate(userName, password);
        try {

            UserInfo<T> userInfo = getUserInfo(userName); //get UserInfo in memory variable

            T userRecord = getUserRecord(userName);
            Integer flag;
            if (userInfo != null) {
                flag = userInfo.getUser().getActiveFlag();
            } else {
                flag = userRecord.getActiveFlag();
                userInfo = new UserInfo<>();
                userInfo.setUser(userRecord);
                setUserInfo(userName, userInfo);
            }
            LoginConstant activeFlag = LoginConstant.getByStatusCode(flag);

            Operator operator = (Operator) userRecord;

            // 已经登入成功，下边填入用户资讯并保存

            OperatorProfile profile = operator.getProfile();

            OperatorAuth oa = authService.getGoogleAuth(profile.getOperatorId());
            if(loginType == LoginType.GOOGLE_OTP && (oa == null || oa.getStatus() == OperatorAuth.Status.ORIGIN)){
                updateBehaviorLogLoginError(AdminErrorCode.GOOGLE_AUTHENTICATION_IS_NOT_SETUP, userName);
                throw new AdminServiceBaseException(AdminErrorCode.GOOGLE_AUTHENTICATION_IS_NOT_SETUP, "Google Authentication is not setup");
            }

            // ------------------------------------------以下為正式商業邏輯驗證------------------------------------------

            //验证逻辑过程
            validateProcess(userRecord, activeFlag, userName, password, loginType, operator, userInfo);


            // 檢查 IP
            if (!checkUserIp(userRecord)) {
                updateBehaviorLogLoginError(AdminErrorCode.IP_NOT_ALLOW, userName);
                throw new AdminServiceBaseException(AdminErrorCode.IP_NOT_ALLOW, "invalid user ip.");
            }


            
            UserInfo<T> newUserInfo = initUserInfo(userName, userRecord);
            String ip = RequestHelper.getIp();


            // -----------------------------以下是使用者首次登入相關邏輯-------------------------------

            AuthorizationUtils.putIsNeedReset(operator.getOperatorId(), operator.getIsNeedReset() == 1);

            setUserFirstLoginInformation(userRecord, newUserInfo, profile);

            newUserInfo.setLastLoginTime(profile.getLastLoginTime());
            newUserInfo.setLastPasswdModifyTime(profile.getPasswdLastModifyDate());
            newUserInfo.setLastPassOtpTime(oa == null ? null : oa.getLastPassTime());

            //如果谷歌验证状态origin状态,则强制用户绑定
            newUserInfo.setOtpAuthActive(oa == null ? null : oa.getStatus());
            if(oa != null && oa.getStatus() == OperatorAuth.Status.ORIGIN){
                String key = "otpauth://totp/" + userName + "?secret=" + oa.getAuthKey() + "&issuer=TCG";
                newUserInfo.setGoogleAuthKey(key);
            }
            newUserInfo.setLoginIp(ip);
            newUserInfo.setLastLoginIp(profile.getLastLoginIP());
            newUserInfo.setLoginPasswordFailCount(userRecord.getErrorCount());
            newUserInfo.setLoginType(loginType);
            
            // 驗證通過解除鎖定
            cleanUserErrorInformation(userRecord, userInfo);

            userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());



            if(oa!=null){
                AuthorizationUtils.putIsAuthOrigin(operator.getOperatorId(), oa.getStatus() == OperatorAuth.Status.ORIGIN);
            }


            //不需要认证码才写入日志
            if(!newUserInfo.isNeedOtp() || loginType == LoginType.GOOGLE_OTP ) {
                userRecord.setErrorCount(0);
                userRecord.setGoogleErrorCount(0);

                profile.setLastLoginIP(ip);
                profile.setLastLoginTime(new Date());
                profile.setUpdateTime(new Date());
//                saveLoginLog(profile, ip);
                updateUserRecord(userRecord);

                AuthorizationUtils.putNotNeedOtp(operator.getOperatorId());
            } else {
            	AuthorizationUtils.putNeedOtp(operator.getOperatorId());
            }




            // 所有的登入查驗與動作完成後, 更新UserInfo.status 為 true
            newUserInfo.setStatus(true);

            return newUserInfo;
        } catch (AdminServiceBaseException usbe) {
            LOGGER.info("login fail! {}", usbe.getErrorCode());
            throw usbe;
        } catch (Exception e) {
            LOGGER.error("login Run Time Error!", e);
            updateBehaviorLogLoginError(AdminErrorCode.UNKNOWN_ERROR, userName);
            throw new AdminServiceBaseException(AdminErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }


    private void updateBehaviorLogLoginError(String adminErrorCode, String userName){
        BehaviorLog currentLoginBehaviorLog = operatorService.getCurrentLoginBehaviorLog(userName);
        if(currentLoginBehaviorLog != null){
            currentLoginBehaviorLog.setRemark(adminErrorCode);
            behaviorLogRepository.saveAndFlush(currentLoginBehaviorLog);
        }
    }
    private void validBindGoogleOtp(LoginType loginType, Integer operatorId, String userName) {
    	if(loginType == LoginType.GOOGLE_OTP) {
	    	OperatorAuth oa = authService.getGoogleAuth(operatorId);
	    	if(oa == null || oa.getStatus() == OperatorAuth.Status.INACTIVE) {
                updateBehaviorLogLoginError(AdminErrorCode.NOT_BIND_GOOGLE_OTP, userName);
                throw new AdminServiceBaseException(AdminErrorCode.NOT_BIND_GOOGLE_OTP, "Not Bind Google Otp");
	    	}
    	}
	}

	protected void saveLoginLog(OperatorProfile profile, String lastLoginIP) {
        Date now = new Date();
        profile.setLastLoginIP(lastLoginIP);
        profile.setLastLoginTime(now);
        profile.setUpdateTime(now);
        
        operatorProfileRepository.save(profile);
    }

    private boolean checkUserIp(T userRecord) {
        Operator op = (Operator) userRecord;
        
        String baseMerchant = op.getBaseMerchantCode();
        
        if(baseMerchant == null) {
            return true;
        }
        
        MerchantProperties mp = merchantPropertiesRepository.findByMerchantCode(baseMerchant);
        if(mp == null || !"Y".equals(mp.getWhitelistFunction())) {
            return true;
        }
        
        String ip = RequestHelper.getIp();
        for(MerchantWhitelistIp ipvo : merchantWhitelistIpRepository.findByMerchantCode(baseMerchant)) {
            if(IpUtils.contain(ipvo.getIp(), ip)) {
                return true;
            }
        }
        
        LOGGER.info("user ip try to login but check false, ip: {}, user: {}, baseMerchant: {}", ip, op.getOperatorName(), baseMerchant);
        return false;
    }

    protected void validate(final String userName, final String password) {
        checkParameterAvailable(userName, password);
    }

    private void checkParameterAvailable(String userName, String password) {
        if (StringUtils.isBlank(userName)) {
            // user name 為空
            updateBehaviorLogLoginError(AdminErrorCode.NAME_FORMAT_ERROR, userName);
            throw new AdminServiceBaseException(AdminErrorCode.NAME_FORMAT_ERROR, getUserNameFormatErrorDescription());
        }
        if (StringUtils.isBlank(password)) {
            // password 為空
            updateBehaviorLogLoginError(AdminErrorCode.PWD_FORMAT_ERROR, userName);
            throw new AdminServiceBaseException(AdminErrorCode.PWD_FORMAT_ERROR, "password can't be empty.");
        }
        if (!checkUserExists(userName)) {
            // user name 不存在
            updateBehaviorLogLoginError(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, userName);
            throw new AdminServiceBaseException(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, "Username or Password is not correct ");
        }
    }

    public void logout(String token) {
    	if (token == null) {
        	LOGGER.info("logout without token.");
        	return;
        }
    	//登出的同时清除缓存
        UserInfo<Operator> userInfo = AuthorizationUtils.getSessionUser(token);
        if (null == userInfo || null == userInfo.getUser()) {
            return;
        }
    	this.removeUserInfo(userInfo.getUser().getOperatorName());
        AuthorizationUtils.removeSession(token);
    }

    /**
     * check 使用者狀態是否為Forbidden
     * 
     * @param userFlag {@link LoginConstant}
     * @return boolean 如使用者並非Forbidden&Terminate : false, ActiveFlagLoginSuccess:true
     */
    protected Boolean checkUserIsForbidden(final LoginConstant userFlag) {
        // 後台人員手動禁用
        return LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.equals(userFlag)
               || LoginConstant.ACTIVE_FLAG_DELETE.equals(userFlag)
                || LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.equals(userFlag)
                || LoginConstant.ACTIVE_FLAG_SYSTEM_LOGIN_PROHIBITED.equals(userFlag);
    }

    /**
     * check 使用者狀態是否為Forbidden
     *
     * @param userFlag {@link LoginConstant}
     * @return boolean 如使用者状态为密码登入禁止状态并且不是谷歌登入（）
     */
    protected Boolean checkUserIsPasswordForbiddenAndLoginByGoogle(final LoginConstant userFlag, LoginType loginType) {
        return LoginConstant.ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED.equals(userFlag) && loginType == LoginType.PASSWORD;
    }


    protected Boolean checkUserErrorIsMaximum(final T userRecord) {
        // 如果使用者錯誤次數已經大於五次
        return userRecord.getErrorCount() > 4 && null == userRecord.getErrorTime();
    }


    protected Boolean checkUserGoogleErrorIsMaximum(final T userRecord) {
        // 如果使用者錯誤次數已經大於五次
        return userRecord.getGoogleErrorCount() > 2 ;
    }
    /**
     * check 使用者錯誤次數是否達到上線
     * 
     * @param userFlag {@link LoginConstant}
     * @param userRecord {@link UserInfo}
     * @return boolean 1.使用者最後錯誤時間未超過當前時間30分鐘 :false<br>
     *         2.使用者最後錯誤時間已超過當前時間30分鐘:true<br>
     *         3.使用這狀態正常:true<br>
     */
    protected Boolean checkUserIsTerminative(final LoginConstant userFlag, T userRecord) {
        final Date nowDate = new Date();

        if (LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.equals(userFlag) && userRecord.getErrorTime() != null) {
            Date passTime = DateUtils.addMinutes(userRecord.getErrorTime(), 30);
            // 未超過30分鐘
            if (nowDate.before(passTime)) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

    /**
     * check 使用者登入是否達到錯誤次數上限
     *
     * @param userRecord
     * @param userInfo {@link UserInfo}
     * @throws AdminServiceBaseException 達到錯誤次數上限則拋出對應例外
     */
    protected Boolean checkUserPasswordLimit(final T userRecord, LoginType loginType) {
        return userRecord.getErrorCount() < 5 && loginType == LoginType.PASSWORD || userRecord.getGoogleErrorCount() < 3 && loginType == LoginType.GOOGLE_OTP;
    }

    /**
     * check 使用者登入是否達到谷歌认证錯誤次數上限
     *
     * @param userRecord
     * @param userInfo {@link UserInfo}
     * @throws AdminServiceBaseException 達到錯誤次數上限則拋出對應例外
     */
    protected Boolean checkUserGoogleLimit(final T userRecord) {
        return userRecord.getGoogleErrorCount() < 3;
    }

    protected void cleanUserErrorInformation(final T userRecord, final UserInfo<T> userInfo) {
        userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
        userRecord.setErrorTime(null);
        userInfo.setType(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
    }

    protected void setUserFirstLoginInformation(final T userRecord, final UserInfo<T> userInfo, OperatorProfile profile) {
        Date passwordLastModifyDate = getPasswdLastModifyDate(userRecord);
        UserInfo<Operator> userInfo1 = (UserInfo<Operator>) userInfo;
        Operator op = (Operator) userRecord;
        List<OperatorPasswordHistory> operatorPasswordHistory = iOperatorPasswordHistoryRepository.getOperatorPasswordHistory(userInfo1.getUser().getOperatorName());

        if (passwordLastModifyDate == null) {
            // 初次登入, 強制修改密碼 (後台不會擋初次登入)
            userInfo.setFirstTimeLogin(true);
            userInfo.setPasswordExpired(true);
            AuthorizationUtils.putIsNeedReset(op.getOperatorId(), true);
        } else {
            final Date nowDate = new Date();
            // 檢查 密碼(LastModifyDate)是否超過系統設定天數(Platform Service)
            SystemParameter passwordKeepDaysParam = systemParameterRepository.findOne(SystemParameter.MEMBER_PASSWORD_KEEP_DAYS);
            LOGGER.info("getPasswordKeepDays={}", passwordKeepDaysParam.getParamValue());
            int passwordKeepDays = Integer.parseInt(passwordKeepDaysParam.getParamValue());
            if (nowDate.after(DateUtils.addDays(passwordLastModifyDate, passwordKeepDays))) {
                // 密碼過期, 強制修改密碼
                userInfo.setPasswordExpired(true);
                AuthorizationUtils.putIsNeedReset(op.getOperatorId(), true);
            }
            if(op.getIsNeedReset() == 1){
                userInfo.setPasswordExpired(true);
                AuthorizationUtils.putIsNeedReset(op.getOperatorId(), true);
            }
        }
    }

    protected boolean isUserAuthorized(T userRecord, final String password) {
        // Currently the userRecord is Operator , so it's only support MD5 Hashing.
        return MD5Utils.encrypt(password).equals(userRecord.getPassword());
    }

    protected boolean checkUserUnauthorized(T userRecord, final String password, LoginType loginType) {
    	if(loginType == LoginType.PASSWORD) {
    		return !isUserAuthorized(userRecord, password);
    	} else {
    		return !isUserAuthorizedGoogleOtp(userRecord, password);
    	}
    }

    private boolean isUserAuthorizedGoogleOtp(T userRecord, String password) {
    	Integer otp = Ints.tryParse(password);
		if(otp == null) {
			otp = -1;
		}
		OperatorAuth operatorAuth = authService.getGoogleAuth(((Operator)userRecord).getOperatorId());
		if(operatorAuth == null || operatorAuth.getStatus() == OperatorAuth.Status.INACTIVE) {
			return false;
		}
		return authService.authorize(operatorAuth.getAuthKey(), otp);
	}

    protected void setUserErrorCount(T userRecord, LoginType loginType) {
        if(loginType == LoginType.PASSWORD) {
            userRecord.setErrorCount(userRecord.getErrorCount() + 1);
        }else {
            userRecord.setGoogleErrorCount(userRecord.getGoogleErrorCount() + 1);
        }
    }


    protected void validateProcess(T userRecord, final LoginConstant activeFlag,String userName, final String password,LoginType loginType,Operator operator, final UserInfo<T> userInfo){
        // 檢查帳戶是否處於密碼錯誤次數過多狀態


        OperatorAuth googleAuth = authService.getGoogleAuth(((Operator) userRecord).getOperatorId());
//        if (checkUserIsTerminative(activeFlag, userRecord)) {
//            LOGGER.info("user login unauthorized for terminative: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
//            updateBehaviorLogLoginError(AdminErrorCode.FORBIDDEN_LOGIN_ERROR, userName);
//            throw new AdminServiceBaseException(AdminErrorCode.FORBIDDEN_LOGIN_ERROR, "this user must wait 30 mins to login again.");
//        }

        // 檢查帳戶是否禁用
        if (checkUserIsForbidden(activeFlag)) {
            if(LoginConstant.ACTIVE_FLAG_DELETE.equals(activeFlag)){
                removeUserInfo(userName);
                updateBehaviorLogLoginError(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, userName);
                throw new AdminServiceBaseException(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, "Username or Password is not correct");
            }
            LOGGER.info("user login unauthorized for terminative: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
            removeUserInfo(userName);
            updateBehaviorLogLoginError(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, userName);
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "this user has been forbidden to login.");
        }
        //检查用户是否已经30天未登入，如果未登入，状态改为手动禁用
//        if(checkUserThirtyDaysNotLogin(profile)){
//            LOGGER.info("The user has not logged in for 30 days: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
//            userInfo.setType(LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.getStatusCode());
//            userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.getStatusCode());
//            updateUserRecord(userRecord);
//            updateBehaviorLogLoginError(AdminErrorCode.NOT_LOGIN_BEEN_THIRTY_DAYS, userName);
//            throw new AdminServiceBaseException(AdminErrorCode.NOT_LOGIN_BEEN_THIRTY_DAYS, "The user has not logged in for 30 days.");
//        }
        // 检查帐户是否处于密码登录禁用状态并且是否以谷歌验证登入
        if (checkUserIsPasswordForbiddenAndLoginByGoogle(activeFlag, loginType)) {
            removeUserInfo(userName);
            if(googleAuth == null || googleAuth.getStatus() == OperatorAuth.Status.ORIGIN){
                updateBehaviorLogLoginError(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, userName);
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "this user has been forbidden to login.");
            }
            LOGGER.info("user login unauthorized for password login prohibited: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
            updateBehaviorLogLoginError(AdminErrorCode.OPERATOR_LOGIN_PROHIBITED, userName);
            throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_LOGIN_PROHIBITED, "this user has been forbidden to login by password.");
        }

        // 檢察帳號密碼授權是否正確
        if (checkUserUnauthorized(userRecord, password, loginType)) {
            LOGGER.info("user login unauthorized: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
            setUserErrorCount(userRecord, loginType);

            // 檢察密碼錯誤次數或者google錯誤次數
            if (checkUserPasswordLimit(userRecord, loginType)) {
                updateUserRecord(userRecord);
                validBindGoogleOtp(loginType, ((Operator)userRecord).getOperatorId(), userName);
                if (loginType == LoginType.GOOGLE_OTP ) {
                    updateBehaviorLogLoginError(AdminErrorCode.INCORRECT_GOOGLE_PWD_ERROR, userName);
                    throw new AdminServiceBaseException(AdminErrorCode.INCORRECT_GOOGLE_PWD_ERROR, "Google password is not correct");
                }
                updateBehaviorLogLoginError(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, userName);
                throw new AdminServiceBaseException(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, "Username or Password is not correct");
            }
            // 檢察密碼錯誤次數是否超過5次
            if (checkUserErrorIsMaximum(userRecord)) {

                if(googleAuth == null || googleAuth.getStatus() == OperatorAuth.Status.ORIGIN){
                    userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED.getStatusCode());
                    userRecord.setErrorTime(new Date());
                    userInfo.setType(LoginConstant.ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED.getStatusCode());
                    updateBehaviorLogLoginError(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, userName);
                    updateUserRecord(userRecord);
                    removeUserInfo(userName);
                    throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "this user has been forbidden to login.");
                }
                userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED.getStatusCode());
                userRecord.setErrorTime(new Date());
                userInfo.setType(LoginConstant.ACTIVE_FLAG_PASSWORD_LOGIN_PROHIBITED.getStatusCode());
                updateUserRecord(userRecord);
                removeUserInfo(userName);
                updateBehaviorLogLoginError(AdminErrorCode.OPERATOR_LOGIN_PROHIBITED, userName);
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_LOGIN_PROHIBITED, "this user has been forbidden to login by password.");
            }

            // 检查Google验证错误次数是否超过三次
            if (checkUserGoogleErrorIsMaximum(userRecord)) {
                userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.getStatusCode());
                userRecord.setErrorTime(new Date());
                updateUserRecord(userRecord);
                userInfo.setType(LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.getStatusCode());
                removeUserInfo(userName);
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "this user has been forbidden to login.");
            }

            // 已經超過30分鐘 但認證錯誤 則再次鎖住30分鐘
//            if (!checkUserIsTerminative(activeFlag, userRecord)) {
//                userRecord.setErrorTime(new Date());
//            }

            updateUserRecord(userRecord);
            updateBehaviorLogLoginError(AdminErrorCode.FORBIDDEN_LOGIN_ERROR, userName);
            throw new AdminServiceBaseException(AdminErrorCode.FORBIDDEN_LOGIN_ERROR, "this user must wait 30 mins to login again.");
        }
    }

    private boolean checkUserThirtyDaysNotLogin(OperatorProfile profile) {
        if(profile.getLastLoginTime() == null){
            return  false;
        }
        return ((new Date().getTime() - profile.getLastLoginTime().getTime()) / (1000 * 24 * 60 * 60)) > 30;
    }

    /**
     * 移除memory user info
     */
    protected abstract void removeUserInfo(String userName);

    /**
     * 取得帳號格式錯誤敘述
     * 
     * @return String
     */
    protected abstract String getUserNameFormatErrorDescription();

    /**
     * 檢查用戶是否存在
     * 
     * @return boolean
     */
    public boolean checkUserExists(String userName) {
        if (StringUtils.isBlank(userName)) {
            return Boolean.FALSE;
        }

        T user = getUserRecord(userName);
        if (user != null) {
            LoginConstant userStatus = LoginConstant.getByStatusCode(user.getActiveFlag());
            if (userStatus != null && userStatus != LoginConstant.ACTIVE_FLAG_DELETE) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * 取得用戶不存在時的錯誤代碼
     * 
     * @return String
     */
    protected abstract String getUserNameNotExistErrorCode();

    /**
     * 取得用戶不存在時的錯誤敘述
     * 
     * @return String
     */
    protected abstract String getUserNameNotExistErrorDescription();

    /**
     * 取得存於memory中的用戶資訊
     * 
     * @return
     */
    public abstract UserInfo<T> getUserInfo(String userName);



    /**
     * 進DB取得用戶資訊
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    protected T getUserRecord(String userName) {
            return (T) ((IOperatorRepository) userRepositoryBase).findByOperatorName(userName);
    }

    /**
     * 更新DB中的用戶資訊
     * 
     */
    protected void updateUserRecord(T userRecord) {
         ((IOperatorRepository) userRepositoryBase).saveAndFlush((Operator) userRecord);
    }

    /**
     * 初始化/重置 userName info
     * 
     * @param userName
     */
    public UserInfo<T> initUserInfo(String userName) {
        T userInfo = getUserRecord(userName);
        return this.initUserInfo(userName, userInfo);
    }

    /**
     * 重置memory中的用戶資訊
     * 
     * @return UserInfo<T>
     */
    public abstract UserInfo<T> initUserInfo(String userName, T userRecord);

    /**
     * 取得用戶上次修改密碼的時間
     * 
     * @return Date
     */
    protected abstract Date getPasswdLastModifyDate(T userRecord);

    /**
     * 設定memory中的用戶資訊
     * 
     */
    public abstract void setUserInfo(String userName, UserInfo<T> userInfo);

     protected void registerUserInfo(final UserPrincipal userPrincipal) {

    }

}
