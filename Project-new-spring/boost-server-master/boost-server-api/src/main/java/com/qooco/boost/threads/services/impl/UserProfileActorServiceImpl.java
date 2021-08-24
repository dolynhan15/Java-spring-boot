package com.qooco.boost.threads.services.impl;

import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserFitLanguage;
import com.qooco.boost.data.oracle.entities.UserLanguage;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.UserFitLanguageService;
import com.qooco.boost.data.oracle.services.UserLanguageService;
import com.qooco.boost.threads.services.UserProfileActorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileActorServiceImpl implements UserProfileActorService {
    @Autowired
    private UserLanguageService userLanguageService;
    @Autowired
    private UserFitLanguageService userFitLanguageService;

    @Override
    public UserProfile updateLazyValue(UserProfile userProfile) {
        List<UserLanguage> languages = userLanguageService.findByUserProfileId(userProfile.getUserProfileId());
        userProfile.setUserLanguageList(languages);
        return userProfile;
    }


    @Override
    public UserFit updateLazyValue(UserFit userFit) {
        List<UserFitLanguage> languages = userFitLanguageService.findByUserId(userFit.getUserProfileId());
        userFit.setUserFitLanguages(languages);
        return userFit;
    }

    @Override
    public List<UserFit> updateLazyValueUserFit(List<UserFit> userFits) {
        List<UserFit> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userFits)) {
            List<Long> userProfileIds = userFits.stream().map(UserFit::getUserProfileId).collect(Collectors.toList());
            List<UserFitLanguage> languages = userFitLanguageService.findByUserIds(userProfileIds);
            userFits.forEach(userProfile -> {
                List<UserFitLanguage> userLanguages = languages.stream().filter(
                        ul -> ul.getUserFit().getUserProfileId().equals(userProfile.getUserProfileId()))
                        .collect(Collectors.toList());
                userProfile.setUserFitLanguages(userLanguages);
                result.add(userProfile);
            });
        }
        return result;
    }

    @Override
    public List<UserProfile> updateLazyValue(List<UserProfile> userProfiles) {
        List<UserProfile> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userProfiles)) {
            List<Long> userProfileIds = userProfiles.stream().map(UserProfile::getUserProfileId).collect(Collectors.toList());
            List<UserLanguage> languages = userLanguageService.findByUserProfileIds(userProfileIds);
            userProfiles.forEach(userProfile -> {
                List<UserLanguage> userLanguages = languages.stream().filter(
                        ul -> ul.getUserProfile().getUserProfileId().equals(userProfile.getUserProfileId()))
                        .collect(Collectors.toList());
                userProfile.setUserLanguageList(userLanguages);
                result.add(userProfile);
            });
        }
        return result;
    }
}
