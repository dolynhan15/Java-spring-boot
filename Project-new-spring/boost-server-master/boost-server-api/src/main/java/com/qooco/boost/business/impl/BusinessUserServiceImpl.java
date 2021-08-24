package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessUserService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.mongo.services.ViewProfileDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.user.CareerDTO;
import com.qooco.boost.models.dto.user.UserCurriculumVitaeDTO;
import com.qooco.boost.models.dto.user.UserPreviousPositionDTO;
import com.qooco.boost.models.dto.user.UserProfileDTO;
import com.qooco.boost.models.user.UserCompanyResp;
import com.qooco.boost.models.user.UserProfileReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static java.util.Optional.ofNullable;

@Service
public class BusinessUserServiceImpl extends BusinessUserServiceAbstract implements BusinessUserService {
    protected Logger logger = LogManager.getLogger(BusinessUserServiceImpl.class);
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private UserLanguageService userLanguageService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private EducationService educationService;
    @Autowired
    private UserPreviousPositionService userPreviousPositionService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private ViewProfileDocService viewProfileDocService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private UserAttributeService userAttributeService;

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int certificationPeriod;

    @Value(ApplicationConstant.INCREASE_BOOST_COIN)
    private boolean enableIncreaseBoostCoin;

    @Transactional
    @Override
    public BaseResp<UserProfileDTO> saveUserProfile(UserProfileReq userProfileReq, Authentication auth) {
        if (userProfileReq.getId() == null || userProfileReq.getCountry() == null) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        if (ArrayUtils.isEmpty(userProfileReq.getNativeLanguageIds())) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        Country country = businessValidatorService.checkExistsCountry(userProfileReq.getCountry());
        City city = businessValidatorService.checkExistsCity(userProfileReq.getCityId());

        userProfileReq.setNativeLanguageIds(ListUtil.removeDuplicatesLongArray(userProfileReq.getNativeLanguageIds()));
        userProfileReq.setLanguageIds(ListUtil.removeDuplicatesLongArray(userProfileReq.getLanguageIds()));
        userProfileReq.setLanguageIds(ListUtil.removeDuplicatesLongArray(userProfileReq.getNativeLanguageIds(), userProfileReq.getLanguageIds()));

        List<Language> languagesNatives = languageService.findByIds(userProfileReq.getNativeLanguageIds());
        if (CollectionUtils.isEmpty(languagesNatives) || languagesNatives.size() < userProfileReq.getNativeLanguageIds().length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LANGUAGE);
        }

