package com.qooco.boost.data.enumeration.doc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum  UserCvDocEnum {

    CURRENCY_ID ("currency.id"),
    EDUCATION_ID ("education.id"),
    DESIRED_HOURS_ID ("desiredHours.id"),
    BENEFITS_ID ("benefits.id"),
    JOBS_ID ("jobs.id"),
    PREVIOUS_POSITIONS_ID ("previousPositions.id"),
    SOFT_SKILLS_ID ("softSkills.id"),
    USER_PROFILE_ID ("userProfile.userProfileId"),
    USERNAME ("userProfile.username"),
    PROFILE_STRENGTH ("profileStrength"),

    QUALIFICATIONS_ID ("qualifications.id"),
    QUALIFICATIONS_ASSESSMENT_ID ("qualifications.assessment.id"),
    QUALIFICATIONS_ASSESSMENT_SCALE_ID ("qualifications.assessment.scaleId"),
    QUALIFICATIONS_LEVEL_ASSESSMENT_LEVEL ("qualifications.level.assessmentLevel"),
    QUALIFICATIONS_SUBMISSION_TIME ("qualifications.submissionTime"),
    USER_PROFILE_NATIVE_LANGUAGES ("userProfile.nativeLanguages"),
    USER_PROFILE_LANGUAGES ("userProfile.languages"),
    USER_PROFILE_COUNTRY_ID ("userProfile.country.id"),
    USER_PROFILE_CITY_PROVINCE_COUNTRY_ID ("userProfile.city.province.country.id"),
    USER_PROFILE_CITY_ID ("userProfile.city.id"),
    USER_PROFILE_CITY_PROVINCE_ID ("userProfile.city.province.id"),

    USER_PROFILE_NATIVE_LANGUAGES_ID ("userProfile.nativeLanguages.id"),
    USER_PROFILE_LANGUAGES_ID ("userProfile.languages.id");

    @Getter @Accessors(fluent = true)
    private final String key;

}
