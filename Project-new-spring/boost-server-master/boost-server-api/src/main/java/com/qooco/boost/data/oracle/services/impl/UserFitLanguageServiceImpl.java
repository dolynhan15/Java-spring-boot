package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserFitLanguage;
import com.qooco.boost.data.oracle.reposistories.UserFitLanguageRepository;
import com.qooco.boost.data.oracle.services.UserFitLanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFitLanguageServiceImpl implements UserFitLanguageService {

    @Autowired
    private UserFitLanguageRepository repository;

    @Override
    public List<UserFitLanguage> save(List<UserFitLanguage> userLanguageList) {
        return Lists.newArrayList(repository.saveAll(userLanguageList));
    }

    @Override
    public void deleteUserLangByUserId(Long userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    public List<UserFitLanguage> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<UserFitLanguage> findByUserIds(List<Long> userIds) {
        return repository.findByUserIds(userIds);
    }
}
