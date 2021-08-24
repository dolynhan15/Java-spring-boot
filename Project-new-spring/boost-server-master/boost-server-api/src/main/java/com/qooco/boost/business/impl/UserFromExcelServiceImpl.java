package com.qooco.boost.business.impl;

import com.qooco.boost.business.*;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.Gender;
import com.qooco.boost.data.enumeration.UploadType;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.enumeration.ColumnName;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.PataFileDTO;
import com.qooco.boost.models.dto.user.UserInfoDTO;
import com.qooco.boost.models.request.authorization.SignUpSystemReq;
import com.qooco.boost.models.user.UserCurriculumVitaeReq;
import com.qooco.boost.models.user.UserProfileReq;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

@Service
public class UserFromExcelServiceImpl implements UserFromExcelService {
    protected Logger logger = LogManager.getLogger(UserFromExcelServiceImpl.class);
    @Autowired
    private MediaService mediaService;
    @Autowired
    private POIService poiService;
    @Autowired
    private BusinessAuthorizationService businessAuthorizationService;
    @Autowired
    private BusinessUserService businessUserService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private BusinessUserCurriculumVitaeService businessUserCurriculumVitaeService;

    @Override
    public BaseResp uploadUserProfile(MultipartFile file, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        UserProfile isRootAdmin = userProfileService.checkUserProfileIsRootAdmin(user.getId());
        if (Objects.isNull(isRootAdmin)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }

        BaseResp result = mediaService.store(file, UploadType.EXCEL_FILE.toString(), authentication);
        PataFileDTO fileDTO = (PataFileDTO) result.getData();
        List<List<String>> accounts = null;
        if (Objects.nonNull(fileDTO)) {
            accounts = loadExcelAndCreateUser(fileDTO.getUrl(), authentication);
        }
        return new BaseResp(accounts);
    }

    private List<List<String>> loadExcelAndCreateUser(String filePath, Authentication authentication) {
        List<List<String>> result = new ArrayList<>();
        try {
            Map<Integer, List<Object>> data = poiService.readExcel(filePath);
            if (MapUtils.isNotEmpty(data)) {
                List<Account> accounts = new ArrayList<>();
                data.forEach((K, V) -> {
                    if (K > 0 && CollectionUtils.isNotEmpty(V)) {
                        try {
                            Account account = getAccount(V);
                            accounts.add(account);
                        } catch (Exception e) {
                        }
                    }
                });
                if (CollectionUtils.isNotEmpty(accounts)) {
                    accounts.forEach(acc -> {
                        List<String> accountResp = createUser(acc, authentication);
                        if (CollectionUtils.isNotEmpty(accountResp)) {
                            result.add(accountResp);
                        }
                    });
                }
            }
        } catch (IOException | InvalidFormatException e) {
            logger.info(e.getMessage());
        }
        return result;
    }

    private List<String> createUser(Account account, Authentication authentication) {
        List<String> result = new ArrayList<>();
        result.add(account.getSignUpSystem().getUsername());
        try {
            BaseResp resp = businessAuthorizationService.signUpByQoocoSystem(account.getSignUpSystem(), PROFILE_APP.appId());
            if (resp.getCode() != ResponseStatus.SUCCESS.getCode()) {
                UserProfile userProfile = userProfileService.findByUsername(account.getSignUpSystem().getUsername());
                if (Objects.nonNull(userProfile)) {
                    resp = new BaseResp(new UserInfoDTO(userProfile, ""));
                } else {
                    resp = businessAuthorizationService.loginWithSystemAccount(account.getSignUpSystem().getUsername(), account.getSignUpSystem().getPassword(), SELECT_APP.appId());
                }
            }

            if (resp.getCode() == ResponseStatus.SUCCESS.getCode()) {
                UserInfoDTO userInfoDTO = (UserInfoDTO) resp.getData();
                UserProfileReq userProfile = account.getUserProfile();
                userProfile.setId(userInfoDTO.getUserProfile().getId());
                result.add(userInfoDTO.getUserProfile().getId().toString());
                businessUserService.saveBasicUserProfile(userProfile, null);
                businessUserService.saveAdvancedUserProfile(userProfile, PROFILE_APP.appId(), "");

                UserCurriculumVitaeReq userCurriculumVitae = account.getVitae();
                userCurriculumVitae.setUserProfileId(userProfile.getId());
                businessUserCurriculumVitaeService.savePersonalInformation(userCurriculumVitae, authentication);
                businessUserCurriculumVitaeService.saveJobProfile(userCurriculumVitae, authentication);

            }
        } catch (Exception e) {
            result.add(e.getMessage());
            logger.info(e.getMessage());
        }
        return result;
    }

