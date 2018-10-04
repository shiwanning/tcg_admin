package com.tcg.admin.service.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class EmailAuthenticator extends Authenticator {
    String userName = null;
    String password = null;

    public EmailAuthenticator() {
    	// do nothing
    }

    public EmailAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
    
}
