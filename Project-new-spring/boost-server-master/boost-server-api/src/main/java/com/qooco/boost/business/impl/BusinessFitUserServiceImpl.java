package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessFitUserService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.constants.CompanyWorkedType;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.enumeration.BoostApplication;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.company.CompanyBaseDTO;
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.company.CurrentCompanyDTO;
import com.qooco.boost.models.dto.company.WaitingCompanyDTO;
import com.qooco.boost.models.dto.user.RecruiterDTO;
import com.qooco.boost.models.dto.user.UserFitDTO;
import com.qooco.boost.models.transfer.BoostHelperFitTransfer;
import com.qooco.boost.models.user.FitUserReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ListUtil;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.qooco.boost.enumeration.BoostHelperEventType.SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME;

@Service
public class BusinessFitUserServiceImpl extends BusinessUserServiceAbstract implements BusinessFitUserService {

    @Autowired
    private UserFitService userFitService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private UserFitLanguageService userFitLanguageService;
    @Autowired
    private LanguageService languageService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private CompanyJoinRequestService companyJoinRequestService;
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private AppointmentDetailService appointmentDetailService;

    @Transactional
    @Override
    public BaseResp saveBasicFitUser(FitUserReq fitUserReq, Authentication authentication) {
        UserFit existedFitUser = checkExistedFitUser(fitUserReq);
        if (Objects.isNull(existedFitUser)) {
            existedFitUser = new UserFit(fitUserReq.getId());
            UserProfile userProfile = userProfileService.findById(fitUserReq.getId());
            if (Objects.nonNull(userProfile)) {
                existedFitUser.setUsername(userProfile.getUsername());
                existedFitUser.setEmail(userProfile.getEmail());
            }
        }
        validateBasicUser(fitUserReq);
        var oldFirstName = existedFitUser.getFirstName();

        if (StringUtils.isNotBlank(fitUserReq.getAvatar())) {
            existedFitUser.setAvatar(ServletUriUtils.getRelativePath(fitUserReq.getAvatar()));
        }
        existedFitUser.setGender(fitUserReq.getGender());
        existedFitUser.setFirstName(fitUserReq.getFirstName());
        existedFitUser.setLastName(fitUserReq.getLastName());
        existedFitUser.setBirthday(DateUtils.toUtcForOracle(fitUserReq.getBirthday()));
        existedFitUser.setProfileStep(Objects.nonNull(fitUserReq.getProfileStep()) ? fitUserReq.getProfileStep() : existedFitUser.getProfileStep());
        if (Objects.isNull(oldFirstName))
            boostActorManager.saveBoostHelperMessageAfterFinishBasicProfileFirstTimeInMongo(authentication, BoostHelperFitTransfer.builder().userFit(existedFitUser).eventType(SELECT_FINISH_BASIC_PROFILE_RETURN_HOME_FIRST_TIME).build());
        return saveFitUser(existedFitUser, getLocale(authentication));
    }

