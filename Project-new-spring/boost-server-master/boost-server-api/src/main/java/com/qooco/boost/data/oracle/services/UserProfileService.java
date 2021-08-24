package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserProfile;

import java.util.List;

public interface UserProfileService {

    UserProfile save(UserProfile userProfile);

    List<UserProfile> save(List<UserProfile> userProfiles);

    UserProfile findByUsername(String username);

    UserProfile findById(Long id);

    boolean isExist(Long userProfileId);

    UserProfile checkUserProfileIsRootAdmin(Long userProfileId);

    List<Long> findNextUserProfileIds(Long userProfileId, int size);

    List<UserProfile> findByIds(List<Long> userProfileIds);

    List<UserProfile> findByExceptedUserIds(List<Long> exceptedIds);

    long getMaxUserProfileId();
}

