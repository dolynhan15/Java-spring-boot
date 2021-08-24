package com.qooco.boost.business;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.enumeration.BoostApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Optional.ofNullable;

public interface BaseBusinessService {
    default String getAppId(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getAppId();
    }

    default String getLocale(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getLocale();
    }

    default int getApp(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        return ofNullable(BoostApplication.fromAppId(user.getAppId())).map(BoostApplication::value).orElse(0);
    }

    default int getUserType(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        if (BoostApplication.PROFILE_APP.appId().equals(user.getAppId())) {
            return UserType.PROFILE;
        } else if (BoostApplication.SELECT_APP.appId().equals(user.getAppId())) {
            return UserType.SELECT;
        } else if (BoostApplication.WEB_ADMIN_APP.appId().equals(user.getAppId())) {
            return UserType.SYSTEM_ADMIN;
        }
        return 0;
    }

    default boolean isSystemAdmin(Authentication authentication){
        return ((AuthenticatedUser) authentication.getPrincipal()).isSystemAdmin();
    }
    default Long getUserId(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getId();
    }

    default String getUsername(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getUsername();
    }

    default Long getCompanyId(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getCompanyId();
    }

    default String getUserToken(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getToken();
    }

    default String getTimeZone(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getTimezone();
    }

    default String getPublicKey(Authentication authentication) {
        return ((AuthenticatedUser) authentication.getPrincipal()).getPublicKey();
    }

    default int getAppVersion(Authentication authentication) {
        return firstNonNull(((AuthenticatedUser) authentication.getPrincipal()).getAppVersion(), 0);
    }

    default AuthenticatedUser getUser(Authentication authentication) {
        return (AuthenticatedUser) authentication.getPrincipal();
    }

    default List<String> getRoles(Authentication authentication) {
        return ofNullable(((AuthenticatedUser) authentication.getPrincipal()).getAuthorities())
                .orElseGet(ImmutableList::of)
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
