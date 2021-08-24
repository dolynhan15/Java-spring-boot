package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserLanguage;

import java.util.List;

public interface UserLanguageService {

    List<UserLanguage> save(List<UserLanguage> userLanguageList);

    void deleteUserLangByUserProfileId(Long userProfileId);

    List<UserLanguage> findByUserProfileId(Long userProfileId);

    List<UserLanguage> findByUserProfileIds(List<Long> userProfileIds);
}
