package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserFitLanguage;

import java.util.List;

public interface UserFitLanguageService {

    List<UserFitLanguage> save(List<UserFitLanguage> userLanguageList);

    void deleteUserLangByUserId(Long userId);

    List<UserFitLanguage> findByUserId(Long userId);

    List<UserFitLanguage> findByUserIds(List<Long> userIds);
}
