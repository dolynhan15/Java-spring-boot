package com.qooco.boost.data.constants;

import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 9/17/2018 - 10:56 AM
*/
public final class AssessmentTestHistoryDocConstants {

    private AssessmentTestHistoryDocConstants() {}

    public static final String ID = "id";
    public static final String USER_PROFILE_ID = "userProfileId";
    public static final String SCALE_ID = "scaleId";
    public static final String NAME = "name";
    public static final String ASSESSMENT_ID = "assessmentId";
    public static final String UPDATED_DATE = "updatedDate";
    public static final String UPDATED_DATE_BY_IT_SELF = "updatedDateByItSelf";
    public static final String LEVEL_TEST_DATA_TIMESTAMP = "levelTestDataTimestamp";
    public static final String SUBMISSION_TIME = "submissionTime";
    public static final String ASSESSMENT_NAME = "assessmentName";
    public static final String MIN_LEVEL = "minLevel";
    public static final String MAX_LEVEL = "maxLevel";
    public static final String SCORE = "score";
    public static final String DURATION = "duration";

    private AssessmentLevelEmbedded level;
    public static final String  LEVEL_LEVEL_ID = "level.levelId";
    public static final String  LEVEL_SCALE_ID = "level.scaleId";
    public static final String  LEVEL_MAPPING_ID = "level.mappingId";
    public static final String  LEVEL_LEVEL_NAME = "level.levelName";
    public static final String  LEVEL_LEVEL_DESCRIPTION = "level.levelDescription";
    public static final String  LEVEL_ASSESSMENT_LEVEL = "level.assessmentLevel";




}
