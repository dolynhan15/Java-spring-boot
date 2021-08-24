package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.mongo.embedded.RoleCompanyEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileEmbedded;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.utils.DataUtilsTest;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletUriUtils.class})
public class BusinessValidatorServiceImplTest {
    @InjectMocks
    private BusinessValidatorService businessValidatorService = new BusinessValidatorServiceImpl();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private UserCurriculumVitaeService userCurriculumVitaeService = Mockito.mock(UserCurriculumVitaeService.class);
    @Mock
    private UserCvDocService userCvDocService = Mockito.mock(UserCvDocService.class);
    @Mock
    private CurrencyService currencyService = Mockito.mock(CurrencyService.class);
    @Mock
    private EducationService educationService = Mockito.mock(EducationService.class);
    @Mock
    private CityService cityService = Mockito.mock(CityService.class);
    @Mock
    private LocationService locationService = Mockito.mock(LocationService.class);
    @Mock
    private JobService jobService = Mockito.mock(JobService.class);
    @Mock
    private VacancyService vacancyService = Mockito.mock(VacancyService.class);
    @Mock
    private StaffService staffService = Mockito.mock(StaffService.class);
    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);
    @Mock
    private AppointmentService appointmentService = Mockito.mock(AppointmentService.class);
    @Mock
    private CountryService countryService = Mockito.mock(CountryService.class);
    @Mock
    private AssessmentService assessmentService = Mockito.mock(AssessmentService.class);
    @Mock
    private AppointmentDetailService appointmentDetailService = Mockito.mock(AppointmentDetailService.class);
    @Mock
    private MessageDocService messageDocService = Mockito.mock(MessageDocService.class);
    @Mock
    private UserFitService userFitService = Mockito.mock(UserFitService.class);

    @Test
    public void checkExistsUserProfile_whenUserProfileEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        UserValidateRequest req = initUserValidateRequest();
        Mockito.when(userProfileService.findById(req.getUserProfileId())).thenReturn(null);
        businessValidatorService.checkExistsUserProfile(req.getUserProfileId());
    }

    @Test
    public void checkExistsUserProfile_whenUserProfileRight_thenReturnUserProfile() {
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Assert.assertEquals(businessValidatorService.checkExistsUserProfile(req.getUserProfileId()).getUserProfileId(), req.getUserProfileId());
    }

    @Test
    public void checkExistsUserCurriculumVitae_whenUserCurriculumVitaeEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Mockito.when(userCurriculumVitaeService.findById(req.getUserCVId())).thenReturn(null);
        businessValidatorService.checkExistsUserCurriculumVitae(req.getUserCVId());
    }

    @Test
    public void checkExistsUserCurriculumVitae_whenUserCurriculumVitaeRight_thenReturnUserProfile() {
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Assert.assertEquals(businessValidatorService.checkExistsUserCurriculumVitae(req.getUserCVId()).getCurriculumVitaeId(), req.getUserCVId());
    }

    @Test
    public void checkExistsUserProfiles_whenUserProfilesEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Mockito.when(userProfileService.findByIds(req.getUserProfileIds())).thenReturn(null);
        businessValidatorService.checkExistsUserProfile(req.getUserProfileIds());
    }

    @Test
    public void checkExistsUserProfiles_whenUserProfilesEmpty_thenReturnUserProfiles() {
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Assert.assertEquals(businessValidatorService.checkExistsUserProfile(req.getUserProfileIds()).size(), req.getUserProfileIds().size());
    }

    @Test
    public void checkExistsUserCurriculumVitaes_whenUserCurriculumVitaesEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Mockito.when(userCurriculumVitaeService.findByIds(req.getUserCVIds())).thenReturn(null);
        businessValidatorService.checkExistsUserCurriculumVitae(req.getUserCVIds());
    }

    @Test
    public void checkExistsUserCurriculumVitaes_whenUserCurriculumVitaesEmpty_thenReturnUserCurriculumVitaes() {
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Assert.assertEquals(businessValidatorService.checkExistsUserCurriculumVitae(req.getUserCVIds()).size(), req.getUserCVIds().size());
    }

    @Test
    public void checkExistsUserCvDoc_whenUserCvDocEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Mockito.when(userCvDocService.findAllById(req.getUserCVIds())).thenReturn(null);
        businessValidatorService.checkExistsUserCvDoc(req.getUserCVIds());
    }

    @Test
    public void checkExistsUserCvDocs_whenUserCvDocEmpty_thenReturnUserCvDocs() {
        UserValidateRequest req = initUserValidateRequest();
        mockitoUserService(req);
        Assert.assertEquals(businessValidatorService.checkExistsUserCvDoc(req.getUserCVDocIds()).size(), req.getUserCVDocIds().size());
    }

    @Test
    public void checkExistsCurrency_whenCurrencyEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(currencyService.findById(req.getCurrencyId())).thenReturn(null);
        businessValidatorService.checkExistsCurrency(req.getCurrencyId());
    }

    @Test
    public void checkExistsCurrency_whenUserCvDocEmpty_thenReturnCurrency() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(businessValidatorService.checkExistsCurrency(req.getCurrencyId()).getCurrencyId(), req.getCurrencyId());
    }

    @Test
    public void checkExistsEducation_whenEducationEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(null);
        businessValidatorService.checkExistsEducation(req.getEducationId());
    }

    @Test
    public void checkExistsEducation_whenUserCvDocEmpty_thenReturnEducation() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(businessValidatorService.checkExistsEducation(req.getEducationId()).getEducationId(), req.getEducationId());
    }

    @Test
    public void checkExistsCity_whenCityEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(cityService.findValidById(req.getCityId())).thenReturn(null);
        businessValidatorService.checkExistsCity(req.getCityId());
    }

    @Test
    public void checkExistsCity_whenUserCvDocEmpty_thenReturnCity() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(businessValidatorService.checkExistsCity(req.getCityId()).getCityId(), req.getCityId());
    }

    @Test
    public void checkExistsCountry_whenIdIsNull_ReturnNull() {
        Assert.assertNull(businessValidatorService.checkExistsCountry(null));
    }

    @Test
    public void checkExistsCountry_whenNotFoundCountry_ReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(countryService.findValidById(1L)).thenReturn(null);
        businessValidatorService.checkExistsCountry(1L);
    }

    @Test
    public void checkExistsCountry_whenCountryExists_ReturnCountry() {
        Mockito.when(countryService.findValidById(1L)).thenReturn(new Country(1L));
        Assert.assertEquals((new Country(1L)).getCountryId(), businessValidatorService.checkExistsCountry(1L).getCountryId());
    }

    @Test
    public void checkExistsJob_whenJobEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(jobService.findValidById(req.getJobId())).thenReturn(null);
        businessValidatorService.checkExistsJob(req.getJobId());
    }

    @Test
    public void checkExistsJob_whenUserCvDocEmpty_thenReturnJob() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(businessValidatorService.checkExistsJob(req.getJobId()).getJobId(), req.getJobId());
    }

    @Test
    public void checkExistsLocation_whenLocationEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(locationService.findById(req.getLocationId())).thenReturn(null);
        businessValidatorService.checkExistsLocation(req.getLocationId());
    }

    @Test
    public void checkExistsLocation_whenUserCvDocEmpty_thenReturnLocation() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(businessValidatorService.checkExistsLocation(req.getLocationId()).getLocationId(), req.getLocationId());
    }

    @Test
    public void checkExistsLocations_whenLocationsEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(locationService.findByIdsAndCompanyId(req.getCompanyId(), req.getLocationIds())).thenReturn(null);
        businessValidatorService.checkExistsLocations(req.getCompanyId(), req.getLocationIds());
    }

    @Test
    public void checkExistsLocations_whenLocationsEmpty_thenReturnLocations() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(businessValidatorService.checkExistsLocations(req.getCompanyId(), req.getLocationIds()).size(), req.getLocationIds().size());
    }

    @Test
    public void checkExistsVacancy_whenVacancyEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Mockito.when(vacancyService.findValidById(req.getVacancyId())).thenReturn(null);
        businessValidatorService.checkExistsValidVacancy(req.getVacancyId());
    }

    @Test
    public void checkExistsVacancy_whenVacancyEmpty_thenReturnVacancy() {
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Assert.assertEquals(businessValidatorService.checkExistsValidVacancy(req.getVacancyId()).getId(), req.getVacancyId());
    }

    @Test
    public void checkExistsStaff_whenStaffEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Mockito.when(staffService.findById(req.getStaffId())).thenReturn(null);
        businessValidatorService.checkExistsStaff(req.getStaffId());
    }

    @Test
    public void checkExistsStaff_whenStaffEmpty_thenReturnStaff() {
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Assert.assertEquals(businessValidatorService.checkExistsStaff(req.getStaffId()).getStaffId(), req.getStaffId());
    }

    @Test
    public void checkExistsStaffInCompany_whenStaffInCompanyEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(req.getCompanyId(), req.getUserProfileId())).thenReturn(null);
        businessValidatorService.checkExistsStaffInApprovedCompany(req.getCompanyId(), req.getUserProfileId());
    }

    @Test
    public void checkExistsStaffInCompany_whenStaffInCompanyEmpty_thenReturnStaffInCompany() {
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Assert.assertEquals(businessValidatorService.checkExistsStaffInApprovedCompany(req.getCompanyId(), req.getUserProfileId()).getUserFit().getUserProfileId(), req.getStaffId());
    }

    @Test
    public void existsCompany_whenCompanyEmpty_thenReturnTrue() {
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Assert.assertEquals(businessValidatorService.existsCompany(req.getCompanyId()), true);
    }

    @Test
    public void checkExistsCompany_whenCompanyEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Mockito.when(companyService.findById(req.getCompanyId())).thenReturn(null);
        businessValidatorService.checkExistsCompany(req.getCompanyId());
    }

    @Test
    public void checkExistsCompany_whenCompanyEmpty_thenReturnCompany() {
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Assert.assertEquals(businessValidatorService.checkExistsCompany(req.getCompanyId()).getCompanyId(), req.getCompanyId());
    }

    @Test
    public void checkExistsAppointment_whenAppointmentEmpty_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(appointmentService.findValidById(req.getAppointmentIds().get(0))).thenReturn(null);
        businessValidatorService.checkExistsAppointment(req.getAppointmentIds().get(0));
    }

    @Test
    public void checkExistsAppointment_whenAppointmentIsValid_thenReturnSuccess() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Assert.assertEquals(req.getAppointmentIds().get(0), (businessValidatorService.checkExistsAppointment(req.getAppointmentIds().get(0))).getId());
    }

    @Test
    public void existsAppointment_whenHavingNotFoundLeastOfOneAppointment_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(appointmentService.countValid(req.getAppointmentIds())).thenReturn((long) req.getAppointmentIds().size() - 1);
        businessValidatorService.existsAppointment(req.getAppointmentIds());
    }

    @Test
    public void existsAppointment_whenHavingAppointmentCount_thenReturnTrue() {
        CommonValidateRequest req = initCommonValidateRequest();
        mockitoCommonService(req);
        Mockito.when(appointmentService.countValid(req.getAppointmentIds())).thenReturn((long) req.getAppointmentIds().size());
        Assert.assertTrue(businessValidatorService.existsAppointment(req.getAppointmentIds()));
    }

    @Test
    public void checkHasPermissionToCreateVacancy_whenNoFoundStaff_thenReturnThrowException() {
        thrown.expect(EntityNotFoundException.class);
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(req.getUserProfileId(), req.getCompanyId())).thenReturn(null);
        businessValidatorService.checkHasPermissionToCreateVacancy(req.getCompanyId(), req.getUserProfileId());
    }

    @Test
    public void checkHasPermissionToCreateVacancy_whenRoleNotExist_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(req.getCompanyId(), req.getUserProfileId()))
                .thenReturn(Lists.newArrayList((new Staff(
                        req.getStaffId(),
                        new Company(req.getCompanyId()),
                        new UserFit(req.getUserProfileId()),
                        new RoleCompany(CompanyRole.ANALYST.getCode(), CompanyRole.ANALYST.getName())))));
        businessValidatorService.checkHasPermissionToCreateVacancy(req.getCompanyId(), req.getUserProfileId());
    }

    @Test
    public void checkHasPermissionToCreateVacancy_whenHavingPermissionToCreate_thenReturnSuccess() {
        StaffValidateRequest req = initStaffValidateRequest();
        mockitoStaffService(req);
        Assert.assertEquals(req.getStaffId(), businessValidatorService.checkHasPermissionToCreateVacancy(req.getCompanyId(), req.getUserProfileId()).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenHavingNullVacancyAndNullStaff_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        businessValidatorService.checkPermissionOnVacancy((Vacancy) null, (Staff) null);
    }

    @Test
    public void checkPermissionOnVacancy_whenHavingNullRoleStaff_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        Vacancy vacancy = initVacancy();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(1L);
        staff.setRole(null);
        businessValidatorService.checkPermissionOnVacancy(vacancy, staff);
    }

    @Test
    public void checkPermissionOnVacancy_whenHavingDifferentCompany_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        Vacancy vacancy = initVacancy();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(1L);
        businessValidatorService.checkPermissionOnVacancy(vacancy, staff);
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsAdminAndHasPermissionToAccessVacancy_thenReturnSuccess() {
        Vacancy vacancy = initVacancy();
        Staff staff = DataUtilsTest.initStaffIsAdmin();
        staff.setStaffId(1L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsHeadRecruiterAndHasPermissionToAccessVacancy_thenReturnSuccess() {
        Vacancy vacancy = initVacancyWithStaffIsHeadRecruiter();
        Staff staff = DataUtilsTest.initStaffIsHeadRecruiter();
        staff.setStaffId(2L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsRecruiterAndHasPermissionToAccessVacancy_thenReturnSuccess() {
        Vacancy vacancy = initVacancyWithStaffIsRecruiter();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(3L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsCreatorOfVacancy_thenReturnSuccess() {
        Vacancy vacancy = initVacancyWithStaffIsRecruiter();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(2L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenAreNullInputs_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        businessValidatorService.checkPermissionOnVacancy((VacancyDoc) null, (Staff) null);
    }

    @Test
    public void checkPermissionOnVacancy_whenIsNullRole_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        VacancyDoc vacancy = initVacancyDoc();
        Staff staff = DataUtilsTest.initStaffIsAdmin();
        staff.setStaffId(1L);
        staff.setRole(null);
        businessValidatorService.checkPermissionOnVacancy(vacancy, staff);
    }

    @Test
    public void checkPermissionOnVacancy_whenIsDifferentCompany_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        VacancyDoc vacancy = initVacancyDoc();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(1L);
        businessValidatorService.checkPermissionOnVacancy(vacancy, staff);
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsAdminAndHasPermissionToAccessVacancyDoc_thenReturnSuccess() {
        VacancyDoc vacancy = initVacancyDoc();
        Staff staff = DataUtilsTest.initStaffIsAdmin();
        staff.setStaffId(1L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsHeadRecruiterAndHasPermissionToAccessVacancyDoc_thenReturnSuccess() {
        VacancyDoc vacancy = initVacancyDocIsHeadRecruiter();
        Staff staff = DataUtilsTest.initStaffIsHeadRecruiter();
        staff.setStaffId(2L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsRecruiterAndHasPermissionToAccessVacancyDoc_thenReturnSuccess() {
        VacancyDoc vacancy = initVacancyDocIsRecruiter();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(3L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsCreatorOfVacancyDoc_thenReturnSuccess() {
        VacancyDoc vacancy = initVacancyDocIsRecruiter();
        Staff staff = DataUtilsTest.initStaffIsRecruiter();
        staff.setStaffId(2L);
        staff.getCompany().setCompanyId(1L);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkPermissionOnVacancy(vacancy, staff).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsNotStaffOfCompany_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        Vacancy vacancy = initVacancy();
        Long userProfileId = 1L;
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getCompanyId()))
                .thenReturn(new ArrayList<>());
        businessValidatorService.checkPermissionOnVacancy(vacancy, userProfileId);
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsStaffOfCompanyAndHasPermission_thenReturnSuccess() {
        Vacancy vacancy = initVacancy();
        Long userProfileId = 1L;
        mockitoVacancyService(vacancy, userProfileId);
        Assert.assertEquals((Long)1L, businessValidatorService.checkPermissionOnVacancy(vacancy, userProfileId).getStaffId());
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsNotStaffOfCompanyInVacancyDoc_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        VacancyDoc vacancy = initVacancyDoc();
        Long userProfileId = 1L;
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getId()))
                .thenReturn(new ArrayList<>());
        businessValidatorService.checkPermissionOnVacancy(vacancy, userProfileId);
    }

    @Test
    public void checkPermissionOnVacancy_whenUserIsStaffOfCompanyAndHasPermissionInVacancyDoc_thenReturnSuccess() {
        VacancyDoc vacancy = initVacancyDoc();
        Long userProfileId = 1L;
        mockitoVacancyDocService(vacancy, userProfileId);
        Assert.assertEquals((Long)1L, businessValidatorService.checkPermissionOnVacancy(vacancy, userProfileId).getStaffId());
    }

    @Test
    public void checkValidTimeZone_whenTimeZoneIsBlank_thenReturnThrowException() {
        thrown.expect(InvalidParamException.class);
        String timeZone = "";
        businessValidatorService.checkValidTimeZone(timeZone);
    }

    @Test
    public void checkValidTimeZone_whenTimeZoneIsInvalid_thenReturnThrowException() {
        thrown.expect(InvalidParamException.class);
        String timeZone = "Asia/Vietnam";
        businessValidatorService.checkValidTimeZone(timeZone);
    }

    @Test
    public void checkValidTimeZone_whenTimeZoneIsUTC_thenReturnSuccess() {
        String timeZone = "Africa/Accra";
        Assert.assertEquals("00:00", businessValidatorService.checkValidTimeZone(timeZone));
    }

    @Test
    public void checkValidTimeZone_whenTimeZoneIsValid_thenReturnSuccess() {
        LocalDateTime dt = LocalDateTime.now();
        String timeZone = "Australia/Darwin";
        ZoneId zone = ZoneId.of(timeZone);
        ZonedDateTime zdt = dt.atZone(zone);
        ZoneOffset offset = zdt.getOffset();
        String timeOffset = offset.toString();
        Assert.assertEquals(timeOffset.length(), businessValidatorService.checkValidTimeZone(timeZone).length());
    }

    @Test
    public void checkPermissionOnAppointment_whenInputIsValid_thenReturnSuccess() {
        Long userProfileId = 1L;
        List<String> roleNames = Lists.newArrayList(CompanyRole.ADMIN.getName(), CompanyRole.HEAD_RECRUITER.getName());
        Appointment appointment = DataUtilsTest.initAppointment();
        mockitoAppointmentService(appointment, userProfileId, roleNames);
        Assert.assertEquals(appointment.getId(), businessValidatorService.checkPermissionOnAppointment(appointment.getId(), userProfileId, roleNames).getId());
    }

    @Test
    public void checkStaffPermissionOnAppointment_whenInputIsValidAndStaffIsAdmin_thenReturnSuccess() {
        Long userProfileId = 1L;
        List<String> roleNames = Lists.newArrayList(CompanyRole.ADMIN.getName(), CompanyRole.HEAD_RECRUITER.getName());
        Staff staff = DataUtilsTest.initStaffIsAdmin();
        Appointment appointment = DataUtilsTest.initAppointment();
        mockitoAppointmentService(appointment, userProfileId, roleNames);
        Assert.assertEquals(staff.getStaffId(), businessValidatorService.checkStaffPermissionOnAppointment(appointment, userProfileId, roleNames).getStaffId());
    }

    @Test
    public void checkStaffPermissionOnAppointment_whenInputIsValidAndRoleIsNoPermissionToAccess_thenReturnThrowException() {
        thrown.expect(NoPermissionException.class);
        Long userProfileId = 2L;
        List<String> roleNames = Lists.newArrayList(CompanyRole.ADMIN.getName(), CompanyRole.HEAD_RECRUITER.getName());
        Appointment appointment = DataUtilsTest.initAppointment();
        Mockito.when(staffService.findByUserProfileAndCompanyApprovalAndRoles(userProfileId, appointment.getVacancy().getCompany().getCompanyId(), roleNames)).thenReturn(DataUtilsTest.initStaffs());
        businessValidatorService.checkStaffPermissionOnAppointment(appointment, userProfileId, roleNames);
    }

    @Test
    public void checkExistsAssessment_whenIdIsInvalid_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(assessmentService.findById(1L)).thenReturn(null);
        businessValidatorService.checkExistsAssessment(1L);
    }

    @Test
    public void checkExistsAssessment_whenIdIsValid_thenReturnAssessmentSuccess() {
        Mockito.when(assessmentService.findById(1L)).thenReturn(new Assessment(1L));
        Assert.assertEquals((new Assessment(1L)).getId(), (businessValidatorService.checkExistsAssessment(1L)).getId());
    }

    @Test
    public void existsAssessment_whenIdNotExists_thenReturnFalse() {
        Mockito.when(assessmentService.exists(1L)).thenReturn(false);
        Assert.assertFalse(businessValidatorService.existsAssessment(1L));
    }

    @Test
    public void existsAssessment_whenIdExists_thenReturnTrue() {
        Mockito.when(assessmentService.exists(1L)).thenReturn(true);
        Assert.assertTrue(businessValidatorService.existsAssessment(1L));
    }

    @Test
    public void checkExistAppointmentDetail_whenIdsIsInvalid_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        List<Long> ids = Lists.newArrayList(1L, 2L);
        Mockito.when(appointmentDetailService.findByIds(ids)).thenReturn(new ArrayList<>());
        businessValidatorService.checkExistAppointmentDetail(ids);
    }

    @Test
    public void checkExistAppointmentDetail_whenFoundActualAppointmentLessThenInput_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        List<Long> ids = Lists.newArrayList(1L, 2L);
        List<AppointmentDetail> appointmentDetails = Lists.newArrayList(new AppointmentDetail(1L));
        Mockito.when(appointmentDetailService.findByIds(ids)).thenReturn(appointmentDetails);
        businessValidatorService.checkExistAppointmentDetail(ids);
    }

    @Test
    public void checkExistAppointmentDetail_whenInputIsRight_thenReturnAppointmentListSuccess() {
        List<Long> ids = Lists.newArrayList(1L, 2L);
        List<AppointmentDetail> appointmentDetails = Lists.newArrayList(new AppointmentDetail(1L), new AppointmentDetail(2L));
        Mockito.when(appointmentDetailService.findByIds(ids)).thenReturn(appointmentDetails);
        Assert.assertEquals(ids.size(), (businessValidatorService.checkExistAppointmentDetail(ids)).size());
    }

    @Test
    public void checkExistsMessageDoc_whenIdNotExists_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        String id = "5b8f52fef11a2e77b96e93a2";
        Mockito.when(messageDocService.findById(new ObjectId(id))).thenReturn(null);
        businessValidatorService.checkExistsMessageDoc(id);
    }

    @Test
    public void checkExistsMessageDoc_whenIdExists_thenReturnMessageDocSuccess() {
        String id = "5b8f52fef11a2e77b96e93a2";
        MessageDoc messageDoc = new MessageDoc();
        messageDoc.setId(new ObjectId(id));
        Mockito.when(messageDocService.findById(new ObjectId(id))).thenReturn(messageDoc);
        Assert.assertEquals(messageDoc.getId().hashCode(), businessValidatorService.checkExistsMessageDoc(id).getId().hashCode());
    }

    @Test
    public void checkExistsFitUser_whenNotFoundFitUserProfile_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(userFitService.findById(1L)).thenReturn(null);
        businessValidatorService.checkExistsUserFit(1L);
    }

    @Test
    public void checkExistsFitUser_whenInputIsValid_thenReturnSuccess() {
        Long userProfileId = 1L;
        UserFit fitUserDoc = new UserFit(userProfileId);
        Mockito.when(userFitService.findById(userProfileId)).thenReturn(fitUserDoc);
        Assert.assertEquals(userProfileId, businessValidatorService.checkExistsUserFit(1L).getUserProfileId());
    }

    @Test
    public void checkExistsFitUser_whenNotFoundAppointmentDetail_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(appointmentDetailService.findById(1L)).thenReturn(null);
        businessValidatorService.checkExistAppointmentDetail(1L);
    }

    @Test
    public void checkExistAppointmentDetail_when_thenReturn() {
        Long id = 1L;
        Long ownerId = 1L;
        AppointmentDetail detail = new AppointmentDetail(id, ownerId);
        Mockito.when(appointmentDetailService.findById(id)).thenReturn(detail);
        Assert.assertEquals(id, businessValidatorService.checkExistAppointmentDetail(id).getId());
    }

    @Test
    public void checkUserProfileIsRootAdmin_whenNotFoundRootAdmin_thenReturnErrorException() {
        thrown.expect(NoPermissionException.class);
        Long userProfileId = 1L;
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(userProfileId)).thenReturn(null);
        businessValidatorService.checkUserProfileIsRootAdmin(userProfileId);
    }

    @Test
    public void checkUserProfileIsRootAdmin_whenFoundRootAdmin_thenReturnSuccess() {
        Long userProfileId = 1L;
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(userProfileId)).thenReturn(new UserProfile(userProfileId));
        Assert.assertEquals(userProfileId, businessValidatorService.checkUserProfileIsRootAdmin(userProfileId).getUserProfileId());
    }

    @Test
    public void isValidSalaryRange_whenCurrentIsNull_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        double minSalary = 1.0;
        double maxSalary = 100.5;
        businessValidatorService.isValidSalaryRange(null, minSalary, maxSalary);
    }

    @Test
    public void isValidSalaryRange_whenMinSalaryIsLargerThanAndMaxSalaryIsLessThan_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        double minSalary = 1.0;
        double maxSalary = 100.5;
        Currency currency = new Currency();
        currency.setMinSalary(2L);
        currency.setMaxSalary(90L);
        businessValidatorService.isValidSalaryRange(currency, minSalary, maxSalary);
    }

    @Test
    public void isValidSalaryRange_whenInputAreValid_thenReturnSuccess() {
        double minSalary = 2.0;
        double maxSalary = 90.0;
        Currency currency = new Currency();
        currency.setMinSalary(1L);
        currency.setMaxSalary(100L);
        Assert.assertTrue(businessValidatorService.isValidSalaryRange(currency, minSalary, maxSalary));
    }

    //========================== Prepare data for unit test =============================================
    private void mockitoStaffService(StaffValidateRequest request) {
        Mockito.when(vacancyService.findValidById(request.getVacancyId())).thenReturn(new Vacancy(request.getVacancyId()));
        Mockito.when(staffService.findById(request.getStaffId())).thenReturn(new Staff(request.getStaffId()));
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(request.getCompanyId(), request.getUserProfileId()))
                .thenReturn(Lists.newArrayList((new Staff(
                        request.getStaffId(),
                        new Company(request.getCompanyId()),
                        new UserFit(request.getUserProfileId()),
                        new RoleCompany(CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.getName())))));

        Mockito.when(companyService.exists(request.getCompanyId())).thenReturn(true);
        Mockito.when(companyService.findById(request.getCompanyId())).thenReturn(new Company(request.getCompanyId()));

    }

    private void mockitoVacancyService(Vacancy vacancy, Long userProfileId) {
        Staff staff = DataUtilsTest.initStaffIsAdmin();
        staff.setStaffId(1L);
        staff.getCompany().setCompanyId(1L);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getCompanyId()))
                .thenReturn(Lists.newArrayList(staff));
    }

    private void mockitoVacancyDocService(VacancyDoc vacancy, Long userProfileId) {
        Staff staff = DataUtilsTest.initStaffIsAdmin();
        staff.setStaffId(1L);
        staff.getCompany().setCompanyId(1L);
        Mockito.when(staffService.findByUserProfileAndCompanyApproval(userProfileId, vacancy.getCompany().getId()))
                .thenReturn(Lists.newArrayList(staff));
    }

    private void mockitoAppointmentService(Appointment appointment, Long userProfileId, List<String> roleNames) {
        List<Staff> staffs = DataUtilsTest.initStaffs();
        Staff staff = staffs.get(0);
        Mockito.when(appointmentService.findValidById(appointment.getId())).thenReturn(appointment);
        Mockito.when(staffService.findByUserProfileAndCompanyApprovalAndRoles(userProfileId, appointment.getVacancy().getCompany().getCompanyId(), roleNames)).thenReturn(staffs);
        Mockito.when(businessValidatorService.checkExistsAppointment(appointment.getId())).thenReturn(appointment);
    }

    private void mockitoCommonService(CommonValidateRequest request) {
        Mockito.when(currencyService.findById(request.getCurrencyId())).thenReturn(new Currency(request.getCurrencyId()));
        Mockito.when(educationService.findById(request.getEducationId())).thenReturn(new Education(request.getEducationId()));
        Mockito.when(cityService.findValidById(request.getCityId())).thenReturn(new City(request.getCityId()));
        Mockito.when(jobService.findValidById(request.getJobId())).thenReturn(new Job(request.getJobId()));
        Mockito.when(locationService.findById(request.getLocationId())).thenReturn(new Location(request.getLocationId()));
        Mockito.when(locationService.findByIdsAndCompanyId(request.getCompanyId(), request.getLocationIds()))
                .thenReturn(request.getLocationIds().stream().map(Location::new).collect(Collectors.toList()));
        Appointment appointment = new Appointment();
        appointment.setId(request.getAppointmentIds().get(0));
        Mockito.when(appointmentService.findValidById(request.getAppointmentIds().get(0))).thenReturn(appointment);
        Mockito.when(appointmentService.countValid(request.getAppointmentIds())).thenReturn((long) request.getAppointmentIds().size());
    }

    private void mockitoUserService(UserValidateRequest request) {
        Mockito.when(userProfileService.findById(request.getUserProfileId())).thenReturn(new UserProfile(request.getUserProfileId()));
        Mockito.when(userProfileService.findByIds(request.getUserProfileIds()))
                .thenReturn(request.getUserProfileIds().stream().map(UserProfile::new).collect(Collectors.toList()));

        Mockito.when(userCurriculumVitaeService.findById(request.getUserCVId())).thenReturn(new UserCurriculumVitae(request.getUserCVId()));
        Mockito.when(userCurriculumVitaeService.findByIds(request.getUserProfileIds()))
                .thenReturn(request.getUserProfileIds().stream().map(UserCurriculumVitae::new).collect(Collectors.toList()));

        Mockito.when(userCvDocService.findAllById(request.getUserCVDocIds()))
                .thenReturn(request.getUserCVDocIds().stream().map(UserCvDoc::new).collect(Collectors.toList()));
    }

    private StaffValidateRequest initStaffValidateRequest() {
        StaffValidateRequest req = new StaffValidateRequest();
        req.setVacancyId(1L);
        req.setStaffId(1L);
        req.setCompanyId(1L);
        req.setUserProfileId(1L);
        return req;
    }

    private Vacancy initVacancy() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(1L);
        vacancy.setCompany(new Company(1L));
        vacancy.setContactPerson(new Staff(1L,
                new Company(1L),
                new UserFit(1L),
                new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        vacancy.setCreatedBy(1L);
        return vacancy;
    }

    private Vacancy initVacancyWithStaffIsHeadRecruiter() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(2L);
        vacancy.setCompany(new Company(1L));
        vacancy.setContactPerson(new Staff(2L,
                new Company(1L),
                new UserFit(2L),
                new RoleCompany(CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.getName())));
        vacancy.setCreatedBy(2L);
        return vacancy;
    }

    private Vacancy initVacancyWithStaffIsRecruiter() {
        Vacancy vacancy = new Vacancy();
        vacancy.setId(3L);
        vacancy.setCompany(new Company(1L));
        vacancy.setContactPerson(new Staff(3L,
                new Company(1L),
                new UserFit(3L),
                new RoleCompany(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.getName())));
        vacancy.setCreatedBy(3L);
        return vacancy;
    }

    private VacancyDoc initVacancyDoc() {
        VacancyDoc vacancy = new VacancyDoc();
        vacancy.setId(4L);
        vacancy.setCompany(new CompanyEmbedded(1L));
        vacancy.setContactPerson(new StaffEmbedded(1L,
                new CompanyEmbedded(1L),
                new UserProfileEmbedded(1L, 1),
                new RoleCompanyEmbedded(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        vacancy.setCreatedByStaff(new StaffEmbedded(new UserProfileEmbedded(1L, 1)));
        return vacancy;
    }

    private VacancyDoc initVacancyDocIsHeadRecruiter() {
        VacancyDoc vacancy = new VacancyDoc();
        vacancy.setId(5L);
        vacancy.setCompany(new CompanyEmbedded(1L));
        vacancy.setContactPerson(new StaffEmbedded(2L,
                new CompanyEmbedded(1L),
                new UserProfileEmbedded(2L, 1),
                new RoleCompanyEmbedded(CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.HEAD_RECRUITER.getName())));
        vacancy.setCreatedByStaff(new StaffEmbedded(new UserProfileEmbedded(2L, 1)));
        return vacancy;
    }

    private VacancyDoc initVacancyDocIsRecruiter() {
        VacancyDoc vacancy = new VacancyDoc();
        vacancy.setId(6L);
        vacancy.setCompany(new CompanyEmbedded(1L));
        vacancy.setContactPerson(new StaffEmbedded(3L,
                new CompanyEmbedded(1L),
                new UserProfileEmbedded(3L, 1),
                new RoleCompanyEmbedded(CompanyRole.RECRUITER.getCode(), CompanyRole.RECRUITER.getName())));
        vacancy.setCreatedByStaff(new StaffEmbedded(new UserProfileEmbedded(3L, 1)));
        return vacancy;
    }

    private UserValidateRequest initUserValidateRequest() {
        UserValidateRequest req = new UserValidateRequest();
        req.setUserCVId(1L);
        req.setUserCVId(1L);
        req.setUserProfileIds(Lists.newArrayList(1L, 2L));
        req.setUserCVIds(Lists.newArrayList(1L, 2L));
        req.setUserCVDocIds(Lists.newArrayList(1L, 2L));
        return req;
    }

    private CommonValidateRequest initCommonValidateRequest() {
        CommonValidateRequest req = new CommonValidateRequest();
        req.setCurrencyId(1L);
        req.setEducationId(1L);
        req.setCityId(1L);
        req.setJobId(1L);
        req.setLocationId(1L);
        req.setCompanyId(1L);
        req.setLocationIds(Lists.newArrayList(1L, 2L));
        req.setAppointmentIds(Lists.newArrayList(1L, 2L, 3L));
        return req;
    }
}

@Setter
@Getter
class UserValidateRequest {
    private Long userProfileId;
    private List<Long> userProfileIds;
    private Long userCVId;
    private List<Long> userCVIds;
    private List<Long> userCVDocIds;
}

@Setter
@Getter
class CommonValidateRequest {
    private Long currencyId;
    private Long educationId;
    private Long cityId;
    private Long jobId;
    private Long locationId;
    private Long companyId;
    private List<Long> locationIds;
    private List<Long> appointmentIds;
}

@Setter
@Getter
class StaffValidateRequest {
    private Long vacancyId;
    private Long staffId;
    private Long userProfileId;
    private Long companyId;
}