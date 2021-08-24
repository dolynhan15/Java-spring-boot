package com.qooco.boost.configurations;

import com.qooco.boost.constants.SocketConstants;
import com.qooco.boost.core.security.SecurityConfigurerAdapter;
import com.qooco.boost.securities.BoostAuthenticationEntryPoint;
import com.qooco.boost.securities.BoostAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class BoostSecurityConfig extends SecurityConfigurerAdapter {

    private static final String END_POINT_SECURED = SocketConstants.END_POINT_MESSAGE_REGISTRY_SECURED + "/**";
    private static final String CHANNEL_SECURED = SocketConstants.BROKER_CHANNEL_SECURED + "/**";

    @Autowired
    private BoostAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private BoostAuthenticationProvider authenticationProvider;

    @Override
    protected AntPathRequestMatcher[] getPathRequestMatcher() {
        return new AntPathRequestMatcher[]{
                new AntPathRequestMatcher("/pata/**")
                , new AntPathRequestMatcher("/v2/pata/**")
                , new AntPathRequestMatcher("/auth/logout")
                //Start Socket request matcher
                , new AntPathRequestMatcher(END_POINT_SECURED)
                , new AntPathRequestMatcher(CHANNEL_SECURED)
                //End Socket request matcher
                , new AntPathRequestMatcher("/auth/update-channel-id")
                , new AntPathRequestMatcher("/v2/auth/update-client-info")
        };
    }

    @Override
    protected AbstractUserDetailsAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    @Override
    protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }
}