    @Transactional
    @Override
    public BaseResp saveAdvancedFitUser(FitUserReq fitUserReq, Authentication authentication) {
        UserFit existedFitUser = businessValidatorService.checkExistsUserFit(fitUserReq.getId());

        List<UserFitLanguage> languages = null;
        if (ArrayUtils.isNotEmpty(fitUserReq.getNativeLanguageIds())) {
            fitUserReq.setNativeLanguageIds(ListUtil.removeDuplicatesLongArray(fitUserReq.getNativeLanguageIds()));
            List<Language> languagesNatives = languageService.findByIds(fitUserReq.getNativeLanguageIds());
            if (CollectionUtils.isEmpty(languagesNatives) || languagesNatives.size() < fitUserReq.getNativeLanguageIds().length) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LANGUAGE);
            }
            languages = createUserFitLanguages(fitUserReq.getId(), languagesNatives, true);
        }
        userFitLanguageService.deleteUserLangByUserId(existedFitUser.getUserProfileId());
        existedFitUser.setUserFitLanguages(languages);

        Country country = businessValidatorService.checkExistsCountry(fitUserReq.getCountryId());
        existedFitUser.setCountry(country);
        existedFitUser.setPhoneNumber(fitUserReq.getPhoneNumber());
        existedFitUser.setAddress(fitUserReq.getAddress());
        existedFitUser.setNationalId(Objects.nonNull(fitUserReq.getNationalId()) ? fitUserReq.getNationalId() : null);
        existedFitUser.setPersonalPhotos(StringUtil.convertToJson(ServletUriUtils.getRelativePaths(fitUserReq.getPersonalPhotos())));
        existedFitUser.setProfileStep(Objects.nonNull(fitUserReq.getProfileStep()) ? fitUserReq.getProfileStep() : existedFitUser.getProfileStep());
        return saveFitUser(existedFitUser, getLocale(authentication));
    }

    private BaseResp saveFitUser(UserFit existedFitUser, String locale) {
        UserFit savedFitUserDoc = userFitService.save(existedFitUser);
        boostActorManager.saveUserCvInMongo(savedFitUserDoc);
        return new BaseResp<>(new UserFitDTO(savedFitUserDoc, locale));
    }

    private UserFit checkExistedFitUser(FitUserReq fitUserReq) {
        if (Objects.isNull(fitUserReq.getId())) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        return userFitService.findById(fitUserReq.getId());
    }

    @Override
    public BaseResp getRecruiterInfo(Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = user.getId();
        UserFit fitUser = businessValidatorService.checkExistsUserFit(userId);
        if (Objects.nonNull(fitUser.getFirstName())) {
            var fitTransfer = BoostHelperFitTransfer.builder().userFit(new UserFit(fitUser)).build();
            boostActorManager.saveBoostHelperMessageForOldUserNotHaveConversation(authentication, fitTransfer);
        }

        List<Staff> staffs = staffService.findByUserProfileAndStatus(userId, ApprovalStatus.APPROVED);
        Page<Company> pendingCompanyPage = companyService.findLastPendingCompanyCreatedByUser(userId, 0, Integer.MAX_VALUE);
        Page<CompanyJoinRequest> joinRequestCompanyPage = companyJoinRequestService.findLastRequest(userId, 0, Integer.MAX_VALUE);

        CurrentCompanyDTO currentCompanyDTO = createCurrentCompany(user.getCompanyId(), fitUser, staffs, pendingCompanyPage.getContent(), joinRequestCompanyPage.getContent(), getLocale(authentication));

        //TODO: Will remove in next time
        Company company = null;
        if (CollectionUtils.isNotEmpty(staffs)) {
            company = Objects.isNull(fitUser.getDefaultCompany()) || Objects.isNull(fitUser.getDefaultCompany().getCompanyId())
                    ? staffs.get(0).getCompany() : fitUser.getDefaultCompany();
        }

        Company pendingCompany = null;
        CompanyJoinRequest companyJoinRequest = null;
        if (CollectionUtils.isNotEmpty(joinRequestCompanyPage.getContent())) {
            companyJoinRequest = joinRequestCompanyPage.getContent().get(0);
        }

        if (CollectionUtils.isNotEmpty(pendingCompanyPage.getContent())) {
            pendingCompany = pendingCompanyPage.getContent().get(0);
        }

        WaitingCompanyDTO waitingCompanyDTO = null;
        if (Objects.nonNull(companyJoinRequest) || Objects.nonNull(pendingCompany)) {
            if (Objects.nonNull(companyJoinRequest) && Objects.nonNull(pendingCompany)) {
                if (companyJoinRequest.getCreatedDate().after(pendingCompany.getCreatedDate())) {
                    waitingCompanyDTO = createCompanyJoinRequestWaiting(companyJoinRequest.getCompany());
                } else {
                    waitingCompanyDTO = createCompanyAuthorizedWaiting(pendingCompany);
                }
            } else if (Objects.nonNull(companyJoinRequest)) {
                waitingCompanyDTO = createCompanyJoinRequestWaiting(companyJoinRequest.getCompany());
            } else {
                waitingCompanyDTO = createCompanyAuthorizedWaiting(pendingCompany);
            }
        }
        //End code will remove

        int countVacancy = 0;
        int totalAppointment = 0;
        if (Objects.nonNull(currentCompanyDTO)) {
            countVacancy = vacancyService.countOpenVacancyByUserAndCompany(userId, currentCompanyDTO.getCompany().getId());
        }

        if (Objects.nonNull(company) && CollectionUtils.isNotEmpty(staffs)) {
            List<String> isRoleName = new ArrayList<>();
            CompanyDTO finalCompanyDTO = currentCompanyDTO.getCompany();
            staffs.stream().filter(s -> s.getCompany().getCompanyId().equals(finalCompanyDTO.getId())).forEach(s -> isRoleName.add(s.getRole().getName()));

            if (CollectionUtils.isNotEmpty(isRoleName)) {
                List<Long> roleIds = CompanyRole.valueOf(isRoleName.get(0)).getRolesSmallerOrAdminRole();
                if (CollectionUtils.isNotEmpty(roleIds)) {
                    totalAppointment = appointmentDetailService.countAvailableByUserProfileIdAndCompanyIdAndStatusesAndRoles(userId, finalCompanyDTO.getId(), AppointmentStatus.getAcceptedStatus(), roleIds);
                }

            } else {
                totalAppointment = appointmentDetailService.countAvailableByUserProfileIdAndCompanyIdAndStatuses(userId, finalCompanyDTO.getId(), AppointmentStatus.getAcceptedStatus());
            }
        }

        RecruiterDTO recruiterDTO = new RecruiterDTO(
                new UserFitDTO(fitUser, getLocale(authentication)),
                Objects.nonNull(company) ? new CompanyDTO(company, getLocale(authentication)) : null,
                waitingCompanyDTO,
                countVacancy,
                getTotalUnreadMessage(getUserId(authentication), BoostApplication.SELECT_APP.value()),
                0,
                totalAppointment);
        recruiterDTO.setCurrentCompany(currentCompanyDTO);
        return new BaseResp<>(recruiterDTO);
    }

    private CurrentCompanyDTO createCurrentCompany(Long companyFromToken, UserFit fitUser,
                                                   List<Staff> staffs, List<Company> pendingCompanies,
                                                   List<CompanyJoinRequest> companyJoinRequests,
                                                   String locale) {

        CurrentCompanyDTO currentCompanyDTO = null;

        if (Objects.nonNull(companyFromToken)) {
            Company company = companyService.findById(companyFromToken);
            if (Objects.nonNull(company)) {
                currentCompanyDTO = getCurrentCompanyDTOWithDefaultCompany(fitUser.getUserProfileId(), staffs, pendingCompanies, companyJoinRequests, company, locale);
            }
        }

        if (Objects.isNull(currentCompanyDTO) && Objects.nonNull(fitUser.getDefaultCompany())) {
            currentCompanyDTO = getCurrentCompanyDTOWithDefaultCompany(fitUser.getUserProfileId(), staffs, pendingCompanies, companyJoinRequests, fitUser.getDefaultCompany(), locale);
        }

        if (Objects.isNull(currentCompanyDTO)) {
            currentCompanyDTO = getCurrentCompanyDTOWithoutDefaultCompany(fitUser.getUserProfileId(), staffs, pendingCompanies, companyJoinRequests, locale);
        }

        List<Company> companies = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(staffs)) {
            companies.addAll(staffs.stream().map(Staff::getCompany).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(pendingCompanies)) {
            companies.addAll(pendingCompanies);
        }

        if (CollectionUtils.isNotEmpty(companyJoinRequests)) {
            companies.addAll(companyJoinRequests.stream().map(CompanyJoinRequest::getCompany).collect(Collectors.toList()));
        }

        if (Objects.nonNull(currentCompanyDTO)) {
            currentCompanyDTO.setNumberOfCompany(companies.stream().distinct().collect(Collectors.toList()).size());
        }
        return currentCompanyDTO;
    }

    private CurrentCompanyDTO getCurrentCompanyDTOWithDefaultCompany(Long userId, List<Staff> staffs,
                                                                     List<Company> pendingCompanies,
                                                                     List<CompanyJoinRequest> companyJoinRequests,
                                                                     @NotNull Company defaultCompany,
                                                                     String locale) {
        CurrentCompanyDTO currentCompanyDTO = null;

        if (CollectionUtils.isNotEmpty(staffs)) {
            Optional<Staff> optionalCompany = staffs.stream()
                    .filter(staff -> staff.getCompany().getCompanyId().equals(defaultCompany.getCompanyId()))
                    .findFirst();
            if (optionalCompany.isPresent()) {
                int type = userId.equals(defaultCompany.getCreatedBy()) ? CompanyWorkedType.APPROVED_AUTHORIZE_COMPANY : CompanyWorkedType.APPROVED_JOIN_COMPANY;
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(defaultCompany, locale), optionalCompany.get(), type, locale);
            }
        }

        if (Objects.isNull(currentCompanyDTO)) {
            Optional<Company> optionalPendingCompany = pendingCompanies.stream()
                    .filter(pendingCompany -> pendingCompany.getCompanyId().equals(defaultCompany.getCompanyId()))
                    .findFirst();
            if (optionalPendingCompany.isPresent()) {
                Company company = optionalPendingCompany.get();
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(company, locale), null, CompanyWorkedType.PENDING_AUTHORIZE_COMPANY, locale);
            }

            Optional<CompanyJoinRequest> optionalJoinRequestCompany = companyJoinRequests.stream()
                    .filter(joinRequestCompany -> joinRequestCompany.getCompany().getCompanyId().equals(defaultCompany.getCompanyId()))
                    .findFirst();
            if (optionalJoinRequestCompany.isPresent()) {
                CompanyJoinRequest request = optionalJoinRequestCompany.get();
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(request.getCompany(), locale), null, CompanyWorkedType.PENDING_JOIN_COMPANY, locale);
            }
        }
        return currentCompanyDTO;
    }

    private CurrentCompanyDTO getCurrentCompanyDTOWithoutDefaultCompany(Long userId, List<Staff> staffs, List<Company> pendingCompanies, List<CompanyJoinRequest> companyJoinRequests, String locale) {
        CurrentCompanyDTO currentCompanyDTO = null;
        Company company = CollectionUtils.isNotEmpty(staffs) ? staffs.get(0).getCompany() : null;
        Company pendingCompany = CollectionUtils.isNotEmpty(pendingCompanies) ? pendingCompanies.get(0) : null;
        CompanyJoinRequest joinRequestCompany = CollectionUtils.isNotEmpty(companyJoinRequests) ? companyJoinRequests.get(0) : null;

        if (Objects.nonNull(company)) {
            int type = userId.equals(company.getCreatedBy()) ? CompanyWorkedType.APPROVED_AUTHORIZE_COMPANY : CompanyWorkedType.APPROVED_JOIN_COMPANY;
            if (CollectionUtils.isNotEmpty(staffs)) {
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(company, locale), staffs.get(0), type, locale);
            } else {
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(company, locale), null, type, locale);
            }
        }

        if (Objects.isNull(currentCompanyDTO)) {
            if (Objects.nonNull(pendingCompany) && Objects.nonNull(joinRequestCompany)) {
                if (pendingCompany.getCreatedDate().after(joinRequestCompany.getCreatedDate())) {
                    currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(pendingCompany, locale), null, CompanyWorkedType.PENDING_AUTHORIZE_COMPANY, locale);
                } else {
                    currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(joinRequestCompany.getCompany(), locale), null, CompanyWorkedType.PENDING_JOIN_COMPANY, locale);
                }
            } else if (Objects.nonNull(pendingCompany)) {
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(pendingCompany, locale), null, CompanyWorkedType.PENDING_AUTHORIZE_COMPANY, locale);
            } else if (Objects.nonNull(joinRequestCompany)) {
                currentCompanyDTO = new CurrentCompanyDTO(new CompanyDTO(joinRequestCompany.getCompany(), locale), null, CompanyWorkedType.PENDING_JOIN_COMPANY, locale);
            }
        }
        return currentCompanyDTO;
    }


    private WaitingCompanyDTO createCompanyAuthorizedWaiting(Company company) {
        WaitingCompanyDTO result = new WaitingCompanyDTO();
        result.setType(CompanyWorkedType.PENDING_AUTHORIZE_COMPANY);
        result.setCompany(new CompanyBaseDTO(company.getCompanyId(), company.getCompanyName()));
        return result;
    }

    private WaitingCompanyDTO createCompanyJoinRequestWaiting(Company company) {
        WaitingCompanyDTO result = new WaitingCompanyDTO();
        result.setType(CompanyWorkedType.PENDING_JOIN_COMPANY);
        result.setCompany(new CompanyBaseDTO(company.getCompanyId(), company.getCompanyName()));
        return result;
    }

    @Override
    public BaseResp uploadPublicKey(AuthenticatedUser authenticatedUser, UserUploadKeyReq req) {
        updatePublicKey(authenticatedUser, req);
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    private List<UserFitLanguage> createUserFitLanguages(Long userProfileId, List<Language> languages,
                                                         boolean isNativeLang) {
        List<UserFitLanguage> userFitLanguages = new ArrayList<>();
        for (Language language : languages) {
            UserFitLanguage userLanguage = new UserFitLanguage(isNativeLang, language, new UserFit(userProfileId));
            userFitLanguages.add(userLanguage);
        }
        return userFitLanguages;
    }
}
