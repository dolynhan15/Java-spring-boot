package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;

public enum VacancyEmbeddedEnum {
    ID("id"),
    LOGO("logo"),
    COMPANY("company"),
    JOB("job"),
    CONTACT_PERSON("contactPerson"),
    EDUCATION("education"),
    NUMBER_OF_SEAT("numberOfSeat"),
    SALARY(" salary"),
    SALARY_MAX("salaryMax"),
    CURRENCY(" currency"),
    IS_HOUR_SALARY("isHourSalary"),
    IS_FULL_TIME("isFullTime"),
    IS_ASAP("isAsap"),
    EXPECTED_START_DATE("expectedStartDate"),
    JOB_LOCATION("jobLocation"),
    SEARCH_LOCATION("searchLocation"),
    CITY("city"),
    SEARCH_CITY("searchCity");

    public Object getValue(VacancyEmbedded embedded) {
        String prefix = StringUtil.append(this.getKey(), ".");
        switch (this) {
            case ID:
                return embedded.getId();
            case LOGO:
                return embedded.getLogo();
            case COMPANY:
                return MongoInitData.initCompanyEmbedded(prefix,embedded.getCompany());
            case JOB:
                return MongoInitData.initJobEmbedded(prefix, embedded.getJob());
            case CONTACT_PERSON:
                return MongoInitData.initStaffEmbedded(prefix, embedded.getContactPerson());
            case EDUCATION:
                return MongoInitData.initEducationEmbedded(prefix, embedded.getEducation());
            case NUMBER_OF_SEAT:
                return embedded.getNumberOfSeat();
            case SALARY:
                return embedded.getSalary();
            case SALARY_MAX:
                return embedded.getSalaryMax();
            case CURRENCY:
                return MongoInitData.initCurrencyEmbedded(prefix, embedded.getCurrency());
            case IS_HOUR_SALARY:
                return embedded.isHourSalary();
            case IS_FULL_TIME:
                return embedded.isFullTime();
            case IS_ASAP:
                return embedded.isAsap();
            case EXPECTED_START_DATE:
                return embedded.getExpectedStartDate();
            case JOB_LOCATION:
                return MongoInitData.initLocationEmbedded(prefix, embedded.getJobLocation());
            case SEARCH_LOCATION:
                return MongoInitData.initLocationEmbedded(prefix, embedded.getSearchLocation());
            case CITY:
                return MongoInitData.initCityEmbedded(prefix, embedded.getCity());
            case SEARCH_CITY:
                return MongoInitData.initCityEmbedded(prefix, embedded.getSearchCity());
        }
        return null;
    }

    private final String key;

    public String getKey() {
        return key;
    }

    VacancyEmbeddedEnum(String key) {
        this.key = key;
    }
}
