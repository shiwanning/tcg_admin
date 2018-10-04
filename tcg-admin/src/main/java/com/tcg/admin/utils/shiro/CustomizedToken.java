package com.tcg.admin.utils.shiro;


import org.apache.shiro.authc.AuthenticationToken;

public class CustomizedToken implements AuthenticationToken {

    private static final long serialVersionUID = 5275555504253840214L;

	private String username;

	private String password;
	
	private int loginUserType;

	public CustomizedToken(String username, String password) {

		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public Object getPrincipal() {
		return this.username;
	}

	@Override
	public Object getCredentials() {

		return this.password;
	}

	public int getLoginUserType() {
		return loginUserType;
	}

	public void setLoginUserType(int loginUserType) {
		this.loginUserType = loginUserType;
	}
}
