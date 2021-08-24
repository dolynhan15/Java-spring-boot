package com.qooco.boost.data.oracle.services.views;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/3/2018 - 1:27 PM
 */

import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;

import java.util.List;

public interface ViewAccessTokenRolesService {
    ViewAccessTokenRoles findByToken(String token);
    List<ViewAccessTokenRoles> findByUserProfileIds(List<Long> userProfileIds, String appId, boolean hasChannelId);
}
