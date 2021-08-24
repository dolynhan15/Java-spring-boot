package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/18/2018 - 11:02 AM
 */
public interface SyncQualificationService {
    void syncQualification(Long userProfileId, List<AssessmentTestHistoryDoc> newTestsSyncFromQooco, List<AssessmentTestHistoryDoc> historyUserTests);
}
