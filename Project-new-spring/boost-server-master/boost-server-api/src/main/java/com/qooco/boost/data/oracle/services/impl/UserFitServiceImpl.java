package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.reposistories.UserFitRepository;
import com.qooco.boost.data.oracle.services.UserFitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserFitServiceImpl implements UserFitService {

    @Autowired
    private UserFitRepository repository;

    @Override
    public UserFit save(UserFit userFit) {
        return repository.save(userFit);
    }

    @Override
    public List<UserFit> save(List<UserFit> userFits) {
        return Lists.newArrayList(repository.saveAll(userFits));
    }

    @Override
    public UserFit findById(Long id) {
        return repository.findValidById(id);
    }
    @Override
    public List<UserFit> findByIds(List<Long> userIds) {
        return repository.findByIds(userIds);
    }

    @Override
    public void updateDefaultCompany(Long userId, Long companyId) {
        repository.setDefaultCompany(userId, companyId);
    }

    @Override
    public boolean isExist(Long userId) {
        return repository.existsById(userId);
    }


}
