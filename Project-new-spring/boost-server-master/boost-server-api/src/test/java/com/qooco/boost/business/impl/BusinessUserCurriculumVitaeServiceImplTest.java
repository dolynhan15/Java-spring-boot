package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessUserCurriculumVitaeService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.user.UserCurriculumVitaeReq;
import com.qooco.boost.models.user.UserCurriculumVitaeResp;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.ServletUriUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServletUriUtils.class)
@SpringBootTest
public class BusinessUserCurriculumVitaeServiceImplTest {

    @InjectMocks
    private BusinessUserCurriculumVitaeService businessUserCvService = new BusinessUserCurriculumVitaeServiceImpl();

    @Mock
    private JobService jobService = Mockito.mock(JobService.class);

    @Mock
    private CurrencyService currencyService = Mockito.mock(CurrencyService.class);

    @Mock
    private EducationService educationService = Mockito.mock(EducationService.class);
    @Mock
    private WorkingHourService workingHourService = Mockito.mock(WorkingHourService.class);
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private UserCurriculumVitaeService userCurriculumVitaeService = Mockito.mock(UserCurriculumVitaeService.class);
    @Mock
    private UserQualificationService userQualificationService = Mockito.mock(UserQualificationService.class);

    @Mock
    private CurriculumVitaeJobService curriculumVitaeJobService = Mockito.mock(CurriculumVitaeJobService.class);

    @Mock
    private UserDesiredHourService userDesiredHourService = Mockito.mock(UserDesiredHourService.class);

    @Mock
    private BenefitService benefitService = Mockito.mock(BenefitService.class);

    @Mock
    private SoftSkillsService softSkillsService = Mockito.mock(SoftSkillsService.class);

    @Mock
    private UserPreviousPositionService userPreviousPositionService = Mockito.mock(UserPreviousPositionService.class);

    @Mock
    private UserSoftSkillService userSoftSkillService = Mockito.mock(UserSoftSkillService.class);

