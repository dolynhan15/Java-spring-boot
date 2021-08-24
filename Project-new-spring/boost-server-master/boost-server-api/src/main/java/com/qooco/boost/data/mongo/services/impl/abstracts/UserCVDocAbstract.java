package com.qooco.boost.data.mongo.services.impl.abstracts;

import com.qooco.boost.data.constants.SearchRange;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.SearchCriteria;
import com.qooco.boost.data.enumeration.doc.UserCvDocEnum;
import com.qooco.boost.data.mongo.embedded.LanguageEmbedded;
import com.qooco.boost.data.mongo.embedded.RejectedUserCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.WorkingHourEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentSlotEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public abstract class UserCVDocAbstract {
    @Autowired
    private StaffService staffService;

    protected Criteria initCriteriaMatchingCvForVacancy(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        if (Objects.nonNull(vacancyDoc)) {
            int alphaTestId = Objects.nonNull(config) ? config.getVacancyJobAlphaTest() : -1;
            if (alphaTestId != vacancyDoc.getJob().getId()) {
                var criteria = new ArrayList<Criteria>();
                SearchCriteria.getVacancyMatchingCriteria().forEach(it -> ofNullable(initSearchCriteria(it, vacancyDoc, config, limitTime)).ifPresent(criteria::add));
                return new Criteria().andOperator(criteria.toArray(new Criteria[0]));
            } else {
                return initAlphaTestCriteria(vacancyDoc, limitTime, config);
            }
        }

        return null;
    }

    protected Criteria initCriteriaIgnoreSalary(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        int alphaTestId = Objects.nonNull(config) ? config.getVacancyJobAlphaTest() : -1;
        if (alphaTestId != vacancyDoc.getJob().getId()) {
            var criteria = new ArrayList<Criteria>();
            SearchCriteria.getVacancyMatchingCriteriaIgnoreSalary().forEach(it -> ofNullable(initSearchCriteria(it, vacancyDoc, config, limitTime)).ifPresent(criteria::add));
            return new Criteria().andOperator(criteria.toArray(new Criteria[0]));
        } else {
            return initAlphaTestCriteria(vacancyDoc, limitTime, config);
        }
    }

    protected Criteria initAlphaTestCriteria(VacancyDoc vacancyDoc, int limitTime, MatchingCandidateConfig config) {
        var alphaTest = new ArrayList<Criteria>();
        SearchCriteria.getAlphaTestCriteria().forEach(it -> ofNullable(initSearchCriteria(it, vacancyDoc, config, limitTime)).ifPresent(alphaTest::add));
        if (CollectionUtils.isNotEmpty(alphaTest)) {
            return new Criteria().andOperator(alphaTest.toArray(new Criteria[0]));
        }
        return null;
    }

    protected Criteria initSearchCriteria(SearchCriteria it, VacancyDoc vacancy, MatchingCandidateConfig config, int timeLimit) {
        switch (it) {
            case BOOST_SCORE:
                return initBoostScoreCriteria(vacancy, config);
            case JOB:
                return initJobCriteria(vacancy);
            case SEARCH_RANGE:
                return initSearchRangeCriteria(vacancy);
            case NATIVE_LANGUAGE:
                return initNativeLanguageCriteria(vacancy);
            case PREFER_LANGUAGE:
                return initLanguageCriteria(vacancy);
            case EDUCATION:
                return initEducationCriteria(vacancy);
            case SALARY:
                return initSalaryCriteria(vacancy);
            case SALARY_RATE:
                return initSalaryRateCriteria(vacancy);
            case WORKING_DAY:
                return initWorkingDayCriteria(vacancy);
            case DESIRED_HOUR:
                return initDesiredHoursCriteria(vacancy);
            case AVAILABLE_DATE:
                return initAvailabilityDateCriteria(vacancy);
            case CLOSED_CANDIDATE:
                return initClosedCandidateCriteria(vacancy);
            case STAFF:
                return initStaffCriteria(vacancy);
            case CLOSED_CANDIDATE_ON_CLONED_VACANCY:
                return initClosedCandidateOnClonedVacancyCriteria(vacancy);
            case APPLIED_USER:
                return intiAppliedUserCriteria(vacancy);
            case REJECTED_USER:
                return initRejectedUserCriteria(vacancy, timeLimit);
            case APPOINTMENT_SLOT:
                return initAppointmentSlotsCriteria(vacancy);
            default:
                return null;
        }
    }

    //============================== Init ==================================================
    private Criteria initBoostScoreCriteria(VacancyDoc vacancyDoc, MatchingCandidateConfig config) {
        if (Objects.nonNull(config) && config.isBoostScoreEnabled()) {
            List<QualificationEmbedded> qualificationEmbeddeds = vacancyDoc.getQualifications();
            if (CollectionUtils.isNotEmpty(qualificationEmbeddeds)) {
                List<Criteria> assessmentLevelCriterias = new ArrayList<>();

                Date expiredDate = DateUtils.addDays(DateUtils.toServerTimeForMongo(), -config.getExpiredDays());
                qualificationEmbeddeds.forEach(it -> {
                    Criteria assessmentLevelCriteria = new Criteria().andOperator(
                            Criteria.where(UserCvDocEnum.QUALIFICATIONS_ASSESSMENT_ID.key()).is(it.getAssessment().getId()),
                            Criteria.where(UserCvDocEnum.QUALIFICATIONS_LEVEL_ASSESSMENT_LEVEL.key()).gte(it.getLevel().getAssessmentLevel()),
                            Criteria.where(UserCvDocEnum.QUALIFICATIONS_SUBMISSION_TIME.key()).gte(expiredDate));
                    assessmentLevelCriterias.add(assessmentLevelCriteria);
                });

                if (assessmentLevelCriterias.size() > 1) {
                    return new Criteria().andOperator(assessmentLevelCriterias.toArray(new Criteria[0]));
                } else if (assessmentLevelCriterias.size() == 1) {
                    return assessmentLevelCriterias.get(0);
                }
            }
        }
        return null;
    }

    private Criteria initJobCriteria(VacancyDoc vacancyDoc) {
        return Criteria.where(UserCvDocEnum.JOBS_ID.key()).is(vacancyDoc.getJob().getId());
    }

    private Criteria initSearchRangeCriteria(VacancyDoc vacancyDoc) {

        switch (vacancyDoc.getSearchRange()) {
            case SearchRange.CITY:
                return Criteria.where(UserCvDocEnum.USER_PROFILE_CITY_ID.key())
                        .is(ofNullable(vacancyDoc.getSearchLocation()).map(it -> it.getCity().getId()).orElse(0L));

            case SearchRange.PROVINCE:
                return Criteria.where(UserCvDocEnum.USER_PROFILE_CITY_PROVINCE_ID.key())
                        .is(ofNullable(vacancyDoc.getSearchLocation()).map(it -> it.getCity().getProvinceId()).orElse(0L));

            case SearchRange.COUNTRY:
                long countryId = ofNullable(vacancyDoc.getSearchLocation()).map(it -> it.getCity().getCountryId()).orElse(0L);

                return new Criteria().orOperator(
                        Criteria.where(UserCvDocEnum.USER_PROFILE_COUNTRY_ID.key()).is(countryId),
                        Criteria.where(UserCvDocEnum.USER_PROFILE_CITY_PROVINCE_COUNTRY_ID.key()).is(countryId));
            default:
                return null;
        }
    }

    private Criteria initSalaryRateCriteria(VacancyDoc vacancyDoc) {
        return Criteria.where(UserCvDoc.Fields.isHourSalary).is(vacancyDoc.isHourSalary());
    }


    private Criteria initStaffCriteria(VacancyDoc vacancyDoc) {
        Page<Staff> staffPage = staffService.findStaffOfCompany(vacancyDoc.getCompany().getId(), 0, 0);
        if (CollectionUtils.isNotEmpty(staffPage.getContent())) {
            List<Long> staffOfCompany = staffPage.getContent().stream().map(s -> s.getUserFit().getUserProfileId()).collect(toList());
            return Criteria.where(UserCvDocEnum.USER_PROFILE_ID.key()).nin(staffOfCompany);
        }
        return null;
    }

    private Criteria initRejectedUserCriteria(VacancyDoc vacancyDoc, int limitTime) {
        if (CollectionUtils.isNotEmpty(vacancyDoc.getRejectedUserCv())) {
            Date expiredDate = DateUtils.addSecond(DateUtils.toServerTimeForMongo(), limitTime);
            List<Long> blackListUserCvId = vacancyDoc.getRejectedUserCv().stream()
                    .filter(cv -> expiredDate.compareTo(cv.getRejectedDate()) <= 0)
                    .map(RejectedUserCvEmbedded::getUserCvId).collect(toList());

            return Criteria.where(UserCvDoc.Fields.id).nin(blackListUserCvId);
        }
        return null;
    }

    private Criteria initAppointmentSlotsCriteria(VacancyDoc vacancyDoc) {
        if (MapUtils.isNotEmpty(vacancyDoc.getAppointmentSlots())) {
            Map<Long, List<AppointmentSlotEmbedded>> appointmentSlotMap = vacancyDoc.getAppointmentSlots();
            List<Long> candidateAvailableInAppointment = new ArrayList<>();
            if (MapUtils.isNotEmpty(appointmentSlotMap)) {
                appointmentSlotMap.forEach((userCVId, slots) -> {
                    if (isAvailableAppointmentSlot(slots)) {
                        candidateAvailableInAppointment.add(userCVId);
                    }
                });
            }
            return Criteria.where(UserCvDoc.Fields.id).nin(candidateAvailableInAppointment);
        }
        return null;
    }

    private Criteria intiAppliedUserCriteria(VacancyDoc vacancyDoc) {
        return ofNullable(vacancyDoc.getAppliedUserCvId()).map(it -> Criteria.where(UserCvDoc.Fields.id).nin(it)).orElse(null);
    }

    private Criteria initClosedCandidateOnClonedVacancyCriteria(VacancyDoc vacancyDoc) {
        return ofNullable(vacancyDoc.getClosedCandidateIdsOnClonedVacancy()).map(it -> Criteria.where(UserCvDoc.Fields.id).nin(it)).orElse(null);
    }

    private Criteria initClosedCandidateCriteria(VacancyDoc vacancyDoc) {
        if (CollectionUtils.isNotEmpty(vacancyDoc.getClosedCandidates())) {
            List<Long> closedCandidateIds = vacancyDoc.getClosedCandidates().stream().map(UserProfileCvEmbedded::getUserProfileCvId).collect(toList());
            return Criteria.where(UserCvDoc.Fields.id).nin(closedCandidateIds);
        }
        return null;
    }

    private Criteria initEducationCriteria(VacancyDoc vacancyDoc) {
        return ofNullable(vacancyDoc.getEducation()).map(it -> Criteria.where(UserCvDocEnum.EDUCATION_ID.key()).gte(it.getId())).orElse(null);
    }

    private Criteria initWorkingDayCriteria(VacancyDoc vacancyDoc) {
        return Criteria.where(UserCvDoc.Fields.isFullTime).is(vacancyDoc.isFullTime());
    }

    private Criteria initDesiredHoursCriteria(VacancyDoc vacancyDoc) {
        if (CollectionUtils.isNotEmpty(vacancyDoc.getDesiredHours())) {
            List<Long> workingHourIds = vacancyDoc.getDesiredHours().stream().map(WorkingHourEmbedded::getId).collect(toList());
            return Criteria.where(UserCvDocEnum.DESIRED_HOURS_ID.key()).in(workingHourIds);
        }
        return null;
    }

    private Criteria initNativeLanguageCriteria(VacancyDoc vacancyDoc) {
        if (CollectionUtils.isNotEmpty(vacancyDoc.getNativeLanguages())) {
            List<Criteria> langIdCriteria = new ArrayList<>();
            vacancyDoc.getNativeLanguages().stream().map(LanguageEmbedded::getId).forEach(id -> {
                langIdCriteria.add(Criteria.where(UserCvDocEnum.USER_PROFILE_NATIVE_LANGUAGES_ID.key()).is(id));
            });
            return new Criteria().andOperator(langIdCriteria.toArray(new Criteria[0]));
        }
        return null;
    }

    private Criteria initLanguageCriteria(VacancyDoc vacancyDoc) {
        if (CollectionUtils.isNotEmpty(vacancyDoc.getLanguages())) {
            List<Criteria> langIdCriteria = new ArrayList<>();
            vacancyDoc.getLanguages().stream().map(LanguageEmbedded::getId).forEach(id -> {
                langIdCriteria.add(Criteria.where(UserCvDocEnum.USER_PROFILE_LANGUAGES_ID.key()).is(id));
            });
            return new Criteria().andOperator(langIdCriteria.toArray(new Criteria[0]));
        }
        return null;
    }

    private Criteria initSalaryCriteria(VacancyDoc vacancyDoc) {
        Criteria salaryCase1 = new Criteria().andOperator(
                Criteria.where(UserCvDoc.Fields.minSalaryUsd).lt(vacancyDoc.getSalaryUsd()),
                Criteria.where(UserCvDoc.Fields.maxSalaryUsd).gt(vacancyDoc.getSalaryUsd()));

        Criteria salaryCase2 = new Criteria().andOperator(
                Criteria.where(UserCvDoc.Fields.minSalaryUsd).is(vacancyDoc.getSalaryUsd()),
                Criteria.where(UserCvDoc.Fields.maxSalaryUsd).is(vacancyDoc.getSalaryMaxUsd()));

        Criteria salaryCase3 = new Criteria().andOperator(
                Criteria.where(UserCvDoc.Fields.minSalaryUsd).gt(vacancyDoc.getSalaryUsd()),
                Criteria.where(UserCvDoc.Fields.maxSalaryUsd).lt(vacancyDoc.getSalaryMaxUsd()));

        Criteria salaryCase4 = new Criteria().andOperator(
                Criteria.where(UserCvDoc.Fields.minSalaryUsd).gt(vacancyDoc.getSalaryUsd()),
                Criteria.where(UserCvDoc.Fields.maxSalaryUsd).gt(vacancyDoc.getSalaryMaxUsd()),
                Criteria.where(UserCvDoc.Fields.minSalaryUsd).lte(vacancyDoc.getSalaryMaxUsd()));
        return new Criteria().orOperator(salaryCase1, salaryCase2, salaryCase3, salaryCase4);
    }

    private Criteria initAvailabilityDateCriteria(VacancyDoc vacancyDoc) {
        if (!vacancyDoc.isAsap()) {
            Date endDate = DateUtils.atEndOfDateFromStart(vacancyDoc.getExpectedStartDate());
            return new Criteria().orOperator(
                    Criteria.where(UserCvDoc.Fields.isAsap).is(true),
                    Criteria.where(UserCvDoc.Fields.expectedStartDate).lte(endDate));
        }
        return null;
    }

    private boolean isAvailableAppointmentSlot(List<AppointmentSlotEmbedded> slots) {
        AtomicBoolean result = new AtomicBoolean();
        if (CollectionUtils.isNotEmpty(slots)) {
            slots.forEach(sl -> {
                if (AppointmentStatus.getAvailableStatus().contains(sl.getStatus())
                        && (Objects.nonNull(sl.getAppointmentTime()) && DateUtils.toServerTimeForMongo().compareTo(sl.getAppointmentTime()) <= 0
                        || Objects.nonNull(sl.getExpiredDate()) && DateUtils.toServerTimeForMongo().compareTo(sl.getExpiredDate()) <= 0)) {
                    result.set(true);
                }
            });
        }
        return result.get();
    }
}