        List<Language> languages = languageService.findByIds(userProfileReq.getLanguageIds());
        if ((CollectionUtils.isEmpty(languages) && userProfileReq.getLanguageIds().length > 0) || languages.size() < userProfileReq.getLanguageIds().length) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LANGUAGE);
        }

        UserProfile existedUserProfile = businessValidatorService.checkExistsUserProfile(userProfileReq.getId());
        existedUserProfile = userProfileReq.updateEntity(existedUserProfile, existedUserProfile.getUserProfileId());
        existedUserProfile.setCountry(country);
        existedUserProfile.setCity(city);
        userLanguageService.deleteUserLangByUserProfileId(existedUserProfile.getUserProfileId());

        List<UserLanguage> nativeLanguages = createUserLanguageFromLanguageIds(userProfileReq.getId(), languagesNatives, true);
        List<UserLanguage> foreignLanguages = createUserLanguageFromLanguageIds(userProfileReq.getId(), languages, false);
        if (CollectionUtils.isNotEmpty(nativeLanguages) && CollectionUtils.isNotEmpty(foreignLanguages)) {
            nativeLanguages.addAll(foreignLanguages);
        }
        existedUserProfile.setUserLanguageList(nativeLanguages);

        if (Objects.nonNull(userProfileReq.getProfileStep())) {
            existedUserProfile.setProfileStep(userProfileReq.getProfileStep());
        }

        return new BaseResp<>(saveUserProfile(existedUserProfile,  getLocale(auth)));
    }

    @Transactional
    @Override
    public BaseResp<UserProfileDTO> saveBasicUserProfile(UserProfileReq userProfileReq, Authentication auth) {
        UserProfile existedUserProfile = checkExistedUserProfile(userProfileReq);
        validateBasicUser(userProfileReq);
        if (StringUtils.isNotBlank(userProfileReq.getAvatar())) {
            existedUserProfile.setAvatar(ServletUriUtils.getRelativePath(userProfileReq.getAvatar()));
        }
        existedUserProfile.setGender(userProfileReq.getGender());
        existedUserProfile.setFirstName(userProfileReq.getFirstName());
        existedUserProfile.setLastName(userProfileReq.getLastName());
        existedUserProfile.setBirthday(DateUtils.toUtcForOracle(userProfileReq.getBirthday()));
        existedUserProfile.setUpdatedDate(DateUtils.nowUtcForOracle());

        if (Objects.nonNull(userProfileReq.getProfileStep()) && Objects.nonNull(auth)) {
            if (Objects.isNull(existedUserProfile.getProfileStep()) || existedUserProfile.getProfileStep() == 0)
                boostActorManager.saveBoostHelperMessageAfterFinishBasicProfileFirstTimeInMongo(auth, existedUserProfile);
            existedUserProfile.setProfileStep(userProfileReq.getProfileStep());
        }

        return new BaseResp<>(saveUserProfile(existedUserProfile, getLocale(auth)));
    }

    private UserProfileDTO saveUserProfile(UserProfile existedUserProfile, String locale) {
        UserProfile savedUserProfile = userProfileService.save(existedUserProfile);
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(savedUserProfile);
        if (Objects.nonNull(userCurriculumVitae)) {
            boostActorManager.saveUserCvInMongo(new UserCurriculumVitae(userCurriculumVitae));
        } else {
            boostActorManager.saveUserPersonalityInOracleAndMongo(new UserProfile(savedUserProfile));
        }
        trackingFillAllProfileFields(savedUserProfile.getUserProfileId());
        return (new UserProfileDTO(savedUserProfile, userCurriculumVitae, locale));
    }

    @Transactional
    @Override
    public BaseResp<UserProfileDTO> saveAdvancedUserProfile(UserProfileReq userProfileReq, String appId, String locale) {
        UserProfile existedUserProfile = checkExistedUserProfile(userProfileReq);
        boolean isCareerApp = appId.equals(PROFILE_APP.appId());

        if (Objects.isNull(userProfileReq.getNativeLanguageIds()) || (isCareerApp && 0 == userProfileReq.getNativeLanguageIds().length)) {
            throw new InvalidParamException(ResponseStatus.LANGUAGES_ARE_REQUIRED);
        }
        if (ArrayUtils.isNotEmpty(userProfileReq.getNativeLanguageIds())) {
            userProfileReq.setNativeLanguageIds(ListUtil.removeDuplicatesLongArray(userProfileReq.getNativeLanguageIds()));
            userProfileReq.setLanguageIds(ListUtil.removeDuplicatesLongArray(userProfileReq.getLanguageIds()));
            userProfileReq.setLanguageIds(ListUtil.removeDuplicatesLongArray(userProfileReq.getNativeLanguageIds(), userProfileReq.getLanguageIds()));

            List<Language> languagesNatives = languageService.findByIds(userProfileReq.getNativeLanguageIds());
            if (CollectionUtils.isEmpty(languagesNatives) || languagesNatives.size() < userProfileReq.getNativeLanguageIds().length) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LANGUAGE);
            }

            List<Language> languages = new ArrayList<>();
            if (isCareerApp) {
                languages = languageService.findByIds(userProfileReq.getLanguageIds());
                if ((CollectionUtils.isEmpty(languages) && userProfileReq.getLanguageIds().length > 0) || languages.size() < userProfileReq.getLanguageIds().length) {
                    throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LANGUAGE);
                }
            }

            userLanguageService.deleteUserLangByUserProfileId(existedUserProfile.getUserProfileId());

            List<UserLanguage> nativeLanguages = createUserLanguageFromLanguageIds(userProfileReq.getId(), languagesNatives, true);
            List<UserLanguage> foreignLanguages = createUserLanguageFromLanguageIds(userProfileReq.getId(), languages, false);
            if (CollectionUtils.isNotEmpty(nativeLanguages) && CollectionUtils.isNotEmpty(foreignLanguages)) {
                nativeLanguages.addAll(foreignLanguages);
            }
            existedUserProfile.setUserLanguageList(nativeLanguages);
        }

        if (isCareerApp) {
            City validCity = this.validateCity(userProfileReq.getCityId());
            existedUserProfile.setCity(validCity);
        } else {
            Country country = businessValidatorService.checkExistsCountry(userProfileReq.getCountry());
            existedUserProfile.setCountry(country);
            existedUserProfile.setAddress(userProfileReq.getAddress());
            existedUserProfile.setNationalId(Objects.nonNull(userProfileReq.getNationalId()) ? userProfileReq.getNationalId() : null);
            existedUserProfile.setPersonalPhotos(StringUtil.convertToJson(ServletUriUtils.getRelativePaths(userProfileReq.getPersonalPhotos())));
        }

        existedUserProfile.setPhoneNumber(userProfileReq.getPhoneNumber());
        existedUserProfile.setUpdatedDate(DateUtils.nowUtcForOracle());

        Education education = null;
        if (isCareerApp && Objects.nonNull(userProfileReq.getEducationId())) {
            education = educationService.findById(userProfileReq.getEducationId());
            if (Objects.isNull(education)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_EDUCATION);
            }
        }

        if (Objects.nonNull(userProfileReq.getProfileStep())) {
            existedUserProfile.setProfileStep(userProfileReq.getProfileStep());
        }

        UserProfile savedUserProfile = userProfileService.save(existedUserProfile);
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(new UserProfile(userProfileReq.getId()));
        if (Objects.isNull(userCurriculumVitae)) {
            userCurriculumVitae = new UserCurriculumVitae(savedUserProfile, education);
        } else {
            userCurriculumVitae.setEducation(education);
        }
        userCurriculumVitaeService.save(userCurriculumVitae);
        UserCurriculumVitae curriculumVitae = new UserCurriculumVitae(userCurriculumVitae);
        boostActorManager.saveUserCvInMongo(curriculumVitae);
        trackingFillAllProfileFields(savedUserProfile.getUserProfileId());
        return new BaseResp<>(new UserProfileDTO(savedUserProfile, curriculumVitae, locale));
    }

    private UserProfile checkExistedUserProfile(UserProfileReq userProfileReq) {
        ofNullable(userProfileReq.getId()).orElseThrow(() -> new InvalidParamException(ResponseStatus.ID_IS_EMPTY));
        return businessValidatorService.checkExistsUserProfile(userProfileReq.getId());
    }

    @Override
    public BaseResp findById(Long id) {
        UserProfile userProfile = userProfileService.findById(id);
        return new BaseResp<>(userProfile);
    }

    @Override
    public BaseResp getBasicProfile(Long userProfileId, Authentication authentication ) {
        UserCompanyResp userCompanyResp = new UserCompanyResp();
        ofNullable(userProfileId).orElseThrow(() -> new InvalidParamException(ResponseStatus.ID_IS_EMPTY));

        UserProfile existedUserProfile = userProfileService.findById(userProfileId);
        ofNullable(existedUserProfile).orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND));

        UserProfileDTO userProfileAfterConvert = new UserProfileDTO(existedUserProfile, getLocale(authentication));
        userCompanyResp.setUserProfile(userProfileAfterConvert);
        return new BaseResp<>(userCompanyResp);
    }

    private City validateCity(Long cityId) {
        ofNullable(cityId).orElseThrow(() -> new InvalidParamException(ResponseStatus.CITY_IS_REQUIRED));
        return businessValidatorService.checkExistsCity(cityId);
    }

    @Override
    public BaseResp getCareerInfo(Authentication auth, String timeZone) {
        UserProfile userProfile = userProfileService.findById(getUserId(auth));
        ofNullable(userProfile).orElseThrow(() -> new EntityNotFoundException(ResponseStatus.NOT_FOUND_USER_PROFILE));
        boostActorManager.saveBoostHelperMessageEventsInMongo(auth);
        UserCurriculumVitae userCurriculumVitae = userCurriculumVitaeService.findByUserProfile(userProfile);
        if (Objects.isNull(userCurriculumVitae)) {
            userCurriculumVitae = new UserCurriculumVitae();
            userCurriculumVitae.setUserProfile(userProfile);
        }

        UserCurriculumVitaeDTO userCv = new UserCurriculumVitaeDTO(userCurriculumVitae, getLocale(auth));
        List<UserPreviousPosition> previousPositions = userPreviousPositionService.findByUserProfileId(getUserId(auth));
        if (CollectionUtils.isNotEmpty(previousPositions)) {
            List<UserPreviousPositionDTO> results = new ArrayList<>();
            for (UserPreviousPosition userPreviousPosition : previousPositions) {
                results.add(new UserPreviousPositionDTO(userPreviousPosition, getLocale(auth)));
            }
            userCv.setUserPreviousPositions(results);
        }

        int qualificationNumber = userQualificationService.countValidQualification(getUserId(auth),
                DateUtils.addDays(DateUtils.atEndOfDate(DateUtils.nowUtcForOracle()), -certificationPeriod));
        userCv.setHasQualification(qualificationNumber > 0);

        LocalDateTime localDateTime = DateUtils.convertDateBetweenTimeZones(new Date(), timeZone);
        ofNullable(localDateTime).orElseThrow(() -> new InvalidParamException(ResponseStatus.TIME_ZONE_INVALID));

        LocalDateTime startWeekLocal = DateUtils.getStartDayOfWeek(localDateTime);
        long millisecondDayOfWeek = DateUtils.getMillisecond(startWeekLocal, ZoneId.systemDefault());
        Date startDateOfWeekInMongo = DateUtils.convertMillisecondToDate(millisecondDayOfWeek);
        long viewsPerWeek = viewProfileDocService.countViewProfileByCandidateIdAndTimestamp(getUserId(auth), startDateOfWeekInMongo);

        UserWallet userWallet = userWalletService.findById(getUserId(auth));
        AtomicInteger userCoins = new AtomicInteger();
        ofNullable(userWallet).ifPresent(it -> userCoins.set(userWallet.getBoostCoins()));

        CareerDTO careerDTO = CareerDTO.builder()
                .userCurriculumVitae(userCv)
                .totalViews((int) viewsPerWeek)
                .totalUnreadMessage(getTotalUnreadMessage(getUserId(auth), PROFILE_APP.value()))
                .totalOpportunities(0)
                .totalAppointment(0)
                .attribute(userAttributeService.countTotalAttributeAvailable(getUserId(auth)))
                .totalAttribute(userAttributeService.countTotalAttributeAvailable())
                .userCoins(userCoins.get())
                .build();
        return new BaseResp<>(careerDTO);
    }


    @Override
    public BaseResp increaseCoin(Long userProfileId) {
        if (!enableIncreaseBoostCoin) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        UserWallet userWallet = userWalletService.findById(userProfileId);
        int userCoins = 0;
        if (Objects.nonNull(userWallet)) {
            userCoins = userWallet.getBoostCoins() + Constants.DEFAULT_INSCREASE_COIN;
        } else {
            userCoins += Constants.DEFAULT_INSCREASE_COIN;
            userWallet = new UserWallet();
            userWallet.setOwner(userProfileId);
        }
        userWallet.setBoostCoins(userCoins);
        userWalletService.save(userWallet);
        return new BaseResp<>(userCoins);
    }


    @Override
    public BaseResp uploadPublicKey(AuthenticatedUser authenticatedUser, UserUploadKeyReq req) {
        logger.info("=====Start upload message: " + new Date());
        updatePublicKey(authenticatedUser, req);
        logger.info("=====End upload message: " + new Date() + "===============");
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private List<UserLanguage> createUserLanguageFromLanguageIds(Long userProfileId, List<Language> languages, boolean isNativeLang) {
        if (CollectionUtils.isNotEmpty(languages)) {
            List<UserLanguage> listUserLanguages = new ArrayList<>();
            for (Language language : languages) {
                UserLanguage userLanguage = new UserLanguage(isNativeLang, language, new UserProfile(userProfileId));
                listUserLanguages.add(userLanguage);
            }
            return listUserLanguages;
        }
        return null;
    }
}
