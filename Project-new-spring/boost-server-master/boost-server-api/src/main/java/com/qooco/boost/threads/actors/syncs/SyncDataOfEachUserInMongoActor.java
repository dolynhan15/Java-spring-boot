package com.qooco.boost.threads.actors.syncs;

import akka.actor.UntypedAbstractActor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooco.boost.business.QoocoSyncService;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.services.AssessmentService;
import com.qooco.boost.models.qooco.sync.levelTestHistory.GetLevelTestHistoryResponse;
import com.qooco.boost.models.qooco.sync.levelTestHistory.TestHistory;
import com.qooco.boost.threads.services.SyncQualificationService;
import com.qooco.boost.threads.services.SyncUserCVService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.UnixEpochDateTypeAdapter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class SyncDataOfEachUserInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SyncDataOfEachUserInMongoActor.class);
    public static final String ACTOR_NAME = "syncDataOfEachUserInMongoActor";

    private final QoocoSyncService qoocoSyncService;
    private final AssessmentService assessmentService;
    private final AssessmentTestHistoryDocService assessmentTestHistoryDocService;
    private final SyncQualificationService syncQualificationService;
    private final SyncUserCVService syncUserCVService;

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            final String userProfileId = (String) message;
            try {
                List<AssessmentTestHistoryDoc> newTestsSyncFromQooco = syncLevelTestHistory(userProfileId);
                if (CollectionUtils.isNotEmpty(newTestsSyncFromQooco)) {
                    List<AssessmentTestHistoryDoc> historyUserTests = assessmentTestHistoryDocService.findByUserAndAssessmentIds(NumberUtils.toLong(userProfileId), newTestsSyncFromQooco.stream().map(AssessmentTestHistoryDoc::getAssessmentId).collect(toImmutableList()));
                    newTestsSyncFromQooco = assessmentTestHistoryDocService.save(newTestsSyncFromQooco);
                    syncQualificationService.syncQualification(NumberUtils.toLong(userProfileId), newTestsSyncFromQooco, historyUserTests);
                    syncUserCVService.syncUserCV(NumberUtils.toLong(userProfileId));
                    logger.info("Finish sync data for user : " + userProfileId);
                }
            } catch (ResourceAccessException ex) {
                logger.error(ex.getMessage());
            }

        }
    }

    private List<AssessmentTestHistoryDoc> syncLevelTestHistory(String userId) {
        List<AssessmentTestHistoryDoc> testHistoryDocs = new ArrayList<>();
        long timestamp = 0;
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, UnixEpochDateTypeAdapter.getUnixEpochDateTypeAdapter()).create();
        Long userProfileId = Long.valueOf(userId);
        AssessmentTestHistoryDoc lastAssessmentTestHistoryDoc = assessmentTestHistoryDocService.findByLatestLevelTestHistoryByUser(userProfileId);
        if (Objects.nonNull(lastAssessmentTestHistoryDoc) && Objects.nonNull(lastAssessmentTestHistoryDoc.getUpdatedDateByItSelf())) {
            timestamp = lastAssessmentTestHistoryDoc.getUpdatedDateByItSelf().getTime();
        }
        GetLevelTestHistoryResponse levelTestHistoryResponse = qoocoSyncService.getLevelTestHistory(userId, timestamp);
        if (MapUtils.isNotEmpty(levelTestHistoryResponse.getLevelTestHistory())) {
            long finalTimestamp = timestamp;
            levelTestHistoryResponse.getLevelTestHistory().forEach((enLocale, value) -> value.forEach((featureId, feature) -> {
                Date updatedTime = null;
                if (Objects.nonNull(feature.getTimestamp())) {
                    String dateJson = gson.toJson(feature.getTimestamp());
                    updatedTime = gson.fromJson(dateJson, Date.class);
                }
                Date finalUpdatedTime = new Date(updatedTime.getTime());
                if (MapUtils.isNotEmpty(feature.getTests())) {
                    feature.getTests().forEach((testId, tests) -> {
                        Long assessmentId = Long.valueOf(testId);
                        Assessment foundAssessment = assessmentService.findById(assessmentId);
                        if (Objects.isNull(foundAssessment)) {
                            foundAssessment = new Assessment(assessmentId);
                        }
                        for (TestHistory testHistory : (Iterable<TestHistory>)tests.stream().filter(it -> ofNullable(it.getTimestamp()).map(Date::getTime).orElse(0L) > finalTimestamp)::iterator) {
                            TestHistory levelTestHistory = gson.fromJson(gson.toJson(testHistory), TestHistory.class);
                            ofNullable(MongoConverters.convertToAssessmentTestHistoryDoc(levelTestHistory, assessmentId, foundAssessment, finalUpdatedTime, userProfileId, true)).ifPresent(testHistoryDocs::add);
                        }
                    });
                }
            }));
        }
        return testHistoryDocs;
    }
}
