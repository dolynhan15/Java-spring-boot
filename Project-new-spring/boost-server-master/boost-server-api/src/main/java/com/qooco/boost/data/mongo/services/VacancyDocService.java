package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.model.count.CountByLocation;
import com.qooco.boost.data.model.count.VacancyGroupByLocation;
import com.qooco.boost.data.mongo.embedded.ProvinceEmbedded;
import com.qooco.boost.data.mongo.embedded.RejectedUserCvEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.models.sdo.AppointmentSlotEmbeddedSDO;

import java.util.Date;
import java.util.List;

public interface VacancyDocService extends DocService<VacancyDoc, Long> {
    List<VacancyDoc> findByAppointmentIds(List<Long> ids);

    void updateAppointmentCandidates(List<AppointmentDetailDoc> docs);

    void updateAppointmentCandidatesEmbedded(List<AppointmentSlotEmbeddedSDO> appointmentDetails, long vacancyId);

    void updateOrInsertVacancyDoc(List<VacancyDoc> vacancyDocs);

    void updateContactPerson(List<Long> ids, StaffEmbedded contactPerson);

    void updateCandidateProfiles(List<UserProfileCvEmbedded> candidateProfiles, long vacancyId, long foundCandidate);

    void addAppliedUserCvIds(List<Long> appliedUserCvIds, List<RejectedUserCvEmbedded> rejectedUserCVs, long vacancyId);

    void addRejectedUserCv(List<RejectedUserCvEmbedded> rejectedUserCVs, long vacancyId);

    void updateDateTimeRangeAndType(long appointmentId, List<Date> dateRanges, List<Date> timeRanges, int type, Date fromDate, Date toDate);

    void updateStatusAndClosedCandidatesOfVacancy(long id, Integer status, List<UserProfileCvEmbedded> closedCandidates);

    void updateStatusOfVacancy(List<Vacancy> vacancies);

    List<CountByLocation> countVacancyGroupByLocation(UserCvDoc userCvDoc, MatchingCandidateConfig config);

    VacancyGroupByLocation countVacancyGroupByProvince(ProvinceEmbedded province);

    List<VacancyDoc> findMatchingVacancyForCv(UserCvDoc userCvDoc, int ignoreMatchingType, MatchingCandidateConfig config, List<Long> exceptedVacancyIds, int size);

    long countMatchingVacancyForCv(UserCvDoc userCvDoc, int ignoreMatchingType, MatchingCandidateConfig config);

    long countLowQualificationForAssessment(QualificationEmbedded qualification, UserCvDoc userCvDoc, MatchingCandidateConfig config);

    List<VacancyDoc> findOpenVacancyByIdGreaterThan(Long prevId, int limit);

    List<VacancyDoc> findByCurrencyIdGreaterThan(long currencyId, Long prevId, int limit);

    void updateCurrencyAndSalaryInUsd(Currency currency, List<VacancyDoc> vacancyDocs);
}
