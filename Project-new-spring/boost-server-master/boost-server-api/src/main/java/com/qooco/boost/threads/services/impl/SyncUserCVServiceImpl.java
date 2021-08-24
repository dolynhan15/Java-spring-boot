package com.qooco.boost.threads.services.impl;

import com.google.gson.Gson;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.embedded.UserProfileCvEmbeddedService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.threads.models.SaveQualificationInMongo;
import com.qooco.boost.threads.services.SyncUserCVService;
import com.qooco.boost.threads.services.UserCurriculumVitaeActorService;
import com.qooco.boost.threads.services.UserProfileActorService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.qooco.boost.data.constants.Constants.DEFAULT_LIMITED_ID;
import static com.qooco.boost.data.constants.Constants.DEFAULT_LIMITED_ITEM;
import static java.util.Optional.ofNullable;

@Service
public class SyncUserCVServiceImpl implements SyncUserCVService {
    protected Logger logger = LogManager.getLogger(SyncUserCVServiceImpl.class);

    @Autowired
    private UserPreviousPositionService userPreviousPositionService;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private UserProfileActorService userProfileActorService;
    @Autowired
    private UserCurriculumVitaeActorService userCurriculumVitaeActorService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private UserProfileCvEmbeddedService userProfileCvEmbeddedService;
    @Autowired
    private AssessmentService assessmentService;

    @Override
    public void syncUserCV(Long userProfileId) {
        UserProfile userProfile = userProfileService.findById(userProfileId);
        syncUserCV(userProfile, true);
    }

    @Override
    public void syncUserCV(UserProfile userProfile, boolean isUpdateLazy) {
        if (Objects.nonNull(userProfile)) {
            userProfile = userProfileActorService.updateLazyValue(userProfile);
            UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(userProfile);
            if (Objects.nonNull(userCurriculumVitae)) {
                userCurriculumVitae.setUserProfile(userProfile);
                syncUserCV(userCurriculumVitae, isUpdateLazy);
            } else {
                UserProfileCvEmbedded userProfileCvEmbedded = MongoConverters.convertToUserProfileCvEmbedded(userProfile);
                syncUserCVEmbedded(userProfileCvEmbedded, UserType.PROFILE);
                logger.info(StringUtil.append("Update user profile Id =", userProfile.toString()));
            }
        }
    }

    @Override
    public void syncUserCV(UserCurriculumVitae userCurriculumVitae, boolean isUpdateLazy) {
        if (Objects.nonNull(userCurriculumVitae)) {
            var userCvId = userCurriculumVitae.getCurriculumVitaeId();
            if (Objects.nonNull(userCvId)) {
                var positions = userPreviousPositionService.findByUserProfileId(userCurriculumVitae.getUserProfile().getUserProfileId());
                var saveQualificationInMongos = getQualificationSDOs(userCurriculumVitae.getUserProfile().getUserProfileId());
                if (isUpdateLazy) {
                    userCurriculumVitae = userCurriculumVitaeActorService.updateLazyValue(userCurriculumVitae);
                }
                var userCvDoc = MongoConverters.convertToUserCvDoc(userCurriculumVitae, positions, saveQualificationInMongos);
                userCvDocService.save(userCvDoc);

                var userProfileCvEmbedded = MongoConverters.convertToUserProfileCvEmbedded(userCvDoc);
                syncUserCVEmbedded(userProfileCvEmbedded, UserType.PROFILE);
                logger.info(StringUtil.append("Update user Cv Id =", userCvId.toString()));
            }
        }
    }

    @Override
    public void syncUserCV(UserFit userFit) {
        UserProfileCvEmbedded embedded = MongoConverters.convertToUserProfileCvEmbedded(userFit);
        syncUserCVEmbedded(embedded, UserType.SELECT);
    }

