package com.qooco.boost.core.security;

import com.google.gson.GsonBuilder;
import com.qooco.boost.core.enumeration.ResponseStatus;
import com.qooco.boost.core.exception.BoostTokenMissingException;
import com.qooco.boost.core.exception.UnauthorizedException;
import com.qooco.boost.core.model.BaseResp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandler implements org.springframework.security.web.authentication.AuthenticationFailureHandler {
    protected Logger log = LogManager.getLogger(AuthenticationFailureHandler.class);
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException ex) throws IOException, ServletException {
        log.info("Authentication fail:" + ex.getMessage());
        BaseResp<?> rsp = null;
        if (ex instanceof UnauthorizedException) {
            UnauthorizedException exception = (UnauthorizedException) ex;
        }else if(ex instanceof BoostTokenMissingException){
            BoostTokenMissingException exception = (BoostTokenMissingException) ex;
            rsp = new BaseResp<>(exception.getCode(), exception.getMsg());
        } else {
            rsp = new BaseResp<>(ResponseStatus.LOGIN_UNAUTHORIZED.getCode(), ResponseStatus.LOGIN_UNAUTHORIZED.getDescription());
        }
        String json = new GsonBuilder().create().toJson(rsp);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.getWriter().write(json);
        httpServletResponse.getWriter().flush();
        httpServletResponse.getWriter().close();
    }
}

