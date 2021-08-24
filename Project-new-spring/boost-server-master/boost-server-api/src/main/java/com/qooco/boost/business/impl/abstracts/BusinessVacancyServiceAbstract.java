package com.qooco.boost.business.impl.abstracts;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.business.BusinessAppointmentService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.SearchRange;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.dto.vacancy.VacancyDTO;
import com.qooco.boost.models.request.EditVacancyRequest;
import com.qooco.boost.models.request.VacancyBaseReq;
import com.qooco.boost.models.request.VacancyV2Req;
import com.qooco.boost.models.sdo.LocationSDO;
import com.qooco.boost.models.sdo.VacancyClonedSDO;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import com.qooco.boost.utils.Validation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BusinessVacancyServiceAbstract {
    @Autowired
    protected VacancyService vacancyService;
    @Autowired
    protected BoostActorManager boostActorManager;
    @Autowired
    protected BusinessValidatorService validateService;

    @Autowired
    private BenefitService benefitService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private SoftSkillsService softSkillsService;
    @Autowired
    private WorkingHourService workingHourService;
    @Autowired
    private AssessmentLevelService assessmentLevelService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private BusinessAppointmentService businessAppointmentService;

    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    protected String qoocoDomainPath = "";

    protected Vacancy initVacancy(EditVacancyRequest vacancyReq, Authentication authentication) {
        validateEditVacancyRequest(vacancyReq);
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Vacancy vacancy = vacancyService.findByIdAndCompanyAndUser(vacancyReq.getId(), user.getCompanyId(), user.getId());
        if (Objects.isNull(vacancy)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS_VACANCY);
        }
        if (CollectionUtils.isNotEmpty(vacancy.getClosedCandidates()) && Objects.nonNull(vacancyReq.getNumberOfSeat())) {
            if (Const.Vacancy.Status.INACTIVE != vacancy.getStatus()) {
                if (vacancyReq.getNumberOfSeat() <= vacancy.getClosedCandidates().size()) {
                    throw new InvalidParamException(ResponseStatus.NUMBER_OF_SEAT_LESS_THAN_OR_EQUAL_NUMBER_OF_CLOSED_CANDIDATES);
                }
            }
        }
        Vacancy editedVacancy = new Vacancy(vacancy);
        Currency currency = validateService.checkExistsCurrency(vacancyReq.getCurrencyId());
        validateService.isValidSalaryRange(currency, vacancyReq.getSalary(), vacancyReq.getSalaryMax());
        List<AssessmentLevel> assessmentLevels = validateAssessmentLevels(vacancyReq.getAssessmentLevelIds());
        editedVacancy.setVacancyAssessmentLevels(createVacancyAssessmentLevels(editedVacancy, assessmentLevels, user.getId()));
        editedVacancy.setCurrency(currency);
        editedVacancy.setSalary(vacancyReq.getSalary());
        editedVacancy.setSalaryMax(vacancyReq.getSalaryMax());
        editedVacancy.setSearchRange(vacancyReq.getSearchRange());
        editedVacancy.setNumberOfSeat(vacancyReq.getNumberOfSeat());
        editedVacancy.setUpdatedDate(new Date());
        editedVacancy.setUpdatedBy(user.getId());
        return editedVacancy;
    }

    protected Vacancy prepareVacancyFromInput(Long id, VacancyV2Req req, Authentication authentication) {
        Long updateOwner = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        Company company = validateService.checkExistsCompany(req.getCompanyId());
        Job job = Objects.nonNull(req.getJobId()) ? validateService.checkExistsJob(req.getJobId()) : null;
        Staff contactPerson = Objects.nonNull(req.getContactPersonId()) ? validateService.checkExistsStaff(req.getContactPersonId()) : null;
        Education education = Objects.nonNull(req.getEducationId()) ? validateService.checkExistsEducation(req.getEducationId()) : null;
        Currency currency = Objects.nonNull(req.getCurrencyId()) ? validateService.checkExistsCurrency(req.getCurrencyId()) : null;
        List<Benefit> benefits = validateBenefits(req.getBenefitIds());
        List<SoftSkill> softSkills = validateSoftSkills(req.getSoftSkillIds());
        List<WorkingHour> workingHours = validateWorkingHours(req.getWorkHourIds(), req.isFullTime());
        List<AssessmentLevel> assessmentLevels = validateAssessmentLevels(req.getAssessmentLevelIds());
        List<Language> languages = validateLanguages(req.getLanguageIds());
        List<Language> languagesNative = validateLanguages(req.getNativeLanguageIds());
        List<Appointment> appointments = businessAppointmentService.validateAppointmentRequests(req.getAppointments(), company, authentication);
        List<Long> locationIds = new ArrayList<>();
        if (Objects.nonNull(req.getJobLocationId())) {
            locationIds.add(req.getJobLocationId());
        }
        if (Objects.nonNull(req.getSearchLocationId())) {
            locationIds.add(req.getSearchLocationId());
        }
        List<Location> locations = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(locationIds)) {
            locationIds = locationIds.stream().distinct().collect(Collectors.toList());
            locations = validateService.checkExistsLocations(req.getCompanyId(), locationIds);
        }

        Vacancy vacancy = new Vacancy(id, updateOwner);
        vacancy = req.updateEntity(vacancy, updateOwner);
        vacancy.setStatus(Const.Vacancy.Status.OPENING);
        vacancy.setSearchRange(SearchRange.CITY);
        vacancy.setCurrency(currency);
        vacancy.setVacancyBenefits(createVacancyBenefits(vacancy, benefits, updateOwner));
        vacancy.setVacancyDesiredHours(createDesiredHours(vacancy, workingHours, updateOwner));
        vacancy.setVacancySoftSkills(createSoftSkills(vacancy, softSkills, updateOwner));
        vacancy.setVacancyAssessmentLevels(createVacancyAssessmentLevels(vacancy, assessmentLevels, updateOwner));
        vacancy.setVacancyLanguages(createVacancyLanguages(vacancy, languages, languagesNative, updateOwner));
        vacancy.setCompany(company);
        vacancy.setJob(job);
        vacancy.setContactPerson(contactPerson);
        vacancy.setEducation(education);

        vacancy.setVacancyAppointments(createAppointments(vacancy, appointments, updateOwner));
        Optional<Location> jobLocationOptional = locations.stream().filter(l -> l.getLocationId().equals(req.getJobLocationId())).findFirst();
        vacancy.setJobLocation(jobLocationOptional.orElse(null));
        vacancy.setCity(null);
        if (Objects.nonNull(vacancy.getJobLocation())) {
            vacancy.setCity(vacancy.getJobLocation().getCity());
        }
        Optional<Location> searchLocationOptional = locations.stream().filter(l -> l.getLocationId().equals(req.getSearchLocationId())).findFirst();
        vacancy.setSearchLocation(searchLocationOptional.orElse(null));
        vacancy.setSearchCity(null);
        if (Objects.nonNull(vacancy.getSearchLocation())) {
            vacancy.setSearchCity(vacancy.getSearchLocation().getCity());
        }

        if (req.isAsap()) {
            vacancy.setExpectedStartDate(null);
        }
        return vacancy;
    }

    protected List<Location> getLocationForCity(Long companyId, List<Long> cityIds) {
        List<Location> locations = locationService.findByCompanyIdAndCityId(companyId, cityIds);
        if (CollectionUtils.isEmpty(locations) && locations.size() < cityIds.size()) {
            List<Long> cityOfLocations = locations.stream().map(l -> l.getCity().getCityId()).collect(Collectors.toList());
            List<Long> cityNotInLocations = cityIds.stream().filter(cityId -> !cityOfLocations.contains(cityId)).collect(Collectors.toList());
            List<LocationSDO> results = new ArrayList<>();
            cityNotInLocations.forEach(cityId -> results.add(new LocationSDO(cityId, companyId)));
            List<Location> locationNews = results.stream().distinct().map(this::convertToLocation).collect(Collectors.toList());
            locationNews.forEach(location -> location.setUsed(true));
            locationNews = locationService.save(locationNews);
            locations.addAll(locationNews);
        }
        return locations;
    }

    protected VacancyDTO saveClonedVacancy(Vacancy vacancy, String qoocoDomainPath, Long oldVacancyId, String locale) {
        vacancy = saveVacancy(vacancy);
        VacancyDTO resultDTO = new VacancyDTO(vacancy, qoocoDomainPath, locale);
        VacancyClonedSDO vacancyClonedSDO = new VacancyClonedSDO(new Vacancy(vacancy), oldVacancyId);
        boostActorManager.saveVacancyInMongo(vacancyClonedSDO);
        return resultDTO;
    }

    protected VacancyDTO saveVacancy(Vacancy vacancy, String qoocoDomainPath, String locale) {
        vacancy = saveVacancy(vacancy);
        VacancyDTO resultDTO = new VacancyDTO(vacancy, qoocoDomainPath, locale);
        boostActorManager.saveVacancyInMongo(new Vacancy(vacancy));
        return resultDTO;
    }

    protected void removeDuplicates(VacancyBaseReq vacancyReq) {
        vacancyReq.setBenefitIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getBenefitIds()));
        vacancyReq.setSoftSkillIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getSoftSkillIds()));
        vacancyReq.setWorkHourIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getWorkHourIds()));
        vacancyReq.setLanguageIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getLanguageIds()));
        vacancyReq.setNativeLanguageIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getNativeLanguageIds()));
        vacancyReq.setLanguageIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getNativeLanguageIds(), vacancyReq.getLanguageIds()));
        vacancyReq.setAssessmentLevelIds(ListUtil.removeDuplicatesLongArray(vacancyReq.getAssessmentLevelIds()));
    }

    protected void validateRequiredCommon(VacancyBaseReq vacancyReq) {
        if (Objects.isNull(vacancyReq.getCompanyId())
                || Objects.isNull(vacancyReq.getJobId())
                || Objects.isNull(vacancyReq.getNumberOfSeat())
                || Objects.isNull(vacancyReq.getContactPersonId())
                || Objects.isNull(vacancyReq.getCurrencyId())
        ) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        if (!Validation.validateHttpOrHttps(vacancyReq.getLogo())) {
            throw new InvalidParamException(ResponseStatus.HTTP_OR_HTTPS_WRONG_FORMAT);
        }

        if (StringUtils.isBlank(vacancyReq.getShortDescription())) {
            throw new InvalidParamException(ResponseStatus.SHORT_DESCRIPTION_IS_EMPTY);
        }
    }

    protected void validateRequired(VacancyBaseReq vacancyReq) {
        validateRequiredCommon(vacancyReq);
        if (Objects.isNull(vacancyReq.getCityId())
                || Objects.isNull(vacancyReq.getSearchCityId())
                || 0 == vacancyReq.getLanguageIds().length
                || 0 == vacancyReq.getNativeLanguageIds().length
                || Objects.isNull(vacancyReq.getEducationId())
                || 0 == vacancyReq.getWorkHourIds().length
        ) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
    }

    protected void validateBusinessLogicV2(VacancyBaseReq vacancyReq) {
        validateBusinessLogicCommon(vacancyReq);
        if (vacancyReq.getSalaryMax() < vacancyReq.getSalary()) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY_RANGE);
        }
    }

    protected void validateBusinessLogic(VacancyBaseReq vacancyReq) {
        validateBusinessLogicCommon(vacancyReq);
    }

    private void validateBusinessLogicCommon(VacancyBaseReq vacancyReq) {
        if (vacancyReq.getNumberOfSeat() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_NUMBER_OF_SEAT);
        }

        if (!vacancyReq.isAsap()
                && (Objects.isNull(vacancyReq.getExpectedStartDate())
                || (!DateUtils.isStartOfCurrentDate(vacancyReq.getExpectedStartDate())))) {
            throw new InvalidParamException(ResponseStatus.EXPECTED_START_DATE_IS_NULL);
        }

        if (vacancyReq.getSalary() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY);
        }
    }

    private List<Language> validateLanguages(long[] languageIds) {
        if (ArrayUtils.isEmpty(languageIds)) return new ArrayList<>();
        List<Language> languages = languageService.findByIds(languageIds);
        if (CollectionUtils.isEmpty(languages)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LANGUAGE);
        }
        return languages;
    }

    private List<AssessmentLevel> validateAssessmentLevels(Long[] ids) {
        if (ArrayUtils.isEmpty(ids)) return new ArrayList<>();
        List<AssessmentLevel> assessmentLevels = assessmentLevelService.findByIds(ids);
        if (CollectionUtils.isEmpty(assessmentLevels) || assessmentLevels.size() < ids.length) {
            throw new EntityNotFoundException(ResponseStatus.ASSESSMENT_ID_NOT_EXIST);
        }
        return assessmentLevels;
    }

    private List<WorkingHour> validateWorkingHours(long[] ids, boolean isFullTime) {
        if (ArrayUtils.isEmpty(ids)) return new ArrayList<>();
        List<WorkingHour> workingHours = workingHourService.findByIds(ids);
        if (CollectionUtils.isEmpty(workingHours) || workingHours.size() < ids.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_WORKING_HOUR);
        }

        if (workingHours.stream().anyMatch(wh -> wh != null && wh.isWorkingType() != isFullTime)) {
            throw new InvalidParamException(ResponseStatus.CONFLICT_WORKING_HOUR);
        }
        return workingHours;
    }

    private List<SoftSkill> validateSoftSkills(long[] ids) {
        if (ArrayUtils.isEmpty(ids)) return new ArrayList<>();
        List<SoftSkill> softSkills = softSkillsService.findByIds(ids);
        if ((CollectionUtils.isEmpty(softSkills) && ids.length > 0) || softSkills.size() < ids.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_SOFT_SKILL);
        }
        return softSkills;
    }

    private List<Benefit> validateBenefits(long[] ids) {
        if (ArrayUtils.isEmpty(ids)) return new ArrayList<>();
        List<Benefit> benefits = benefitService.findByIds(ids);
        if ((CollectionUtils.isEmpty(benefits) && ids.length > 0) || benefits.size() < ids.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_BENEFIT);
        }
        return benefits;
    }

    private List<VacancyBenefit> createVacancyBenefits(Vacancy vacancy, List<Benefit> benefits, Long updateOwner) {
        List<VacancyBenefit> vacancyBenefits = new ArrayList<>();
        for (Benefit benefit : benefits) {
            vacancyBenefits.add(new VacancyBenefit(benefit, vacancy, updateOwner));
        }
        return vacancyBenefits;
    }

    private List<VacancySoftSkill> createSoftSkills(Vacancy vacancy, List<SoftSkill> softSkills, Long updateOwner) {
        List<VacancySoftSkill> vacancySoftSkills = new ArrayList<>();
        for (SoftSkill softSkill : softSkills) {
            vacancySoftSkills.add(new VacancySoftSkill(softSkill, vacancy, updateOwner));
        }
        return vacancySoftSkills;
    }

    private List<VacancyDesiredHour> createDesiredHours(Vacancy vacancy, List<WorkingHour> workingHours, Long updateOwner) {
        List<VacancyDesiredHour> vacancyDesiredHours = new ArrayList<>();
        for (WorkingHour workingHour : workingHours) {
            vacancyDesiredHours.add(new VacancyDesiredHour(workingHour, vacancy, updateOwner));
        }
        return vacancyDesiredHours;
    }

    private List<VacancyLanguage> createVacancyLanguages(Vacancy vacancy, List<Language> languages, List<Language> languagesNative, Long updateOwner) {
        List<VacancyLanguage> vacancyLanguages = new ArrayList<>();
        for (Language language : languages) {
            vacancyLanguages.add(new VacancyLanguage(false, language, vacancy, updateOwner));
        }

        for (Language language : languagesNative) {
            vacancyLanguages.add(new VacancyLanguage(true, language, vacancy, updateOwner));
        }

        return vacancyLanguages;
    }

    private List<Appointment> createAppointments(Vacancy vacancy, List<Appointment> appointments, Long updateOwner) {
        List<Appointment> results = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(appointments)) {
            for (Appointment appointment : appointments) {
                appointment.setVacancy(vacancy);
                appointment.setUpdatedDate(DateUtils.nowUtcForOracle());
                appointment.setUpdatedBy(updateOwner);
                results.add(appointment);
            }
        }
        return results;
    }

    private List<VacancyAssessmentLevel> createVacancyAssessmentLevels(Vacancy vacancy,
                                                                       List<AssessmentLevel> assessmentLevels,
                                                                       Long updateOwner) {
        List<VacancyAssessmentLevel> vacancyAssessmentLevels = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(assessmentLevels)) {
            vacancyAssessmentLevels = assessmentLevels.stream()
                    .map(al -> new VacancyAssessmentLevel(al, vacancy, updateOwner))
                    .collect(Collectors.toList());
        }
        return vacancyAssessmentLevels;
    }

    private Vacancy saveVacancy(Vacancy vacancy) {
        vacancy = vacancyService.save(vacancy);
        List<Location> locations = new ArrayList<>();
        vacancy.getJobLocation().setUsed(true);
        vacancy.getSearchLocation().setUsed(true);
        locations.add(vacancy.getJobLocation());
        locations.add(vacancy.getSearchLocation());
        if (CollectionUtils.isNotEmpty(vacancy.getVacancyAppointments())) {
            vacancy.getVacancyAppointments().forEach(appointment -> {
                if (Objects.nonNull(appointment.getLocation())) {
                    appointment.getLocation().setUsed(true);
                    locations.add(appointment.getLocation());
                }
            });
        }
        locationService.save(locations);
        return vacancy;
    }

    private void validateEditVacancyRequest(EditVacancyRequest editVacancyRequest) {
        editVacancyRequest.setAssessmentLevelIds(ListUtil.removeDuplicatesLongArray(editVacancyRequest.getAssessmentLevelIds()));
        if (Objects.isNull(editVacancyRequest.getCurrencyId())) {
            throw new InvalidParamException(ResponseStatus.CURRENCY_ID_IS_NULL);
        }
        if (editVacancyRequest.getNumberOfSeat() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_NUMBER_OF_SEAT);
        }

        if (editVacancyRequest.getSalary() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY);
        }
        if (editVacancyRequest.getSalaryMax() < editVacancyRequest.getSalary()) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY_RANGE);
        }

        var searchRange = ImmutableList.of(SearchRange.CITY, SearchRange.PROVINCE, SearchRange.COUNTRY, SearchRange.WORLD);
        if (!searchRange.contains(editVacancyRequest.getSearchRange())) {
            throw new InvalidParamException(ResponseStatus.INVALID_VACANCY_SEARCH_RANGE);
        }
    }

    private Location convertToLocation(LocationSDO locationSDO) {
        Location location = new Location();
        location.setCity(new City(locationSDO.getCityId()));
        location.setCompany(new Company(locationSDO.getCompanyId()));
        return location;
    }
}