    @Override
    public void syncUserCV(List<UserFit> userFits) {
        List<UserProfileCvEmbedded> embeddeds = userFits.stream()
                .map(MongoConverters::convertToUserProfileCvEmbedded)
                .collect(Collectors.toList());
        embeddeds.forEach(embedded -> syncUserCVEmbedded(embedded, UserType.SELECT));
    }

    @Override
    public void syncUserCVEmbedded(UserProfileCvEmbedded cvEmbedded, int userType) {
        logger.info(StringUtil.append("updateUserProfileCvEmbedded username =", cvEmbedded.getUsername()) + " || json = " + new Gson().toJson(cvEmbedded));
        userProfileCvEmbeddedService.update(cvEmbedded);
    }

    @Override
    public void updateUserCvPersonalityInOracleAndMongo(Long userProfileId) {
        if (Objects.nonNull(userProfileId)) {
            UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(
                    new UserProfile(userProfileId));
            if (Objects.nonNull(userCurriculumVitae)) {
                userCurriculumVitae.setHasPersonality(true);
                userCurriculumVitaeService.save(userCurriculumVitae);
                userCvDocService.updateHasPersonality(userCurriculumVitae.getCurriculumVitaeId(), true);
            }
        }
    }

    @Override
    public List<SaveQualificationInMongo> getQualificationSDOs(Long userProfileId) {
        List<SaveQualificationInMongo> saveQualificationInMongos = new ArrayList<>();
        List<UserQualification> qualifications = userQualificationService.findByUserProfileId(userProfileId);
        if (CollectionUtils.isNotEmpty(qualifications)) {
            List<Long> assessmentIds = qualifications.stream().map(UserQualification::getAssessmentId).collect(Collectors.toList());
            List<Assessment> assessments = assessmentService.findByIds(assessmentIds);
            for (UserQualification qualification : qualifications) {
                Optional<Assessment> optional = assessments.stream().filter(ass -> ass.getId().equals(qualification.getAssessmentId())).findFirst();
                optional.ifPresent(assessment -> saveQualificationInMongos.add(new SaveQualificationInMongo(qualification, assessment)));
            }
        }
        return saveQualificationInMongos;
    }

    @Override
    public void syncUserCVToMongo(boolean isOnlyMissingOnMongo) {
        if (!isOnlyMissingOnMongo) {
            sysAllDataInMongo();
        } else {
            sysOnlyMissingDataInMongo();
        }
    }

    private void sysOnlyMissingDataInMongo() {
        logger.info(StringUtil.append("Start sysOnlyMissingDataInMongo"));
        Long prevId = -1L;
        List<BigDecimal> ids = userCurriculumVitaeService.findIdGreaterThan(prevId, DEFAULT_LIMITED_ID);
        while (CollectionUtils.isNotEmpty(ids)) {
            List<Long> longIds = ids.stream().map(BigDecimal::longValue).collect(Collectors.toList());
            prevId = longIds.get(longIds.size() - 1);
            List<Long> userCvIds = userCvDocService.findIdByIds(longIds);
            longIds.removeAll(userCvIds);
            if (CollectionUtils.isNotEmpty(longIds)) {
                List<UserCurriculumVitae> userCurriculumVitaes = userCurriculumVitaeService.findByIds(longIds);
                userCurriculumVitaes.forEach(u -> syncUserCV(u, false));
            }
            ids = userCurriculumVitaeService.findIdGreaterThan(prevId, DEFAULT_LIMITED_ID);
        }
        logger.info(StringUtil.append("End sysOnlyMissingDataInMongo"));
    }

    private void sysAllDataInMongo() {
        Long prevId = -1L;
        for (List<UserCvDoc> userCvDocs; !(userCvDocs = userCvDocService.getByIdGreaterThan(prevId, DEFAULT_LIMITED_ITEM)).isEmpty(); ) {
            userCvDocs.forEach(u -> ofNullable(u.getUserProfile()).ifPresent(it -> syncUserCV(it.getUserProfileId())));
            prevId = userCvDocs.get(userCvDocs.size() - 1).getId();
        }
    }
}
