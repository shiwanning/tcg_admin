package com.tcg.admin.controller.core.session.authentication;

import org.springframework.beans.factory.annotation.Autowired;

public class UserAuthenticatorFactory {

    @Autowired
    private BuiltInUserAuthenticator builtInUserAuthenticator;

    public UserAuthenticator createUserAuthenticator() {
        return builtInUserAuthenticator;
    }
}
