package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessUserCurriculumVitaeService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.FileStorageService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ProfileStep;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.assessment.PersonalAssessmentDTO;
import com.qooco.boost.models.dto.assessment.QualificationDTO;
import com.qooco.boost.models.dto.attribute.AttributeShortDTO;
import com.qooco.boost.models.dto.user.UserCVAttributeDTO;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.dto.user.UserPreviousPositionDTO;
import com.qooco.boost.models.user.*;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.*;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.AttributeEventType.EVT_COMPLETE_ALL_PROFILE_STEPS;
import static java.util.Optional.ofNullable;

@Service
public class BusinessUserCurriculumVitaeServiceImpl extends BusinessUserServiceAbstract implements BusinessUserCurriculumVitaeService {
    @Autowired
    private UserPreviousPositionService userPreviousPositionService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private EducationService educationService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private JobService jobService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private WorkingHourService workingHourService;
    @Autowired
    private CurriculumVitaeJobService curriculumVitaeJobService;
    @Autowired
    private UserDesiredHourService userDesiredHourService;
    @Autowired
    private SoftSkillsService softSkillsService;
    @Autowired
    private UserSoftSkillService userSoftSkillService;
    @Autowired
    private BenefitService benefitService;
    @Autowired
    private UserBenefitService userBenefitService;
    @Autowired
    private UserPreferredHotelService userPreferredHotelService;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private UserPersonalityService userPersonalityService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private UserAttributeService userAttributeService;
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int certificationPeriod;

    @Override
    public BaseResp saveUserPreviousPosition(UserPreviousPositionReq req, Authentication authentication) {
        doValidateUserPreviousPositionInput(req);
        Long usedId = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        UserPreviousPosition userPreviousPosition;
        if (Objects.nonNull(req.getId())) {
            userPreviousPosition = userPreviousPositionService.findById(req.getId());
            if (Objects.isNull(userPreviousPosition)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PREVIOUS_JOB);
            }
            userPreviousPosition = req.updateToPreviousPosition(userPreviousPosition);
        } else {
            userPreviousPosition = req.updateToPreviousPosition(new UserPreviousPosition());
            userPreviousPosition.setCreatedBy(usedId);
        }

        userPreviousPosition.setUpdatedBy(usedId);
        userPreviousPosition.setUpdatedDate(DateUtils.nowUtcForOracle());

