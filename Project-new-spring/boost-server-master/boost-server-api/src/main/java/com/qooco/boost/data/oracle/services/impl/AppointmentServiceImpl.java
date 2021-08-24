package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.model.VacancyHasAppointment;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.reposistories.AppointmentRepository;
import com.qooco.boost.data.oracle.services.AppointmentService;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository repository;

    @Override
    public Appointment findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Appointment findValidById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public List<Appointment> findById(Long vacancyId, Long managerId, Long locationId, Date appointmentDate) {
        return repository.findByVacancyIdAndManagerIdAndLocationIdAndAppointmentDate(vacancyId, managerId, locationId, appointmentDate);
    }

    @Override
    public Appointment save(Appointment appointment) {
        return repository.save(appointment);
    }

    @Override
    public List<Appointment> save(List<Appointment> appointments) {
        return Lists.newArrayList(repository.saveAll(appointments));
    }

    @Override
    public int updateDeleteStatus(Long id, boolean isDelete, Long updatedBy, Date updatedDate) {
        return repository.updateDeleteStatus(id, isDelete, updatedBy, updatedDate);
    }

    @Override
    public int updateDeleteStatus(List<Long> ids, boolean isDelete, Long updatedBy, Date updatedDate) {
        return repository.updateDeleteStatus(ids, isDelete, updatedBy, updatedDate);
    }

    @Override
    public List<Appointment> findNotExpiredByCompanyIdAndUserProfileId(Long companyId, Long userProfileId) {
        return repository.findNotExpiredByCompanyIdAndUserProfileId(companyId, userProfileId);
    }

    @Override
    public List<Appointment> findNotExpiredByCompanyId(Long companyId) {
        return repository.findNotExpiredByCompanyId(companyId);
    }

    @Override
    public List<Appointment> findNotExpiredByVacancyIdAndUserProfileId(Long vacancyId, Long userProfileId) {
        return repository.findNotExpiredByVacancyIdAndUserProfileId(vacancyId, userProfileId);
    }

    @Override
    public List<Appointment> findByVacancyId(Long vacancyId) {
        return repository.findByVacancyId(vacancyId);
    }

    @Override
    public List<Appointment> findByOpeningVacancyId(Long vacancyId) {
        return repository.findByOpeningVacancyId(vacancyId);
    }

    @Override
    public List<Appointment> findByVacancyIdAndUserProfileId(Long vacancyId, Long userProfileId) {
        return repository.findByVacancyIdAndUserProfileId(vacancyId, userProfileId);
    }

    @Override
    public List<Appointment> findByCompanyIdAndUserProfileId(Long companyId, Long userProfileId) {
        return repository.findByCompanyIdAndUserProfileId(companyId, userProfileId);
    }

    @Override
    public List<Appointment> findByCompanyId(Long companyId) {
        return repository.findByCompanyId(companyId);
    }

    @Override
    public List<Long> findByStaff(Long staffId) {
        return repository.findByStaff(staffId);
    }

    @Override
    public List<Appointment> findAvailableByIdsAndAppointmentDetailStatus(List<Long> ids, List<Integer> appointmentDetailStatus) {
        if (CollectionUtils.isNotEmpty(ids) && CollectionUtils.isNotEmpty(appointmentDetailStatus)) {
            return repository.findAvailableByIdsAndAppointmentDetailStatus(ids, appointmentDetailStatus);
        }
        return new ArrayList<>();
    }

    @Override
    public Page<Appointment> findCurrentByVacancyAndUserAndStatuses(Long vacancyId, Long userProfileId, List<Integer> statuses, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "FROM_DATE"));
        return repository.findCurrentByVacancyIdAndUserProfileIdAndStatuses(vacancyId, userProfileId, statuses, PageRequest.of(page, size, sort));
    }

    @Override
    public Page<Appointment> findCurrentByVacancyAndUserAndStatusesAndRoles(Long vacancyId, Long userProfileId, List<Integer> statuses, List<Long> roleIds, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "FROM_DATE"));
        return repository.findCurrentByVacancyIdAndUserProfileIdAndStatusesAndRoles(vacancyId, userProfileId, statuses, roleIds, PageRequest.of(page, size, sort));
    }

    @Override
    public Page<Appointment> findPastByVacancyAndUserAndStatuses(Long vacancyId, Long userProfileId, List<Integer> statuses, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "FROM_DATE"));
        return repository.findPastByVacancyIdAndUserProfileIdAndStatuses(vacancyId, userProfileId, statuses, PageRequest.of(page, size, sort));
    }

    @Override
    public Page<Appointment> findPastByVacancyAndUserAndStatusesAndRoles(Long vacancyId, Long userProfileId, List<Integer> statuses, List<Long> roleIds, int page, int size) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "FROM_DATE"));
        return repository.findPastByVacancyIdAndUserProfileIdAndStatusesAndRoles(vacancyId, userProfileId, statuses, roleIds, PageRequest.of(page, size, sort));
    }

    @Override
    public Page<VacancyHasAppointment> findHavingAppointmentsOpeningVacancyByUserIdAndCompanyId(Long userProfileId, long companyId, List<String> largerRoles, int page, int size) {

        if (CollectionUtils.isNotEmpty(largerRoles)) {
            return repository.findHavingAppointmentsByUserIdAndCompanyIdWithRoles(userProfileId, companyId, largerRoles, PageRequest.of(page, size));
        }
        return repository.findHavingAppointmentsByUserIdAndCompanyIdNoRoles(userProfileId, companyId, PageRequest.of(page, size));

    }

    @Override
    public List<Appointment> findAppointmentInSuspendedRangeOfVacancy(Vacancy vacancy) {
        if (Const.Vacancy.Status.SUSPEND == vacancy.getVacancyStatus()) {
            Date endDate;
            Date startDate = DateUtils.toUtcForOracle(vacancy.getStartSuspendDate());
            if (vacancy.getStatus() == Const.Vacancy.Status.PERMANENT_SUSPEND) {
                endDate = DateUtils.MAX_DATE;
            } else {
                endDate = DateUtils.addDays(startDate, vacancy.getSuspendDays());
            }
            return repository.findAppointmentInSuspendedRangeOfVacancy(vacancy.getId(),
                    DateUtils.toUtcForOracle(vacancy.getStartSuspendDate()), endDate);
        }
        return Lists.newArrayList();
    }

    @Override
    public int countAppointmentInVacancy(long vacancyId, long managerId) {
        return repository.countAppointmentInVacancy(vacancyId, managerId);
    }

    @Override
    public List<Appointment> findNotExpiredByVacancyId(Long vacancyId) {
        return repository.findNotExpiredByVacancyId(vacancyId);
    }

    @Override
    public long countValid(List<Long> ids) {
        return repository.countByIds(ids);
    }
}
