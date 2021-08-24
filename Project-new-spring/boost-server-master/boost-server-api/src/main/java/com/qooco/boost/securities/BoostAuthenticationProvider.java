package com.qooco.boost.securities;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.SystemLoggerService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.core.model.authentication.BoostAuthenticationToken;
import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;
import com.qooco.boost.data.oracle.services.views.ViewAccessTokenRolesService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.UnauthorizedException;
import com.qooco.boost.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.qooco.boost.constants.AttributeEventType.EVT_ENTER_APPLICATION;

@Component
public class BoostAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Autowired
    private ViewAccessTokenRolesService viewAccessTokenRolesService;
    @Autowired
    private SystemLoggerService systemLoggerService;
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @Override
    public boolean supports(Class<?> authentication) {
        return (BoostAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authenticationToken) throws UnauthorizedException {
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authenticationToken) throws UnauthorizedException {
        //Get token first
        BoostAuthenticationToken boostAuth = (BoostAuthenticationToken) authenticationToken;
        String token = boostAuth.token();
        String timezone = boostAuth.timezone();
        String locale = boostAuth.locale();
        if (StringUtil.isEmpty(locale)) {
            locale = "en_US";
        }


        //Check token is exit in DB => To make sure that it is authenticated
        ViewAccessTokenRoles userAccessToken = viewAccessTokenRolesService.findByToken(token);
        if (null == userAccessToken) {
            //The user is not authorization
            throw new UnauthorizedException(ResponseStatus.LOGIN_UNAUTHORIZED);
        }
        businessProfileAttributeEventService.onAttributeEventAsync(EVT_ENTER_APPLICATION, userAccessToken.getUserProfileId());

        //Get Role of each user: ADMIN, HEAD_RECRUITER, RECRUITER, ANALYST
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (StringUtils.hasText(userAccessToken.getRoleName())) {
            authorityList.add(new SimpleGrantedAuthority(userAccessToken.getRoleName()));
        }
        UserDetails userDetails = AuthenticatedUser.builder()
                .id(userAccessToken.getUserProfileId())
                .username(userAccessToken.getUsername())
                .token(userAccessToken.getToken())
                .appId(userAccessToken.getAppId())
                .isSystemAdmin(userAccessToken.isSystemAdmin())
                .publicKey(userAccessToken.getPublicKey())
                .companyId(userAccessToken.getCompanyId())
                .timezone(timezone)
                .authorities(authorityList)
                .appVersion(boostAuth.appVersion())
                .appVersionName(boostAuth.appVersionName())
                .platform(userAccessToken.getPlatform())
                .locale(locale)
                .build();
        systemLoggerService.saveSystemLogger(userDetails);
        return userDetails;
    }
}
