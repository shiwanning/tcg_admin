package com.tcg.admin.controller.core.session.authentication;

public interface UserAuthenticator {

    boolean validateToken(String token);
}
