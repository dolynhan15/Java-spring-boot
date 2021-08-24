package com.qooco.boost.data.mongo.services.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.SearchRange;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.enumeration.doc.UserCvDocEnum;
import com.qooco.boost.data.enumeration.doc.VacancyDocEnum;
import com.qooco.boost.data.model.count.CountByLocation;
import com.qooco.boost.data.model.count.VacancyGroupByLocation;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentSlotEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.repositories.VacancyDocRepository;
import com.qooco.boost.data.mongo.services.AppointmentDetailDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.mongo.services.embedded.VacancyEmbeddedService;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.entities.VacancyAssessmentLevel;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.models.sdo.AppointmentSlotEmbeddedSDO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.MongoInitData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class VacancyDocServiceImpl implements VacancyDocService {
    @Autowired
    private VacancyDocRepository repository;
    @Autowired
    private AppointmentDetailDocService appointmentDetailDocService;
    @Autowired
    private VacancyEmbeddedService vacancyEmbeddedService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StaffService staffService;
    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    @Override
    public MongoRepository<VacancyDoc, Long> getRepository() {
        return repository;
    }

    @Override
    public List<VacancyDoc> findByAppointmentIds(List<Long> ids) {
        return ofNullable(ids).filter(CollectionUtils::isNotEmpty).map(it ->
                mongoTemplate.find(new Query(Criteria.where(VacancyDocEnum.APPOINTMENTS_ID.key()).in(it)), VacancyDoc.class)
        ).orElseGet(ImmutableList::of);
    }

    @Override
    public void updateAppointmentCandidates(List<AppointmentDetailDoc> docs) {
        if (CollectionUtils.isNotEmpty(docs)) {
            List<Long> vacancyIds = docs.stream().map(ad -> ad.getVacancy().getId()).distinct().collect(Collectors.toList());
            List<VacancyDoc> vacancyDocs = findAllById(vacancyIds);

            vacancyDocs.forEach(v -> {
                List<AppointmentDetailDoc> detailDocs = docs.stream()
                        .filter(ad -> v.getId().equals(ad.getVacancy().getId()))
                        .collect(Collectors.toList());

                Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(v.getId());

                //TODO: Remove this update after update search role
                Update updater = initAppointmentCandidateUpdate(v, detailDocs);
                mongoTemplate.upsert(new Query(criteria), updater, VacancyDoc.class);

                Update updaterAppointmentSlots = initAppointmentSlotsUpdate(v, detailDocs);
                mongoTemplate.upsert(new Query(criteria), updaterAppointmentSlots, VacancyDoc.class);

            });
        }
    }

    @Override
    public void updateAppointmentCandidatesEmbedded(List<AppointmentSlotEmbeddedSDO> appointmentDetails, long vacancyId) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancyId);
            Update update = new Update();
            appointmentDetails.forEach(ap -> {
                StringBuilder key = new StringBuilder(VacancyDocEnum.APPOINTMENT_CANDIDATES.key());
                key.append(Constants.DOT).append(ap.getUserCvId());
                update.set(key.toString(), ap.getAppointmentSlotEmbedded());
            });
            mongoTemplate.updateFirst(new Query(criteria), update, VacancyDoc.class);
        }
    }

    @Override
    public void updateOrInsertVacancyDoc(List<VacancyDoc> vacancyDocs) {
        BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VacancyDoc.class);
        vacancyDocs.forEach(v -> {
            Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(v.getId());
            bulkOps.updateOne(new Query(criteria), initVacancyDocUpdate(v));
        });
        bulkOps.execute();

        vacancyDocs.forEach(v -> vacancyEmbeddedService.update(MongoConverters.convertToVacancyEmbedded(v)));
    }

    @Override
    public void updateContactPerson(List<Long> ids, StaffEmbedded contactPerson) {
        if (CollectionUtils.isNotEmpty(ids)) {
            Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).in(ids);
            Update update = new Update().set(VacancyDocEnum.CONTACT_PERSON.key(), contactPerson);
            mongoTemplate.updateMulti(new Query(criteria), update, VacancyDoc.class);
            List<VacancyDoc> vacancies = findAllById(ids);

            List<VacancyEmbedded> embedded = vacancies.stream()
                    .map(MongoConverters::convertToVacancyEmbedded)
                    .collect(Collectors.toList());
            vacancyEmbeddedService.update(embedded);
        }
    }

    @Override
    public void updateCandidateProfiles(List<UserProfileCvEmbedded> candidateProfiles, long vacancyId, long foundCandidate) {
        Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancyId);
        Update update = new Update();
        update.set(VacancyDocEnum.CANDIDATE_PROFILES.key(), candidateProfiles);
        update.set(VacancyDocEnum.NUMBER_OF_CANDIDATE.key(), foundCandidate);
        mongoTemplate.updateFirst(new Query(criteria), update, VacancyDoc.class);
    }

    @Override
    public void addAppliedUserCvIds(List<Long> appliedUserCvIds, List<RejectedUserCvEmbedded> rejectedUserCVs, long vacancyId) {
        Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancyId);
        Update update = new Update();
        update.set(VacancyDocEnum.APPLIED_USER_CV_ID.key(), appliedUserCvIds);
        update.set(VacancyDocEnum.REJECTED_USER_CV.key(), rejectedUserCVs);
        update.set(VacancyDocEnum.UPDATED_DATE.key(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateFirst(new Query(criteria), update, VacancyDoc.class);
    }

    @Override
    public void addRejectedUserCv(List<RejectedUserCvEmbedded> rejectedUserCVs, long vacancyId) {
        Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancyId);
        Update update = new Update();
        update.set(VacancyDocEnum.REJECTED_USER_CV.key(), rejectedUserCVs);
        update.set(VacancyDocEnum.UPDATED_DATE.key(), DateUtils.toServerTimeForMongo());
        mongoTemplate.updateFirst(new Query(criteria), update, VacancyDoc.class);
    }

    @Override
    public void updateDateTimeRangeAndType(long appointmentId, List<Date> dateRanges, List<Date> timeRanges, int type, Date fromDate, Date toDate) {
        Criteria criteria = Criteria.where(VacancyDocEnum.APPOINTMENTS_ID.key()).is(appointmentId);

        Update update = new Update();
        update.set(VacancyDocEnum.APPOINTMENT_DATE_RANGE.key(), dateRanges);
        update.set(VacancyDocEnum.APPOINTMENT_TIME_RANGE.key(), timeRanges);
        update.set(VacancyDocEnum.APPOINTMENT_TYPE.key(), type);
        update.set(VacancyDocEnum.APPOINTMENT_FROM_DATE.key(), fromDate);
        update.set(VacancyDocEnum.APPOINTMENT_TO_DATE.key(), toDate);
        mongoTemplate.updateMulti(new Query(criteria), update, VacancyDoc.class);
    }

    @Override
    public void updateStatusAndClosedCandidatesOfVacancy(long id, Integer status, List<UserProfileCvEmbedded> closedCandidates) {
        Criteria criteria = Criteria.where(VacancyDocEnum.ID.key()).is(id);
        Update update = new Update();
        update.set(VacancyDocEnum.STATUS.key(), status);
        update.set(VacancyDocEnum.CLOSED_CANDIDATES.key(), closedCandidates);
        mongoTemplate.updateMulti(new Query(criteria), update, VacancyDoc.class);
    }

    private Update initAppointmentSlotsUpdate(VacancyDoc vacancyDoc, List<AppointmentDetailDoc> appointmentDetailDocs) {
        Update update = new Update();
        if (CollectionUtils.isNotEmpty(appointmentDetailDocs)) {
            Map<Long, List<AppointmentSlotEmbedded>> slotMap = vacancyDoc.getAppointmentSlots();
            Map<Long, List<AppointmentSlotEmbedded>> appointmentSlotsMap = MapUtils.isNotEmpty(slotMap) ? slotMap : new HashMap<>();

            Map<Long, AppointmentDetailDoc> appointmentCandidateMap = new HashMap<>();
            appointmentDetailDocs.forEach(ap -> appointmentCandidateMap.put(ap.getCandidate().getUserProfileCvId(), ap));

            appointmentCandidateMap.forEach((userCVId, appointmentDetailDoc) -> {
                List<AppointmentSlotEmbedded> slots = appointmentSlotsMap.get(userCVId);
                if (CollectionUtils.isEmpty(slots)) {
                    slots = new ArrayList<>();
                }

                AppointmentSlotEmbedded slot = new AppointmentSlotEmbedded(appointmentDetailDoc);
                slots.remove(slot);
                slots.add(slot);

                slots = removeUnavailableSlot(slots);
                appointmentSlotsMap.put(userCVId, slots);
            });

            update.set(VacancyDocEnum.APPOINTMENT_SLOTS.key(), appointmentSlotsMap);
        }
        return update;
    }

    private List<AppointmentSlotEmbedded> removeUnavailableSlot(List<AppointmentSlotEmbedded> slots) {
        List<AppointmentSlotEmbedded> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(slots)) {
            result = slots.stream().filter(sl -> AppointmentStatus.getAvailableStatus().contains(sl.getStatus())).collect(Collectors.toList());
        }
        return result;
    }

    @Override
    public void updateStatusOfVacancy(List<Vacancy> vacancies) {
        if (CollectionUtils.isNotEmpty(vacancies)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VacancyDoc.class);
            vacancies.forEach(vacancy -> {
                Criteria vacancyIdCriteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancy.getId());
                Update update = new Update();
                update.set(VacancyDocEnum.STATUS.key(), vacancy.getStatus());
                Date suspendedDate = null;
                Date endSuspendedDate = null;
                if (Objects.nonNull(vacancy.getStartSuspendDate())) {
                    suspendedDate = DateUtils.toServerTimeForMongo(vacancy.getStartSuspendDate());
                    if (Objects.nonNull(vacancy.getSuspendDays())) {
                        endSuspendedDate = DateUtils.addDays(suspendedDate, vacancy.getSuspendDays());
                    }
                }
                if (Objects.nonNull(suspendedDate)) {
                    update.set(VacancyDocEnum.START_SUSPEND_DATE.key(), suspendedDate);
                } else {
                    update.unset(VacancyDocEnum.START_SUSPEND_DATE.key());
                }
                if (Objects.nonNull(vacancy.getSuspendDays())) {
                    update.set(VacancyDocEnum.SUSPEND_DAYS.key(), vacancy.getSuspendDays());
                } else {
                    update.unset(VacancyDocEnum.SUSPEND_DAYS.key());
                }
                if (Objects.nonNull(endSuspendedDate)) {
                    update.set(VacancyDocEnum.END_SUSPEND_DATE.key(), endSuspendedDate);
                } else {
                    update.unset(VacancyDocEnum.END_SUSPEND_DATE.key());
                }

                if (Objects.nonNull(vacancy.getArchivist())) {
                    update.set(VacancyDocEnum.ARCHIVIST.key(), new StaffEmbedded(vacancy.getArchivist()));
                } else {
                    update.unset(VacancyDocEnum.ARCHIVIST.key());
                }
                double usdRate = vacancy.getCurrency().getValidUnitPerUsd();
                double salaryUsd = vacancy.getSalary() / usdRate;
                double salaryMaxUsd = vacancy.getSalaryMax() / usdRate;
                update.set(VacancyDocEnum.SALARY_USD.key(), salaryUsd);
                update.set(VacancyDocEnum.SALARY_MAX_USD.key(), salaryMaxUsd);
                List<AppointmentEmbedded> appointmentEmbeddeds = emptyIfNull(vacancy.getVacancyAppointments()).stream()
                        .map(MongoConverters::convertToAppointmentEmbedded).collect(Collectors.toList());
                if (appointmentEmbeddeds.isEmpty()) {
                    update.unset(VacancyDocEnum.APPOINTMENTS.key());
                } else {
                    update.set(VacancyDocEnum.APPOINTMENTS.key(), appointmentEmbeddeds);

                }
                List<VacancyAssessmentLevel> vacancyAssessmentLevels = vacancy.getVacancyAssessmentLevels();
                if (Objects.nonNull(vacancy.getVacancyAssessmentLevels())) {
                    update.set(VacancyDocEnum.QUALIFICATIONS.key(), vacancyAssessmentLevels.stream()
                            .map(al -> MongoConverters.convertToQualificationEmbedded(al.getAssessmentLevel()))
                            .collect(Collectors.toList()));

                } else {
                    update.unset(VacancyDocEnum.QUALIFICATIONS.key());
                }
                bulkOps.updateMulti(new Query(vacancyIdCriteria), update);
            });
            try {
                bulkOps.execute();
            } catch (IllegalArgumentException ignore) {
            }
        }
    }

    @Override
    public List<CountByLocation> countVacancyGroupByLocation(UserCvDoc userCvDoc, MatchingCandidateConfig config) {

        Criteria matchingCriteria = initMatchingVacancyForCv(userCvDoc, MatchingCandidateConfig.EXPAND_SEARCH_RANGE, config, newArrayList());

        var groupOperation = group(VacancyDocEnum.SEARCH_LOCATION_CITY_ID.key())
                .first(VacancyDocEnum.SEARCH_LOCATION_CITY_ID.key()).as(CountByLocation.LocationFields.cityId)
                .first(VacancyDocEnum.SEARCH_LOCATION_CITY_PROVINCE_ID.key()).as(CountByLocation.LocationFields.provinceId)
                .first(VacancyDocEnum.SEARCH_LOCATION_CITY_PROVINCE_COUNTRY_ID.key()).as(CountByLocation.LocationFields.countryId)
                .count().as(CountByLocation.LocationFields.total);

        var aggregation = newAggregation(match(matchingCriteria), groupOperation);
        return mongoTemplate.aggregate(aggregation, VacancyDoc.class, CountByLocation.class).getMappedResults();
    }

    @Override
    public VacancyGroupByLocation countVacancyGroupByProvince(ProvinceEmbedded province) {
        var criteria = Criteria.where(VacancyDocEnum.JOB_LOCATION_CITY_PROVINCE_ID.key()).is(province.getId());

        var groupOperation = group(VacancyDocEnum.JOB_LOCATION_CITY_PROVINCE_ID.key())
                .addToSet(VacancyDocEnum.ID.key()).as(VacancyGroupByLocation.VacancyFields.vacancyIds)
                .count().as(CountByLocation.LocationFields.total);

        var aggregation = newAggregation(match(criteria), groupOperation);
        return mongoTemplate.aggregate(aggregation, VacancyDoc.class, VacancyGroupByLocation.class).getUniqueMappedResult();
    }

    @Override
    public List<VacancyDoc> findMatchingVacancyForCv(UserCvDoc userCvDoc, int ignoreMatchingType, MatchingCandidateConfig config, List<Long> exceptedVacancyIds, int size) {
        Criteria criteria = initMatchingVacancyForCv(userCvDoc, ignoreMatchingType, config, exceptedVacancyIds);
        if (Objects.nonNull(criteria)) {
            Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, UserCvDoc.Fields.minSalary),
                    new Sort.Order(Sort.Direction.DESC, UserCvDocEnum.EDUCATION_ID.key()),
                    new Sort.Order(Sort.Direction.DESC, UserCvDoc.Fields.isAsap),
                    new Sort.Order(Sort.Direction.ASC, UserCvDoc.Fields.expectedStartDate));
            Query query = new Query(criteria);
            query.with(sort);

            query.limit(size);
            return mongoTemplate.find(query, VacancyDoc.class);
        }
        return newArrayList();
    }

    @Override
    public long countMatchingVacancyForCv(UserCvDoc userCvDoc, int ignoreMatchingType, MatchingCandidateConfig config) {
        Criteria criteria = initMatchingVacancyForCv(userCvDoc, ignoreMatchingType, config, newArrayList());
        return ofNullable(criteria).map(it -> mongoTemplate.count(new Query(it), VacancyDoc.class)).orElse(0L);
    }

    @Override
    public long countLowQualificationForAssessment(QualificationEmbedded qualification, UserCvDoc userCvDoc, MatchingCandidateConfig config) {
        Criteria criteria = initLowQualificationForAssement(qualification, userCvDoc, MatchingCandidateConfig.LOWER_QUALIFICATION, config);
        return ofNullable(criteria).map(it -> mongoTemplate.count(new Query(it), VacancyDoc.class)).orElse(0L);
    }

    @Override
    public List<VacancyDoc> findOpenVacancyByIdGreaterThan(Long prevId, int limit) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(UserCvDoc.Fields.id).gt(prevId),
                initOpenVacancyCriteria());
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, UserCvDoc.Fields.id));
        Query query = new Query(criteria).limit(limit).with(sort);
        return mongoTemplate.find(query, VacancyDoc.class);
    }

    @Override
    public List<VacancyDoc> findByCurrencyIdGreaterThan(long currencyId, Long prevId, int limit) {
        Criteria criteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.CURRENCY_ID.key()).is(currencyId),
                Criteria.where(UserCvDoc.Fields.id).gt(prevId));
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, UserCvDoc.Fields.id));
        Query query = new Query(criteria).limit(limit).with(sort);
        return mongoTemplate.find(query, VacancyDoc.class);
    }

    @Override
    public void updateCurrencyAndSalaryInUsd(Currency currency, List<VacancyDoc> vacancyDocs) {
        if (CollectionUtils.isNotEmpty(vacancyDocs) && Objects.nonNull(currency)) {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, VacancyDoc.class);
            CurrencyEmbedded currencyEmbedded = MongoConverters.convertToCurrencyEmbedded(currency);
            vacancyDocs.forEach(vacancy -> {
                Criteria vacancyIdCriteria = Criteria.where(VacancyDocEnum.ID.key()).is(vacancy.getId());
                Update update = new Update();

                double usdRate = vacancy.getCurrency().getValidUnitPerUsd();
                double salaryUsd = vacancy.getSalary() / usdRate;
                double salaryMaxUsd = vacancy.getSalaryMax() / usdRate;
                update.set(VacancyDocEnum.SALARY_USD.key(), salaryUsd);
                update.set(VacancyDocEnum.SALARY_MAX_USD.key(), salaryMaxUsd);

                update.set(VacancyDocEnum.CURRENCY.key(), currencyEmbedded);

                bulkOps.updateMulti(new Query(vacancyIdCriteria), update);
            });
            try {
                bulkOps.execute();
            } catch (IllegalArgumentException ignore) {
            }
        }
    }

    private Criteria initOpenVacancyCriteria() {
        Criteria openVacancyCriteria = new Criteria();
        Criteria suspendCriteria = new Criteria();
        suspendCriteria.orOperator(Criteria.where(VacancyDocEnum.START_SUSPEND_DATE.key()).exists(false),
                new Criteria().andOperator(Criteria.where(VacancyDocEnum.SUSPEND_DAYS.key()).exists(true),
                        Criteria.where(VacancyDocEnum.END_SUSPEND_DATE.key()).exists(true),
                        Criteria.where(VacancyDocEnum.END_SUSPEND_DATE.key()).lt(DateUtils.toServerTimeForMongo())));
        openVacancyCriteria.andOperator(Criteria.where(VacancyDocEnum.STATUS.key()).is(Const.Vacancy.Status.OPENING),
                suspendCriteria);
        return openVacancyCriteria;
    }

    private Criteria initMatchingVacancyForCv(UserCvDoc userCvDoc, int ignoreMatchingType, MatchingCandidateConfig config, List<Long> exceptedVacancyIds) {
        Criteria matchingCriteria = new Criteria();
        List<Criteria> criterias = new ArrayList<>();

        Criteria openVacancyCriteria = initOpenVacancyCriteria();
        criterias.add(openVacancyCriteria);

        List<Long> jobIds = userCvDoc.getJobs().stream().map(JobEmbedded::getId).collect(Collectors.toList());
        Criteria jobCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.JOB_ID.key()).ne(config.getVacancyJobAlphaTest()),
                Criteria.where(VacancyDocEnum.JOB_ID.key()).in(jobIds));
        criterias.add(jobCriteria);

        Criteria hourlyCriteria = Criteria.where(VacancyDocEnum.IS_HOUR_SALARY.key()).is(userCvDoc.isHourSalary());
        criterias.add(hourlyCriteria);

        Criteria fullTimeCriteria = Criteria.where(VacancyDocEnum.IS_FULL_TIME.key()).is(userCvDoc.isFullTime());
        criterias.add(fullTimeCriteria);

        if (!userCvDoc.isAsap()) {
            Criteria startDateCriteria = new Criteria();
            Criteria isAsapCriteria = Criteria.where(VacancyDocEnum.IS_ASAP.key()).is(true);

            Date endDate = DateUtils.atEndOfDateFromStart(userCvDoc.getExpectedStartDate());
            Criteria expectedDateCriteria = Criteria.where(VacancyDocEnum.EXPECTED_START_DATE.key()).gte(endDate);
            startDateCriteria.orOperator(isAsapCriteria, expectedDateCriteria);
            criterias.add(startDateCriteria);
        }

        if (CollectionUtils.isNotEmpty(userCvDoc.getUserProfile().getNativeLanguages())) {
            List<Long> nativeLanguageIds = userCvDoc.getUserProfile().getNativeLanguages().stream()
                    .map(LanguageEmbedded::getId).collect(Collectors.toList());
            Criteria nativeLanguagesCriteria = Criteria.where(VacancyDocEnum.NATIVE_LANGUAGES_ID.key()).all(nativeLanguageIds);
            criterias.add(nativeLanguagesCriteria);
        } else {
            Criteria nativeLanguagesCriteria = new Criteria().orOperator(
                    Criteria.where(VacancyDocEnum.NATIVE_LANGUAGES.key()).exists(false),
                    Criteria.where(VacancyDocEnum.NATIVE_LANGUAGES.key()).size(0));
            criterias.add(nativeLanguagesCriteria);
        }

        if (CollectionUtils.isNotEmpty(userCvDoc.getUserProfile().getLanguages())) {
            List<Long> languageIds = userCvDoc.getUserProfile().getLanguages().stream()
                    .map(LanguageEmbedded::getId).collect(Collectors.toList());
            Criteria languagesCriteria = Criteria.where(VacancyDocEnum.LANGUAGES_ID.key()).all(languageIds);
            criterias.add(languagesCriteria);
        } else {
            Criteria languagesCriteria = new Criteria().orOperator(
                    Criteria.where(VacancyDocEnum.LANGUAGES.key()).exists(false),
                    Criteria.where(VacancyDocEnum.LANGUAGES.key()).size(0));
            criterias.add(languagesCriteria);
        }

        if (CollectionUtils.isNotEmpty(userCvDoc.getDesiredHours())) {
            List<Long> workingHourIds = userCvDoc.getDesiredHours().stream().map(WorkingHourEmbedded::getId).collect(Collectors.toList());
            Criteria workingHourCriteria = Criteria.where(VacancyDocEnum.DESIRED_HOURS_ID.key()).in(workingHourIds);
            criterias.add(workingHourCriteria);
        }

        if (Objects.nonNull(userCvDoc.getEducation())) {
            Criteria educationCriteria = Criteria.where(VacancyDocEnum.EDUCATION_ID.key()).lte(userCvDoc.getEducation().getId());
            criterias.add(educationCriteria);
        }

        List<Staff> staffs = staffService.findByUserProfileAndStatus(userCvDoc.getUserProfile().getUserProfileId(), ApprovalStatus.APPROVED);
        if (CollectionUtils.isNotEmpty(staffs)) {
            List<Long> companyIds = staffs.stream().map(s -> s.getCompany().getCompanyId()).distinct().collect(Collectors.toList());
            Criteria staffCriteria = Criteria.where(VacancyDocEnum.COMPANY_ID.key()).nin(companyIds);
            criterias.add(staffCriteria);
        }

        Criteria closedCandidateCriteria = Criteria.where(VacancyDocEnum.CLOSED_CANDIDATES_USER_PROFILE_ID.key())
                .nin(newArrayList(userCvDoc.getUserProfile().getUserProfileId()));
        criterias.add(closedCandidateCriteria);

        Criteria closedCandidateOnClonedCriteria = Criteria.where(VacancyDocEnum.CLOSED_CANDIDATE_IDS_ON_CLONED_VACANCY.key())
                .nin(newArrayList(userCvDoc.getUserProfile().getUserProfileId()));
        criterias.add(closedCandidateOnClonedCriteria);

        Criteria appliedCvCriteria = Criteria.where(VacancyDocEnum.APPLIED_USER_CV_ID.key()).nin(newArrayList(userCvDoc.getId()));
        criterias.add(appliedCvCriteria);

        Date expiredDateOfRejected = DateUtils.addSecond(DateUtils.toServerTimeForMongo(null), rejectedLimitTime);

        Criteria blackListUserCvCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.REJECTED_USER_CV_ID_ELEMENT.key()).in(newArrayList(userCvDoc.getId())),
                Criteria.where(VacancyDocEnum.REJECTED_USER_CV_REJECTED_DATE.key()).gt(expiredDateOfRejected));
        Criteria rejectedCandidate = new Criteria().orOperator(
                Criteria.where(VacancyDocEnum.REJECTED_USER_CV_ID.key()).nin(newArrayList(userCvDoc.getId())),
                Criteria.where(VacancyDocEnum.REJECTED_USER_CV.key()).elemMatch(blackListUserCvCriteria));
        criterias.add(rejectedCandidate);

        Criteria appointmentCriteria = new Criteria().orOperator(
                Criteria.where(VacancyDocEnum.APPOINTMENT_SLOTS_USER_CV_ID.key()).exists(false),
                Criteria.where(VacancyDocEnum.APPOINTMENT_SLOTS_USER_CV_ID.key()).size(0));
        criterias.add(appointmentCriteria);

        boolean boostScoreMatching = config.isBoostScoreEnabled();
        if (boostScoreMatching) {
            if (MatchingCandidateConfig.IGNORE_SALARY == ignoreMatchingType
                    || MatchingCandidateConfig.EXPAND_SEARCH_RANGE == ignoreMatchingType) {
                List<Long> assessmentIds = getActiveAssessments(userCvDoc, config.getExpiredDays());
                // TODO qualification
                Criteria qualificationCriteria = new Criteria();
                List<Criteria> assessmentCriteria = new ArrayList<>();
                assessmentIds.forEach(id -> assessmentCriteria.add(Criteria.where(VacancyDocEnum.QUALIFICATIONS_ASSESSMENT_ID.key()).is(id)));
                if (CollectionUtils.isNotEmpty(assessmentCriteria)) {
                    qualificationCriteria.andOperator(assessmentCriteria.toArray(new Criteria[0]));
                    criterias.add(qualificationCriteria);
                } else {
                    return null;
                }
            }
        }

        switch (ignoreMatchingType) {
            case MatchingCandidateConfig.IGNORE_SALARY:
                criterias.add(initLocationCriteria(userCvDoc));
                break;

            case MatchingCandidateConfig.EXPAND_SEARCH_RANGE:
                criterias.add(initSalaryCriteria(userCvDoc));
                criterias.add(initSearchRangeCriteria(userCvDoc));
                break;

            case MatchingCandidateConfig.LOWER_QUALIFICATION:
                criterias.add(initSalaryCriteria(userCvDoc));
                criterias.add(initLocationCriteria(userCvDoc));
                break;

            default:
                break;
        }

        ofNullable(exceptedVacancyIds).filter(CollectionUtils::isNotEmpty).ifPresent(it -> criterias.add(Criteria.where(VacancyDocEnum.ID.key()).nin(it)));

        return matchingCriteria.andOperator(criterias.toArray(new Criteria[0]));
    }

    private Criteria initSearchRangeCriteria(UserCvDoc userCvDoc) {
        Criteria cityCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SEARCH_LOCATION_CITY_PROVINCE_ID.key()).is(userCvDoc.getUserProfile().getCity().getProvinceId()),
                Criteria.where(VacancyDocEnum.SEARCH_RANGE.key()).is(SearchRange.CITY));
        Criteria provinceCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SEARCH_LOCATION_CITY_PROVINCE_COUNTRY_ID.key()).is(userCvDoc.getUserProfile().getCity().getCountryId()),
                Criteria.where(VacancyDocEnum.SEARCH_RANGE.key()).is(SearchRange.PROVINCE));
        return new Criteria().orOperator(cityCriteria, provinceCriteria);
    }

    private Criteria initLocationCriteria(UserCvDoc userCvDoc) {
        Criteria cityCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SEARCH_LOCATION_CITY_ID.key()).is(userCvDoc.getUserProfile().getCity().getId()),
                Criteria.where(VacancyDocEnum.SEARCH_RANGE.key()).is(SearchRange.CITY));
        Criteria provinceCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SEARCH_LOCATION_CITY_PROVINCE_ID.key()).is(userCvDoc.getUserProfile().getCity().getProvince().getId()),
                Criteria.where(VacancyDocEnum.SEARCH_RANGE.key()).is(SearchRange.PROVINCE));
        Criteria countryCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SEARCH_LOCATION_CITY_PROVINCE_COUNTRY_ID.key()).is(userCvDoc.getUserProfile().getCity().getProvince().getCountry().getId()),
                Criteria.where(VacancyDocEnum.SEARCH_RANGE.key()).is(SearchRange.COUNTRY));
        Criteria worldCriteria = Criteria.where(VacancyDocEnum.SEARCH_RANGE.key()).is(SearchRange.WORLD);
        return new Criteria().orOperator(cityCriteria, provinceCriteria, countryCriteria, worldCriteria);
    }

    private Criteria initSalaryCriteria(UserCvDoc userCvDoc) {
        Criteria salaryCriteria = new Criteria();
        Criteria salaryCase1 = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SALARY_USD.key()).lt(userCvDoc.getMaxSalaryUsd()),
                Criteria.where(VacancyDocEnum.SALARY_USD.key()).gt(userCvDoc.getMinSalaryUsd()));

        Criteria salaryCase2 = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SALARY_USD.key()).is(userCvDoc.getMinSalaryUsd()),
                Criteria.where(VacancyDocEnum.SALARY_MAX_USD.key()).is(userCvDoc.getMaxSalaryUsd()));

        Criteria salaryCase3 = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SALARY_MAX_USD.key()).gt(userCvDoc.getMaxSalaryUsd()),
                Criteria.where(VacancyDocEnum.SALARY_USD.key()).lt(userCvDoc.getMinSalaryUsd()));

        Criteria salaryCase4 = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.SALARY_USD.key()).lt(userCvDoc.getMinSalaryUsd()),
                Criteria.where(VacancyDocEnum.SALARY_MAX_USD.key()).gte(userCvDoc.getMinSalaryUsd()),
                Criteria.where(VacancyDocEnum.SALARY_MAX_USD.key()).lt(userCvDoc.getMaxSalaryUsd()));

        salaryCriteria.orOperator(salaryCase1, salaryCase2, salaryCase3, salaryCase4);
        return salaryCriteria;
    }

    private List<Long> getActiveAssessments(UserCvDoc userCvDoc, int expiredDays) {
        Date expiredDate = DateUtils.addDays(DateUtils.toServerTimeForMongo(), -expiredDays);
        return userCvDoc.getQualifications().stream()
                .filter(it -> expiredDate.after(it.getSubmissionTime()))
                .map(it -> it.getAssessment().getId()).collect(Collectors.toList());
    }


    private Criteria initLowQualificationForAssement(QualificationEmbedded qualification, UserCvDoc userCvDoc, int ignoreMatchingType, MatchingCandidateConfig config) {
        Criteria matchingCriteria = initMatchingVacancyForCv(userCvDoc, ignoreMatchingType, config, newArrayList());
        Criteria missingQualification = Criteria.where(VacancyDocEnum.QUALIFICATIONS_ASSESSMENT_ID.key()).nin(qualification.getAssessment().getId());

        Criteria levelCriteria = new Criteria().andOperator(
                Criteria.where(VacancyDocEnum.QUALIFICATIONS_ASSESSMENT_ID_ITEM.key()).is(qualification.getAssessment().getId()),
                Criteria.where(VacancyDocEnum.QUALIFICATIONS_LEVEL_ITEM.key()).gt(qualification.getLevel().getAssessmentLevel()));
        Criteria lowerQualification = Criteria.where(VacancyDocEnum.QUALIFICATIONS.key()).elemMatch(levelCriteria);

        Criteria qualificationCriteria = new Criteria().orOperator(missingQualification, lowerQualification);

        return new Criteria().andOperator(matchingCriteria, qualificationCriteria);
    }

    @Deprecated
    private Update initAppointmentCandidateUpdate(VacancyDoc vacancyDoc, List<AppointmentDetailDoc> appointmentDetailDocs) {
        Update update = new Update();
        if (CollectionUtils.isNotEmpty(appointmentDetailDocs)) {
            Map<Long, AppointmentSlotEmbedded> slotEmbeddedMap = vacancyDoc.getAppointmentCandidates();

            Map<Long, AppointmentDetailDoc> appointmentCandidateMap = new HashMap<>();
            appointmentDetailDocs.forEach(ap -> appointmentCandidateMap.put(ap.getCandidate().getUserProfileCvId(), ap));

            appointmentCandidateMap.forEach((userCVId, appointmentDetailDoc) -> {
                List<AppointmentDetailDoc> docList = findAppointmentDetail(vacancyDoc.getId(), userCVId);
                boolean checkStatus = CollectionUtils.isNotEmpty(docList) && docList.stream().allMatch(ap ->
                        (AppointmentStatus.PENDING.getValue() == ap.getStatus() && DateUtils.toServerTimeForMongo().after(ap.getAppointment().getToDate()))
                                || AppointmentStatus.getPendingAndCancelStatus().contains(ap.getStatus()));
                if (CollectionUtils.isEmpty(docList) || checkStatus) {
                    slotEmbeddedMap.remove(userCVId);
                } else {
                    slotEmbeddedMap.put(userCVId, new AppointmentSlotEmbedded(appointmentDetailDoc));
                }
            });
            update.set(VacancyDocEnum.APPOINTMENT_CANDIDATES.key(), slotEmbeddedMap);
        }
        return update;
    }

    private Update initVacancyDocUpdate(VacancyDoc vacancy) {
        Update update = new Update();
        Map<String, Object> map = MongoInitData.initVacancyDoc(vacancy);
        map.forEach(update::set);
        return update;
    }

    private List<AppointmentDetailDoc> findAppointmentDetail(Long vacancyId, Long userCVId) {
        return appointmentDetailDocService.findByVacancyIdAndCandidateCVId(vacancyId, newArrayList(userCVId));
    }
}
