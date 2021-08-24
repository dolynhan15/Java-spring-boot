package com.qooco.boost.threads.actors.syncs;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedAbstractActor;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooco.boost.business.QoocoSyncService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.mongo.embedded.LevelEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import com.qooco.boost.data.mongo.entities.LevelTestScaleDoc;
import com.qooco.boost.data.mongo.entities.WizardSkinDoc;
import com.qooco.boost.data.mongo.entities.localization.qooco.EnUsDoc;
import com.qooco.boost.data.mongo.services.AssessmentTestHistoryDocService;
import com.qooco.boost.data.mongo.services.LevelTestScaleDocService;
import com.qooco.boost.data.mongo.services.WizardSkinDocService;
import com.qooco.boost.data.mongo.services.localization.qooco.EnUsDocService;
import com.qooco.boost.data.oracle.entities.Assessment;
import com.qooco.boost.data.oracle.entities.AssessmentLevel;
import com.qooco.boost.data.oracle.services.AssessmentLevelService;
import com.qooco.boost.data.oracle.services.AssessmentService;
import com.qooco.boost.data.utils.IdGeneration;
import com.qooco.boost.models.qooco.sync.levelTestScales.GetLevelTestScalesResponse;
import com.qooco.boost.models.qooco.sync.leveltest.GetLevelTestResponse;
import com.qooco.boost.models.qooco.sync.leveltest.Test;
import com.qooco.boost.models.qooco.sync.leveltestdata.GetLevelTestDataResponse;
import com.qooco.boost.models.qooco.sync.leveltestdata.LevelTestData;
import com.qooco.boost.models.qooco.sync.leveltestdata.TestData;
import com.qooco.boost.models.qooco.sync.leveltestwizards.GetLevelTestWizardsResponse;
import com.qooco.boost.models.qooco.sync.leveltestwizards.WizardSkin;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import com.qooco.boost.utils.UnixEpochDateTypeAdapter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.*;
import java.util.stream.Collectors;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 10/4/2018 - 11:24 AM
*/
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AssessmentActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(LocalizationActor.class);
    public static final String ACTOR_NAME = "assessmentActor";
    private static final Long USER_PROFILE_START = 0L;

    @Autowired
    private ActorSystem system;


    private QoocoSyncService qoocoSyncService;
    private AssessmentService assessmentService;
    private EnUsDocService enUsDocService;
    private WizardSkinDocService wizardSkinDocService;
    private LevelTestScaleDocService levelTestScaleDocService;
    private AssessmentLevelService assessmentLevelService;
    private AssessmentTestHistoryDocService assessmentTestHistoryDocService;

    @Value(ApplicationConstant.BOOST_SHOW_IN_DEBUG_MODE_ONLY)
    private boolean showInDebugModeOnly;

    public AssessmentActor(QoocoSyncService qoocoSyncService,
                           AssessmentService assessmentService,
                           EnUsDocService enUsDocService,
                           WizardSkinDocService wizardSkinDocService,
                           LevelTestScaleDocService levelTestScaleDocService,
                           AssessmentLevelService assessmentLevelService,
                           AssessmentTestHistoryDocService assessmentTestHistoryDocService) {
        this.qoocoSyncService = qoocoSyncService;
        this.assessmentService = assessmentService;
        this.enUsDocService = enUsDocService;
        this.wizardSkinDocService = wizardSkinDocService;
        this.levelTestScaleDocService = levelTestScaleDocService;
        this.assessmentLevelService = assessmentLevelService;
        this.assessmentTestHistoryDocService = assessmentTestHistoryDocService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof String) {
            String syncKey = (String) message;
            boolean isSyncForce = false;
            String forceKey = "";
            try {
                if(syncKey.contains(QoocoApiConstants.SYNC_FORCE)){
                    isSyncForce = true;
                    forceKey = QoocoApiConstants.SYNC_FORCE;
                    syncKey = syncKey.replace(QoocoApiConstants.SYNC_FORCE, "");
                }
                syncQoocoData(syncKey, isSyncForce, forceKey);

            } catch (ResourceAccessException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    private void syncQoocoData(String syncKey, boolean isSyncForce, String forceKey){
        switch (syncKey) {
            case QoocoApiConstants.GET_LEVEL_TEST_WIZARDS:
                syncLevelTestWizards(isSyncForce);
                logger.info(ACTOR_NAME + " : " + QoocoApiConstants.GET_LEVEL_TEST_WIZARDS);
                getSelf().tell(StringUtil.append(QoocoApiConstants.GET_LEVEL_TESTS, forceKey), ActorRef.noSender());
                break;
            case QoocoApiConstants.GET_LEVEL_TESTS:
                syncLevelTests(isSyncForce);
                logger.info(ACTOR_NAME + " : " + QoocoApiConstants.GET_LEVEL_TESTS);
                getSelf().tell(StringUtil.append(QoocoApiConstants.GET_LEVEL_TEST_SCALES, forceKey), ActorRef.noSender());
                break;
            case QoocoApiConstants.GET_LEVEL_TEST_SCALES:
                syncLevelTestScales(isSyncForce);
                logger.info(ACTOR_NAME + " : " + QoocoApiConstants.GET_LEVEL_TEST_SCALES);
                getSelf().tell(StringUtil.append(QoocoApiConstants.GET_LEVEL_TEST_DATA, forceKey), ActorRef.noSender());
                break;
            case QoocoApiConstants.GET_LEVEL_TEST_DATA:
                syncLevelTestData(isSyncForce);
                logger.info(ACTOR_NAME + " : " + QoocoApiConstants.GET_LEVEL_TEST_DATA);
//                getSelf().tell(StringUtil.append(QoocoApiConstants.GET_OWNED_PACKAGES, forceKey), ActorRef.noSender());
                break;
//            case QoocoApiConstants.GET_OWNED_PACKAGES:
//                logger.info(ACTOR_NAME + " : " + QoocoApiConstants.GET_OWNED_PACKAGES);
//                ActorRef ownedPackageActor = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
//                        .props(OwnedPackageAllActor.ACTOR_NAME));
//                ownedPackageActor.tell(USER_PROFILE_START, ActorRef.noSender());
//                break;
            default:
                break;
        }
    }

    private void syncLevelTestData(boolean isForceSync) {
        List<AssessmentTestHistoryDoc> testHistoryDocs = new ArrayList<>();
        long timestamp = 0;
        if(!isForceSync) {
            AssessmentTestHistoryDoc lastSync = assessmentTestHistoryDocService.findLatestSyncLevelTestData();
            if (Objects.nonNull(lastSync) && Objects.nonNull(lastSync.getUpdatedDate())) {
                timestamp = lastSync.getUpdatedDate().getTime();
            }
        }
        GetLevelTestDataResponse levelTestDataResponse = qoocoSyncService.getLevelTestData(timestamp);
        List<LevelTestScaleDoc> levelTestScaleDocs = levelTestScaleDocService.findAll();
        List<Long> assessmentIds = new ArrayList<>();
        if (Objects.nonNull(levelTestDataResponse) && MapUtils.isNotEmpty(levelTestDataResponse.getLevelTestData())) {
            Map<String, TestData> enUsLevelTestData = levelTestDataResponse.getLevelTestData().get(QoocoApiConstants.LOCALE_EN_US);
            if (MapUtils.isNotEmpty(enUsLevelTestData)) {
                enUsLevelTestData.forEach((key, value) -> {
                    for (LevelTestData levelTest : value.getData()) {
                        AssessmentTestHistoryDoc assessmentTestHistoryDoc = new AssessmentTestHistoryDoc();
                        assessmentIds.add(levelTest.getTestId());
                        assessmentTestHistoryDoc.setId(IdGeneration.generateTestHistory(levelTest.getUserId(),
                                levelTest.getTestId(), levelTest.getTimestamp().getTime()));
                        assessmentTestHistoryDoc.setAssessmentId(levelTest.getTestId());
                        assessmentTestHistoryDoc.setUserProfileId(levelTest.getUserId());
                        assessmentTestHistoryDoc.setSubmissionTime(levelTest.getTimestamp());
                        assessmentTestHistoryDoc.setDuration(levelTest.getDuration());
                        assessmentTestHistoryDoc.setUpdatedDate(value.getTimestamp());
                        assessmentTestHistoryDoc.setMaxLevel(levelTest.getMaxLevel());
                        assessmentTestHistoryDoc.setMinLevel(levelTest.getMinLevel());
                        assessmentTestHistoryDoc.setScaleId(levelTest.getScaleId());
                        Optional<LevelTestScaleDoc> optional = levelTestScaleDocs.stream()
                                .filter(lt -> lt.getId().equals(levelTest.getScaleId())).findFirst();
                        AssessmentLevelEmbedded assessmentLevelEmbedded = new AssessmentLevelEmbedded();
                        assessmentLevelEmbedded.setAssessmentLevel(levelTest.getLevel());
                        if (optional.isPresent()) {
                            Optional<LevelEmbedded> levelEmbeddedOptional = optional.get().getLevels().stream()
                                    .filter(l -> l.getValue().equals(String.valueOf(levelTest.getLevel()))).findFirst();
                            if (levelEmbeddedOptional.isPresent()) {

                                assessmentLevelEmbedded.setLevelName(levelEmbeddedOptional.get().getName());
                                assessmentLevelEmbedded.setLevelDescription(levelEmbeddedOptional.get().getDescr());
                                assessmentLevelEmbedded.setMappingId(optional.get().getMappingID());
                                assessmentLevelEmbedded.setScaleId(levelTest.getScaleId());

                            }
                        }
                        assessmentTestHistoryDoc.setLevel(assessmentLevelEmbedded);

                        testHistoryDocs.add(assessmentTestHistoryDoc);
                    }
                });
            }
        }

        if (CollectionUtils.isNotEmpty(assessmentIds)) {
            List<Assessment> assessments = assessmentService.findByIds(assessmentIds);
            if (CollectionUtils.isNotEmpty(assessments)) {
                for (AssessmentTestHistoryDoc historyDoc : testHistoryDocs) {
                    Optional<Assessment> assessmentOption = assessments.stream()
                            .filter(a -> a.getId().equals(historyDoc.getAssessmentId())).findFirst();
                    assessmentOption.ifPresent(
                            assessment -> historyDoc.setAssessmentName(assessmentOption.get().getName()));
                }
            }
        }
        if (CollectionUtils.isNotEmpty(testHistoryDocs)) {
            assessmentTestHistoryDocService.save(testHistoryDocs);
        }
    }

    private void syncLevelTestScales(boolean isForceSync) {
        long timestamp = 0;
        if(!isForceSync) {
            LevelTestScaleDoc lastLevelTestScaleDoc = levelTestScaleDocService.findByLatestLevelTestScale();
            if (Objects.nonNull(lastLevelTestScaleDoc) && Objects.nonNull(lastLevelTestScaleDoc.getTimestamp())) {
                timestamp = lastLevelTestScaleDoc.getTimestamp().getTime();
            }
        }
        GetLevelTestScalesResponse getLevelTestScalesResponse = qoocoSyncService.getLevelTestScales(timestamp);
        if (Objects.nonNull(getLevelTestScalesResponse) && MapUtils.isNotEmpty(getLevelTestScalesResponse.getLevelTestScales())) {
            if (Objects.nonNull(getLevelTestScalesResponse.getLevelTestScalesTimestamp())) {
                timestamp = getLevelTestScalesResponse.getLevelTestScalesTimestamp().getTime();
            }
            List<LevelTestScaleDoc> levelTestScaleDocs = new ArrayList<>();
            long finalTimestamp = timestamp;
            getLevelTestScalesResponse.getLevelTestScales().forEach((key, value) -> {
                LevelTestScaleDoc levelTestScaleDoc = MongoConverters.convertToLevelTestScaleDoc(value, key, finalTimestamp);
                levelTestScaleDocs.add(levelTestScaleDoc);
            });
            levelTestScaleDocService.save(levelTestScaleDocs);
        }

        List<Assessment> assessments = assessmentService.getNoneLevelAssessment();
        List<String> scaleIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assessments)) {
            assessments.forEach(a -> {
                if (Objects.nonNull(a.getScaleId())) {
                    scaleIds.add(a.getScaleId());
                }
            });
            List<LevelTestScaleDoc> levelTestScaleDocs = levelTestScaleDocService.findByScaleIds(scaleIds);
            List<AssessmentLevel> assessmentLevels = new ArrayList<>();
            assessments.forEach(a -> {
                Optional<LevelTestScaleDoc> levelTestScaleDocOptional = levelTestScaleDocs.stream().filter(
                        l -> l.getId().equals(a.getScaleId())).findFirst();
                levelTestScaleDocOptional.ifPresent(levelTestScaleDoc -> assessmentLevels.addAll(initAssessmentLevel(a, levelTestScaleDoc)));
            });

            assessmentLevelService.save(assessmentLevels);
        }

    }

    private List<Assessment> getLevelTests(boolean isForceSync) {
        List<Assessment> assessments = new ArrayList<>();
        long timeStamp = 0;
        if(!isForceSync) {
            timeStamp = assessmentService.findLatestUpdateDate();
        }
        GetLevelTestResponse levelTestResponse = qoocoSyncService.getLevelTest(QoocoApiConstants.DEFAULT_USER_ID, timeStamp);
        if (Objects.nonNull(levelTestResponse) && MapUtils.isNotEmpty(levelTestResponse.getLevelTests())) {
            Map<String, Test> enUsLevelTests = levelTestResponse.getLevelTests().get(QoocoApiConstants.LOCALE_EN_US);
            if (MapUtils.isNotEmpty(enUsLevelTests)) {
                enUsLevelTests.forEach((key, value) -> value.getTests().forEach((testId, testData) -> {
                    Long id = StringUtil.parseAsLong(testId);
                    if (Objects.nonNull(id)) {
                        Assessment assessment = new Assessment();
                        assessment.setId(id);
                        assessment.setScaleId(testData.getScaleId());
                        assessment.setPackageId(testData.getPackageId());
                        assessment.setTopicId(testData.getTopicId());
                        assessment.setCategoryId(testData.getCategoryId());
                        assessment.setCreatedDate(value.getTimestamp());
                        assessment.setUpdatedDate(value.getTimestamp());
                        assessment.setTimeLimit(testData.getTimeLimit());
                        assessments.add(assessment);
                    }
                }));
            }
        }
        List<EnUsDoc> levelNames = getLevelTestName(assessments.stream()
                .map(Assessment::getId).collect(Collectors.toList()));
        Optional<EnUsDoc> enUsDocOption;
        List<Long> assessmentIds = assessments.stream().map(Assessment::getId).collect(Collectors.toList());
        List<WizardSkinDoc> wizardSkinDocs = wizardSkinDocService.findByTestIds(assessmentIds);
        Optional<WizardSkinDoc> wizardSkinDocOption;
        for (Assessment assessment : assessments) {
            enUsDocOption = levelNames.stream().filter(
                    name -> getIdFromString(name.getId()) == assessment.getId()).findFirst();
            enUsDocOption.ifPresent(enUsDoc -> assessment.setName(enUsDoc.getContent()));
            wizardSkinDocOption = wizardSkinDocs.stream().filter(w -> w.getQuestions().stream().anyMatch(q -> q.getAnswers().stream().anyMatch(a -> a.getTestId() == assessment.getId()))).findFirst();
            wizardSkinDocOption.ifPresent(wizardSkinDoc -> wizardSkinDoc.getQuestions().forEach(question -> question.getAnswers().forEach(answer -> {
                if (answer.getTestId() == assessment.getId()) {
                    if (answer.isShowInDebugModeOnly()) {
                        if (showInDebugModeOnly) {
                            assessment.setIsDeleted(false);
                        } else {
                            assessment.setIsDeleted(true);
                        }
                    }
                    assessment.setPicture(answer.getImage());
                    assessment.setMappingId(wizardSkinDoc.getMappingId());

                }
            })));
        }
        return assessments;
    }

    private void syncLevelTests(boolean isForceSync) {
        List<Assessment> assessments = getLevelTests(isForceSync);
        if (CollectionUtils.isNotEmpty(assessments)) {
            assessmentService.save(assessments);
        }
    }

    private List<AssessmentLevel> initAssessmentLevel(Assessment assessment, LevelTestScaleDoc levelTestScaleDoc) {
        List<AssessmentLevel> assessmentLevels = new ArrayList<>();
        if (Objects.nonNull(levelTestScaleDoc)) {
            levelTestScaleDoc.getLevels().forEach(levelTest -> {
                AssessmentLevel assessmentLevel = new AssessmentLevel();
                assessmentLevel.setAssessment(assessment);
                assessmentLevel.setAssessmentLevel(levelTest.getValue());
                assessmentLevel.setScaleId(assessment.getScaleId());
                assessmentLevel.setLevelName(levelTest.getName());
                assessmentLevel.setLevelDescription(levelTest.getDescr());
                assessmentLevel.setMappingId(levelTestScaleDoc.getMappingID());
                assessmentLevels.add(assessmentLevel);
            });

        }
        return assessmentLevels;
    }

    private List<EnUsDoc> getLevelTestName(List<Long> assessmentIds) {
        if (CollectionUtils.isNotEmpty(assessmentIds)) {
            List<String> levelTestNames = new ArrayList<>();
            for (Long assessmentId : assessmentIds) {
                levelTestNames.add(QoocoApiConstants.LEVEL_TEST_NAME.concat(assessmentId.toString()));
            }
            return enUsDocService.findByIds(levelTestNames);
        }
        return Lists.newArrayList();
    }

    private long getIdFromString(String text) {
        if (Objects.nonNull(text)) {
            String number = text.replaceAll("\\D+", "");
            Long id = StringUtil.parseAsLong(number);
            if (Objects.nonNull(id)) {
                return id;
            }
        }
        return 0;
    }

    private void syncLevelTestWizards(boolean isForceSync) {
        long timestamp = 0;
        if(!isForceSync) {
            WizardSkinDoc lastWizardSkin = wizardSkinDocService.findByLatestWizardSkin();
            if (Objects.nonNull(lastWizardSkin) && Objects.nonNull(lastWizardSkin.getTimestamp())) {
                timestamp = lastWizardSkin.getTimestamp().getTime();
            }
        }
        GetLevelTestWizardsResponse getLevelTestWizardsResponse = qoocoSyncService.getLevelTestWizards(timestamp);
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, UnixEpochDateTypeAdapter.getUnixEpochDateTypeAdapter()).create();
        List<WizardSkinDoc> wizardSkinDocs = new ArrayList<>();
        if (Objects.nonNull(getLevelTestWizardsResponse) && MapUtils.isNotEmpty(getLevelTestWizardsResponse.getLevelTestWizards())) {
            getLevelTestWizardsResponse.getLevelTestWizards().forEach((key, value) -> value.forEach((featureId, feature) -> {
                Date updatedTime = null;
                if (Objects.nonNull(feature.get(QoocoApiConstants.TIMESTAMP))) {
                    String dateJson = gson.toJson(feature.get(QoocoApiConstants.TIMESTAMP));
                    updatedTime = gson.fromJson(dateJson, Date.class);
                }
                Date finalUpdatedTime = new Date(updatedTime.getTime());
                feature.forEach((mappingId, skin) -> {
                    if (!QoocoApiConstants.TIMESTAMP.equals(mappingId)) {
                        WizardSkin wizardSkin = gson.fromJson(gson.toJson(skin), WizardSkin.class);
                        wizardSkinDocs.add(MongoConverters.convertToWizardSkinDoc(wizardSkin, mappingId, finalUpdatedTime));
                    }
                });
            }));
        }
        if (CollectionUtils.isNotEmpty(wizardSkinDocs)) {
            wizardSkinDocService.save(wizardSkinDocs);
        }
    }
}
