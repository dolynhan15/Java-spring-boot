package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessCompanyService;
import com.qooco.boost.business.BusinessMessageService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.constants.ColumnLength;
import com.qooco.boost.constants.CompanyWorkedType;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.enumeration.JoinCompanyStatus;
import com.qooco.boost.data.model.count.LongCount;
import com.qooco.boost.data.mongo.entities.CompanyDoc;
import com.qooco.boost.data.mongo.services.CompanyDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.AuthorizationRequestDTO;
import com.qooco.boost.models.dto.company.*;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.dto.user.BaseUserDTO;
import com.qooco.boost.models.dto.user.ShortUserDTO;
import com.qooco.boost.models.request.CompanyReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.utils.Validation;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static java.util.stream.Collectors.toUnmodifiableList;

@Service
public class BusinessCompanyServiceImpl extends BusinessUserServiceAbstract implements BusinessCompanyService {
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CityService cityService;
    @Autowired
    private HotelTypeService hotelTypeService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private CompanyDocService companyDocService;
    @Autowired
    private CompanyJoinRequestService companyJoinRequestService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private UserProfileService userProfileService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private VacancyService vacancyService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private BusinessMessageService businessMessageService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private UserAccessTokenService userAccessTokenService;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    private static final int COMPANY_ID_INDEX = 0;
    private static final int JOIN_REQUEST_STATUS_INDEX = 1;

    @Override
    public BaseResp get(Long companyId, Authentication authentication) {
        Company foundCompany = checkExistsCompany(companyId);
        return new BaseResp<>(new CompanyDTO(foundCompany, getLocale(authentication)));
    }

    @Override
    public BaseResp getShortCompany(Long companyId, Authentication authentication) {
        Company foundCompany = checkExistsCompany(companyId);
        List<Staff> adminStaffs = staffService.findStaffOfCompanyByRole(foundCompany.getCompanyId(), CompanyRole.ADMIN.getCode());
        CompanyShortInformationDTO companyShortInformationDTO = new CompanyShortInformationDTO(foundCompany, getLocale(authentication));

        if (CollectionUtils.isEmpty(adminStaffs)) {
            return new BaseResp<>(companyShortInformationDTO);
        }
        companyShortInformationDTO.setContactPerson(new ShortUserDTO(adminStaffs.get(0).getUserFit()));
        return new BaseResp<>(companyShortInformationDTO);
    }

    private Company checkExistsCompany(Long companyId) {
        if (Objects.isNull(companyId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        Company foundCompany = companyService.findById(companyId);
        if (Objects.isNull(foundCompany)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_COMPANY);
        }
        return foundCompany;
    }

    @Override
    public BaseResp updateJoinCompanyStatusRequest(@NotNull String messageId, int status, long userProfileId, Authentication authentication) {
        List<MessageDTO> assignmentMessages = businessMessageService.approvalRequestMessage(messageId, status, userProfileId, getLocale(authentication));
        return new BaseResp<>(assignmentMessages);
    }

    @Override
    public BaseResp getCompanyByStatus(String status, Authentication authentication) {
        List<Company> companies = companyService.findByStatus(ApprovalStatus.valueOf(status.trim()));
        if (CollectionUtils.isEmpty(companies)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND);
        }
        return new BaseResp<>(convertToCompanyDTOS(companies, getLocale(authentication)));
    }

    @Override
    public BaseResp getWorkingCompanies(List<Integer> types, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = user.getId();
        List<Company> companies = companyService.findCompanyOfStaffOrJoinRequestByUserId(userId);
        List<Object[]> companyOfJoinRequests = companyJoinRequestService.findCompanyIdWithPendingAndApprovedJoinRequestByUserId(userId);
        List<Long> companyIdsHavePendingJoinRequests = new ArrayList<>();
        List<Long> companyIdsHaveApprovedJoinRequests = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(companyOfJoinRequests)) {
            companyOfJoinRequests.forEach(j -> {
                if (JoinCompanyStatus.PENDING.ordinal() == ((BigDecimal) j[JOIN_REQUEST_STATUS_INDEX]).intValue()) {
                    companyIdsHavePendingJoinRequests.add(((BigDecimal) j[COMPANY_ID_INDEX]).longValue());
                } else if (JoinCompanyStatus.AUTHORIZED.ordinal() == ((BigDecimal) j[JOIN_REQUEST_STATUS_INDEX]).intValue()) {
                    companyIdsHaveApprovedJoinRequests.add(((BigDecimal) j[COMPANY_ID_INDEX]).longValue());
                }
            });
        }

