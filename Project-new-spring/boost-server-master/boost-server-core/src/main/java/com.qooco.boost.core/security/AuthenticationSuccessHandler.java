package com.qooco.boost.core.security;

import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationSuccessHandler implements org.springframework.security.web.authentication.AuthenticationSuccessHandler {
    protected Logger log = LogManager.getLogger(AuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        AuthenticatedUser user = ((AuthenticatedUser) authentication.getPrincipal());
        log.info("Authentication success for " + user.getUsername());
    }
}