    private Account getAccount(List<Object> cells) {
        try {
            Account account = new Account();
            Arrays.stream(ColumnName.values()).forEach(f -> {
                String valueStr;
                Integer valueInt;
                Date valueDate;
                switch (f) {
                    case USERNAME:
                        valueStr = (String) cells.get(ColumnName.USERNAME.ordinal());
                        account.getSignUpSystem().setUsername(valueStr);
                        break;
                    case EMAIL:
                        valueStr = (String) cells.get(ColumnName.EMAIL.ordinal());
                        account.getSignUpSystem().setEmail(valueStr);
                        break;
                    case PASSWORD:
                        valueStr = (String) cells.get(ColumnName.PASSWORD.ordinal());
                        account.getSignUpSystem().setPassword(valueStr);
                        break;
                    case FIRST_NAME:
                        valueStr = (String) cells.get(ColumnName.FIRST_NAME.ordinal());
                        account.getUserProfile().setFirstName(valueStr);
                        break;
                    case LAST_NAME:
                        valueStr = (String) cells.get(ColumnName.LAST_NAME.ordinal());
                        account.getUserProfile().setLastName(valueStr);
                        break;
                    case GENDER:
                        valueInt = (Integer) cells.get(ColumnName.GENDER.ordinal());
                        account.getUserProfile().setGender(Objects.nonNull(valueInt) ? Gender.fromValue(valueInt) : null);
                        break;
                    case AVATAR:
                        valueStr = (String) cells.get(ColumnName.AVATAR.ordinal());
                        account.getUserProfile().setAvatar(ServletUriUtils.getAbsolutePath(valueStr));
                        break;
                    case BIRTHDAY:
                        valueDate = (Date) cells.get(ColumnName.BIRTHDAY.ordinal());
                        account.getUserProfile().setBirthday(valueDate);
                        break;
                    case PHONE_NUMBER:
                        valueStr = (String) cells.get(ColumnName.PHONE_NUMBER.ordinal());
                        account.getUserProfile().setPhoneNumber(valueStr);
                        break;
                    case ADDRESS:
                        valueStr = (String) cells.get(ColumnName.ADDRESS.ordinal());
                        account.getUserProfile().setAddress(valueStr);
                        break;
                    case COUNTRY_ID:
                        valueInt = (Integer) cells.get(ColumnName.COUNTRY_ID.ordinal());
                        account.getUserProfile().setCountry(Objects.nonNull(valueInt) ? valueInt.longValue() : null);
                        break;
                    case CITY_ID:
                        valueInt = (Integer) cells.get(ColumnName.CITY_ID.ordinal());
                        account.getUserProfile().setCityId(Objects.nonNull(valueInt) ? valueInt.longValue() : null);
                        break;
                    case PROFILE_STEP:
                        valueInt = (Integer) cells.get(ColumnName.PROFILE_STEP.ordinal());
                        account.getUserProfile().setProfileStep(Objects.nonNull(valueInt) ? valueInt : 0);
                        break;
                    case NATION_ID:
                        valueStr = (String) cells.get(ColumnName.NATION_ID.ordinal());
                        account.getUserProfile().setNationalId(valueStr);
                        account.getVitae().setNationalId(valueStr);
                        break;
                    case PERSONAL_PHOTOS:
                        valueStr = (String) cells.get(ColumnName.PERSONAL_PHOTOS.ordinal());
                        List<String> photos = ServletUriUtils.getAbsolutePaths(StringUtil.convertToList(valueStr));
                        account.getUserProfile().setPersonalPhotos(photos);
                        account.getVitae().setPersonalPhotos(photos);
                        break;
                    case NATIVE_LANGUAGE_IDS:
                        valueStr = (String) cells.get(ColumnName.NATIVE_LANGUAGE_IDS.ordinal());
                        account.getUserProfile().setNativeLanguageIds(covertToArrayLong(valueStr));
                        break;
                    case LANGUAGE_IDS:
                        valueStr = (String) cells.get(ColumnName.LANGUAGE_IDS.ordinal());
                        account.getUserProfile().setLanguageIds(covertToArrayLong(valueStr));
                        break;
                    case EDUCATION_ID:
                        valueInt = (Integer) cells.get(ColumnName.EDUCATION_ID.ordinal());
                        account.getUserProfile().setEducationId(valueInt);
                        account.getVitae().setEducationId(valueInt);
                        break;
                    case IS_HOUR_SALARY:
                        valueInt = (Integer) cells.get(ColumnName.IS_HOUR_SALARY.ordinal());
                        account.getVitae().setHourSalary(valueInt == 1);
                        break;
                    case IS_ASAP:
                        valueInt = (Integer) cells.get(ColumnName.IS_ASAP.ordinal());
                        account.getVitae().setAsap(valueInt == 1);
                        break;
                    case IS_FULL_TIME:
                        valueInt = (Integer) cells.get(ColumnName.IS_FULL_TIME.ordinal());
                        account.getVitae().setFullTime(valueInt == 1);
                        break;
                    case MIN_SALARY:
                        valueInt = (Integer) cells.get(ColumnName.MIN_SALARY.ordinal());
                        account.getVitae().setMinSalary(valueInt);
                        break;
                    case MAX_SALARY:
                        valueInt = (Integer) cells.get(ColumnName.MAX_SALARY.ordinal());
                        account.getVitae().setMaxSalary(valueInt);
                        break;
                    case EXPECTED_START_DATE:
                        Object value = cells.get(ColumnName.EXPECTED_START_DATE.ordinal());
                        if (value instanceof Date) {
                            account.getVitae().setExpectedStartDate((Date) value);
                        }
                        break;
                    case SOCIAL_LINKS:
                        valueStr = (String) cells.get(ColumnName.SOCIAL_LINKS.ordinal());
                        account.getVitae().setSocialLinks(StringUtil.convertToArray(valueStr));
                        break;
                    case CURRENCY_CODE:
                        valueStr = (String) cells.get(ColumnName.CURRENCY_CODE.ordinal());
                        account.getVitae().setCurrencyCode(valueStr);
                        break;
                    case WORK_HOUR_IDS:
                        valueStr = (String) cells.get(ColumnName.WORK_HOUR_IDS.ordinal());
                        account.getVitae().setWorkHourIds(covertToArrayLong(valueStr));
                        break;
                    case BENEFIT_IDS:
                        valueStr = (String) cells.get(ColumnName.BENEFIT_IDS.ordinal());
                        account.getVitae().setBenefitIds(covertToArrayLong(valueStr));
                        break;
                    case SOFT_SKILL_IDS:
                        valueStr = (String) cells.get(ColumnName.WORK_HOUR_IDS.ordinal());
                        account.getVitae().setSoftSkillIds(covertToArrayLong(valueStr));
                        break;
                    case JOB_IDS:
                        valueStr = (String) cells.get(ColumnName.JOB_IDS.ordinal());
                        account.getVitae().setJobIds(covertToArrayLong(valueStr));
                        break;
                }
            });

            return account;
        } catch (Exception e) {
            logger.info(e.getMessage());
        }
        return null;
    }

    private long[] covertToArrayLong(String ids) {
        String[] idArray = StringUtil.convertToArray(ids);
        if (ArrayUtils.isNotEmpty(idArray)) {
            return Arrays.stream(idArray).mapToLong(Long::valueOf).toArray();
        }
        return null;
    }
}

@Getter
class Account {
    private SignUpSystemReq signUpSystem;
    private UserProfileReq userProfile;
    private UserCurriculumVitaeReq vitae;

    public Account() {
        signUpSystem = new SignUpSystemReq();
        userProfile = new UserProfileReq();
        vitae = new UserCurriculumVitaeReq();
    }

}
