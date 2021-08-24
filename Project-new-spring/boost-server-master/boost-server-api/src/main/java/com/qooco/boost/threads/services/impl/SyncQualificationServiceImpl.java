package com.qooco.boost.threads.services.impl;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.oracle.entities.UserQualification;
import com.qooco.boost.data.oracle.services.UserQualificationService;
import com.qooco.boost.threads.services.SyncQualificationService;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.AttributeEventType.*;
import static com.qooco.boost.enumeration.ProfileStep.QUALIFICATION_STEP;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/18/2018 - 11:06 AM
 */
@Service
public class SyncQualificationServiceImpl extends BusinessUserServiceAbstract implements SyncQualificationService {
    @Autowired
    private AssessmentTestHistoryDocService assessmentTestHistoryDocService;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @Override
    public void syncQualification(Long userProfileId, List<AssessmentTestHistoryDoc> newTestsSyncFromQooco, List<AssessmentTestHistoryDoc> historyUserTests) {
        List<AssessmentTestHistoryDoc> latestTestDoc = assessmentTestHistoryDocService.getLastTestAllAssessment(userProfileId);
        List<UserQualification> historyQualifications = userQualificationService.findByUserProfileId(userProfileId);
        syncQualification(userProfileId, latestTestDoc, historyQualifications, newTestsSyncFromQooco, historyUserTests);
    }

