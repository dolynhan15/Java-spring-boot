package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserQualification;
import com.qooco.boost.data.oracle.reposistories.UserQualificationRepository;
import com.qooco.boost.data.oracle.services.UserQualificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserQualificationServiceImpl implements UserQualificationService {

    @Autowired
    private UserQualificationRepository repository;

    @Override
    public List<UserQualification> findByUserProfileIdAndScaleId(Long userProfileId, String scaleId) {
        return repository.findByUserProfileIdAndScaleId(userProfileId, scaleId);
    }

    @Override
    public UserQualification findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<UserQualification> findByUserProfileId(Long userProfileId) {
        return repository.findByUserProfileId(userProfileId);
    }

    @Override
    public UserQualification save(UserQualification qualification) {
        return repository.save(qualification);
    }

    @Override
    public List<UserQualification> save(List<UserQualification> qualification) {
        return Lists.newArrayList(repository.saveAll(qualification));
    }

    @Override
    public List<UserQualification> findByUserProfileIdAndScaleIdForHomePage(Long userProfileId, String scaleId) {
        return repository.findByUserProfileIdAndScaleIdForHomePage(userProfileId,scaleId);
    }

    @Override
    public List<UserQualification> findByUserProfileIdForHomePage(Long userProfileId) {
        return repository.findByUserProfileIdForHomePage(userProfileId);
    }

    @Override
    public int countValidQualification(Long userProfileId, Date submissionValidDate) {
        return repository.countValidQualification(userProfileId, submissionValidDate);
    }

    @Override
    public List<UserQualification> findValidQualification(Long userProfileId, Date submissionValidDate) {
        return repository.findValidQualification(userProfileId, submissionValidDate);
    }
}