    @Mock
    private UserBenefitService userBenefitService = Mockito.mock(UserBenefitService.class);

    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);

    @Mock
    private UserPreferredHotelService userPreferredHotelService = Mockito.mock(UserPreferredHotelService.class);

    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);

    @Mock
    private UserAttributeService userAttributeService = Mockito.mock(UserAttributeService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    @Test
    public void saveUserCurriculumVitae_whenInvalidSalaryRange_thenReturnInvalidSalaryRange() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(10);
        req.setMinSalary(11);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INVALID_SALARY_RANGE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenEmptyJobId_thenReturnEmptyJob() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.REQUIRED_JOB.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenNotExistedJob_thenReturnNotFoundJob() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4,5};
        req.setJobIds(jobIds);
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(null);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_JOB.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenCurrencyIsNone_thenReturnMissingCurrency() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.CURRENCY_CODE_IS_REQUIRED.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenNotExistedCurrency_thenReturnNotFoundCurrency() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(null);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_CURRENCY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenNotExistedEducation_thenReturnNotFoundEducation() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(null);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_EDUCATION.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenEmptyWorkingHour_thenReturnEmptyWorkingHour() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Education education = new Education();
        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(education);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.REQUIRED_WORKING_HOUR.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenNotExistedWorkingHour_thenReturnNotWorkingHour() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        long[] workingIds = new long[] {1};
        req.setWorkHourIds(workingIds);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Education education = new Education();
        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(education);
        Mockito.when(workingHourService.findByIds(req.getWorkHourIds())).thenReturn(null);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_WORKING_HOUR.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenConflictWorkingHour_thenReturnConflictWorkingHour() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        req.setFullTime(false);
        long[] workingIds = new long[] {1};
        req.setWorkHourIds(workingIds);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Education education = new Education();
        List<WorkingHour> workingHours = new ArrayList<>();
        WorkingHour workingHour = new WorkingHour();
        workingHour.setWorkingType(true);
        workingHours.add(workingHour);

        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(education);
        Mockito.when(workingHourService.findByIds(req.getWorkHourIds())).thenReturn(workingHours);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.CONFLICT_WORKING_HOUR.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenNotExistUser_thenReturnNotFoundUser() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        req.setFullTime(true);
        req.setUserProfileId(1L);
        long[] workingIds = new long[] {1};
        req.setWorkHourIds(workingIds);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Education education = new Education();
        List<WorkingHour> workingHours = new ArrayList<>();
        WorkingHour workingHour = new WorkingHour();
        workingHour.setWorkingType(true);
        workingHours.add(workingHour);

        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(education);
        Mockito.when(workingHourService.findByIds(req.getWorkHourIds())).thenReturn(workingHours);
        Mockito.when(userProfileService.findById(req.getUserProfileId())).thenReturn(null);
        try {
            businessUserCvService.saveUserCurriculumVitae(req, authentication);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_USER_PROFILE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveUserCurriculumVitae_whenEmptyUserCv_thenInsertNewCv() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        req.setFullTime(true);
        req.setUserProfileId(1L);
        long[] workingIds = new long[] {1};
        req.setWorkHourIds(workingIds);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Education education = new Education();
        List<WorkingHour> workingHours = new ArrayList<>();
        WorkingHour workingHour = new WorkingHour();
        workingHour.setWorkingType(true);
        workingHours.add(workingHour);
        UserProfile userProfile = new UserProfile();
        userProfile.setUserProfileId(req.getUserProfileId());
        userProfile.setUsername("phuc.nguyen");
        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(education);
        Mockito.when(workingHourService.findByIds(req.getWorkHourIds())).thenReturn(workingHours);
        Mockito.when(userProfileService.findById(req.getUserProfileId())).thenReturn(userProfile);
        Mockito.when(userCurriculumVitaeService.findByUserProfile(userProfile)).thenReturn(null);
        UserCurriculumVitae userCurriculumVitae = req.toUserCurriculumVitae(null);
        userCurriculumVitae.setCurriculumVitaeJobs(addListCvJob(userCurriculumVitae, lstJob, req.getUserProfileId()));
        userCurriculumVitae.setUserDesiredHours(addListUserDesiredHour(userCurriculumVitae, workingHours, req.getUserProfileId()));
        userCurriculumVitae.setEducation(education);
        userCurriculumVitae.setCurrency(currency);
        userCurriculumVitae.setUserProfile(userProfile);
        userCurriculumVitae.setUserDesiredHours(userCurriculumVitae.getUserDesiredHours());
        userCurriculumVitae.setCurriculumVitaeJobs(userCurriculumVitae.getCurriculumVitaeJobs());
        UserCurriculumVitae insertCv = userCurriculumVitae.toBuilder().build();
        insertCv.setCurriculumVitaeId(1L);
        Mockito.when(userCurriculumVitaeService.save(userCurriculumVitae)).thenReturn(insertCv);
        prepareStaticClass();
        UserCurriculumVitaeResp resp = new UserCurriculumVitaeResp();
        Mockito.when(benefitService.findByIds(null)).thenReturn(new ArrayList<>());
        Mockito.doNothing().when(boostActorManager).saveUserCvInMongo(insertCv);
        resp.setUserCurriculumVitae(new UserCurriculumVitaeDTO(insertCv, ""));
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Assert.assertEquals(resp.getCode(), businessUserCvService.saveUserCurriculumVitae(req, authentication).getCode());
    }


    @Test
    public void saveUserCurriculumVitae_whenExistedUserCv_thenUpdateCv() {
        UserCurriculumVitaeReq req = new UserCurriculumVitaeReq();
        req.setMaxSalary(15);
        req.setMinSalary(11);
        long[] jobIds = new long[] {4};
        req.setJobIds(jobIds);
        req.setCurrencyCode("VND");
        req.setEducationId(1);
        req.setFullTime(true);
        req.setUserProfileId(1L);
        long[] workingIds = new long[] {1};
        req.setWorkHourIds(workingIds);
        List<Job> lstJob = new ArrayList<>();
        lstJob.add(new Job());
        Mockito.when(jobService.findByIds(jobIds)).thenReturn(lstJob);
        Currency currency = new Currency();
        currency.setCode("VND");
        currency.setCurrencyId(1L);
        Education education = new Education();
        List<WorkingHour> workingHours = new ArrayList<>();
        WorkingHour workingHour = new WorkingHour();
        workingHour.setWorkingType(true);
        workingHours.add(workingHour);
        UserProfile userProfile = new UserProfile();
        userProfile.setUserProfileId(req.getUserProfileId());
        userProfile.setUsername("phuc.nguyen");
        Mockito.when(currencyService.findByCode(req.getCurrencyCode())).thenReturn(currency);
        Mockito.when(educationService.findById(req.getEducationId())).thenReturn(education);
        Mockito.when(workingHourService.findByIds(req.getWorkHourIds())).thenReturn(workingHours);
        Mockito.when(userProfileService.findById(req.getUserProfileId())).thenReturn(userProfile);
        UserCurriculumVitae userCurriculumVitae = req.toUserCurriculumVitae(null);
        userCurriculumVitae.setCurriculumVitaeId(1L);
        Mockito.when(userCurriculumVitaeService.findByUserProfile(userProfile)).thenReturn(userCurriculumVitae);
        userCurriculumVitae.setCurriculumVitaeJobs(addListCvJob(userCurriculumVitae, lstJob, req.getUserProfileId()));
        userCurriculumVitae.setUserDesiredHours(addListUserDesiredHour(userCurriculumVitae, workingHours, req.getUserProfileId()));
        userCurriculumVitae.setEducation(education);
        userCurriculumVitae.setCurrency(currency);
        userCurriculumVitae.setUserProfile(userProfile);
        userCurriculumVitae.setUserDesiredHours(userCurriculumVitae.getUserDesiredHours());
        userCurriculumVitae.setCurriculumVitaeJobs(userCurriculumVitae.getCurriculumVitaeJobs());
        UserCurriculumVitae insertCv = userCurriculumVitae.toBuilder().build();
        insertCv.setCurriculumVitaeId(1L);
        prepareStaticClass();
        Mockito.when(softSkillsService.findByIds(null)).thenReturn(new ArrayList<>());
        Mockito.doAnswer(new DeleteDataAnswer()).when(userSoftSkillService).deleteByUserCurriculumVitaeId(1L);
        Mockito.when(userCurriculumVitaeService.save(userCurriculumVitae)).thenReturn(insertCv);
        Mockito.doAnswer(new DeleteDataAnswer()).when(curriculumVitaeJobService).deleteByUserCurriculumVitaeId(userCurriculumVitae.getCurriculumVitaeId());
        Mockito.doAnswer(new DeleteDataAnswer()).when(userDesiredHourService).deleteByUserCurriculumVitaeId(userCurriculumVitae.getCurriculumVitaeId());
        Mockito.doAnswer(new DeleteDataAnswer()).when(userBenefitService).deleteByUserCurriculumVitaeId(userCurriculumVitae.getCurriculumVitaeId());
        Mockito.doAnswer(new DeleteDataAnswer()).when(userPreferredHotelService).deleteByUserCurriculumVitaeId(userCurriculumVitae.getCurriculumVitaeId());
        Mockito.doNothing().when(boostActorManager).saveUserCvInMongo(insertCv);
        UserCurriculumVitaeResp resp = new UserCurriculumVitaeResp();
        resp.setUserCurriculumVitae(new UserCurriculumVitaeDTO(insertCv, ""));
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Assert.assertEquals(resp.getCode(), businessUserCvService.saveUserCurriculumVitae(req, authentication).getCode());
    }

    private List<CurriculumVitaeJob> addListCvJob(UserCurriculumVitae userCurriculumVitae, List<Job> lstJob, Long userProfileId) {
        List<CurriculumVitaeJob> lstCvJob = new ArrayList<>();
        CurriculumVitaeJob curriculumVitaeJob;
        int jobSize = lstJob.size();
        for (int i = 0; i < jobSize; i++) {
            curriculumVitaeJob = new CurriculumVitaeJob();
            curriculumVitaeJob.setJob(lstJob.get(i));
            curriculumVitaeJob.setUserCurriculumVitae(userCurriculumVitae);
            curriculumVitaeJob.setCreatedBy(userProfileId);
            curriculumVitaeJob.setUpdatedBy(userProfileId);
            lstCvJob.add(curriculumVitaeJob);
        }
        return lstCvJob;
    }

    private List<UserDesiredHour> addListUserDesiredHour(UserCurriculumVitae userCurriculumVitae, List<WorkingHour> workingHours, Long userProfileId) {
        List<UserDesiredHour> lstUserDesiredHours = new ArrayList<>();
        UserDesiredHour userDesiredHour;
        int workingHourSize = workingHours.size();
        for (int i = 0; i < workingHourSize; i++) {
            userDesiredHour = new UserDesiredHour();
            userDesiredHour.setWorkingHour(workingHours.get(i));
            userDesiredHour.setUserCurriculumVitae(userCurriculumVitae);
            userDesiredHour.setCreatedBy(userProfileId);
            userDesiredHour.setUpdatedBy(userProfileId);
            lstUserDesiredHours.add(userDesiredHour);
        }
        return lstUserDesiredHours;
    }

    @Test
    public void getUserCurriculumVitaeByUserProfileId_whenNotExistedUserProfile_thenReturnNotFoundUser() {
        Mockito.when(userProfileService.findById(1L)).thenReturn(null);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenThrow(new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE));
        try {
            businessUserCvService.getUserCurriculumVitaeByUserProfileId(1L, "en_US");
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_USER_PROFILE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getUserCurriculumVitaeByUserProfileId_whenNotExistedUserUserCV_thenReturnEmptyUserCV() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserProfileId(1L);
        prepareStaticClass();
        Mockito.when(userProfileService.findById(userProfile.getUserProfileId())).thenReturn(userProfile);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(userProfile);
        Mockito.when(userAttributeService.findActiveByUserProfile(1L, 0, Constants.TOP_THREE_ATTRIBUTES)).thenReturn(new PageImpl<>(List.of()));
        BaseResp response = businessUserCvService.getUserCurriculumVitaeByUserProfileId(userProfile.getUserProfileId(), "en_US");
        UserCurriculumVitaeDTO userCvDTO = (UserCurriculumVitaeDTO) response.getData();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), response.getCode());
        Assert.assertNull(userCvDTO.getId());
        Assert.assertEquals(userProfile.getUserProfileId(), userCvDTO.getUserProfile().getId());
    }

    @Test
    public void getUserCurriculumVitaeByUserProfileId_whenExistedUserUserCV_thenReturnUserCV() {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserProfileId(1L);
        UserCurriculumVitae userCurriculumVitae = new UserCurriculumVitae();
        userCurriculumVitae.setUserProfile(userProfile);
        userCurriculumVitae.setCurriculumVitaeId(1L);
        prepareStaticClass();
        Mockito.when(userProfileService.findById(userProfile.getUserProfileId())).thenReturn(userProfile);
        Mockito.when(businessValidatorService.checkExistsUserProfile(1L)).thenReturn(userProfile);
        Mockito.when(userCurriculumVitaeService.findByUserProfile(userProfile)).thenReturn(userCurriculumVitae);
        Mockito.when(userAttributeService.findActiveByUserProfile(1L, 0, Constants.TOP_THREE_ATTRIBUTES)).thenReturn(new PageImpl<>(List.of()));
        BaseResp response = businessUserCvService.getUserCurriculumVitaeByUserProfileId(userProfile.getUserProfileId(), "en_US");
        UserCurriculumVitaeDTO userCvDTO = (UserCurriculumVitaeDTO) response.getData();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), response.getCode());
        Assert.assertEquals(userProfile.getUserProfileId(), userCvDTO.getUserProfile().getId());
        Assert.assertEquals(userCurriculumVitae.getCurriculumVitaeId(), userCvDTO.getUserProfile().getId());
    }

    private class DeleteDataAnswer implements Answer {
        @Override
        public Long answer(InvocationOnMock invocation) throws Throwable {
            return (Long) invocation.getArguments()[0];
        }
    }

    private static void prepareStaticClass(){
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePath(Mockito.anyObject())).thenReturn("avatarLink");
    }


}
