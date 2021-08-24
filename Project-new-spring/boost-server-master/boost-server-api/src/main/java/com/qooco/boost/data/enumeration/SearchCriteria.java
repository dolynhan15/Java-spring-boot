package com.qooco.boost.data.enumeration;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.stream.Collectors;

public enum SearchCriteria {
    BOOST_SCORE,
    JOB,
    SEARCH_RANGE,
    NATIVE_LANGUAGE,
    PREFER_LANGUAGE,
    EDUCATION,
    SALARY,
    SALARY_RATE,
    WORKING_DAY,
    DESIRED_HOUR,
    AVAILABLE_DATE,
    CLOSED_CANDIDATE,
    STAFF,
    CLOSED_CANDIDATE_ON_CLONED_VACANCY,
    APPLIED_USER,
    REJECTED_USER,
    APPOINTMENT_SLOT;

    public static List<SearchCriteria> getAlphaTestCriteria() {
        return ImmutableList.of(CLOSED_CANDIDATE, STAFF, CLOSED_CANDIDATE_ON_CLONED_VACANCY, APPLIED_USER, REJECTED_USER, APPOINTMENT_SLOT);
    }

    public static List<SearchCriteria> getVacancyMatchingCriteria() {
        return ImmutableList.copyOf(values());
    }

    public static List<SearchCriteria> getVacancyMatchingCriteriaIgnoreQualification() {
        return ImmutableList.copyOf(values()).stream().filter(it -> it != BOOST_SCORE).collect(Collectors.toList());
    }

    public static List<SearchCriteria> getVacancyMatchingCriteriaIgnoreSalary() {
        return ImmutableList.copyOf(values()).stream().filter(it -> it != SALARY).collect(Collectors.toList());
    }
}
