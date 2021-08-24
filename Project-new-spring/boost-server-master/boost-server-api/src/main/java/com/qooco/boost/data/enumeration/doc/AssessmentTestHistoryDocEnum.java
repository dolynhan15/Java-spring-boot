package com.qooco.boost.data.enumeration.doc;

public enum AssessmentTestHistoryDocEnum {
    ID("id"),
    USER_PROFILE_ID("userProfileId"),
    SCALE_ID("scaleId"),
    ASSESSMENT_ID("assessmentId"),
    UPDATED_DATE("updatedDate"),
    UPDATED_DATE_BY_IT_SELF("updatedDateByItSelf"),
    LEVEL_TEST_DATA_TIMESTAMP("levelTestDataTimestamp"),
    SUBMISSION_TIME("submissionTime"),
    ASSESSMENT_NAME("assessmentName"),
    MIN_LEVEL("minLevel"),
    MAX_LEVEL("maxLevel"),
    SCORE("score"),
    DURATION("duration"),
    LEVEL_LEVEL_ID("level.levelId"),
    LEVEL_SCALE_ID("level.scaleId"),
    LEVEL_MAPPING_ID("level.mappingId"),
    LEVEL_LEVEL_NAME("level.levelName"),
    LEVEL_LEVEL_DESCRIPTION("level.levelDescription"),
    LEVEL_ASSESSMENT_LEVEL("level.assessmentLevel");

    private final String key;

    AssessmentTestHistoryDocEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
