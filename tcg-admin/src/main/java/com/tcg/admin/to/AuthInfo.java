package com.tcg.admin.to;

import java.util.Date;

public class AuthInfo {
	
	private String token;
	private Date lastAccessTime;
	private boolean isNeetOtp;
	private int otpValidErrorCount;
	private boolean isNeedReset;
	private boolean isAuthOrigin;
	
	public int getOtpValidErrorCount() {
		return otpValidErrorCount;
	}
	public void setOtpValidErrorCount(int otpValidErrorCount) {
		this.otpValidErrorCount = otpValidErrorCount;
	}
	public boolean isNeetOtp() {
		return isNeetOtp;
	}
	public void setNeetOtp(boolean isNeetOtp) {
		this.isNeetOtp = isNeetOtp;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getLastAccessTime() {
		return lastAccessTime;
	}
	public void setLastAccessTime(Date lastAccessTime) {
		this.lastAccessTime = lastAccessTime;
	}

	public boolean isNeedReset() {
		return isNeedReset;
	}

	public void setNeedReset(boolean needReset) {
		isNeedReset = needReset;
	}

	public boolean isAuthOrigin() {
		return isAuthOrigin;
	}

	public void setAuthOrigin(boolean authOrigin) {
		isAuthOrigin = authOrigin;
	}
}
