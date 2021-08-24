package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.model.VacancyHasAppointment;
import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.data.oracle.reposistories.AppointmentDetailRepository;
import com.qooco.boost.data.oracle.services.AppointmentDetailService;
import com.qooco.boost.data.oracle.services.AppointmentService;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
public class AppointmentDetailServiceImpl implements AppointmentDetailService {
    @Autowired
    private AppointmentDetailRepository repository;
    @Autowired
    private AppointmentService appointmentService;

    @Override
    public List<CountByDate> countAppointmentByManager(Long staffId, Long startDate, Long endDate, String timeZoneOffset) {
        List<Object[]> result;
        if (Objects.nonNull(endDate)) {
            result = repository.countAppointmentByManager(staffId, DateUtils.toUtcForOracle(new Date(startDate)),
                    DateUtils.toUtcForOracle(new Date(endDate)), AppointmentStatus.getAcceptedStatus());
        } else {
            result = repository.countAppointmentByManagerFromDate(staffId,
                    DateUtils.toUtcForOracle(new Date(startDate)), AppointmentStatus.getAcceptedStatus());
        }

        return getCountEventByDate(result);
    }

    @Override
    public List<CountByDate> countAppointmentByUserInCompany(Long staffId, Long companyId, Long startDate, Long endDate, String timeZoneOffset, List<String> largerRoles) {
        List<Object[]> result;
        if (Objects.nonNull(endDate)) {
            if (CollectionUtils.isNotEmpty(largerRoles)) {
                result = repository.countAppointmentByUserInCompanyWithRoles(staffId, companyId, DateUtils.toUtcForOracle(new Date(startDate)),
                        DateUtils.toUtcForOracle(new Date(endDate)), AppointmentStatus.getAcceptedStatus(), largerRoles);
            } else {
                result = repository.countAppointmentByManager(staffId, DateUtils.toUtcForOracle(new Date(startDate)),
                        DateUtils.toUtcForOracle(new Date(endDate)), AppointmentStatus.getAcceptedStatus());
            }

        } else {
            if (CollectionUtils.isNotEmpty(largerRoles)) {
                result = repository.countAppointmentByUserInCompanyFromDateWithRoles(staffId, companyId,
                        DateUtils.toUtcForOracle(new Date(startDate)), AppointmentStatus.getAcceptedStatus(), largerRoles);
            } else {
                result = repository.countAppointmentByManagerFromDate(staffId,
                        DateUtils.toUtcForOracle(new Date(startDate)), AppointmentStatus.getAcceptedStatus());
            }

        }

        return getCountEventByDate(result);
    }

    private List<CountByDate> getCountEventByDate(List<Object[]> result) {
        List<CountByDate> countByDates = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {
            result.forEach(item -> {
                int total = Integer.valueOf(item[1].toString());
                Date date = DateUtils.getUtcForOracle(new Date(((Timestamp) item[0]).getTime()));
                CountByDate countByDate = new CountByDate(date, total);
                countByDates.add(countByDate);
            });
        }
        return countByDates;
    }

    @Override
    public AppointmentDetail save(AppointmentDetail appointmentDetail) {
        if (Objects.nonNull(appointmentDetail.getId())) {
            appointmentDetail.setUpdatedDate(DateUtils.nowUtcForOracle());
        }
        return repository.save(appointmentDetail);
    }

