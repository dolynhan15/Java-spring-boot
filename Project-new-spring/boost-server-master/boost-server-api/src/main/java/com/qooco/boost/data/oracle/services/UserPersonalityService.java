package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.entities.UserPersonality;

import java.util.List;

public interface UserPersonalityService {
    List<Long> getPersonalAssessmentIdByUserProfileId(long userProfileId);

    List<UserPersonality> getByUserProfileIdAndPersonalAssessmentId(long userProfileId, Long personalAssessmentId);

    List<UserPersonality> getByUserProfileId(long userProfileId);

    int countPersonalAssessmentByUserProfileId(long userProfileId, long personalAssessmentId);

    List<UserPersonality> save(List<UserPersonality> userPersonalities);

    void removeOldResult(long userProfileId, long personalAssessmentId);

    List<PersonalAssessment> findPersonalAssessmentByUserProfile(long userProfileId);
}
