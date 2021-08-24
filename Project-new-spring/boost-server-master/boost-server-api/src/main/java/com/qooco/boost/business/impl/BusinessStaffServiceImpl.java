package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessAppointmentService;
import com.qooco.boost.business.BusinessStaffService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.services.CompanyDocService;
import com.qooco.boost.data.oracle.entities.RoleCompany;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.StaffWorking;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.services.RoleCompanyService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.oracle.services.StaffWorkingService;
import com.qooco.boost.data.oracle.services.VacancyService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.RoleCompanyDTO;
import com.qooco.boost.models.dto.staff.ContactPersonDTO;
import com.qooco.boost.models.dto.staff.StaffDTO;
import com.qooco.boost.models.request.ActiveTime;
import com.qooco.boost.models.request.RoleAssignedReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.models.SetRoleInMongo;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BusinessStaffServiceImpl implements BusinessStaffService {

    @Autowired
    private StaffService staffService;
    @Autowired
    private StaffWorkingService staffWorkingService;
    @Autowired
    private RoleCompanyService roleCompanyService;
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private CompanyDocService companyDocService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private BusinessAppointmentService businessAppointmentService;

    @Override
    public BaseResp getContactPersons(long companyId, long userProfileId, int page, int size, Authentication authentication) {
        validatePage(page, size);
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId);
        if (CollectionUtils.isEmpty(staffs)) {
            throw new NoPermissionException(ResponseStatus.NOT_STAFF_OF_COMPANY);
        }

        try {
            CompanyRole staffRole = CompanyRole.valueOf(staffs.get(0).getRole().getName());
            List<String> roles = staffRole.getRolesEqualOrLessNoAnalyst();
            Page<Staff> companyStaffs = staffService.findCompanyStaffsByRoles(companyId, roles, page, size);
            List<ContactPersonDTO> contactPersons = new ArrayList<>();
            companyStaffs.getContent().forEach(s -> contactPersons.add(new ContactPersonDTO(s, getLocale(authentication))));

            PagedResult<ContactPersonDTO> result = new PagedResult<>(contactPersons, page, companyStaffs.getSize(),
                    companyStaffs.getTotalPages(), companyStaffs.getTotalElements(),
                    companyStaffs.hasNext(), companyStaffs.hasPrevious());
            return new BaseResp<>(result);
        } catch (IllegalArgumentException ex) {
            throw new InvalidParamException(ResponseStatus.NOT_FOUND_ROLE);
        }
    }

    @Override
    public BaseResp setRoleStaffOfCompany(RoleAssignedReq req, Authentication authentication) {
        AuthenticatedUser updateOwner = ((AuthenticatedUser) authentication.getPrincipal());
        Long companyId = updateOwner.getCompanyId();
        Long staffId = req.getStaffId();
        Long assigneeId = req.getAssigneeId();
        Staff staff = businessValidatorService.checkExistsStaff(staffId);
        Staff assignee;
        if (Objects.nonNull(assigneeId)) {
            assignee = businessValidatorService.checkExistsStaff(assigneeId);
        } else {
            assignee = businessValidatorService.checkExistsStaffInApprovedCompany(staff.getCompany().getCompanyId(), updateOwner.getId());
        }

        if (Objects.isNull(companyId)
                || !companyId.equals(staff.getCompany().getCompanyId())
                || !companyId.equals(assignee.getCompany().getCompanyId())) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }

        doSetRoleAndAssign(staff, req.getRole(), assignee, authentication);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private void doSetRoleAndAssign(Staff staff, CompanyRole newRole, Staff assignee, Authentication authentication) {
        AuthenticatedUser updateOwner = ((AuthenticatedUser) authentication.getPrincipal());
        RoleCompany roleCompany = null;
        if (Objects.nonNull(newRole)) {
            roleCompany = roleCompanyService.findByName(newRole.name());
        }

        if (Objects.isNull(roleCompany)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_ROLE);
        }

        CompanyRole oldRole = CompanyRole.valueOf(Objects.nonNull(staff.getRole()) ? staff.getRole().getName() : CompanyRole.NORMAL_USER.getName());

        staff.setRole(new RoleCompany(newRole.getCode(), newRole.getName()));
        staff.setUpdatedBy(updateOwner.getId());
        staff.setUpdatedDate(DateUtils.nowUtcForOracle());
        staffService.save(staff);
        StaffShortEmbedded staffShortEmbedded = MongoConverters.convertToStaffShortEmbedded(staff);
        boostActorManager.updateStaffToCompanyInMongo(staffShortEmbedded);

        assignAllTaskToOtherRecruiter(staff, oldRole.getName(), newRole.getName(), assignee, authentication);
        SetRoleInMongo roleInMongo = new SetRoleInMongo(staff, oldRole, newRole);
        boostActorManager.setRoleInMessage(roleInMongo);
    }

    private void assignAllTaskToOtherRecruiter(Staff oldStaff, String oldRole, String newRole, Staff targetStaff, Authentication authentication) {
        if ((CompanyRole.ADMIN.getName().equals(oldRole)
                || CompanyRole.HEAD_RECRUITER.getName().equals(oldRole)
                || CompanyRole.RECRUITER.getName().equals(oldRole))
                && (CompanyRole.ANALYST.getName().equals(newRole)
                || CompanyRole.NORMAL_USER.getName().equals(newRole))) {

            List<Vacancy> vacancies = vacancyService.findAllByContactPersonAndCompany(oldStaff.getStaffId(), oldStaff.getCompany().getCompanyId());
            if (CollectionUtils.isNotEmpty(vacancies)) {
                vacancies.forEach(v -> v.setContactPerson(targetStaff));
                vacancyService.save(vacancies);
                boostActorManager.sendApplicantMessageWithNewContactPersonActor(oldStaff, targetStaff, vacancies);
            }

            businessAppointmentService.assignAppointmentToNewManagerAfterChangeRole(oldStaff, targetStaff, authentication);
        }
    }

    @Override
    public BaseResp findStaffsByRoleCompany(long companyId, Long userProfileId, int page, int size, Authentication authentication) {
        //TODO: rewrite below code => reduce get roleName
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId);
        if (CollectionUtils.isEmpty(staffs)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        String roleName = staffs.get(0).getRole().getName();
        if (roleName.equals(CompanyRole.ADMIN.name())) {
            Page<Staff> staffPage = staffService.findStaffByCompanyExceptOwner(companyId, userProfileId, page, size);
            return convertToStaffPages(page, staffPage, getLocale(authentication));
        }
        if (roleName.equals(CompanyRole.HEAD_RECRUITER.name())) {
            List<String> roles = Arrays.asList(CompanyRole.HEAD_RECRUITER.name(),
                    CompanyRole.RECRUITER.name(), CompanyRole.ANALYST.name(), CompanyRole.NORMAL_USER.name());
            Page<Staff> staffPage = staffService.findCompanyStaffsByRolesExceptOwner(companyId, userProfileId, roles, page, size);
            return convertToStaffPages(page, staffPage, getLocale(authentication));
        }
        return new BaseResp();
    }

    @Override
    public BaseResp getCompanyStaffs(Authentication authentication, Long companyId, boolean isExcludedMe, int page, int size) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        PagedResultV2 result = getCompanyStaff(companyId, user.getId(), isExcludedMe, page, size, getLocale(authentication));
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getCompanyStaffs(Authentication authentication, boolean isExcludedMe, int page, int size) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        PagedResultV2 result = null;
        if (Objects.nonNull(user.getCompanyId())) {
            result = getCompanyStaff(user.getCompanyId(), user.getId(), isExcludedMe, page, size, user.getLocale());
        }
        return new BaseResp<>(result);
    }

    private PagedResultV2 getCompanyStaff(Long companyId, Long userId, boolean isExcludeMe, int page, int size, String locale) {
        validatePage(page, size);
        businessValidatorService.checkExistsStaffInCompany(companyId, userId, ResponseStatus.NOT_STAFF_OF_COMPANY);

        List<Long> ignoreUserIds = null;
        if (isExcludeMe) {
            ignoreUserIds = Lists.newArrayList(userId);
        }
        Page<Staff> companyStaffs = staffService.findStaffOfCompany(companyId, ignoreUserIds, page, size);
        PagedResultV2 result = new PagedResultV2<>(new ArrayList<>());
        List<ContactPersonDTO> contactPersons = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(companyStaffs.getContent())) {
            for (Staff staff : companyStaffs.getContent()) {
                contactPersons.add(new ContactPersonDTO(staff, locale));
            }
            result = new PagedResultV2<>(contactPersons, page, companyStaffs);
        }
        return result;
    }

    private BaseResp convertToStaffPages(int page, Page<Staff> companyStaffs, String locale) {
        List<StaffDTO> staffDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(companyStaffs.getContent())) {
            for (Staff staff : companyStaffs.getContent()) {
                staffDTOS.add(new StaffDTO(staff, locale));
            }
        }
        PagedResult<StaffDTO> result = new PagedResult<>(staffDTOS, page, companyStaffs.getSize(),
                companyStaffs.getTotalPages(), companyStaffs.getTotalElements(),
                companyStaffs.hasNext(), companyStaffs.hasPrevious());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp deleteStaff(Authentication authentication, long staffId) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        long userId = user.getId();
        long companyId = user.getCompanyId();
        deleteStaff(userId, companyId, staffId);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp saveStaffWorking(Authentication authentication, List<ActiveTime> request) {
        Optional.ofNullable(request).filter(it -> !it.isEmpty())
                .filter(it -> it.stream().allMatch(activeTime -> activeTime.getEndDate() > activeTime.getStartDate()))
                .orElseThrow(() -> new InvalidParamException(ResponseStatus.INVALID_TIME_RANGE));
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Staff staff = businessValidatorService.checkExistsStaffInCompany(user.getCompanyId(), user.getId(), ResponseStatus.NOT_STAFF_OF_COMPANY);
        staffWorkingService.save(request.stream().map(
                activeTime -> new StaffWorking(staff, activeTime.getStartDate(), activeTime.getEndDate()))::iterator);
        return new BaseResp();
    }

    @Override
    public BaseResp deleteStaff(Long userProfileId, long companyId, long staffId) {
        Staff foundStaffToDelete = staffService.findByStaffIdAndCompany(staffId, companyId);
        if (Objects.isNull(foundStaffToDelete)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_STAFF);
        }

        List<String> roleAdmin = Lists.newArrayList(CompanyRole.ADMIN.name());
        List<Staff> staffs = staffService.findByUserProfileAndCompanyApprovalAndRoles(userProfileId, companyId, roleAdmin);
        if (CollectionUtils.isEmpty(staffs)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_DELETE);
        }
        softDeleteStaff(foundStaffToDelete);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private Staff softDeleteStaff(Staff foundStaffToDelete) {
        foundStaffToDelete.setIsDeleted(true);
        foundStaffToDelete.setUpdatedDate(DateUtils.nowUtcForOracle());
        Staff staff = staffService.save(foundStaffToDelete);
        if (Objects.nonNull(foundStaffToDelete.getStaffId())) {
            companyDocService.removeStaffInCompany(foundStaffToDelete.getCompany().getCompanyId(), staff.getStaffId());
        }
        boostActorManager.hideMessageWhenDeleteStaffActor(staff);
        return staff;
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }
    }

    @Override
    public BaseResp getCompanyRolesByUser(long companyId, Long userProfileId, Authentication authentication) {
        List<Staff> foundStaffs = staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId);
        if (CollectionUtils.isEmpty(foundStaffs)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        List<RoleCompany> roleCompanies = Lists.newArrayList();
        List<RoleCompanyDTO> roleListResp;
        String roleName = foundStaffs.get(0).getRole().getName();
        if (roleName.equals(CompanyRole.ADMIN.name())) {
            roleCompanies = roleCompanyService.findAll();
        } else if (roleName.equals(CompanyRole.HEAD_RECRUITER.name())) {
            List<String> roles = Lists.newArrayList(CompanyRole.RECRUITER.name(), CompanyRole.ANALYST.name(), CompanyRole.NORMAL_USER.name());
            roleCompanies = roleCompanyService.findByAuthorization(roles);
        }
        roleListResp = roleCompanies.stream().map(it -> new RoleCompanyDTO(it, getLocale(authentication))).collect(Collectors.toList());
        return new BaseResp<>(roleListResp);
    }
}