    private void syncQualification(Long userProfileId,
                                   List<AssessmentTestHistoryDoc> latestTestDoc,
                                   List<UserQualification> qualifications,
                                   List<AssessmentTestHistoryDoc> newTestsSyncFromQooco,
                                   List<AssessmentTestHistoryDoc> historyUserTests) {
        List<UserQualification> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(latestTestDoc)) {
            UserQualification qualification;
            for (AssessmentTestHistoryDoc historyDoc : latestTestDoc) {
                qualification = null;
                if (CollectionUtils.isNotEmpty(qualifications)) {
                    Optional<UserQualification> optional = qualifications.stream()
                            .filter(q -> q.getAssessmentId().equals(historyDoc.getAssessmentId()))
                            .findFirst();
                    if (optional.isPresent()) {
                        qualification = optional.get();
                    }
                }
                if (Objects.isNull(qualification)) {
                    qualification = new UserQualification();
                    qualification.setCreatedBy(historyDoc.getUserProfileId());
                }
                result.add(updateAssessmentQualification(qualification, historyDoc));
            }

            userQualificationService.save(result);
            trackingUserAttributeAssessmentEvents(userProfileId, qualifications, newTestsSyncFromQooco, historyUserTests);
        }
    }

    private void trackingUserAttributeAssessmentEvents(Long userProfileId, List<UserQualification> qualifications, List<AssessmentTestHistoryDoc> newTestsSyncFromQooco, List<AssessmentTestHistoryDoc> historyUserTests) {
        newTestsSyncFromQooco = newTestsSyncFromQooco.stream().filter(it -> ofNullable(it.getLevel()).map(AssessmentLevelEmbedded::getAssessmentLevel).orElse(0) > 0).collect(toImmutableList());
        historyUserTests = historyUserTests.stream()
                .filter(it -> ofNullable(it.getLevel()).map(AssessmentLevelEmbedded::getAssessmentLevel).orElse(0) > 0)
                .collect(toImmutableList());
        List<Long> oldUserTestedIds = qualifications.stream().filter(it -> firstNonNull(it.getLevelValue(), 0) > 0).map(UserQualification::getAssessmentId).collect(toImmutableList());
        List<AssessmentTestHistoryDoc> renewTestsA1Plus = newTestsSyncFromQooco.stream()
                .filter(it -> oldUserTestedIds.contains(it.getAssessmentId())).collect(toImmutableList());
        Map<Long, List<AssessmentTestHistoryDoc>> newTestsA1PlusMap = newTestsSyncFromQooco.stream()
                .filter(it -> !oldUserTestedIds.contains(it.getAssessmentId()))
                .collect(groupingBy(AssessmentTestHistoryDoc::getAssessmentId));
        newTestsA1PlusMap.forEach((key, value) -> businessProfileAttributeEventService.onAttributeEvent(EVT_GET_NEW_ASSESSMENT, userProfileId));

        List<AssessmentTestHistoryDoc> renewOfNewTestsA1Plus = newTestsA1PlusMap.values()
                .stream().filter(it -> it.size() > 1).peek(it -> it.remove(it.get(0))).collect(toImmutableList())
                .stream().flatMap(List::stream).collect(toImmutableList());
        renewTestsA1Plus = Stream.of(renewTestsA1Plus, renewOfNewTestsA1Plus).flatMap(List::stream).collect(toImmutableList());
        renewTestsA1Plus.forEach(it -> businessProfileAttributeEventService.onAttributeEvent(EVT_RENEW_ASSESSMENT, userProfileId));

        Map<Long, AssessmentTestHistoryDoc> historyUserTestFilterLatestSubmitTime = historyUserTests.stream()
                .collect(toMap(AssessmentTestHistoryDoc::getAssessmentId, identity(), (a, b) -> a.getSubmissionTime().after(b.getSubmissionTime()) ? a : b));
        Map<Long, List<AssessmentTestHistoryDoc>> renewTestsMap = renewTestsA1Plus.stream()
                .filter(it -> it.getLevel().getAssessmentLevel() > 0)
                .collect(groupingBy(AssessmentTestHistoryDoc::getAssessmentId));
        renewTestsMap.forEach((key, values) -> {
            int levelOfLatestHistoryUserTest = ofNullable(historyUserTestFilterLatestSubmitTime.get(key)).map(it -> it.getLevel().getAssessmentLevel()).orElse(0);
            for (AssessmentTestHistoryDoc test : values) {
                int assessmentLevel = ofNullable(test).map(AssessmentTestHistoryDoc::getLevel).map(AssessmentLevelEmbedded::getAssessmentLevel).orElse(1);
                if (assessmentLevel > levelOfLatestHistoryUserTest && levelOfLatestHistoryUserTest > 0)
                    businessProfileAttributeEventService.onAttributeEvent(EVT_IMPROVE_ASSESSMENT_RESULT, userProfileId);
                levelOfLatestHistoryUserTest = assessmentLevel;
            }
        });

        Map<Long, List<AssessmentTestHistoryDoc>> historyUserTestMap = historyUserTests.stream().collect(groupingBy(AssessmentTestHistoryDoc::getAssessmentId));
        renewTestsA1Plus.stream().collect(groupingBy(AssessmentTestHistoryDoc::getAssessmentId)).entrySet().stream()
                .filter(ent -> ent.getValue().size() + ofNullable(historyUserTestMap.get(ent.getKey())).map(List::size).orElse(0) > 2)
                .forEach(it -> businessProfileAttributeEventService.onAttributeEvent(EVT_RENEW_ASSESSMENT_MORE_THAN_TWO_TIMES, userProfileId));

        if ((newTestsA1PlusMap.size() + oldUserTestedIds.size()) > 2)
            businessProfileAttributeEventService.onAttributeEvent(EVT_GET_MORE_THAN_TWO_DIFFERENT_ASSESSMENTS, userProfileId);

        Optional.of(newTestsSyncFromQooco).filter(CollectionUtils::isNotEmpty)
                .ifPresent(it -> {
                    businessProfileAttributeEventService.onUserProfileStep(userProfileId, QUALIFICATION_STEP);
                    trackingFillAllProfileFields(userProfileId);
                });

    }

    private UserQualification updateAssessmentQualification(UserQualification qualification,
                                                            AssessmentTestHistoryDoc doc) {

        if (Objects.nonNull(qualification)) {
            qualification.setUserProfileId(doc.getUserProfileId());
            qualification.setAssessmentId(doc.getAssessmentId());
            qualification.setAssessmentName(Objects.nonNull(doc.getAssessmentName()) ? doc.getAssessmentName() : "NULL");
            qualification.setScaleId(doc.getScaleId());
            qualification.setSubmissionTime(doc.getSubmissionTime());
            if (Objects.nonNull(doc.getLevel())) {
                qualification.setLevelValue(doc.getLevel().getAssessmentLevel());
                qualification.setLevelName(Objects.nonNull(doc.getLevel().getLevelName()) ? doc.getLevel().getLevelName() : "NULL");
            }
            qualification.setUpdatedDate(DateUtils.nowUtcForOracle());
            qualification.setUpdatedBy(doc.getUserProfileId());
        }
        return qualification;
    }
}
