package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.entities.UserPersonality;
import com.qooco.boost.data.oracle.reposistories.UserPersonalityRepository;
import com.qooco.boost.data.oracle.services.UserPersonalityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 2:17 PM
*/
@Service
public class UserPersonalityServiceImpl implements UserPersonalityService {

    @Autowired
    private UserPersonalityRepository userPersonalityRepository;

    @Override
    public List<Long> getPersonalAssessmentIdByUserProfileId(long userProfileId) {
        return userPersonalityRepository.getPersonalAssessmentIdByUserProfileId(userProfileId);
    }

    @Override
    public List<UserPersonality> getByUserProfileIdAndPersonalAssessmentId(long userProfileId, Long personalAssessmentId) {
        return userPersonalityRepository.getByUserProfileIdAndPersonalAssessmentId(userProfileId, personalAssessmentId);
    }

    @Override
    public List<UserPersonality> getByUserProfileId(long userProfileId) {
        return userPersonalityRepository.findByUserProfileId(userProfileId);
    }

    @Override
    public int countPersonalAssessmentByUserProfileId(long userProfileId, long personalAssessmentId) {
        return userPersonalityRepository.countPersonalAssessmentByUserProfileId(userProfileId, personalAssessmentId);
    }

    @Override
    public List<UserPersonality> save(List<UserPersonality> userPersonalities) {
        return Lists.newArrayList(userPersonalityRepository.saveAll(userPersonalities));
    }

    @Override
    public void removeOldResult(long userProfileId, long personalAssessmentId) {
        userPersonalityRepository.removeOldResult(userProfileId, personalAssessmentId);
    }

    @Override
    public List<PersonalAssessment> findPersonalAssessmentByUserProfile(long userProfileId) {
        return userPersonalityRepository.findPersonalAssessmentByUserProfile(userProfileId);
    }

}
