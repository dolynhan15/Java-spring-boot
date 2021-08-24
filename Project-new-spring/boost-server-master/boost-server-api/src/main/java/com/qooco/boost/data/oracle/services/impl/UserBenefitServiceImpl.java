package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.UserBenefit;
import com.qooco.boost.data.oracle.reposistories.UserBenefitRepository;
import com.qooco.boost.data.oracle.services.UserBenefitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/13/2018 - 2:58 PM
*/
@Service
public class UserBenefitServiceImpl implements UserBenefitService {
    @Autowired
    private UserBenefitRepository userBenefitRepository;

    @Override
    public List<UserBenefit> findByUserCurriculumVitaeId(long userCurriculumVitaeId) {
        return userBenefitRepository.findByUserCurriculumVitaeId(userCurriculumVitaeId);
    }

    @Override
    public List<UserBenefit> save(List<UserBenefit> lstUserBenefit) {
        return Lists.newArrayList(userBenefitRepository.saveAll(lstUserBenefit));
    }

    @Override
    public UserBenefit save(UserBenefit userBenefit) {
        return userBenefitRepository.save(userBenefit);
    }

    @Override
    public void deleteByUserCurriculumVitaeId(long curriculumVitaeId) {
        userBenefitRepository.deleteByUserCurriculumVitaeId(curriculumVitaeId);
    }

    @Override
    public void deleteByUserCurriculumVitaeIdExceptBenefitIds(long userCurriculumVitaeId, int[] benefitIds) {
        if (benefitIds != null && benefitIds.length > 0) {
            userBenefitRepository.deleteByUserCurriculumVitaeIdExceptBenefitIds(userCurriculumVitaeId, benefitIds);
        }
    }

    @Override
    public List<UserBenefit> findByUserCurriculumVitaeIds(List<Long> userCurriculumVitaeIds) {
        return userBenefitRepository.findByUserCurriculumVitaeIds(userCurriculumVitaeIds);
    }
}
