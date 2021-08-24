package com.qooco.boost.data.oracle.services.impl.views;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/3/2018 - 1:27 PM
 */

import com.qooco.boost.data.oracle.entities.views.ViewAccessTokenRoles;
import com.qooco.boost.data.oracle.reposistories.views.ViewAccessTokenRolesRepository;
import com.qooco.boost.data.oracle.services.views.ViewAccessTokenRolesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewAccessTokenRolesServiceImpl implements ViewAccessTokenRolesService {

    @Autowired
    private ViewAccessTokenRolesRepository repository;
    @Override
    public ViewAccessTokenRoles findByToken(String token) {
        return repository.findByToken(token);
    }

    @Override
    public List<ViewAccessTokenRoles> findByUserProfileIds(List<Long> userProfileIds, String appId, boolean hasChannelId) {
        if(hasChannelId){
            return repository.findByUserProfileIdsAndValidChannelId(userProfileIds, appId);
        }
        return repository.findByUserProfileIds(userProfileIds, appId);
    }
}
