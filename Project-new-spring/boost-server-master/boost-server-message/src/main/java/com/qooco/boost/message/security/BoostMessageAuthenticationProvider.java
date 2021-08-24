package com.qooco.boost.message.security;

import com.qooco.boost.core.exception.UnauthorizedException;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.core.model.authentication.BoostAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class BoostMessageAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    public boolean supports(Class<?> authentication) {
        return (BoostAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authenticationToken) throws UnauthorizedException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authenticationToken) throws UnauthorizedException {
        //Call API to validate token
        UserDetails userDetails = new AuthenticatedUser();
        return userDetails;
    }
}