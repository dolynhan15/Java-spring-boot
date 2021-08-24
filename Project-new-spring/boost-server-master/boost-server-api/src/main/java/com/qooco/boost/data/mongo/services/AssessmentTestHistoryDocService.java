package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;

import java.util.List;

public interface AssessmentTestHistoryDocService extends DocService<AssessmentTestHistoryDoc, String>{

    int countByUserProfileIdAndScaleIdAndAssessmentId(Long userProfileId, String scaleId, Long assessmentId, boolean isValidQualification);

    List<AssessmentTestHistoryDoc> getTestHistoryByAssessment(Long userProfileId, Long assessmentId);

    List<AssessmentTestHistoryDoc> getLastTestAllAssessment(Long userProfileId);

    AssessmentTestHistoryDoc findLatestSyncLevelTestData();

    AssessmentTestHistoryDoc findByLatestLevelTestHistoryByUser(Long userProfileId);

    List<AssessmentTestHistoryDoc> findByUserAndAssessmentIds(Long userProfileId, List<Long> assessmentIds);
}
