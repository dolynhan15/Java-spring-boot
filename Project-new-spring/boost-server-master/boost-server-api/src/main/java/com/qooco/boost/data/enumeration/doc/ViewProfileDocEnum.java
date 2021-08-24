package com.qooco.boost.data.enumeration.doc;

public enum ViewProfileDocEnum {
    ID("id"),
    CANDIDATE_USER_PROFILE_ID("candidate.userProfileId"),
    VIEWER_USER_PROFILE_ID("viewer.userProfileId"),
    VACANCY_ID("vacancy.id"),
    VACANCY("vacancy"),
    VIEWER("viewer"),
    CANDIDATE("candidate"),
    CREATED_DATE("createdDate"),
    UPDATED_DATE("updatedDate"),
    STATUS("status"),
    CANDIDATE_MIN_SALARY("candidate.minSalary"),
    CANDIDATE_MAX_SALARY("candidate.maxSalary"),
    CANDIDATE_CURRENCY_ID("candidate.currency.id"),
    VACANCY_SALARY("vacancy.salary"),
    VACANCY_MAX_SALARY("vacancy.salaryMax"),
    VACANCY_CURRENCY_ID("vacancy.currency.id"),
    ;

    private final String key;

    ViewProfileDocEnum(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
