package com.tcg.admin.controller.core.session.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.tcg.admin.service.impl.OperatorLoginService;

public class BuiltInUserAuthenticator implements UserAuthenticator {

    private static final Logger LOGGER = LoggerFactory.getLogger(BuiltInUserAuthenticator.class);

    @Autowired
    private OperatorLoginService sessionService;

    @Override
    public boolean validateToken(String token) {
        return this.sessionService.isLogin(token);
    }
}
