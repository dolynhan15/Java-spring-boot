package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserLanguage;
import com.qooco.boost.data.oracle.reposistories.UserLanguageRepository;
import com.qooco.boost.data.oracle.services.UserLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLanguageServiceImpl implements UserLanguageService {

    @Autowired
    private UserLanguageRepository repository;

    @Override
    public List<UserLanguage> save(List<UserLanguage> userLanguageList) {
        return Lists.newArrayList(repository.saveAll(userLanguageList));
    }

    @Override
    public void deleteUserLangByUserProfileId(Long userProfileId) {
        repository.deleteByUserProfileId(userProfileId);
    }

    @Override
    public List<UserLanguage> findByUserProfileId(Long userProfileId) {
        return repository.findByUserProfileId(userProfileId);
    }

    @Override
    public List<UserLanguage> findByUserProfileIds(List<Long> userProfileIds) {
        return repository.findByUserProfileIds(userProfileIds);
    }
}
