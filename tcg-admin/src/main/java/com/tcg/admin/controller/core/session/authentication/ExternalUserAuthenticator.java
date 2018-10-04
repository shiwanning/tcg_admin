package com.tcg.admin.controller.core.session.authentication;

/**
 * TODO : For B2B
 *
 * */
public class ExternalUserAuthenticator implements UserAuthenticator {
    @Override
    public boolean validateToken(String token) {
        return false;
    }
}
