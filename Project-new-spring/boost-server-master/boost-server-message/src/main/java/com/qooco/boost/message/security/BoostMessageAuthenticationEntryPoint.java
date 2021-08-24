package com.qooco.boost.message.security;

import com.qooco.boost.core.security.AbstractAuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class BoostMessageAuthenticationEntryPoint extends AbstractAuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        super.commence(request, response, authException);
    }
}
