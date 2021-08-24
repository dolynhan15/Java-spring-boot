package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessVacancyService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.request.VacancyReq;
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
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletUriUtils.class})
@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
public class BusinessVacancyServiceImplTest extends BaseUserService {

    @InjectMocks
    private BusinessVacancyService businessVacancyService = new BusinessVacancyServiceImpl();
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);
    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);
    @Mock
    private JobService jobService = Mockito.mock(JobService.class);
    @Mock
    private CityService cityService = Mockito.mock(CityService.class);
    @Mock
    private StaffService staffService = Mockito.mock(StaffService.class);
    @Mock
    private EducationService educationService = Mockito.mock(EducationService.class);
    @Mock
    private CurrencyService currencyService = Mockito.mock(CurrencyService.class);
    @Mock
    private BenefitService benefitService = Mockito.mock(BenefitService.class);
    @Mock
    private LanguageService languageService = Mockito.mock(LanguageService.class);
    @Mock
    private SoftSkillsService softSkillsService = Mockito.mock(SoftSkillsService.class);
    @Mock
    private WorkingHourService workingHourService = Mockito.mock(WorkingHourService.class);
    @Mock
    private AssessmentLevelService assessmentLevelService = Mockito.mock(AssessmentLevelService.class);
    @Mock
    private VacancyBenefitService vacancyBenefitService = Mockito.mock(VacancyBenefitService.class);
    @Mock
    private VacancyLanguageService vacancyLanguageService = Mockito.mock(VacancyLanguageService.class);
    @Mock
    private VacancySoftSkillService vacancySoftSkillService = Mockito.mock(VacancySoftSkillService.class);
    @Mock
    private VacancyDesiredHourService vacancyDesiredHourService = Mockito.mock(VacancyDesiredHourService.class);
    @Mock
    private VacancyAssessmentLevelService vacancyAssessmentLevelService = Mockito.mock(VacancyAssessmentLevelService.class);
    @Mock
    private VacancyService vacancyService = Mockito.mock(VacancyService.class);
    @Mock
    private BusinessValidatorService businessValidatorService;

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getVacancy_whenVacancyIdNull_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        businessVacancyService.get(null, initAuthentication());
    }

    @Test
    public void getVacancy_whenVacancyNotExist_thenReturnEntityNotFoundException() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(vacancyService.findById(1L)).thenReturn(null);
        businessVacancyService.get(1L, initAuthentication());
    }

    @Test
    public void deleteVacancy_whenVacancyIdNull_thenReturnInvalidParamException() {
        thrown.expect(InvalidParamException.class);
        businessVacancyService.delete(null);
    }

    @Test
    public void deleteVacancy_whenVacancyExist_thenReturnSuccess() {
        Mockito.doNothing().when(vacancyService).delete(1L);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessVacancyService.delete(1L).getCode());
    }

    private void mockito(VacancyReq vacancyReq) {
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(companyService.findById(vacancyReq.getCompanyId())).thenReturn(new Company(vacancyReq.getCompanyId()));
        Mockito.when(jobService.findValidById(vacancyReq.getJobId())).thenReturn(new Job(vacancyReq.getJobId()));
        Mockito.when(cityService.findValidById(vacancyReq.getCityId())).thenReturn(new City(vacancyReq.getCityId()));
        Mockito.when(cityService.findValidById(vacancyReq.getSearchCityId())).thenReturn(new City(vacancyReq.getSearchCityId()));
        Mockito.when(staffService.findById(vacancyReq.getContactPersonId())).thenReturn(new Staff(vacancyReq.getContactPersonId()));
        Mockito.when(educationService.findById(vacancyReq.getEducationId())).thenReturn(new Education(vacancyReq.getEducationId()));
        Mockito.when(currencyService.findById(vacancyReq.getCurrencyId())).thenReturn(new Currency(vacancyReq.getCurrencyId()));
        Mockito.when(benefitService.findByIds(vacancyReq.getBenefitIds())).thenReturn(createBenefits(vacancyReq.getBenefitIds()));
        Mockito.when(softSkillsService.findByIds(vacancyReq.getSoftSkillIds())).thenReturn(createSoftSkill(vacancyReq.getSoftSkillIds()));
        Mockito.when(workingHourService.findByIds(vacancyReq.getWorkHourIds())).thenReturn(createWorkingHours(vacancyReq.getWorkHourIds(), vacancyReq.isFullTime()));
        Mockito.when(languageService.findByIds(vacancyReq.getLanguageIds())).thenReturn(createLanguages(vacancyReq.getLanguageIds()));
        Mockito.when(languageService.findByIds(vacancyReq.getNativeLanguageIds())).thenReturn(createLanguages(vacancyReq.getNativeLanguageIds()));
        Mockito.when(assessmentLevelService.findByIds(vacancyReq.getAssessmentLevelIds())).thenReturn(createAssessmentLevels(vacancyReq.getAssessmentLevelIds()));
        Mockito.when(assessmentLevelService.findByIds(vacancyReq.getAssessmentLevelIds())).thenReturn(createAssessmentLevels(vacancyReq.getAssessmentLevelIds()));
        Mockito.when(businessValidatorService.checkExistsCompany(vacancyReq.getCompanyId())).thenReturn(new Company(vacancyReq.getCompanyId()));


        Mockito.doNothing().when(vacancyLanguageService).deleteByVacancyId(Mockito.any());
        Mockito.doNothing().when(vacancyBenefitService).deleteByVacancyId(Mockito.any());
        Mockito.doNothing().when(vacancyDesiredHourService).deleteByVacancyId(Mockito.any());
        Mockito.doNothing().when(vacancySoftSkillService).deleteByVacancyId(Mockito.any());
        Mockito.doNothing().when(vacancyAssessmentLevelService).deleteByVacancyId(Mockito.any());

    }

    private void mockitoStatic() {
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getDomain()).thenReturn("");
    }

    private List<Benefit> createBenefits(long[] object) {
        List<Benefit> benefits = new ArrayList<>();
        for (long id : object) {
            benefits.add(new Benefit(id));
        }
        return benefits;
    }

    private List<SoftSkill> createSoftSkill(long[] object) {
        List<SoftSkill> softSkills = new ArrayList<>();
        for (long id : object) {
            softSkills.add(new SoftSkill(id));
        }
        return softSkills;
    }

    private List<WorkingHour> createWorkingHours(long[] object, boolean isFullTime) {
        List<WorkingHour> workingHours = new ArrayList<>();
        WorkingHour workingHour;
        for (long id : object) {
            workingHour = new WorkingHour(id);
            workingHour.setWorkingType(isFullTime);
            workingHours.add(workingHour);
        }
        return workingHours;
    }

    private List<Language> createLanguages(long[] object) {
        List<Language> languages = new ArrayList<>();
        for (long id : object) {
            languages.add(new Language(id));
        }
        return languages;
    }

    private List<AssessmentLevel> createAssessmentLevels(Long[] object) {
        List<AssessmentLevel> results = new ArrayList<>();
        Long i = 1L;
        for (long id : object) {
            AssessmentLevel assessmentLevel = createAssessmentLevel(id);
//            assessmentLevel.setAssessment(createAssessment(i, Arrays.asList(assessmentLevel)));
            results.add(assessmentLevel);
            i++;
        }
        return results;
    }

    private AssessmentLevel createAssessmentLevel(long id){
        AssessmentLevel assessmentLevel = new AssessmentLevel(id);
        assessmentLevel.setLevelName("" + id);
        return assessmentLevel;
    }

    private Assessment createAssessment(int id, List<AssessmentLevel> levels){
        Assessment assessment = new Assessment();
        assessment.setName("" + id);
        assessment.setNumberCompanyRequire(2);
        assessment.setPicture("http://google.com/image.png");
        assessment.setPrice(2D);
        return assessment;
    }

    private VacancyReq initVacancyReq() {
        VacancyReq vacancyReq = new VacancyReq();
        vacancyReq.setLogo("http://google.com/image.png");
        vacancyReq.setCompanyId(1L);
        vacancyReq.setJobId(1L);
        vacancyReq.setCityId(1L);
        vacancyReq.setSearchCityId(2L);
        vacancyReq.setContactPersonId(1L);
        vacancyReq.setEducationId(1L);
        vacancyReq.setNumberOfSeat(1);
        vacancyReq.setHourSalary(false);
        vacancyReq.setSalary(10);
        vacancyReq.setFullTime(true);
        vacancyReq.setAsap(true);
        vacancyReq.setCurrencyId(1L);
        vacancyReq.setExpectedStartDate(new Date());
        vacancyReq.setShortDescription("dsdsdsds");
        vacancyReq.setFullDescription("sdsldks dfdfsd fsdfsd");
        vacancyReq.setNativeLanguageIds(new long[]{1, 2});
        vacancyReq.setLanguageIds(new long[]{1, 2, 3});
        vacancyReq.setWorkHourIds(new long[]{1, 2, 3});
        vacancyReq.setBenefitIds(new long[]{1, 2, 3});
        vacancyReq.setSoftSkillIds(new long[]{1, 4});
        vacancyReq.setAssessmentLevelIds(new Long[]{1L, 4L});
        return vacancyReq;
    }
}
