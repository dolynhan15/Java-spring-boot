package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessStaffService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.ActiveTime;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.utils.DataUtilsTest;
import com.qooco.boost.utils.ServletUriUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@PrepareForTest({ServletUriUtils.class})
@RunWith(PowerMockRunner.class)
public class BusinessStaffServiceImplTest extends BaseUserService {
    @InjectMocks
    private BusinessStaffService businessStaffService = new BusinessStaffServiceImpl();
    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);
    @Mock
    private StaffService staffService = Mockito.mock(StaffService.class);
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private RoleCompanyService roleCompanyService = Mockito.mock(RoleCompanyService.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private MessageDocService messageDocService = Mockito.mock(MessageDocService.class);
    @Mock
    private VacancyService vacancyService = Mockito.mock(VacancyService.class);
    @Mock
    private PushNotificationService pushNotificationService = Mockito.mock(PushNotificationService.class);
    @Mock
    private SendMessageToClientService sendMessageToClientService = Mockito.mock(SendMessageToClientService.class);
    @Mock
    private ConversationDocService conversationDocService = Mockito.mock(ConversationDocService.class);
    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
    @Mock
    private StaffWorkingService staffWorkingService = Mockito.mock(StaffWorkingService.class);
    @Rule

    @Test
    public void getContactPersons_whenPageLessZero_thenThrowInvalidPagination() {
        try {
            businessStaffService.getContactPersons(1L, 1L, -1, 0, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INVALID_PAGINATION.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getContactPersons_whenSizeLessZero_thenThrowInvalidPagination() {
        try {
            businessStaffService.getContactPersons(1L, 1L, 0, -1, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INVALID_PAGINATION.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getContactPersons_whenCompanyNotExist_thenThrowCompanyNotFound() {
        Mockito.when(businessValidatorService.checkExistsCompany(1L)).thenReturn(null);
        try {
            businessStaffService.getContactPersons(1L, 1L, 0, 0, initAuthentication());
        } catch (NoPermissionException ex) {
            Assert.assertEquals(ResponseStatus.NOT_STAFF_OF_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getContactPersons_whenUserNotStaffOfCompany_thenThrowNoPermission() {
        Company company = DataUtilsTest.initCompany();
        Mockito.when(companyService.findById(1L)).thenReturn(company);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(1L, 1L)).thenReturn(null);
        try {
            businessStaffService.getContactPersons(1L, 1L, 0, 0, initAuthentication());
        } catch (NoPermissionException ex) {
            Assert.assertEquals(ResponseStatus.NOT_STAFF_OF_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getContactPersons_whenUserIsAdminOfCompany_thenReturnListStaffOfCompanyForAdmin() {
        Company company = DataUtilsTest.initCompany();
        AuthenticatedUser user = initAuthenticatedUser();
        prepareMockito(company, user);

        BaseResp result = new BaseResp<>(ResponseStatus.SUCCESS);
        BaseResp actualResp = businessStaffService.getContactPersons(company.getCompanyId(), user.getId(), 0, 1, initAuthentication());
        Assert.assertEquals(result.getCode(), actualResp.getCode());
    }

    @Test
    public void getCompanyStaffs_whenStaffNotBelongCompany_thenThrowNoPermissionException() {
        Mockito.when(businessValidatorService.checkExistsCompany(1L)).thenReturn(null);
        Page<Staff> companyStaffs = initEmptyPaging();
        Mockito.when(staffService.findStaffOfCompany(1L, null, 1, 0)).thenReturn(companyStaffs);
        try {
            businessStaffService.getCompanyStaffs(initAuthentication(), 1L, false, 1, 0);
        } catch (NoPermissionException ex) {
            Assert.assertEquals(ResponseStatus.NOT_STAFF_OF_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    private Page<Staff> initEmptyPaging() {
        /*Page<Staff> companyStaffs = new Page<Staff>() {
            @Override
            public int getTotalPages() {
                return 0;
            }

            @Override
            public long getTotalElements() {
                return 0;
            }

            public <S> Page<S> map(Converter<? super Staff, ? extends S> converter) {
                return null;
            }

            public <U> Page<U> map(Function<? super Staff, ? extends U> function) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 0;
            }

            @Override
            public int getNumberOfElements() {
                return 0;
            }

            @Override
            public List<Staff> getContent() {
                return new ArrayList<>();
            }

            @Override
            public boolean hasContent() {
                return false;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public Iterator<Staff> iterator() {
                return new Iterator<Staff>() {
                    @Override
                    public boolean hasNext() {
                        return false;
                    }

                    @Override
                    public Staff next() {
                        return null;
                    }
                };
            }
        };
        return companyStaffs;*/
        return new PageImpl<>(ImmutableList.of());
    }

    @Test
    public void getCompanyStaffs_whenUserNotStaffOfCompany_thenThrowNoPermission() {
        Company company = DataUtilsTest.initCompany();
        Mockito.when(companyService.findById(1L)).thenReturn(company);
        Page<Staff> companyStaffs = initEmptyPaging();
        Mockito.when(staffService.findStaffOfCompany(1L, null, 0, 0)).thenReturn(companyStaffs);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(1L, 1L)).thenReturn(null);
        try {
            businessStaffService.getCompanyStaffs(initAuthentication(), 1L, false, 0, 0);
        } catch (NoPermissionException ex) {
            Assert.assertEquals(ResponseStatus.NOT_STAFF_OF_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getCompanyStaffs_whenUserIsStaffOfCompany_thenReturnListStaffOfCompany() {
        Company company = DataUtilsTest.initCompany();
        prepareMockito(company, initAuthenticatedUser());
        BaseResp result = new BaseResp<>(ResponseStatus.SUCCESS);

        BaseResp actualResp = businessStaffService.getCompanyStaffs(initAuthentication(), company.getCompanyId(), false, 0, 1);
        Assert.assertEquals(result.getCode(), actualResp.getCode());
    }

    private void prepareMockito(Company company, AuthenticatedUser user) {
        company.setCompanyId(1L);
        PowerMockito.mockStatic(ServletUriUtils.class);
        List<Staff> staffs = new ArrayList<>();
        staffs.add(new Staff(company, new UserFit(user.getId()), new RoleCompany(1L, CompanyRole.ADMIN.getName())));
        Mockito.when(ServletUriUtils.getRelativePath(Mockito.anyString())).thenReturn("image");
        Mockito.when(businessValidatorService.checkExistsCompany(company.getCompanyId())).thenReturn(company);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(user.getId(), company.getCompanyId())).thenReturn(staffs);
        Mockito.when(staffService.findByUserProfileAndCompany(company.getCompanyId(), user.getId())).thenReturn(staffs);

        Pageable pageable = PageRequest.of(0, 1);
        Page<Staff> pageStaff = new PageImpl<>(staffs, pageable, 1);
        Mockito.when(staffService.findCompanyStaffsByRoles(company.getCompanyId(), CompanyRole.ADMIN.getRolesEqualOrLessNoAnalyst(), 0, 1)).thenReturn(pageStaff);
        Mockito.when(staffService.findStaffOfCompany(company.getCompanyId(), null, 0, 1)).thenReturn(pageStaff);
    }

    @Test
    public void findStaffsByRoleCompany_whenUserProfileOrCompanyNotExist_thenReturnNoPermissionExceptionError() {
        thrown.expect(NoPermissionException.class);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(0L, 1)).thenReturn(null);
        businessStaffService.findStaffsByRoleCompany(1, 0L, 0, 1, initAuthentication());
    }

    @Test
    public void findStaffsByRoleCompany_whenIsAdmin_thenReturnAllStaffsOfCompanyExceptOwnerSuccess() {
        Long userProfileId = 1L;
        long companyId = 1L;
        List<Staff> staffs = new ArrayList<>();
        staffs.add(new Staff(new Company(companyId), new UserFit(userProfileId),
                new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        Page<Staff> staffPage = new PageImpl<>(staffs, PageRequest.of(0, 1), staffs.size());
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(staffs);
        Mockito.when(staffService.findStaffByCompanyExceptOwner(companyId, userProfileId, 0, 0)).thenReturn(staffPage);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.findStaffsByRoleCompany(companyId, userProfileId, 0, 0, initAuthentication()).getCode());
    }

    @Test
    public void findStaffsByRoleCompany_whenIsHeadRecruiter_thenReturnAllStaffsOfCompanyByRolesSuccess() {
        Long userProfileId = 1L;
        long companyId = 1L;
        List<String> roles = Arrays.asList(CompanyRole.HEAD_RECRUITER.name(), CompanyRole.RECRUITER.name(),
                CompanyRole.ANALYST.name(), CompanyRole.NORMAL_USER.name());
        List<Staff> staffs = new ArrayList<>();
        staffs.add(new Staff(new Company(companyId), new UserFit(userProfileId),
                new RoleCompany(CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.getName())));
        Page<Staff> staffPage = new PageImpl<>(staffs, PageRequest.of(0, 1), staffs.size());
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(staffs);
        Mockito.when(staffService.findCompanyStaffsByRolesExceptOwner(companyId, userProfileId, roles, 0, 0)).thenReturn(staffPage);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.findStaffsByRoleCompany(companyId, userProfileId, 0, 0, initAuthentication()).getCode());
    }

    @Test
    public void findStaffsByRoleCompany_whenIsRecruiter_thenReturnEmptySuccess() {
        Long userProfileId = 1L;
        long companyId = 1L;
        List<Staff> staffs = new ArrayList<>();
        staffs.add(new Staff(new Company(companyId), new UserFit(userProfileId),
                new RoleCompany(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.getName())));
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(staffs);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.findStaffsByRoleCompany(companyId, userProfileId, 0, 0, initAuthentication()).getCode());
    }

    @Test
    public void deleteStaff_whenStaffIdOrCompanyIdNotExist_thenReturnEntityNotFoundExceptionError() {
        thrown.expect(EntityNotFoundException.class);
        Long userProfileId = 1L;
        long staffId = 1;
        long companyId = 1;
        Mockito.when(staffService.findByStaffIdAndCompany(staffId, companyId)).thenReturn(null);
        businessStaffService.deleteStaff(userProfileId, companyId, staffId);
    }

    @Test
    public void deleteStaff_whenIsNotStaffOfCompany_thenReturnNoPermissionExceptionError() {
        thrown.expect(NoPermissionException.class);
        Long userProfileId = 1L;
        long staffId = 1;
        long companyId = 1;
        Staff staff = new Staff(new Company(companyId), new UserFit(userProfileId),
                new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName()));
        Mockito.when(staffService.findByStaffIdAndCompany(staffId, companyId)).thenReturn(staff);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(null);
        businessStaffService.deleteStaff(userProfileId, companyId, staffId);
    }

    @Test
    public void deleteStaff_whenIsAdminOfCompany_thenReturnDeleteSuccess() {
        Long userProfileId = 1L;
        long staffId = 1;
        long companyId = 1;
        mockito(userProfileId, staffId, companyId);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.deleteStaff(userProfileId, companyId, staffId).getCode());
    }

    @Test
    public void deleteStaff_whenIsHeadRecruiterOfCompany_thenReturnNoPermissionExceptionError() {
        thrown.expect(NoPermissionException.class);
        Long userProfileId = 1L;
        long staffId = 1;
        long companyId = 1;
        mockito(userProfileId, staffId, companyId);
        Mockito.when(staffService.findByUserProfileAndCompanyApprovalAndRoles(userProfileId, companyId, Lists.newArrayList(CompanyRole.ADMIN.getName()))).thenReturn(null);
        businessStaffService.deleteStaff(userProfileId, companyId, staffId);
    }

    @Test
    public void deleteStaff_whenIsHeadRecruiterOfCompany_thenReturnDeleteSuccess() {
        Long userProfileId = 1L;
        long staffId = 1;
        long companyId = 1;
        mockito(userProfileId, staffId, companyId);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.deleteStaff(userProfileId, companyId, staffId).getCode());
    }

    private void mockito(Long userProfileId, long staffId, long companyId) {
        Staff staff = new Staff(new Company(companyId), new UserFit(userProfileId),
                new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName()));
        List<Staff> staffs = new ArrayList<>();
        staffs.add(staff);
        Mockito.when(staffService.findByStaffIdAndCompany(staffId, companyId)).thenReturn(staff);
        Mockito.when(staffService.findByUserProfileAndCompanyApprovalAndRoles(userProfileId, companyId, Lists.newArrayList(CompanyRole.ADMIN.getName()))).thenReturn(staffs);

    }

    @Test
    public void deleteStaff_whenIsRecruiterOfCompany_thenReturnNoPermissionExceptionError() {
        thrown.expect(NoPermissionException.class);
        Long userProfileId = 1L;
        long staffId = 1;
        long companyId = 1;
        Staff staff = new Staff(new Company(companyId), new UserFit(userProfileId),
                new RoleCompany(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.getName()));
        List<Staff> staffs = new ArrayList<>();
        staffs.add(staff);
        Mockito.when(staffService.findByStaffIdAndCompany(staffId, companyId)).thenReturn(staff);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(staffs);
        businessStaffService.deleteStaff(userProfileId, companyId, staffId);
    }

    @Test
    public void getCompanyRolesByAuthorization_whenNotFoundStaffByUserIdAndCompanyId_thenReturnErrorException() {
        thrown.expect(NoPermissionException.class);
        Long companyId = 1L;
        Long userProfileId = 1L;
        mockitoGetCompanyRolesByAuthorization();
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(Lists.newArrayList());
        businessStaffService.getCompanyRolesByUser(companyId, userProfileId, initAuthentication());
    }

    @Test
    public void getCompanyRolesByAuthorization_whenFoundStaffIsAdmin_thenReturnSuccess() {
        Long companyId = 1L;
        Long userProfileId = 1L;
        mockitoGetCompanyRolesByAuthorization();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.getCompanyRolesByUser(companyId, userProfileId, initAuthentication()).getCode());
    }

    @Test
    public void getCompanyRolesByAuthorization_whenFoundStaffIsRecruiter_thenReturnSuccess() {
        Long companyId = 1L;
        Long userProfileId = 1L;
        mockitoGetCompanyRolesByAuthorization();
        RoleCompany headRecruiterRole = new RoleCompany(CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.getName());
        List<Staff> foundStaffs = Lists.newArrayList(new Staff(new Company(companyId), new UserFit(userProfileId), headRecruiterRole));
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(foundStaffs);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.getCompanyRolesByUser(companyId, userProfileId, initAuthentication()).getCode());
    }

    @Test
    public void saveStaffWorking_whenEmptyActiveTimeList_thenThrowInvalidDateRangeException() {
        Company company = DataUtilsTest.initCompany();
        AuthenticatedUser user = initAuthenticatedUser();
        prepareMockito(company, user);
        List<ActiveTime> emptyRequest = new ArrayList<>();
        try {
            businessStaffService.saveStaffWorking(initAuthentication(), emptyRequest).getCode();
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INVALID_TIME_RANGE.getCode(), ex.getStatus().getCode());
        }
        try {
            businessStaffService.saveStaffWorking(initAuthentication(), null).getCode();
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INVALID_TIME_RANGE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveStaffWorking_whenStaffNotBelongToCompany_thenThrowNotFound() {
        Company company = DataUtilsTest.initCompany();
        AuthenticatedUser user = initAuthenticatedUser();
        prepareMockito(company, user);
        List<ActiveTime> request = new ArrayList<>();
        ActiveTime activeTime = new ActiveTime();
        activeTime.setStartDate(12);
        activeTime.setEndDate(12313);
        request.add(activeTime);
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(businessValidatorService.checkExistsStaffInCompany(user.getCompanyId(), user.getId(), ResponseStatus.NOT_STAFF_OF_COMPANY)).thenThrow(new EntityNotFoundException(ResponseStatus.NOT_STAFF_OF_COMPANY));
        Assert.assertEquals(ResponseStatus.NOT_STAFF_OF_COMPANY.getCode(), businessStaffService.saveStaffWorking(initAuthentication(), request).getCode());
    }

    @Test
    public void saveStaffWorking_whenStaffCompany_thenReturnSuccess() {
        Company company = DataUtilsTest.initCompany();
        AuthenticatedUser user = initAuthenticatedUser();
        prepareMockito(company, user);
        List<ActiveTime> request = new ArrayList<>();
        ActiveTime activeTime = new ActiveTime();
        activeTime.setStartDate(12);
        activeTime.setEndDate(12313);
        request.add(activeTime);
        Staff staff = new Staff(1l, company, new UserFit(user.getId()), new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName()));
        Mockito.when(businessValidatorService.checkExistsStaffInCompany(user.getCompanyId(), user.getId(), ResponseStatus.NOT_STAFF_OF_COMPANY)).thenReturn(staff);
        List<StaffWorking> staffWorkings = new ArrayList<>();
        request.forEach(a -> staffWorkings.add(new StaffWorking(staff, a.getStartDate(), a.getEndDate())));
        Mockito.when(staffWorkingService.save(staffWorkings)).thenReturn(staffWorkings);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessStaffService.saveStaffWorking(initAuthentication(), request).getCode());
    }

    /*===================================== Prepare data before test ==============================================*/


    private void mockitoGetCompanyRolesByAuthorization() {
        Long userProfileId = 1L;
        Long companyId = 1L;
        RoleCompany adminRole = new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName());
        RoleCompany recruiterRole = new RoleCompany(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.getName());
        List<Staff> foundStaffs = Lists.newArrayList(new Staff(new Company(companyId), new UserFit(userProfileId), adminRole));
        List<RoleCompany> roleCompaniesAdmin = Lists.newArrayList(adminRole);
        List<RoleCompany> roleCompaniesNotAdmin = Lists.newArrayList(recruiterRole);
        List<String> roles = Lists.newArrayList(CompanyRole.RECRUITER.name(), CompanyRole.ANALYST.name(), CompanyRole.NORMAL_USER.name());

        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, companyId)).thenReturn(foundStaffs);
        Mockito.when(roleCompanyService.findAll()).thenReturn(roleCompaniesAdmin);
        Mockito.when(roleCompanyService.findByAuthorization(roles)).thenReturn(roleCompaniesNotAdmin);

    }
}
