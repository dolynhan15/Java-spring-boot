package com.qooco.boost.data.oracle.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.model.StaffStatistic;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.reposistories.StaffRepository;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.utils.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepository repository;

    @Override
    public int countPendingCompanyOfAdmin(long adminId) {
        return repository.countPendingCompanyOfAdmin(adminId);
    }

    @Override
    public Staff save(Staff staff) {
        return repository.save(staff);
    }

    @Override
    public Staff findById(Long id) {
        return repository.findByIdAnCompanyApproved(id);
    }

    @Override
    public Staff findByIdNotCheckDeleted(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Staff> findById(List<Long> ids) {
        return repository.findById(ids);
    }

    @Override
    public List<Staff> findByIdsAndCompanyIdAndRoleNames(Long companyId, List<Long> ids, List<String> roleNames) {
        return repository.findStaffByIdsAndCompanyIdAndRoles(companyId, ids, roleNames);
    }

    @Override
    public Staff findByPendingCompanyAndAdmin(long companyId, long adminId) {
        return repository.findByPendingCompanyAndAdmin(companyId, adminId);
    }

    @Override
    public List<Staff> findStaffOfCompanyByRole(long companyId, long roleId) {
        return repository.findStaffOfCompanyByRole(companyId, roleId);
    }

    @Override
    public List<Staff> findByUserProfileAndStatus(long userProfileId, ApprovalStatus companyStatus) {
        return repository.findByCompanyStatusAndUserProfile(userProfileId, companyStatus);
    }

    @Override
    public Page<Staff> findStaffOfCompany(long companyId, int page, int size) {
        return findStaffOfCompany(companyId, null, page, size);
    }

    @Override
    public Page<Staff> findStaffOfCompany(long companyId, List<Long> ignoreUserIds, int page, int size) {
        if (page < 0 || size <= 0) {
            return repository.findByCompany(companyId, ignoreUserIds, PageRequest.of(0, Integer.MAX_VALUE));
        }
        return repository.findByCompany(companyId, ignoreUserIds, PageRequest.of(page, size));
    }

    @Override
    public Page<Staff> findStaffByCompanyExceptOwner(long companyId, long userProfileId, int page, int size) {
        if (page < 0 || size <= 0) {
            return repository.findByCompany(companyId, userProfileId, PageRequest.of(0, Integer.MAX_VALUE));
        }
        return repository.findByCompany(companyId, userProfileId, PageRequest.of(page, size));
    }

    @Override
    public List<Staff> findByUserProfileAndCompanyApproval(long userProfileId, long companyId) {
        return repository.findByUserProfileAndCompanyApproved(userProfileId, companyId);
    }

    @Override
    public List<Staff> findByUserProfileAndCompany(Long companyId, Long userProfileId) {
        if (Objects.nonNull(companyId) && Objects.nonNull(userProfileId)) {
            return repository.findByUserProfileAndCompany(userProfileId, companyId);
        }
        return Lists.newArrayList();
    }

    @Override
    public List<Staff> findByUserProfileAndCompany(Long companyId, List<Long> userProfileIds) {
        if (Objects.nonNull(companyId) && CollectionUtils.isNotEmpty(userProfileIds)) {
            return repository.findByCompanyAndUserProfileIds(companyId, userProfileIds);
        }
        return Lists.newArrayList();
    }

    @Override
    public Boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Override
    public List<Staff> findByCompanyApprovalAndAdmin(long companyId, long adminId) {
        return repository.findByCompanyApprovalAndAdmin(companyId, adminId);
    }

    @Override
    public List<Staff> findByRole(long roleId) {
        return repository.findByRole(roleId);
    }

    @Override
    public List<Staff> findByCompanyAndAdmin(long companyId, long adminId) {
        return repository.findByCompanyAndAdmin(companyId, adminId);
    }

    @Override
    public Boolean checkStaffRoles(long companyId, Long userProfileId, List<String> roles) {
        int result = repository.countByCompanyAndUserProfileAndRoles(companyId, userProfileId, roles);
        return result > 0;
    }

    @Override
    public Page<Staff> findCompanyStaffsByRolesExceptOwner(long companyId, long userProfileId, List<String> roles, int page, int size) {
        if (page < 0 || size <= 0) {
            return repository.findStaffListByRoles(companyId, userProfileId, roles, PageRequest.of(0, Integer.MAX_VALUE));
        }
        return repository.findStaffListByRoles(companyId, userProfileId, roles, PageRequest.of(page, size));
    }

    @Override
    public Page<Staff> findCompanyStaffsByRoles(long companyId, List<String> roles, int page, int size) {
        if (page < 0 || size <= 0) {
            return repository.findStaffListByRoles(companyId, roles, PageRequest.of(0, Integer.MAX_VALUE));
        }
        return repository.findStaffListByRoles(companyId, roles, PageRequest.of(page, size));
    }

    @Override
    public Staff findByStaffIdAndCompany(long staffId, long companyId) {
        return repository.findByStaffIdAndCompany(staffId, companyId);
    }

    @Override
    public int countByUserProfileAndCompany(long userProfileId, long companyId) {
        return repository.countByUserProfileAndCompany(userProfileId, companyId);
    }

    @Override
    public List<Staff> findByUserProfileAndCompanyApprovalAndRoles(Long userProfileId, Long companyId, List<String> roles) {
        return repository.findByUserProfileAndCompanyApprovalIdAndRoles(userProfileId, companyId, roles);
    }

    @Override
    public List<Staff> findByCompanyIds(List<Long> companyIds) {
        return repository.findByCompanyIds(companyIds);
    }

    @Override
    public int countByCompany(Long companyId) {
        return repository.countByCompany(companyId);
    }

    @Override
    public Page<StaffStatistic> findStaffStatisticByCompany(long companyId, long startDate, long beginningOfEndDate, long endDate, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Date startDateOracle = DateUtils.toUtcForOracle(new Date(startDate));
        Date endDateOracle = DateUtils.toUtcForOracle(new Date(endDate));
        Page<Object[]> pageResult = repository.findStaffClosedSeatByCompany(
                companyId, startDateOracle,
                endDateOracle, pageRequest);
        Page<StaffStatistic> result = pageResult.map(it -> new StaffStatistic(((BigDecimal) it[0]).longValue(), (String) it[1], (String) it[2],
                (String) it[3], (String) it[4], (String) it[5], (String) it[6], Objects.isNull(it[7]) ? null : ((BigDecimal) it[7]).longValue(),
                (String) it[8], (String) it[9], (String) it[10], ((BigDecimal) it[11]).intValue(),
                0, 0, 0, 0));
        if (CollectionUtils.isNotEmpty(pageResult.getContent())) {
            List<Long> staffIds = pageResult.getContent().stream().map(it -> ((BigDecimal) it[0]).longValue()).collect(Collectors.toList());
            List<Object[]> openSeats = repository.findStaffOpenSeatByStaffs(
                    endDateOracle, DateUtils.toUtcForOracle(new Date(beginningOfEndDate)), staffIds);
            List<Object[]> candidateProcessing = repository.findStaffCandidateProcessingByStaffs(startDateOracle,
                    endDateOracle, staffIds);
            List<Object[]> appointmentFeedback = repository.findStaffAppointmentFeedbackByStaffs(startDateOracle,
                    endDateOracle, staffIds);
            List<Object[]> activeTimes = repository.findStaffActiveMinsByStaffs(startDateOracle,
                    endDateOracle, staffIds);
            result.getContent().forEach(staffStatistic -> {
                Optional<Object[]> foundOpenSeat = openSeats.stream().filter(
                        it -> staffStatistic.getId() == ((BigDecimal) it[0]).longValue()).findFirst();
                foundOpenSeat.ifPresent(it -> staffStatistic.setOpenSeats(((BigDecimal) it[1]).intValue()));

                Optional<Object[]> foundCandidateProcess = candidateProcessing.stream().filter(
                        it -> staffStatistic.getId() == ((BigDecimal) it[0]).longValue()).findFirst();
                foundCandidateProcess.ifPresent(it -> staffStatistic.setCandidateProcessing(((BigDecimal) it[1]).intValue()));

                Optional<Object[]> foundAppointment = appointmentFeedback.stream().filter(
                        it -> staffStatistic.getId() == ((BigDecimal) it[0]).longValue()).findFirst();
                foundAppointment.ifPresent(it -> staffStatistic.setAppointments(((BigDecimal) it[1]).intValue()));

                Optional<Object[]> foundActiveTimes = activeTimes.stream().filter(
                        it -> staffStatistic.getId() == ((BigDecimal) it[0]).longValue()).findFirst();
                foundActiveTimes.ifPresent(it -> staffStatistic.setActiveTimes(((BigDecimal) it[1]).intValue()));
            });
        }
        return result;
    }
}
