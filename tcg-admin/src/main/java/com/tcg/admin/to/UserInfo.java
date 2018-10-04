package com.tcg.admin.to;

import java.util.Date;

import com.tcg.admin.common.constants.LoginConstant;
import com.tcg.admin.common.constants.LoginType;

/**
 * 紀錄登入回覆資訊
 *
 * @author sammy
 */
public class UserInfo<T> {

    /**
     *
     */
    private String userName;

    /**
     * 本次登入密码错误次数
     */
    private int loginPasswordFailCount;
    
    /**
     * 前一次登入的 ip
     */
    private String lastLoginIp;
    
    /**
     * 本次登入的 ip
     */
    private String loginIp;
    
    private Boolean otpAuthActive;
    
    private Date lastPassOtpTime;
    
    /**
     * 最后密码修改时间
     */
    private Date lastPasswdModifyTime;
    
    private Date lastLoginTime;
    
    /**
     * 可登入次數限制
     */
    private int count;

    /** 資金密碼登入錯誤次數 */
    private int paymentCount;

    /**
     * 登入狀態
     */
    private boolean status = false;
    /**
     * 參考 {@link LoginConstant}
     */
    private int type = 1;

    /**
     * 是否為初次登入
     */
    private boolean firstTimeLogin;

    /**
     * 是否密碼過期
     */
    private boolean passwordExpired;

    /**
     * 登入禁用時間
     */
    private Date errorTime;
    
    /**
     * otp 验证通过时间
     */
    private Date optValidTime;
    
    private String googleAuthKey;

    /**
     * 前台customer/後台operator
     */
    private T user;

    private String token;

    private String loginUuid;

    private String userAgent;

    private String merchants;
    
    private String companies;

    private String allMerchants;
    
    private String allCompanies;
    
    private LoginType loginType;
    
    public int getLoginPasswordFailCount() {
        return loginPasswordFailCount;
    }

    public void setLoginPasswordFailCount(int loginPasswordFailCount) {
        this.loginPasswordFailCount = loginPasswordFailCount;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public String getGoogleAuthKey() {
        return googleAuthKey;
    }

    public void setGoogleAuthKey(String googleAuthKey) {
        this.googleAuthKey = googleAuthKey;
    }

    public Date getOptValidTime() {
        return optValidTime;
    }

    public void setOptValidTime(Date optValidTime) {
        this.optValidTime = optValidTime;
    }

    public String getCompanies() {
        return companies;
    }

    public void setCompanies(String companies) {
        this.companies = companies;
    }

    public String getAllCompanies() {
        return allCompanies;
    }

    public void setAllCompanies(String allCompanies) {
        this.allCompanies = allCompanies;
    }

    public Date getErrorTime() {
        return errorTime;
    }

    public void setErrorTime(Date errorTime) {
        this.errorTime = errorTime;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public T getUser() {
        return user;
    }

    public void setUser(T user) {
        this.user = user;
    }

    public int getPaymentCount() {
        return paymentCount;
    }

    public void setPaymentCount(int paymentCount) {
        this.paymentCount = paymentCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getLoginUuid() {
        return loginUuid;
    }

    public void setLoginUuid(String loginUuid) {
        this.loginUuid = loginUuid;
    }

    public String getMerchants() {
        return merchants;
    }

    public void setMerchants(String merchants) {
        this.merchants = merchants;
    }

    public String getAllMerchants() {
        return allMerchants;
    }

    public void setAllMerchants(String allMerchants) {
        this.allMerchants = allMerchants;
    }
    
    public Boolean getOtpAuthActive() {
        return otpAuthActive;
    }

    public void setOtpAuthActive(Boolean otpAuthActive) {
        this.otpAuthActive = otpAuthActive;
    }

    public Date getLastPassOtpTime() {
        return lastPassOtpTime;
    }

    public void setLastPassOtpTime(Date lastPassOtpTime) {
        this.lastPassOtpTime = lastPassOtpTime;
    }
    
    public Date getLastPasswdModifyTime() {
        return lastPasswdModifyTime;
    }

    public void setLastPasswdModifyTime(Date lastPasswdModifyTime) {
        this.lastPasswdModifyTime = lastPasswdModifyTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public boolean isNeedOtp() {
    	if(loginType == LoginType.GOOGLE_OTP) {
    		return false;
    	}
    	boolean isNeedOtp = loginPasswordFailCount > 0;
        isNeedOtp = isNeedOtp || !loginIp.equals(lastLoginIp);
        isNeedOtp = isNeedOtp || lastPassOtpTime == null;
        isNeedOtp = isNeedOtp || (lastPasswdModifyTime != null && lastPasswdModifyTime.after(this.getLastLoginTime()));
        return otpAuthActive && isNeedOtp;
    }

	public void setLoginType(LoginType loginType) {
		this.loginType = loginType;
	}
}