        List<CompanyWorkedDTO> companyWorkedDTOS = new ArrayList<>();
        companies.forEach(company -> {
            CompanyWorkedDTO dto = new CompanyWorkedDTO(company, getLocale(authentication));
            if (ApprovalStatus.PENDING.equals(company.getStatus())) {
                dto.setType(CompanyWorkedType.PENDING_AUTHORIZE_COMPANY);
            } else if (ApprovalStatus.APPROVED.equals(company.getStatus()) && CollectionUtils.isNotEmpty(companyIdsHavePendingJoinRequests)
                    && companyIdsHavePendingJoinRequests.contains(company.getCompanyId())) {
                dto.setType(CompanyWorkedType.PENDING_JOIN_COMPANY);
            } else if (ApprovalStatus.APPROVED.equals(company.getStatus()) && CollectionUtils.isNotEmpty(companyIdsHaveApprovedJoinRequests)
                    && companyIdsHaveApprovedJoinRequests.contains(company.getCompanyId())) {
                dto.setType(CompanyWorkedType.APPROVED_JOIN_COMPANY);
            } else {
                dto.setType(CompanyWorkedType.APPROVED_AUTHORIZE_COMPANY);
            }
            companyWorkedDTOS.add(dto);
        });

        List<CompanyWorkedDTO> results = companyWorkedDTOS;
        if (CollectionUtils.isNotEmpty(types)) {
            results = companyWorkedDTOS.stream().filter(c -> types.contains(c.getType())).collect(Collectors.toList());
        }
        return new BaseResp<>(results);
    }

    @Override
    public BaseResp approveNewCompany(Long companyId, Long userProfileId) {
        if (Objects.isNull(companyId) || Objects.isNull(userProfileId)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        UserProfile isRootAdmin = userProfileService.checkUserProfileIsRootAdmin(userProfileId);
        if (Objects.isNull(isRootAdmin)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        Integer updatedResult = companyService.updateCompanyStatus(companyId);
        if (updatedResult != 0) {
            Company company = companyService.findById(companyId);
            if (Objects.nonNull(company)) {
                pushNotificationService.notifyCompanyApproval(new Company(company), true);
                boostActorManager.updateCompanyInMongo(new Company(company));
            }
            return new BaseResp<>(ResponseStatus.SUCCESS);
        }
        throw new EntityNotFoundException(ResponseStatus.NOT_FOUND);
    }

    @Override
    public BaseResp getCompanyProfileInfo(@NotNull Long companyId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = user.getId();

        //Company staff (include creator or joiner)
        List<Staff> owners = staffService.findByUserProfileAndCompany(companyId, userId);
        Company company = null;
        Staff staff = null;

        if (CollectionUtils.isNotEmpty(owners)) {
            staff = owners.get(0);
            company = staff.getCompany();
        }

        int staffNumber = 0;
        int locationNumber = 0;
        int vacancyNumber = 0;
        if (Objects.nonNull(company) && ApprovalStatus.APPROVED.equals(company.getStatus())) {
            staffNumber = staffService.countByCompany(companyId);
            locationNumber = locationService.countByCompany(companyId);
            vacancyNumber = vacancyService.countOpeningByUserAndCompany(userId, companyId);
        }

        //The joiner who is waiting to approval
        if (Objects.isNull(company)) {
            List<CompanyJoinRequest> companyJoinRequests = companyJoinRequestService.findPendingJoinRequestByCompanyAndUserProfile(companyId, userId);
            if (CollectionUtils.isNotEmpty(companyJoinRequests)) {
                company = companyJoinRequests.get(0).getCompany();
            }
        }

        if (Objects.isNull(company)) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_ACCESS);
        }
        CompanyDTO companyDTO = new CompanyDTO(company, getLocale(authentication));
        boolean isEditable = false;
        if (Objects.nonNull(staff)) {
            isEditable = CompanyRole.ADMIN.getName().equals(staff.getRole().getName());
        }
        int countPendingCompanyRequest = companyService.countPendingJoinedCompanyRequestAndCompanyAuthorizationRequestByUser(userId);
        boolean hasPendingCompanyRequest = countPendingCompanyRequest > 0;
        CompanyWidgetDTO companyWidget = new CompanyWidgetDTO(vacancyNumber, staffNumber, locationNumber, 0);
        CompanyProfileDTO companyProfile = new CompanyProfileDTO(companyDTO, companyWidget, staff, isEditable, hasPendingCompanyRequest, getLocale(authentication));
        return new BaseResp<>(companyProfile);
    }

    @Override
    public BaseResp switchCompany(long companyId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        //Fit user can switch to pending join company
        List<CompanyJoinRequest> pendingJoinRequestCompany = companyJoinRequestService.findPendingJoinRequestByCompanyAndUserProfile(companyId, user.getId());
        if (CollectionUtils.isEmpty(pendingJoinRequestCompany)) {
            //Fit user can switch to pending company
            businessValidatorService.checkExistsStaffInCompany(companyId, user.getId());
        }
        userFitService.updateDefaultCompany(user.getId(), companyId);
        userAccessTokenService.updateCompanyByAccessToken(user.getToken(), companyId);
        return getCompanyProfileInfo(companyId, authentication);
    }

    @Override
    public BaseResp findByCountryAndStatusApproved(long countryId, int page, int size) {
        var companyPage = companyService.findByCountryAndStatusApproved(countryId, page, size);
        return new BaseResp<>(new PagedResultV2<>(companyPage.stream().map(CompanyBaseDTO::new).collect(toUnmodifiableList()), page, companyPage));
    }

    @Transactional
    @Override
    public BaseResp saveCompany(CompanyReq companyReq, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        this.validateCompany(companyReq);

        /** Ivan say that:
         *  If he is waiting to approving his company or his join request
         *  => He can not create new company.
         *  But API will not check this at create company now.
         */
        City city = cityService.findValidById(companyReq.getCityId());
        if (Objects.isNull(city)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_CITY);
        }

        HotelType hotelType = null;
        if (Objects.nonNull(companyReq.getHotelTypeId())) {
            hotelType = hotelTypeService.findById(companyReq.getHotelTypeId());
            if (Objects.isNull(hotelType)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_HOTEL_TYPE);
            }
        }

        if (Objects.isNull(companyReq.getId())) {
            int countPendingCompany = staffService.countPendingCompanyOfAdmin(user.getId());
            if (countPendingCompany > 0) {
                throw new NoPermissionException(ResponseStatus.SAVE_COMPANY_HAS_PENDING_COMPANY);
            }
            return createNewCompany(companyReq, city, hotelType, user);
        } else {
            Company company = companyService.findById(companyReq.getId());
            if (Objects.isNull(company)) {
                throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_COMPANY);
            }
            List<Staff> lstAdminCompany = staffService.findByCompanyAndAdmin(company.getCompanyId(), user.getId());
            if (CollectionUtils.isEmpty(lstAdminCompany)) {
                throw new NoPermissionException(ResponseStatus.SAVE_COMPANY_NOT_ADMIN_OF_COMPANY);
            }
            return updateCompany(companyReq.updateEntity(company), city, hotelType, user.getId(), companyReq, getLocale(authentication));
        }
    }

    private void validateCompany(CompanyReq companyReq) {

        if (StringUtils.isNotBlank(companyReq.getWeb()) && !Validation.validateHttpOrHttps(companyReq.getWeb().trim())) {
            throw new InvalidParamException(ResponseStatus.HTTP_OR_HTTPS_WRONG_FORMAT);
        }

        if (StringUtils.isBlank(companyReq.getLogo())) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EMPTY_LOGO);
        }
        if (StringUtils.isBlank(companyReq.getName())) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EMPTY_NAME);
        }

        if (StringUtils.isBlank(companyReq.getPhone())) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EMPTY_PHONE_NUMBER);
        }
        if (StringUtils.isBlank(companyReq.getEmail())) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EMPTY_EMAIL);
        }

        if (companyReq.getLogo().trim().length() > ColumnLength.COMPANY_LOGO) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_LOGO);
        }
        if (StringUtils.isNotBlank(companyReq.getName()) && companyReq.getName().trim().length() > ColumnLength.COMPANY_NAME) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_COMPANY_NAME);
        }
        if (StringUtils.isNotBlank(companyReq.getAddress()) && companyReq.getAddress().trim().length() > ColumnLength.COMPANY_ADDRESS) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_ADDRESS);
        }
        if (StringUtils.isNotBlank(companyReq.getPhone()) && companyReq.getPhone().trim().length() > ColumnLength.COMPANY_PHONE) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_PHONE);
        }
        if (StringUtils.isNotBlank(companyReq.getWeb()) && companyReq.getWeb().trim().length() > ColumnLength.COMPANY_WEBPAGE) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_WEBPAGE);
        }
        if (StringUtils.isNotBlank(companyReq.getAmadeus()) && companyReq.getAmadeus().trim().length() > ColumnLength.COMPANY_AMADEUS) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_AMADEUS);
        }
        if (StringUtils.isNotBlank(companyReq.getGalileo()) && companyReq.getGalileo().trim().length() > ColumnLength.COMPANY_GALILEO) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_GALILEO);
        }
        if (StringUtils.isNotBlank(companyReq.getWorldspan()) && companyReq.getWorldspan().trim().length() > ColumnLength.COMPANY_WORLDSPAN) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_WORLDSPAN);
        }
        if (StringUtils.isNotBlank(companyReq.getSabre()) && companyReq.getSabre().trim().length() > ColumnLength.COMPANY_SABRE) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_SABRE);
        }
        if (StringUtils.isNotBlank(companyReq.getDescription()) && companyReq.getDescription().trim().length() > ColumnLength.COMPANY_DESCRIPTION) {
            throw new InvalidParamException(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_DESCRIPTION);
        }
    }

    @Override
    public BaseResp getCompanyOfUser(long userProfileId, Authentication authentication) {
        List<Integer> status = Lists.newArrayList(ApprovalStatus.APPROVED.getCode());
        List<Long> roles = Lists.newArrayList(CompanyRole.ADMIN.getCode(), CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.RECRUITER.getCode());
        List<Company> companies = companyService.findByUserProfileAndRoleAndStatus(userProfileId, roles, status);
        List<CompanyShortInformationDTO> companyDTOs = new ArrayList<>();
        companies.forEach(c -> companyDTOs.add(new CompanyShortInformationDTO(c, getLocale(authentication))));
        return new BaseResp<>(companyDTOs);
    }

    @Override
    public BaseResp searchCompanyByName(String keyword, int page, int size) {
        Page<CompanyDoc> companyDocPage = companyDocService.searchFullTextByName(keyword, page, size);
        PagedResultV2<CompanyBaseDTO, CompanyDoc> result = convertToCompanyBaseDTOPage(page, companyDocPage);
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp searchCompanyByNameForJoinCompany(String keyword, int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Page<CompanyDoc> companyDocPage = companyDocService.searchFullTextByNameExceptStaff(keyword, page, size, user.getId());
        PagedResultV2<CompanyBaseDTO, CompanyDoc> result = convertToCompanyBaseDTOPage(page, companyDocPage);
        return new BaseResp<>(result);
    }

    private PagedResultV2<CompanyBaseDTO, CompanyDoc> convertToCompanyBaseDTOPage(int page, Page<CompanyDoc> companyDocPage) {
        List<CompanyBaseDTO> companies = companyDocPage.getContent().stream()
                .map(CompanyBaseDTO::new).collect(Collectors.toList());
        return new PagedResultV2<>(companies, page, companyDocPage);
    }

    private boolean hasPendingRequest(List<CompanyJoinRequest> joinRequests) {
        boolean hasPendingRequest = false;
        if (CollectionUtils.isNotEmpty(joinRequests)
                && joinRequests.stream().anyMatch(j -> JoinCompanyStatus.PENDING.name().equals(j.getStatus().name()))) {
            hasPendingRequest = true;
        }
        return hasPendingRequest;
    }

    private boolean hasAuthorizedRequest(List<CompanyJoinRequest> joinRequests, Long companyId, Long userProfileId) {
        boolean hasAuthorizedRequest = false;
        if (CollectionUtils.isNotEmpty(joinRequests)
                && joinRequests.stream().anyMatch(j -> JoinCompanyStatus.AUTHORIZED.name().equals(j.getStatus().name()))) {
            int staffs = staffService.countByUserProfileAndCompany(userProfileId, companyId);
            if (staffs > 0) {
                hasAuthorizedRequest = true;
            }
        }
        return hasAuthorizedRequest;
    }

    @Override
    public BaseResp joinCompanyRequest(Long companyId, Long userProfileId) {
        List<CompanyJoinRequest> foundJoinRequest = companyJoinRequestService.findByCompanyIdAndUserProfileId(companyId, userProfileId);
        if (hasPendingRequest(foundJoinRequest)) {
            throw new InvalidParamException(ResponseStatus.PENDING_JOIN_COMPANY_REQUEST);
        } else if (hasAuthorizedRequest(foundJoinRequest, companyId, userProfileId)) {
            throw new InvalidParamException(ResponseStatus.AUTHORIZED_JOIN_COMPANY_REQUEST);
        }

        CompanyJoinRequest joinRequest = new CompanyJoinRequest(JoinCompanyStatus.PENDING, new Company(companyId), new UserFit(userProfileId, companyId));
        CompanyJoinRequest result = companyJoinRequestService.save(joinRequest);
        if (Objects.isNull(result)) {
            throw new InvalidParamException(ResponseStatus.SAVE_FAIL);
        }
        boostActorManager.saveJoinCompanyRequestInMongoActor(result);
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp getJoinCompanyRequest(Long companyId, long userProfileId) {
        List<CompanyJoinRequest> joinRequests = companyJoinRequestService.findByCompanyId(companyId);
        List<ShortUserDTO> shortUserProfileDTOList = joinRequests.stream().map(
                companyJoinRequest -> new ShortUserDTO(companyJoinRequest.getUserFit())).collect(Collectors.toList());

        List<AuthorizationRequestDTO> result = new ArrayList<>();
        List<LongCount> countUnreadMessage = messageDocService.countUnreadMessageByUserProfileCvId(
                shortUserProfileDTOList.stream().map(BaseUserDTO::getId)
                        .collect(Collectors.toList()), SELECT_APP.value(), userProfileId);
        long unreadMsg;
        Optional<LongCount> countOptional;
        for (ShortUserDTO shortUserProfileDTO : shortUserProfileDTOList) {
            unreadMsg = 0;
            countOptional = countUnreadMessage.stream().filter(
                    c -> c.getId().equals(shortUserProfileDTO.getId())).findFirst();
            if (countOptional.isPresent()) {
                unreadMsg = countOptional.get().getTotal();
            }
            result.add(new AuthorizationRequestDTO(shortUserProfileDTO, unreadMsg));
        }
        return new BaseResp<>(result);
    }

    private List<CompanyDTO> convertToCompanyDTOS(List<Company> companies, String locale) {
        List<CompanyDTO> companyDTOS = new ArrayList<>();
        for (Company company : companies) {
            companyDTOS.add(new CompanyDTO(company, locale));
        }
        return companyDTOS;
    }

    private BaseResp createNewCompany(@NotNull CompanyReq companyReq, @NotNull City city,
                                      @NotNull HotelType hotelType, AuthenticatedUser user) {
        Company company = companyReq.updateEntity(new Company());
        company.setCity(city);
        company.setHotelType(hotelType);
        company.setCreatedBy(user.getId());
        company.setUpdatedBy(user.getId());
        company.setStatus(ApprovalStatus.PENDING);
        Company result = companyService.save(company);

        Staff staff = new Staff();
        staff.setCompany(company);
        staff.setUserFit(new UserFit(user.getId()));
        staff.setRole(new RoleCompany(CompanyRole.ADMIN.getCode()));
        staff.setCreatedBy(user.getId());
        staff.setUpdatedBy(user.getId());
        staffService.save(staff);
        //Save Company city as an location
        saveNewLocation(user.getId(), result);
        saveDefaultCompany(user.getId(), user.getToken(), result.getCompanyId());
        return new BaseResp<>(new CompanyDTO(result, user.getLocale()));
    }

    private BaseResp updateCompany(@NotNull Company company, @NotNull City city,
                                   @NotNull HotelType hotelType, Long userProfileId,
                                   CompanyReq companyReq, String locale) {


        boolean isSaveNewLocation = false;
        if (Objects.isNull(company.getCity())) {
            isSaveNewLocation = true;
        }
        if (!company.getCity().getCityId().equals(companyReq.getCityId())
                || (Objects.nonNull(company.getAddress()) && Objects.isNull(companyReq.getAddress()))
                || (Objects.isNull(company.getAddress()) && Objects.nonNull(companyReq.getAddress()))
                || (Objects.nonNull(company.getAddress()) && Objects.nonNull(companyReq.getAddress())
                        && !company.getAddress().trim().equalsIgnoreCase(companyReq.getAddress().trim()))
        ) {
            isSaveNewLocation = true;
        }
        company.setCity(city);
        company.setHotelType(hotelType);
        company.setUpdatedBy(userProfileId);
        Company result = companyService.save(company);
        if (isSaveNewLocation) {
            saveNewLocation(userProfileId, result);
        }
        if (ApprovalStatus.APPROVED == company.getStatus()) {
            boostActorManager.updateCompanyInMongo(new Company(result));
        }

        return new BaseResp<>(new CompanyDTO(result, locale));
    }

    private Location saveNewLocation(long userProfileId, @NotNull Company company) {
        Location location = new Location(null, userProfileId);
        location.setAddress(company.getAddress());
        location.setCity(company.getCity());
        location.setCompany(company);
        location.setPrimary(true);
        locationService.updateNonePrimaryForCompany(company.getCompanyId());
        return locationService.save(location);
    }
}