    @Override
    public List<AppointmentDetail> save(List<AppointmentDetail> appointmentDetails) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            appointmentDetails.forEach(ad -> {
                if (Objects.nonNull(ad.getId())) {
                    ad.setUpdatedDate(DateUtils.nowUtcForOracle());
                }
            });
        }
        return Lists.newArrayList(repository.saveAll(appointmentDetails));
    }

    @Override
    public AppointmentDetail findById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public List<AppointmentDetail> findByIds(List<Long> ids) {
        return repository.findByIds(ids);
    }

    @Override
    public List<Appointment> findAppointmentByIds(List<Long> ids) {
        return repository.findAppointmentByIds(ids);
    }

    @Override
    public List<AppointmentDetail> findByAppointmentId(Long appointmentId) {
        return Lists.newArrayList(repository.findByAppointmentId(appointmentId, Lists.newArrayList(Boolean.TRUE, Boolean.FALSE)));
    }

    @Override
    public AppointmentDetail findOneByVacancy(Long vacancyId) {
        return repository.findByVacancy(vacancyId, PageRequest.of(0, 1)).getContent().stream().findFirst().orElse(null);
    }

    @Override
    public List<AppointmentDetail> findByAppointmentIds(List<Long> appointmentIds) {
        return repository.findByAppointmentIds(appointmentIds);
    }

    @Override
    public List<AppointmentDetail> findByAppointmentId(Long appointmentId, Boolean isDelete) {
        return Lists.newArrayList(repository.findByAppointmentId(appointmentId, Lists.newArrayList(isDelete)));
    }

    @Override
    public List<AppointmentDetail> findAcceptedAppointmentInDuration(Long appointmentId, Date fromDate, Date toDate) {
        return repository.findAcceptedAppointmentDetailInDuration(appointmentId, fromDate, toDate, AppointmentStatus.getAcceptedStatus());
    }

    @Override
    public Page<AppointmentDetail> findByUserProfileIdAndStatus(Long userProfileId, List<Integer> statuses, int page, int size) {
        return repository.findByUserProfileIdAndStatus(userProfileId, statuses, PageRequest.of(page, size));
    }

    @Override
    public List<AppointmentDetail> findByUserProfileIdAndStatus(Long userProfileId, List<Integer> statuses, Date fromDate, Date toDate) {
        return repository.findByUserProfileIdAndStatus(userProfileId, statuses, fromDate, toDate);
    }

    @Override
    public List<AppointmentDetail> findByUserFitAndStatus(Long userId, Long companyId, List<Integer> statuses, Date lastTime, int limit) {
        if (Objects.isNull(companyId)) {
            return repository.findByUserFitAndStatus(userId, statuses, lastTime, limit);
        }
        return repository.findByUserFitAndStatusAndCompany(userId, companyId, statuses, lastTime, limit);
    }

    @Override
    public List<AppointmentDetail> findByUserProfileAndStatus(Long userId, List<Integer> statuses, Date lastTime, int limit) {
        return repository.findByUserProfileIdAndStatus(userId, statuses, lastTime, limit);
    }

    @Override
    public List<AppointmentDetail> getAppointmentDetailByCompanyIdInDuration(Long companyId, Long staffId, Date startDate, Date endDate, List<Integer> statuses) {
        return repository.getAppointmentDetailByCompanyIdInDuration(companyId, staffId, startDate, endDate, statuses);
    }

    @Override
    public List<AppointmentDetail> getAppointmentDetailByCompanyIdInDurationWithRoles(Long companyId, Long staffId, Date startDate, Date endDate, List<Integer> statuses, List<String> largerRoles) {
        if (CollectionUtils.isEmpty(largerRoles)) {
            return getAppointmentDetailByCompanyIdInDuration(companyId, staffId, startDate, endDate, statuses);
        }
        return repository.getAppointmentDetailByCompanyIdInDurationWithRoles(companyId, staffId, startDate, endDate, statuses, largerRoles);
    }

    @Override
    public List<AppointmentDetail> getAllAppointmentDetailByManagerAppointmentOrUserProfileInDuration(Long staffId, Long userProfileId, Date startDate, Date endDate, List<Integer> statuses) {
        return repository.getAllAppointmentDetailByStaffOrUserProfileInDuration(staffId, userProfileId, startDate, endDate, statuses);
    }

    @Override
    public boolean checkExistsAppointmentDetailByCompanyIdInDuration(Long companyId, Long staffId, Date eventTime, List<Integer> statuses) {
        int count = repository.checkExistsAppointmentDetailByCompanyIdInDuration(companyId, staffId, eventTime, statuses);
        return count > 0;
    }

    @Override
    public List<Object[]> findIdByUpdaterWithStatusAndRoles(Long ownedIds, List<Long> appointmentIds, List<Integer> statuses, List<String> roleNames) {
        return repository.findIdByUpdaterWithStatusAndRoles(ownedIds, appointmentIds, statuses, roleNames);
    }

    @Override
    public int countByAppointmentId(Long appointmentId) {
        return repository.countByAppointmentId(appointmentId);
    }

    @Override
    public List<AppointmentDetail> findByAppointmentIdAndStatuses(Long appointmentId, List<Integer> availableStatus) {
        return repository.findByAppointmentIdAndStatuses(appointmentId, availableStatus);
    }

    @Override
    public List<AppointmentDetail> findByAppointmentIdAndStatuses(List<Long> appointmentIds, List<Integer> availableStatus) {
        return repository.findByAppointmentIdsAndStatuses(appointmentIds, availableStatus);
    }

    @Override
    public List<AppointmentDetail> findByCandidateAndStatus(Long userProfileId, List<Integer> statuses) {
        return repository.findByCandidateAndStatus(userProfileId, statuses);
    }

    @Override
    public AppointmentDetail findAvailableByUserProfileIdAndStatuses(Long userProfileId, List<Integer> statuses) {
        Page<AppointmentDetail> details = repository.findAvailableByUserProfileIdAndStatuses(userProfileId, statuses, PageRequest.of(0, 1));
        return CollectionUtils.isNotEmpty(details.getContent()) ? details.getContent().get(0) : null;
    }

    @Override
    public AppointmentDetail findAvailableLatestEventByUserProfileIdAndCompanyIdAndStatuses(Long userProfileId, Long companyId, List<Integer> statuses) {
        Page<AppointmentDetail> details = repository.findAvailableLatestEventByUserProfileIdAndCompanyIdAndStatuses(userProfileId, companyId, statuses, PageRequest.of(0, 1));
        return CollectionUtils.isNotEmpty(details.getContent()) ? details.getContent().get(0) : null;
    }

    @Override
    public int countAvailableByUserProfileIdAndCompanyIdAndStatusesAndRoles(Long userProfileId, Long companyId, List<Integer> statuses, List<Long> roleIds) {
        return repository.countAvailableByUserProfileIdAndCompanyIdAndStatusesAndRoles(userProfileId, companyId, statuses, roleIds);
    }

    @Override
    public int countAvailableByUserProfileIdAndCompanyIdAndStatuses(Long userProfileId, Long companyId, List<Integer> statuses) {
        return repository.countAvailableByUserProfileIdAndCompanyIdAndStatuses(userProfileId, companyId, statuses);
    }

    @Override
    public int countAvailableByAppointmentIdAndStatuses(Long appointmentId, List<Integer> statuses) {
        return repository.countAvailableByAppointmentIdAndStatuses(appointmentId, statuses);
    }

    @Override
    public List<Long> findExpiredEventByUserAndCompany(Long userId, Long companyId) {
        return repository.findExpiredEventByUserAndCompany(userId, companyId).stream().map(BigDecimal::longValue).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentDetail> findByAppointmentIdAndUserCVIdAndStatus(Long appointmentId, List<Long> userCVIds, List<Integer> statuses) {
        return repository.findByAppointmentIdAndUserCurriculumVitaeIdAndStatus(appointmentId, userCVIds, statuses);
    }

    @Override
    public boolean checkExistsByIdAndUserProfileId(Long appointmentId, Long userProfileId) {
        int foundEvent = repository.checkExistsByIdAndUserProfileId(appointmentId, userProfileId);
        return foundEvent > 0;
    }

    @Override
    public List<AppointmentDetail> findAvailableByVacancyIdsAndStatuses(List<Long> vacancyIds, List<Integer> statuses, List<String> roles, long staffId) {
        if (CollectionUtils.isEmpty(roles)) {
            return repository.findAvailableByVacancyIdsAndStatusesNoRoles(vacancyIds, statuses, staffId);
        }
        return repository.findAvailableByVacancyIdsAndStatusesWithRoles(vacancyIds, statuses, roles, staffId);
    }

    @Override
    public Page<VacancyHasAppointment> findOpeningVacancyHavingAppointmentsByUserAndCompany(Long userProfileId, long companyId, List<String> largerRoles, int page, int size) {
        Page<VacancyHasAppointment> result = appointmentService.findHavingAppointmentsOpeningVacancyByUserIdAndCompanyId(userProfileId, companyId, largerRoles, page, size);

        if (CollectionUtils.isNotEmpty(result.getContent())) {
            List<Long> vacancyIds = result.getContent().stream().map(VacancyHasAppointment::getId).collect(Collectors.toList());
            List<AppointmentDetail> appointmentDetails;
            if (CollectionUtils.isNotEmpty(largerRoles)) {
                appointmentDetails = repository.findHavingActiveAppointmentDetailByUserIdAndCompanyIdWithRoles(
                        userProfileId, companyId, AppointmentStatus.getAcceptedStatus(), largerRoles, vacancyIds);
            } else {
                appointmentDetails = repository.findHavingActiveAppointmentDetailByUserIdAndCompanyIdNoRole(
                        userProfileId, companyId, AppointmentStatus.getAcceptedStatus(), vacancyIds);
            }

            result.getContent().forEach(vacancyHasAppointment -> {
                List<AppointmentDetail> appointedAppointment = appointmentDetails.stream().filter(
                        ap -> vacancyHasAppointment.getId() == ap.getAppointment().getVacancy().getId())
                        .collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(appointedAppointment)) {
                    vacancyHasAppointment.setAppointedCandidates(
                            appointedAppointment.stream().map(AppointmentDetail::getUserCurriculumVitae)
                                    .collect(Collectors.toList()));
                    vacancyHasAppointment.setVacancy(appointedAppointment.get(0).getAppointment().getVacancy());
                }
            });
        }
        return result;
    }

    @Override
    public List<AppointmentDetail> findPendingAndAcceptedAppointmentInSuspendRange(long vacancyId, Date startDate, Date endDate) {
        return repository.findPendingAndAcceptedAppointmentInSuspendRange(
                vacancyId, startDate, endDate, AppointmentStatus.PENDING.getValue(), AppointmentStatus.getAcceptedStatus());
    }

    public List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndExpired(long userCVId, List<Integer> statuses, long vacancyId) {
        return repository.findByUserCVIdAndStatusOfVacancyAndExpired(userCVId, statuses, vacancyId);
    }

    @Override
    public List<AppointmentDetail> findByUserCVIdAndStatusOfVacancyAndNotExpired(long userCVId, List<Integer> statuses, long vacancyId) {
        return repository.findByUserCVIdAndStatusOfVacancyAndNotExpired(userCVId, statuses, vacancyId);
    }

    @Override
    public List<AppointmentDetail> findByStatusOfVacancyAndNotExpired(List<Integer> statuses, Long vacancyId) {
        return repository.findByStatusOfVacancyAndNotExpired(statuses, vacancyId);
    }

    @Override
    public Page<Object[]> getVacancyCandidateExpiredEventsOfAppointmentForFeedBack(Long userId, Long companyId, List<String> decideLaterIds, boolean appointmentIdIsNull, Long appointmentId, int size) {
        return repository.getVacancyCandidateOfExpiredEventsForFeedback(
                userId,
                companyId,
                ofNullable(decideLaterIds).orElseGet(ImmutableList::of),
                appointmentIdIsNull ? 1 : 0,
                ofNullable(appointmentId).map(ImmutableList::of).orElseGet(() -> ImmutableList.of(-1L)),
                PageRequest.of(0, size == 0 ? Integer.MAX_VALUE : size, Sort.Direction.ASC, "APPOINTMENT_TIME"));
    }

    public List<AppointmentDetail> findAcceptedByUserCVIdOfVacancyAndExpiredAfter(long userCVId, long vacancyId, Date date) {
        return repository.findByUserCVIdAndStatusOfVacancyAndExpiredAfter(userCVId, AppointmentStatus.getAcceptedStatus(), vacancyId, date);
    }
}
