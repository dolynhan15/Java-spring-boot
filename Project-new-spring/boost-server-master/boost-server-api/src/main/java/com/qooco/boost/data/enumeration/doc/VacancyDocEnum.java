package com.qooco.boost.data.enumeration.doc;

import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.utils.MongoInitData;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public enum VacancyDocEnum {
    ID("id"),
    LOGO("logo"),
    COMPANY("company"),
    JOB("job"),
    CITY("city"),
    SEARCH_CITY("searchCity"),
    JOB_LOCATION("jobLocation"),
    SEARCH_LOCATION("searchLocation"),
    CONTACT_PERSON("contactPerson"),
    EDUCATION("education"),
    NUMBER_OF_SEAT("numberOfSeat"),
    SALARY("salary"),
    SALARY_MAX("salaryMax"),
    SALARY_USD("salaryUsd"),
    SALARY_MAX_USD("salaryMaxUsd"),
    CURRENCY("currency"),
    IS_HOUR_SALARY("isHourSalary"),
    IS_FULL_TIME("isFullTime"),
    IS_ASAP("isAsap"),
    EXPECTED_START_DATE("expectedStartDate"),
    SHORT_DESCRIPTION("shortDescription"),
    FULL_DESCRIPTION("fullDescription"),
    STATUS("status"),
    SEARCH_RANGE("searchRange"),
    NATIVE_LANGUAGES("nativeLanguages"),
    LANGUAGES("languages"),
    DESIRED_HOURS("desiredHours"),
    BENEFITS("benefits"),
    SOFT_SKILLS("softSkills"),
    QUALIFICATIONS("qualifications"),
    APPOINTMENTS("appointments"),
    UPDATED_DATE("updatedDate"),
    NUMBER_OF_CANDIDATE("numberOfCandidate"),
    CLOSED_CANDIDATES("closedCandidates"),
    START_SUSPEND_DATE("startSuspendDate"),
    SUSPEND_DAYS("suspendDays"),
    END_SUSPEND_DATE("endSuspendDate"),
    ARCHIVIST("archivist"),

//  ====== External Data, Not come from Vacancy Oracle======
    APPOINTMENT_CANDIDATES("appointmentCandidates"),
    APPOINTMENT_SLOTS("appointmentSlots"),
    CANDIDATE_PROFILES("candidateProfiles"),
    REJECTED_USER_CV("rejectedUserCv"),
    REJECTED_USER_CV_ID_ELEMENT("userCvId"),
    REJECTED_USER_CV_ID("rejectedUserCv.userCvId"),
    REJECTED_USER_CV_REJECTED_DATE("rejectedDate"),
    APPLIED_USER_CV_ID("appliedUserCvId"),

    CREATED_BY_STAFF("createdByStaff"),

    NATIVE_LANGUAGES_ID("nativeLanguages.id"),
    LANGUAGES_ID("languages.id"),
    DESIRED_HOURS_ID("desiredHours.id"),
    EDUCATION_ID("education.id"),
    CLOSED_CANDIDATES_USER_PROFILE_ID("closedCandidates.userProfileId"),
    CLOSED_CANDIDATE_IDS_ON_CLONED_VACANCY("closedCandidateIdsOnClonedVacancy"),
    APPOINTMENT_SLOTS_USER_PROFILE_ID("appointmentSlots.userProfileId"),
    APPOINTMENT_SLOTS_USER_CV_ID("appointmentSlots.userProfileCvId"),
    // Set type
    APPOINTMENT_DATE_RANGE("appointments.$.dateRanges"),
    APPOINTMENT_TIME_RANGE("appointments.$.timeRanges"),
    APPOINTMENT_TYPE("appointments.$.type"),
    APPOINTMENT_FROM_DATE("appointments.$.fromDate"),
    APPOINTMENT_TO_DATE("appointments.$.toDate"),
    CURRENCY_ID("currency.id"),

    //Custom Key
    JOB_ID("job.id"),
    COMPANY_ID("company.id"),
    APPOINTMENTS_ID("appointments.id"),
    APPOINTMENTS_IS_DELETED("appointments.isDeleted"),
    SEARCH_LOCATION_CITY_ID("searchLocation.city.id"),
    SEARCH_LOCATION_CITY_NAME("searchLocation.city.name"),
    SEARCH_LOCATION_CITY_PROVINCE_ID("searchLocation.city.province.id"),
    SEARCH_LOCATION_CITY_PROVINCE_NAME("searchLocation.city.province.name"),
    SEARCH_LOCATION_CITY_PROVINCE_COUNTRY_ID("searchLocation.city.province.country.id"),
    SEARCH_LOCATION_CITY_PROVINCE_COUNTRY_NAME("searchLocation.city.province.country.name"),
    QUALIFICATIONS_ASSESSMENT_ID("qualifications.assessment.id"),
    QUALIFICATIONS_LEVEL_ITEM("level.assessmentLevel"),
    QUALIFICATIONS_ASSESSMENT_ID_ITEM("assessment.id"),
    JOB_LOCATION_CITY_PROVINCE_ID("jobLocation.city.province.id")
    ;



    @Getter @Accessors(fluent = true)
    private final String key;

    public Object getValue(VacancyDoc doc) {
        String prefix = StringUtil.append(this.key(), ".");
        switch (this) {
            case ID:
                return doc.getId();
            case LOGO:
                return doc.getLogo();
            case COMPANY:
                return MongoInitData.initCompanyEmbedded(prefix, doc.getCompany());
            case JOB:
                return doc.getJob();
            case CITY:
                return MongoInitData.initCityEmbedded(prefix, doc.getCity());
            case SEARCH_CITY:
                return MongoInitData.initCityEmbedded(prefix, doc.getSearchCity());
            case JOB_LOCATION:
                return MongoInitData.initLocationEmbedded(prefix, doc.getJobLocation());
            case SEARCH_LOCATION:
                return MongoInitData.initLocationEmbedded(prefix, doc.getSearchLocation());
            case CONTACT_PERSON:
                return MongoInitData.initStaffEmbedded(prefix, doc.getContactPerson());
            case EDUCATION:
                return MongoInitData.initEducationEmbedded(prefix, doc.getEducation());
            case NUMBER_OF_SEAT:
                return doc.getNumberOfSeat();
            case SALARY:
                return doc.getSalary();
            case SALARY_MAX:
                return doc.getSalaryMax();
            case CURRENCY:
                return MongoInitData.initCurrencyEmbedded(prefix, doc.getCurrency());
            case IS_HOUR_SALARY:
                return doc.isHourSalary();
            case IS_FULL_TIME:
                return doc.isFullTime();
            case IS_ASAP:
                return doc.isAsap();
            case EXPECTED_START_DATE:
                return doc.getExpectedStartDate();
            case SHORT_DESCRIPTION:
                return doc.getShortDescription();
            case FULL_DESCRIPTION:
                return doc.getFullDescription();
            case STATUS:
                return doc.getStatus();
            case SEARCH_RANGE:
                return doc.getSearchRange();
            case NATIVE_LANGUAGES:
                return doc.getNativeLanguages();
            case LANGUAGES:
                return doc.getLanguages();
            case DESIRED_HOURS:
                return doc.getDesiredHours();
            case BENEFITS:
                return doc.getBenefits();
            case SOFT_SKILLS:
                return doc.getBenefits();
            case QUALIFICATIONS:
                return doc.getQualifications();
            case APPOINTMENTS:
                return doc.getAppointments();
            case NUMBER_OF_CANDIDATE:
                return doc.getNumberOfCandidate();
            case UPDATED_DATE:
                return doc.getUpdatedDate();
            case APPOINTMENT_CANDIDATES:
                return doc.getAppointmentCandidates();
            case APPOINTMENT_SLOTS:
                return doc.getAppointmentSlots();
            case CANDIDATE_PROFILES:
                return doc.getCandidateProfiles();
            case REJECTED_USER_CV:
                return doc.getRejectedUserCv();
            case APPLIED_USER_CV_ID:
                return doc.getAppliedUserCvId();
            default:
                return null;
        }
    }

    class Key{
        public static final String ABC="";

    }
}

