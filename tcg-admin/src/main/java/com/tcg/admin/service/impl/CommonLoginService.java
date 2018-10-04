package com.tcg.admin.service.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.primitives.Ints;
import com.tcg.admin.common.constants.IErrorCode;
import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.LoginType;
import com.tcg.admin.common.constants.SessionConstants;
import com.tcg.admin.common.error.AdminErrorCode;
import com.tcg.admin.common.exception.AdminServiceBaseException;
import com.tcg.admin.common.helper.RequestHelper;
import com.tcg.admin.model.MerchantProperties;
import com.tcg.admin.model.MerchantWhitelistIp;
import com.tcg.admin.model.Operator;
import com.tcg.admin.model.OperatorAuth;
import com.tcg.admin.model.OperatorProfile;
import com.tcg.admin.model.SystemParameter;
import com.tcg.admin.model.UserPrincipal;
import com.tcg.admin.persistence.springdata.IMerchantPropertiesRepository;
import com.tcg.admin.persistence.springdata.IMerchantWhitelistIpRepository;
import com.tcg.admin.persistence.springdata.IOperatorProfileRepository;
import com.tcg.admin.persistence.springdata.IOperatorRepository;
import com.tcg.admin.persistence.springdata.ISystemParameterRepository;
import com.tcg.admin.persistence.springdata.IUserRepositoryBase;
import com.tcg.admin.service.AuthService;
import com.tcg.admin.to.UserInfo;
import com.tcg.admin.utils.IpUtils;
import com.tcg.admin.utils.MD5Utils;
import com.tcg.admin.utils.shiro.OtpUtils;
import com.tcg.admin.utils.shiro.ShiroUtils;

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
    private AuthService authService;
    
    @Transactional(noRollbackFor = Exception.class)
    public UserInfo<T> login(String userName, String password, LoginType loginType) {

        validate(userName, password);
        try {

            UserInfo<T> userInfo = getUserInfo(userName); //get UserInfo in memory variable

            T userRecord = getUserRecord(userName);
            Integer flag;
            if (userInfo != null) {
                flag = userInfo.getType();
            } else {
                flag = userRecord.getActiveFlag();
                userInfo = new UserInfo<T>();
                userInfo.setUser(userRecord);
                setUserInfo(userName, userInfo);
            }
            LoginConstant activeFlag = LoginConstant.getByStatusCode(flag);

            // ------------------------------------------以下為正式商業邏輯驗證------------------------------------------
            Operator operator = (Operator) userRecord;
            
            // 檢查帳戶是否處於密碼錯誤次數過多狀態
            if (checkUserIsTerminative(activeFlag, userRecord)) {
                LOGGER.info("user login unauthorized for terminative: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
                throw new AdminServiceBaseException(AdminErrorCode.FORBIDDEN_LOGIN_ERROR, "this user must wait 30 mins to login again.");
            }

            // 檢查帳戶是否禁用
            if (checkUserIsForbidden(activeFlag)) {
            	LOGGER.info("user login unauthorized for terminative: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
                removeUserInfo(userName);
                throw new AdminServiceBaseException(AdminErrorCode.OPERATOR_ACTIVE_FLAG_ERROR, "this user has been forbidden to login.");
            }

            // 檢察帳號密碼授權是否正確
            if (checkUserUnauthorized(userRecord, password, loginType)) {
                LOGGER.info("user login unauthorized: name: {}, ip: {}", operator.getOperatorName(), RequestHelper.getIp());
                setUserErrorCount(userRecord);

                // 檢察密碼錯誤次數
                if (checkUserPasswordLimit(userRecord)) {
                    updateUserRecord(userRecord);
                    validBindGoogleOtp(loginType, ((Operator)userRecord).getOperatorId());
                    throw new AdminServiceBaseException(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, "Username or Password is not corret");
                }

                // 檢察密碼錯誤次數是否超過5次
                if (checkUserErrorIsMaximum(userRecord)) {
                    userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.getStatusCode());
                    userRecord.setErrorTime(new Date());
                    userInfo.setType(LoginConstant.ACTIVE_FLAG_TERMINATE_LOGIN.getStatusCode());
                }

                // 已經超過30分鐘 但認證錯誤 則再次鎖住30分鐘
                if (!checkUserIsTerminative(activeFlag, userRecord)) {
                    userRecord.setErrorTime(new Date());
                }

                updateUserRecord(userRecord);
                throw new AdminServiceBaseException(AdminErrorCode.FORBIDDEN_LOGIN_ERROR, "this user must wait 30 mins to login again.");

            }
            
            // 檢查 IP
            if (!checkUserIp(userRecord)) {
                throw new AdminServiceBaseException(AdminErrorCode.IP_NOT_ALLOW, "invalid user ip.");
            }
            
            // 已经登入成功，下边填入用户资讯并保存
            OperatorProfile profile = operatorProfileRepository.findOne(((Operator) userRecord).getOperatorId());
            
            OperatorAuth oa = authService.getGoogleAuth(profile.getOperatorId());
            
            UserInfo<T> newUserInfo = initUserInfo(userName, userRecord);
            String ip = RequestHelper.getIp();

            
            newUserInfo.setLastLoginTime(profile.getLastLoginTime());
            newUserInfo.setLastPasswdModifyTime(profile.getPasswdLastModifyDate());
            newUserInfo.setLastPassOtpTime(oa == null ? null : oa.getLastPassTime());
            newUserInfo.setOtpAuthActive(!(oa == null || oa.getStatus() == OperatorAuth.Status.INACTIVE));
            newUserInfo.setLoginIp(ip);
            newUserInfo.setLastLoginIp(profile.getLastLoginIP());
            newUserInfo.setLoginPasswordFailCount(userRecord.getErrorCount());
            newUserInfo.setLoginType(loginType);
            
            // 驗證通過解除鎖定
            cleanUserErrorInformation(userRecord, userInfo);

            userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());

            //不需要认证码才写入日志
            if(!newUserInfo.isNeedOtp() || loginType == LoginType.GOOGLE_OTP) {
                userRecord.setErrorCount(0);
                saveLoginLog(profile, ip);
                updateUserRecord(userRecord);
                OtpUtils.removeOtpData(((Operator)userRecord).getOperatorId());
            } else {
                OtpUtils.putNeedOtp(operator.getOperatorId(), profile, operator);
            }

            // -----------------------------以下是使用者首次登入相關邏輯-------------------------------
            

            setUserFirstLoginInformation(userRecord, newUserInfo);

            // 所有的登入查驗與動作完成後, 更新UserInfo.status 為 true
            newUserInfo.setStatus(true);

            return newUserInfo;
        } catch (AdminServiceBaseException usbe) {
            LOGGER.info("login fail! {}", usbe.getErrorCode());
            throw usbe;
        } catch (Exception e) {
            LOGGER.error("login Run Time Error!", e);
            throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
        }
    }

    private void validBindGoogleOtp(LoginType loginType, Integer operatorId) {
    	if(loginType == LoginType.GOOGLE_OTP) {
	    	OperatorAuth oa = authService.getGoogleAuth(operatorId);
	    	if(oa == null || oa.getStatus() == OperatorAuth.Status.INACTIVE) {
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
            throw new AdminServiceBaseException(AdminErrorCode.NAME_FORMAT_ERROR, getUserNameFormatErrorDescription());
        }
        if (StringUtils.isBlank(password)) {
            // password 為空
            throw new AdminServiceBaseException(AdminErrorCode.PWD_FORMAT_ERROR, "password can't be empty.");
        }
        if (!checkUserExists(userName)) {
            // user name 不存在
            throw new AdminServiceBaseException(AdminErrorCode.INCORRECT_USERNAME_PWD_ERROR, "Username or Password is not corret ");
        }
    }

    public void logout(String token) {
        Session session = ShiroUtils.getSession(token);

        if (session == null) {
        	LOGGER.info("logout with no session token: {}", token);
        	return;
        }
        @SuppressWarnings("unchecked")
        UserInfo<T> userInfo = (UserInfo<T>) session.getAttribute(SESSION_KEY_USER);
        if (userInfo != null) {
            String userName = userInfo.getUserName();

            try {
                // 檢查帳號是否已存在
                if (!checkUserExists(userName)) {
                    AdminServiceBaseException use =
                            new AdminServiceBaseException(getUserNameNotExistErrorCode(), getUserNameNotExistErrorDescription());
                    LOGGER.error(use.getMessage());
                    throw use;
                }
                userInfo = getUserInfo(userName);
                if (userInfo == null) {
                    throw new AdminServiceBaseException(AdminErrorCode.USER_NOT_LOGIN_ERROR, "the user does not login.");
                }

                removeUserInfo(userName);
            } catch (AdminServiceBaseException usbe) {
                throw usbe;

            } catch (Exception e) {
                throw new AdminServiceBaseException(IErrorCode.UNKNOWN_ERROR, e.getMessage(), e);
            }
        }
        ShiroUtils.kickSessionViaToken(token);
    }

    /**
     * check 使用者狀態是否為Forbidden
     * 
     * @param userFlag {@link LoginConstant}
     * @return boolean 如使用者並非Forbidden&Terminate : false, ActiveFlagLoginSuccess:true
     */
    protected Boolean checkUserIsForbidden(final LoginConstant userFlag) {
        // FIXME 先針對[後台人員手動禁用]功能快速修正, 但程式須再整體調整以方便維護
        // 後台人員手動禁用
        return LoginConstant.ACTIVE_FLAG_LOGIN_FORBID.equals(userFlag)
               || LoginConstant.ACTIVE_FLAG_DELETE.equals(userFlag);
    }


    protected Boolean checkUserErrorIsMaximum(final T userRecord) {
        // 如果使用者錯誤次數已經大於五次
        return userRecord.getErrorCount() > 4 && null == userRecord.getErrorTime();
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
    protected Boolean checkUserPasswordLimit(final T userRecord) {
        return userRecord.getErrorCount() < 5;
    }

    protected void cleanUserErrorInformation(final T userRecord, final UserInfo<T> userInfo) {
        userRecord.setActiveFlag(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
        userRecord.setErrorTime(null);
        userInfo.setType(LoginConstant.ACTIVE_FLAG_LOGIN_SUCCESS.getStatusCode());
    }

    protected void setUserFirstLoginInformation(final T userRecord, final UserInfo<T> userInfo) {
        Date passwordLastModifyDate = getPasswdLastModifyDate(userRecord);

        if (passwordLastModifyDate == null) {
            // 初次登入, 強制修改密碼 (後台不會擋初次登入)
            userInfo.setFirstTimeLogin(true);
            userInfo.setPasswordExpired(false);
        } else {
            final Date nowDate = new Date();
            // 檢查 密碼(LastModifyDate)是否超過系統設定天數(Platform Service)
            SystemParameter passwordKeepDaysParam = systemParameterRepository.findOne(SystemParameter.MEMBER_PASSWORD_KEEP_DAYS);
            LOGGER.info("getPasswordKeepDays={}", passwordKeepDaysParam.getParamValue());
            int passwordKeepDays = Integer.parseInt(passwordKeepDaysParam.getParamValue());
            if (nowDate.after(DateUtils.addDays(passwordLastModifyDate, passwordKeepDays))) {
                // 密碼過期, 強制修改密碼
                userInfo.setPasswordExpired(true);
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

	protected void setUserErrorCount(T userRecord) {
        userRecord.setErrorCount(userRecord.getErrorCount() + 1);
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
         ((IOperatorRepository) userRepositoryBase).save((Operator) userRecord);
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


    public UserInfo<T> getSessionUser(String token) {
        return ShiroUtils.getSessionValue(token, SESSION_KEY_USER);
    }
    
    public UserInfo<T> getSessionUser() {
        return getSessionUser(RequestHelper.getToken());
    }
    
    public UserInfo<T> getSessionUserWithoutException(String token) {
        try {
            return ShiroUtils.getSessionValue(token, SESSION_KEY_USER);
        } catch(AdminServiceBaseException e) {
            if(AdminErrorCode.USER_NOT_LOGIN_ERROR.equals(e.getErrorCode())) {
                return null;
            } else {
                throw e;
            }
        }
    }



    public void setSessionValue(String token, String key, Object value) {
        ShiroUtils.setSessionValue(token, key, value);
    }

    public <V> V getSessionValue(String token, String key) {
        return ShiroUtils.getSessionValue(token, key);

    }


    public boolean isLogin(String token) {
        UserInfo<Operator> userInfo = (UserInfo<Operator>) getSessionUserWithoutException(token);
        if(userInfo == null) {
            return false;
        }
        return !OtpUtils.isNeedOtp(userInfo.getUser().getOperatorId());
    }

    public void touch(String token) {
        ShiroUtils.touch(token);
    }

     protected void registerUserInfo(final UserPrincipal userPrincipal) {

    }

}
