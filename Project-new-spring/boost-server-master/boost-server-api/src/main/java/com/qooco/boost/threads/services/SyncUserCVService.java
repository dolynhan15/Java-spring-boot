package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.threads.models.SaveQualificationInMongo;

import java.util.List;

public interface SyncUserCVService {
    void syncUserCV(Long userProfileId);

    void syncUserCV(UserProfile userProfile, boolean isUpdateLazy);

    void syncUserCV(UserCurriculumVitae userCurriculumVitae, boolean isUpdateLazy);

    void syncUserCV(UserFit userFit);

    void syncUserCV(List<UserFit> userFits);

    void syncUserCVEmbedded(UserProfileCvEmbedded cvEmbedded, int userType);

    void updateUserCvPersonalityInOracleAndMongo(Long userProfileId);

    List<SaveQualificationInMongo> getQualificationSDOs(Long userProfileId);

    void syncUserCVToMongo(boolean isOnlyMissingOnMongo);
}