        Currency currency = currencyService.findById(req.getCurrencyId());
        if (Objects.isNull(currency)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_CURRENCY);
        }

        userPreviousPosition = userPreviousPositionService.save(userPreviousPosition);
        userPreviousPosition.setCurrency(currency);
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(new UserProfile(usedId));
        if (Objects.nonNull(userCurriculumVitae)) {
            boostActorManager.saveUserCvInMongo(new UserCurriculumVitae(userCurriculumVitae));
        }
        UserPreviousPositionDTO previousPositionDTO = new UserPreviousPositionDTO(userPreviousPosition, getLocale(authentication));
        return new BaseResp<>(previousPositionDTO);
    }

    @Override
    public BaseResp getUserPreviousPositionById(Long id, Authentication authentication) {
        if (Objects.isNull(id)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        UserPreviousPosition result = userPreviousPositionService.findById(id);
        return new BaseResp<>(new UserPreviousPositionDTO(result, getLocale(authentication)));
    }

    @Override
    public BaseResp getUserPreviousPositionByUserProfileId(Long userProfileId, Authentication authentication) {
        if (Objects.isNull(userProfileId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        List<UserPreviousPosition> previousPositionDTOS = userPreviousPositionService.findByUserProfileId(userProfileId);
        List<UserPreviousPositionDTO> results = convertToUserPreviousPositionDTO(previousPositionDTOS, getLocale(authentication));
        return new BaseResp<>(results);
    }

    @Override
    public BaseResp deleteUserPreviousPositions(Long id) {
        if (Objects.isNull(id)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        UserPreviousPosition userPreviousPosition = userPreviousPositionService.findById(id);
        if (Objects.isNull(userPreviousPosition)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND);
        }
        boostActorManager.deleteFile(StringUtil.convertToList(userPreviousPosition.getPhoto()));
        userPreviousPositionService.deleteUserPreviousPositionById(id);
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(
                new UserProfile(userPreviousPosition.getCreatedBy()));
        if (Objects.nonNull(userCurriculumVitae)) {
            boostActorManager.saveUserCvInMongo(new UserCurriculumVitae(userCurriculumVitae));
        }
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }


    //TODO: Need to refactor this function and saveJobProfile
    @Override
    public BaseResp saveUserCurriculumVitae(UserCurriculumVitaeReq saveUserCvReq, Authentication authentication) {
        if (saveUserCvReq.getMinSalary() >= saveUserCvReq.getMaxSalary()
                || saveUserCvReq.getMaxSalary() <= 0 || saveUserCvReq.getMinSalary() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY_RANGE);
        }

        var lstJob = getJobs(saveUserCvReq);
        var lstHotel = getPreferredHotels(saveUserCvReq);

        if (saveUserCvReq.getCurrencyCode() == null) {
            throw new InvalidParamException(ResponseStatus.CURRENCY_CODE_IS_REQUIRED);
        }
        var currency = currencyService.findByCode(saveUserCvReq.getCurrencyCode());
        if (Objects.isNull(currency)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_CURRENCY);
        }
        Education education = null;
        if (saveUserCvReq.getEducationId() != null) {
            education = educationService.findById(saveUserCvReq.getEducationId());
            if (Objects.isNull(education)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_EDUCATION);
            }
        }
        var lstWorkingHourId = ListUtil.removeDuplicatesLongArray(saveUserCvReq.getWorkHourIds());
        if (ArrayUtils.isEmpty(lstWorkingHourId)) {
            throw new InvalidParamException(ResponseStatus.REQUIRED_WORKING_HOUR);
        }
        var lstWorkingHour = workingHourService.findByIds(lstWorkingHourId);
        if (CollectionUtils.isEmpty(lstWorkingHour) || lstWorkingHour.size() < lstWorkingHourId.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_WORKING_HOUR);
        }
        if (lstWorkingHour.stream().anyMatch(wh -> wh != null && wh.isWorkingType() != saveUserCvReq.isFullTime())) {
            throw new InvalidParamException(ResponseStatus.CONFLICT_WORKING_HOUR);
        }
        var lstBenefitId = ListUtil.removeDuplicatesLongArray(saveUserCvReq.getBenefitIds());
        var lstBenefit = benefitService.findByIds(lstBenefitId);
        if (lstBenefitId.length > 0 && lstBenefit != null && lstBenefit.size() < lstBenefitId.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_BENEFIT);
        }
        var lstSoftSkillId = ListUtil.removeDuplicatesLongArray(saveUserCvReq.getSoftSkillIds());
        var lstSoftSkill = softSkillsService.findByIds(lstSoftSkillId);
        if (lstSoftSkillId.length > 0 && lstSoftSkill != null && lstSoftSkill.size() < lstSoftSkillId.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_SOFT_SKILL);
        }

        var userProfile = userProfileService.findById(saveUserCvReq.getUserProfileId());
        if (userProfile == null) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }

        return saveUserCv(userProfile, saveUserCvReq, currency, education, lstJob, lstWorkingHour, lstSoftSkill, lstBenefit, lstHotel, getLocale(authentication));
    }

    @Override
    public BaseResp savePersonalInformation(UserCurriculumVitaeReq userCVReq, Authentication authentication) {
        var userProfile = businessValidatorService.checkExistsUserProfile(userCVReq.getUserProfileId());

        userProfile.setNationalId(Objects.nonNull(userCVReq.getNationalId()) ? userCVReq.getNationalId() : null);
        userProfile.setPersonalPhotos(StringUtil.convertToJson(ServletUriUtils.getRelativePaths(userCVReq.getPersonalPhotos())));

        var userCv = findUserCVByUserProfile(userProfile);
        userCv.setUserProfile(userProfile);

        var lstUserSoftSkill = saveSoftSkills(userCv, userCVReq.getSoftSkillIds());
        userCv.setUserSoftSkills(lstUserSoftSkill);
        String socialLinksJson = null;
        if (ArrayUtils.isNotEmpty(userCVReq.getSocialLinks())) {
            socialLinksJson = StringUtil.convertToJson(Arrays.asList(userCVReq.getSocialLinks()));
        }
        userCv.setSocialLinks(socialLinksJson);
        saveUserProfileStep(userProfile, userCVReq.getProfileStep());

        var userCvDTO = saveUserCurriculumVitae(userCv, getLocale(authentication));
        return new BaseResp<>(userCvDTO);
    }

    @Transactional
    protected BaseResp saveUserCv(UserProfile userProfile, UserCurriculumVitaeReq saveUserCvReq, Currency currency,
                                  Education education, List<Job> lstJob,
                                  List<WorkingHour> lstWorkingHour, List<SoftSkill> lstSoftSkill, List<Benefit> lstBenefit, List<Company> lstHotel, String locale) {

        var userCv = userCurriculumVitaeService.findByUserProfile(userProfile);
        var isUpdate = true;
        if (userCv == null) {
            isUpdate = false;
        }
        userCv = saveUserCvReq.toUserCurriculumVitae(userCv);
        userCv.setCurrency(currency);
        userCv.setEducation(education);
        userCv.setUserProfile(userProfile);
        userCv.setUpdatedBy(userProfile.getUserProfileId());

        if (isUpdate) {
            curriculumVitaeJobService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userDesiredHourService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userSoftSkillService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userBenefitService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userPreferredHotelService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
        }
        var lstCvJob = initNewCurriculumVitaeJobs(lstJob, userCv);
        userCv.setCurriculumVitaeJobs(lstCvJob);
        var lstUserDesiredHour = initNewUserWorkingHours(lstWorkingHour, userCv);
        userCv.setUserDesiredHours(lstUserDesiredHour);
        var lstUserSoftSkill = saveNewUserSoftSkill(lstSoftSkill, userCv);
        userCv.setUserSoftSkills(lstUserSoftSkill);
        var lstUserBenefit = initNewUserBenefits(lstBenefit, userCv);
        userCv.setUserBenefits(lstUserBenefit);
        var lstCvHotel = initNewUserPreferredHotels(lstHotel, userCv);
        userCv.setPreferredHotels(lstCvHotel);

        var userCvDTO = saveUserCurriculumVitae(userCv, locale);
        return new BaseResp<>(userCvDTO);
    }

    @Override
    public BaseResp getUserCurriculumVitaeByUserProfileId(long userProfileId, String locale) {
        UserProfile userProfile = businessValidatorService.checkExistsUserProfile(userProfileId);
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(userProfile);
        if (Objects.isNull(userCurriculumVitae)) {
            userCurriculumVitae = new UserCurriculumVitae(userProfile);
        }

        UserCVAttributeDTO userCvDTO = new UserCVAttributeDTO(userCurriculumVitae, locale);
        List<UserPreviousPositionDTO> previousPositionDTOS = getUserPreviousPositionDTOS(userProfileId, locale);
        userCvDTO.setUserPreviousPositions(previousPositionDTOS);

        Date submissionValidDate = DateUtils.addDays(DateUtils.atEndOfDate(DateUtils.nowUtcForOracle()), -certificationPeriod);
        List<UserQualification> qualifications = userQualificationService.findValidQualification(
                userProfileId, submissionValidDate);
        int qualificationNumber = qualifications.size();
        userCvDTO.setHasQualification(qualificationNumber > 0);
        List<QualificationDTO> qualificationDTOS = new ArrayList<>();
        qualifications.forEach(userQualification -> qualificationDTOS.add(new QualificationDTO(userQualification, certificationPeriod)));
        userCvDTO.setQualifications(qualificationDTOS);
        if (userCvDTO.isHasPersonality()) {
            List<PersonalAssessment> personalAssessments = userPersonalityService.findPersonalAssessmentByUserProfile(userProfileId);
            if (CollectionUtils.isNotEmpty(personalAssessments)) {
                List<PersonalAssessmentDTO> personalAssessmentDTOs = new ArrayList<>();
                personalAssessments.forEach(personalAssessment -> personalAssessmentDTOs.add(new PersonalAssessmentDTO(personalAssessment, locale)));
                userCvDTO.setPersonalAssessments(personalAssessmentDTOs);
            }
        }

        userCvDTO.setTopAttributes(userAttributeService.findActiveByUserProfile(userProfileId, 0, Constants.TOP_THREE_ATTRIBUTES).getContent()
                .stream().map(AttributeShortDTO::new).collect(toImmutableList()));
        userCvDTO.setAttribute(userAttributeService.countTotalAttributeAvailable(userProfileId));
        userCvDTO.setTotalAttribute(userAttributeService.countTotalAttributeAvailable());

        return new BaseResp<>(userCvDTO);
    }

    @Override
    public BaseResp deleteSocialLink(long userProfileId, String socialLink) {
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(new UserProfile(userProfileId));
        if (Objects.isNull(userCurriculumVitae)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_CURRICULUM_VITAE);
        }
        if (StringUtils.isBlank(userCurriculumVitae.getSocialLinks())) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_SOCIAL_LINK);
        }

        List<String> socialLinks = StringUtil.convertToList(userCurriculumVitae.getSocialLinks());
        if (StringUtils.isBlank(socialLink) || CollectionUtils.isEmpty(socialLinks)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_SOCIAL_LINK);
        }
        boolean checkDelete = socialLinks.remove(socialLink);
        if (!checkDelete) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_SOCIAL_LINK);
        }
        String finalSocialLink = null;
        if (CollectionUtils.isNotEmpty(socialLinks)) {
            finalSocialLink = StringUtil.convertToJson(socialLinks);
        }
        userCurriculumVitae.setSocialLinks(finalSocialLink);
        userCurriculumVitaeService.save(userCurriculumVitae);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    @Transactional
    public BaseResp saveJobProfile(UserCurriculumVitaeReq jobProfile, Authentication authentication) {
        var lstJob = getJobs(jobProfile);
        var lstHotel = getPreferredHotels(jobProfile);

        if (jobProfile.getMinSalary() == 0 && jobProfile.getMaxSalary() == 0) {
            throw new InvalidParamException(ResponseStatus.SALARY_RANGE_IS_REQUIRED);
        }
        if (jobProfile.getMinSalary() > jobProfile.getMaxSalary()
                || jobProfile.getMaxSalary() <= 0 || jobProfile.getMinSalary() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY_RANGE);
        }

        if (Objects.isNull(jobProfile.getCurrencyCode())) {
            throw new InvalidParamException(ResponseStatus.CURRENCY_CODE_IS_REQUIRED);
        }
        var currency = currencyService.findByCode(jobProfile.getCurrencyCode());
        if (Objects.isNull(currency)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_CURRENCY);
        }

        var lstWorkingHourId = ListUtil.removeDuplicatesLongArray(jobProfile.getWorkHourIds());
        List<WorkingHour> lstWorkingHour = new ArrayList<>();
        if (lstWorkingHourId.length > 0) {
            lstWorkingHour = workingHourService.findByIds(lstWorkingHourId);
            if (CollectionUtils.isEmpty(lstWorkingHour) || lstWorkingHour.size() < lstWorkingHourId.length) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_WORKING_HOUR);
            }
            if (lstWorkingHour.stream().anyMatch(wh -> wh != null && wh.isWorkingType() != jobProfile.isFullTime())) {
                throw new InvalidParamException(ResponseStatus.CONFLICT_WORKING_HOUR);
            }
        }

        var lstBenefitId = ListUtil.removeDuplicatesLongArray(jobProfile.getBenefitIds());
        var lstBenefit = benefitService.findByIds(lstBenefitId);
        if (lstBenefitId.length > 0 && CollectionUtils.isNotEmpty(lstBenefit) && lstBenefit.size() < lstBenefitId.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_BENEFIT);
        }

        if (!jobProfile.isAsap() && Objects.isNull(jobProfile.getExpectedStartDate())) {
            throw new InvalidParamException(ResponseStatus.EXPECTED_START_DATE_IS_REQUIRED);
        }

        var userProfile = userProfileService.findById(jobProfile.getUserProfileId());
        if (Objects.isNull(userProfile)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE);
        }

        return saveUserCvStep3(userProfile, jobProfile, currency, lstJob, lstWorkingHour, lstBenefit, lstHotel, getLocale(authentication));
    }

    private List<Company> getPreferredHotels(@NotNull UserCurriculumVitaeReq jobProfile) {
        var lstPreferredHotelId = ListUtil.removeDuplicatesLongArray(jobProfile.getPreferredHotelIds());
        List<Company> lstHotel = new ArrayList<>();
        if (ArrayUtils.isNotEmpty(lstPreferredHotelId)) {
            lstHotel = companyService.findByIdsAndStatus(lstPreferredHotelId, ApprovalStatus.APPROVED);
            if (CollectionUtils.isEmpty(lstHotel) && lstHotel.size() < lstPreferredHotelId.length) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_COMPANY);
            }
        }
        return lstHotel;
    }

    private List<Job> getJobs(@NotNull UserCurriculumVitaeReq jobProfile) {
        var lstJobId = ListUtil.removeDuplicatesLongArray(jobProfile.getJobIds());
        if (ArrayUtils.isEmpty(lstJobId)) {
            throw new InvalidParamException(ResponseStatus.REQUIRED_JOB);
        }
        var lstJob = jobService.findByIds(lstJobId);
        if (CollectionUtils.isEmpty(lstJob) || lstJob.size() < lstJobId.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_JOB);
        }
        return lstJob;
    }


    private BaseResp saveUserCvStep3(UserProfile userProfile, UserCurriculumVitaeReq saveUserCvReq, Currency currency, List<Job> lstJob,
                                     List<WorkingHour> lstWorkingHour, List<Benefit> lstBenefit, List<Company> lstHotel, String locale) {
        var userCv = userCurriculumVitaeService.findByUserProfile(userProfile);
        boolean isUpdate = true;
        if (userCv == null) {
            isUpdate = false;
        }
        userCv = saveUserCvReq.toUserCurriculumVitaeStepSaveJobProfile(userCv);
        userCv.setUserProfile(userProfile);
        userCv.setCurrency(currency);
        userCv.setUpdatedBy(userProfile.getUserProfileId());

        if (isUpdate) {
            curriculumVitaeJobService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userDesiredHourService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userBenefitService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
            userPreferredHotelService.deleteByUserCurriculumVitaeId(userCv.getCurriculumVitaeId());
        }
        var lstCvJob = initNewCurriculumVitaeJobs(lstJob, userCv);
        userCv.setCurriculumVitaeJobs(lstCvJob);
        var lstUserDesiredHour = initNewUserWorkingHours(lstWorkingHour, userCv);
        userCv.setUserDesiredHours(lstUserDesiredHour);
        var lstUserBenefit = initNewUserBenefits(lstBenefit, userCv);
        userCv.setUserBenefits(lstUserBenefit);
        var lstPreferredHotel = initNewUserPreferredHotels(lstHotel, userCv);
        userCv.setPreferredHotels(lstPreferredHotel);
        saveUserProfileStep(userProfile, saveUserCvReq.getProfileStep());

        var userCvDTO = saveUserCurriculumVitae(userCv, locale);
        return new BaseResp<>(userCvDTO);
    }

    private UserCurriculumVitaeDTO saveUserCurriculumVitae(UserCurriculumVitae userCv, String locale) {
        var result = userCurriculumVitaeService.save(userCv);
        var qualificationNumber = userQualificationService.countValidQualification(userCv.getUserProfile().getUserProfileId(),
                DateUtils.addDays(DateUtils.atEndOfDate(DateUtils.nowUtcForOracle()), -certificationPeriod));

        var userCvDTO = new UserCurriculumVitaeDTO(result, locale);
        var previousPositionDTOS = getUserPreviousPositionDTOS(userCv.getUserProfile().getUserProfileId(), locale);
        userCvDTO.setUserPreviousPositions(previousPositionDTOS);
        userCvDTO.setHasQualification(qualificationNumber > 0);
        boostActorManager.saveUserCvInMongo(new UserCurriculumVitae(result));
        return userCvDTO;
    }

    ///TODO: Need check with client to separate this become new data object. Not mix in UserCurriculumVitae
    private List<UserPreviousPositionDTO> getUserPreviousPositionDTOS(Long userProfileId, String locale) {
        List<UserPreviousPosition> userPreviousPositions = userPreviousPositionService.findByUserProfileId(userProfileId);
        return convertToUserPreviousPositionDTO(userPreviousPositions, locale);
    }

    private List<CurriculumVitaeJob> initNewCurriculumVitaeJobs(@NotNull List<Job> lstJob, UserCurriculumVitae userCv) {
        List<CurriculumVitaeJob> lstCvJob = new ArrayList<>();
        CurriculumVitaeJob curriculumVitaeJob;
        for (Job job : lstJob) {
            curriculumVitaeJob = new CurriculumVitaeJob();
            curriculumVitaeJob.setJob(job);
            curriculumVitaeJob.setUserCurriculumVitae(userCv);
            curriculumVitaeJob.setCreatedBy(userCv.getUserProfile().getUserProfileId());
            curriculumVitaeJob.setUpdatedBy(userCv.getUserProfile().getUserProfileId());
            lstCvJob.add(curriculumVitaeJob);
        }
        return lstCvJob;
    }

    private List<UserPreferredHotel> initNewUserPreferredHotels(@NotNull List<Company> lstCompany, UserCurriculumVitae userCv) {
        List<UserPreferredHotel> lstCvHotel = new ArrayList<>();
        UserPreferredHotel userPreferredHotel;
        for (Company hotel : lstCompany) {
            userPreferredHotel = new UserPreferredHotel();
            userPreferredHotel.setHotel(hotel);
            userPreferredHotel.setUserCurriculumVitae(userCv);
            userPreferredHotel.setCreatedBy(userCv.getUserProfile().getUserProfileId());
            userPreferredHotel.setUpdatedBy(userCv.getUserProfile().getUserProfileId());
            lstCvHotel.add(userPreferredHotel);
        }
        return lstCvHotel;
    }

    private List<UserDesiredHour> initNewUserWorkingHours(@NotNull List<WorkingHour> lstWorkingHour, UserCurriculumVitae userCv) {
        List<UserDesiredHour> lstUserDesiredHours = new ArrayList<>();
        UserDesiredHour userDesiredHour;
        for (WorkingHour workingHour : lstWorkingHour) {
            userDesiredHour = new UserDesiredHour();
            userDesiredHour.setWorkingHour(workingHour);
            userDesiredHour.setUserCurriculumVitae(userCv);
            userDesiredHour.setCreatedBy(userCv.getUserProfile().getUserProfileId());
            userDesiredHour.setUpdatedBy(userCv.getUserProfile().getUserProfileId());
            lstUserDesiredHours.add(userDesiredHour);
        }
        return lstUserDesiredHours;
    }

    private List<UserSoftSkill> saveNewUserSoftSkill(@NotNull List<SoftSkill> lstSoftSkill, UserCurriculumVitae userCv) {
        List<UserSoftSkill> lstUserSoftSkill = new ArrayList<>();
        UserSoftSkill userSoftSkill;
        for (SoftSkill softSkill : lstSoftSkill) {
            userSoftSkill = new UserSoftSkill();
            userSoftSkill.setSoftSkill(softSkill);
            userSoftSkill.setUserCurriculumVitae(userCv);
            userSoftSkill.setCreatedBy(userCv.getUserProfile().getUserProfileId());
            userSoftSkill.setUpdatedBy(userCv.getUserProfile().getUserProfileId());
            lstUserSoftSkill.add(userSoftSkill);
        }
        return lstUserSoftSkill;
    }

    private List<UserBenefit> initNewUserBenefits(@NotNull List<Benefit> lstBenefit, UserCurriculumVitae userCv) {
        List<UserBenefit> lstUserBenefit = new ArrayList<>();
        UserBenefit userBenefit;
        for (Benefit benefit : lstBenefit) {
            userBenefit = new UserBenefit();
            userBenefit.setBenefit(benefit);
            userBenefit.setUserCurriculumVitae(userCv);
            userBenefit.setCreatedBy(userCv.getUserProfile().getUserProfileId());
            userBenefit.setUpdatedBy(userCv.getUserProfile().getUserProfileId());
            lstUserBenefit.add(userBenefit);
        }
        return lstUserBenefit;
    }

    private List<UserPreviousPositionDTO> convertToUserPreviousPositionDTO(List<UserPreviousPosition> userPreviousPositions, String locale) {
        List<UserPreviousPositionDTO> results = null;
        if (CollectionUtils.isNotEmpty(userPreviousPositions)) {
            results = new ArrayList<>();
            for (UserPreviousPosition userPreviousPosition : userPreviousPositions) {
                results.add(new UserPreviousPositionDTO(userPreviousPosition, locale));
            }
        }
        return results;
    }

    private UserCurriculumVitae findUserCVByUserProfile(UserProfile userProfile) {
        UserCurriculumVitae userCv = userCurriculumVitaeService.findByUserProfile(userProfile);
        Date now = DateUtils.nowUtcForOracle();

        if (Objects.isNull(userCv)) {
            userCv = new UserCurriculumVitae();
            userCv.setCreatedDate(now);
            userCv.setCreatedBy(userProfile.getUserProfileId());
        }

        userCv.setUpdatedDate(now);
        userCv.setUpdatedBy(userProfile.getUserProfileId());
        return userCv;
    }

    private List<UserSoftSkill> saveSoftSkills(UserCurriculumVitae userCV, long[] softSkillIds) {
        long[] lstSoftSkillId = ListUtil.removeDuplicatesLongArray(softSkillIds);
        List<SoftSkill> lstSoftSkill = softSkillsService.findByIds(lstSoftSkillId);
        this.userSoftSkillService.deleteByUserCurriculumVitaeId(userCV.getCurriculumVitaeId());

        if (CollectionUtils.isNotEmpty(lstSoftSkill) && lstSoftSkillId.length > 0 && lstSoftSkill.size() < lstSoftSkillId.length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_SOFT_SKILL);
        }
        return saveNewUserSoftSkill(lstSoftSkill, userCV);
    }

    private UserProfile saveUserProfileStep(UserProfile userProfile, Integer profileStep) {
        if (Objects.nonNull(profileStep)) {
            if (profileStep > (ProfileStep.PREVIOUS_EXPERIENCE_STEP.value)) {
                businessProfileAttributeEventService.onAttributeEvent(EVT_COMPLETE_ALL_PROFILE_STEPS, userProfile.getUserProfileId());
            }
            trackingFillAllProfileFields(userProfile.getUserProfileId());
            userProfile.setProfileStep(profileStep);
            userProfile.setUpdatedDate(DateUtils.nowUtcForOracle());
            userProfile = userProfileService.save(userProfile);
        }
        return userProfile;
    }

    @Override
    public BaseResp saveProfileStep(Long userProfileId, UserProfileStep profileStep, Authentication authentication) {
        ofNullable(profileStep.getProfileStep()).orElseThrow(() -> new InvalidParamException(ResponseStatus.ID_IS_EMPTY));
        UserProfile userProfile = businessValidatorService.checkExistsUserProfile(userProfileId);
        userProfile = saveUserProfileStep(userProfile, profileStep.getProfileStep());
        var userCv = userCurriculumVitaeService.findByUserProfile(userProfile);
        var userCvDTO = ofNullable(userCv).map(it -> new UserCurriculumVitaeDTO(it, getLocale(authentication))).orElse(new UserCurriculumVitaeDTO(userProfile, getLocale(authentication)));
        return new BaseResp<>(userCvDTO);
    }

    @Override
    public BaseResp exportUserCvPdf(ExportUserCvPdfReq exportUserCvPdfReq, Authentication authentication) {
        UserCurriculumVitae userCurriculumVitae = businessValidatorService.checkExistsUserCurriculumVitae(exportUserCvPdfReq.getId());
        Vacancy vacancy = businessValidatorService.checkExistsValidVacancy(exportUserCvPdfReq.getVacancyId());
        Date submissionValidDate = DateUtils.addDays(DateUtils.atEndOfDate(DateUtils.nowUtcForOracle()), -certificationPeriod);
        List<UserQualification> qualifications = userQualificationService.findValidQualification(
                userCurriculumVitae.getUserProfile().getUserProfileId(), submissionValidDate);
        UserCvPrint userCvPrint = new UserCvPrint();
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        userCvPrint.setTimezone(user.getTimezone());
        userCvPrint.setUserCurriculumVitae(userCurriculumVitae);
        userCvPrint.setVacancy(vacancy);
        userCvPrint.setUserQualifications(qualifications);
        List<UserPreviousPosition> userPreviousPositions = userPreviousPositionService.findByUserProfileId(userCurriculumVitae.getUserProfile().getUserProfileId());
        userCvPrint.setUserPreviousPositions(userPreviousPositions);
        List<UserPersonality> userPersonalities = userPersonalityService.getByUserProfileId(userCurriculumVitae.getUserProfile().getUserProfileId());
        userCvPrint.setUserPersonalities(userPersonalities);
        List<UserAttribute> userAttributes = userAttributeService.findByUserProfile(
                userCurriculumVitae.getUserProfile().getUserProfileId(), 0, Constants.DEFAULT_ATTRIBUTE_ITEMS).getContent();
        userCvPrint.setUserAttributes(userAttributes);
        String fileName = fileStorageService.createPdfFile(userCvPrint, user.getId(), "user-cv");
        return new BaseResp<>(fileName);
    }

    private void doValidateUserPreviousPositionInput(UserPreviousPositionReq req) {
        if (StringUtils.isBlank(req.getCompanyName())) {
            throw new InvalidParamException(ResponseStatus.COMPANY_NAME_IS_REQUIRED);
        }

        if (StringUtils.isBlank(req.getPositionName())) {
            throw new InvalidParamException(ResponseStatus.POSITION_NAME_IS_REQUIRED);
        }

        if (Objects.isNull(req.getStartDate())) {
            throw new InvalidParamException(ResponseStatus.START_DATE_IS_REQUIRED);
        }

        if (DateUtils.toUtcForOracle(req.getStartDate()).compareTo(DateUtils.nowUtcForOracle()) > 0) {
            throw new InvalidParamException(ResponseStatus.START_DATE_AFTER_NOW);
        }

        if (Objects.nonNull(req.getEndDate())) {
            Date start = DateUtils.atStartOfDate(req.getStartDate());
            Date end = DateUtils.atStartOfDate(req.getEndDate());
            if (start.compareTo(end) > 0) {
                throw new InvalidParamException(ResponseStatus.START_DATE_AFTER_END_DATE);
            }
        }

        if (Objects.nonNull(req.getSalary()) && req.getSalary() <= 0) {
            throw new InvalidParamException(ResponseStatus.INVALID_SALARY);
        }

        if (Objects.isNull(req.getCurrencyId())) {
            throw new InvalidParamException(ResponseStatus.CURRENCY_ID_IS_NULL);
        }
    }
}
