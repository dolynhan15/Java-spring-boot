package com.qooco.boost.message.config;

import com.qooco.boost.core.security.AbstractAuthenticationEntryPoint;
import com.qooco.boost.core.security.SecurityConfigurerAdapter;
import com.qooco.boost.message.constant.SocketConstants;
import com.qooco.boost.message.security.BoostMessageAuthenticationEntryPoint;
import com.qooco.boost.message.security.BoostMessageAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MessageSecurityConfig extends SecurityConfigurerAdapter {

    private static final String END_POINT_SECURED = SocketConstants.END_POINT_MESSAGE_REGISTRY_SECURED + "/**";
    private static final String CHANNEL_SECURED = SocketConstants.BROKER_CHANNEL_SECURED + "/**";


    @Autowired
    private BoostMessageAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private BoostMessageAuthenticationProvider authenticationProvider;

    @Override
    protected AntPathRequestMatcher[] getPathRequestMatcher() {
        return new AntPathRequestMatcher[]{
                new AntPathRequestMatcher(END_POINT_SECURED)
                , new AntPathRequestMatcher(CHANNEL_SECURED)
        };
    }

    @Override
    protected AbstractUserDetailsAuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    @Override
    protected AbstractAuthenticationEntryPoint getAuthenticationEntryPoint() {
        return authenticationEntryPoint;
    }
}
