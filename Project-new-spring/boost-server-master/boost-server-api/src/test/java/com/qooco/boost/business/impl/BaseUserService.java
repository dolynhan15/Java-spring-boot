package com.qooco.boost.business.impl;

import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;

public class BaseUserService {
    public static AuthenticatedUser initAuthenticatedUser() {
        String boostToken = "525b58ecd1f31d8cbec80b4f37327d765f02363a2facbd6c465dcc0743082222";
        String appId = PROFILE_APP.appId();
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority("ADMIN"));
        return AuthenticatedUser.builder()
                .id(1L)
                .username("trungmhv")
                .token(boostToken)
                .appId(appId)
                .locale("en_US")
                .authorities(roles)
                .timezone("UTC")
                .publicKey("")
                .companyId(1L)
                .appVersion(1)
                .appVersionName("1.0.0")
                .build();
    }

    public static Authentication initAuthentication() {

        return new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return initAuthenticatedUser();
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return null;
            }

        };
    }
}
