package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessCompanyService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.constants.CompanyWorkedType;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.MessageConstants;
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
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.company.CompanyProfileDTO;
import com.qooco.boost.models.dto.company.CompanyShortInformationDTO;
import com.qooco.boost.models.request.CompanyReq;
import com.qooco.boost.threads.BoostActorManager;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.utils.DataUtilsTest;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.ServletUriUtils;
import org.hamcrest.CoreMatchers;
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
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PrepareForTest(ServletUriUtils.class)
@RunWith(PowerMockRunner.class)
public class BusinessCompanyServiceImplTest extends BaseUserService {

    @InjectMocks
    private BusinessCompanyService businessCompanyService = new BusinessCompanyServiceImpl();

    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);
    @Mock
    private UserFitService userFitService = Mockito.mock(UserFitService.class);
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private CityService cityService = Mockito.mock(CityService.class);
    @Mock
    private HotelTypeService hotelTypeService = Mockito.mock(HotelTypeService.class);
    @Mock
    private StaffService staffService = Mockito.mock(StaffService.class);
    @Mock
    private CompanyDocService companyDocService = Mockito.mock(CompanyDocService.class);
    @Mock
    private CompanyJoinRequestService companyJoinRequestService = Mockito.mock(CompanyJoinRequestService.class);
    @Mock
    private MessageDocService messageDocService = Mockito.mock(MessageDocService.class);
    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);
    @Mock
    private PushNotificationService pushNotificationService = Mockito.mock(PushNotificationService.class);
    @Mock
    private BusinessValidatorService businessValidatorService;
    @Mock
    private LocationService locationService;
    @Mock
    private VacancyService vacancyService;
    @Mock
    private UserAccessTokenService userAccessTokenService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void get_whenCompanyIdIsNull_thenReturnIdIsEmpty() {
        thrown.expect(InvalidParamException.class);
        businessCompanyService.get(null, initAuthentication());
    }

    @Test
    public void get_whenHaveInvalidCompanyId_thenReturnNotFoundResp() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(companyService.findById(-1L)).thenReturn(null);
        businessCompanyService.get(-1L, initAuthentication());
    }

    @Test
    public void get_whenHaveValidCompanyId_thenReturnBaseCompanyResp() {
        Company responseCompany = DataUtilsTest.initCompany();
        responseCompany.setStatus(ApprovalStatus.PENDING);
        Mockito.when(companyService.findById(1L)).thenReturn(responseCompany);
        CompanyDTO actualResp = (CompanyDTO) businessCompanyService.get(1L, initAuthentication()).getData();
        Assert.assertEquals(responseCompany.getCompanyId(), actualResp.getId());
    }

    @Test
    public void getCompanyByStatus_whenCompaniesIdNotExist_thenReturnEntityNotFoundException() {
        Mockito.when(companyService.findByStatus(ApprovalStatus.PENDING)).thenReturn(null);
        try {
            businessCompanyService.getCompanyByStatus("PENDING", initAuthentication());
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getCompanyByStatus_whenCompaniesIdExist_thenReturnSuccess() {
        Company responseCompany = new Company(1L);
        List<Company> companies = new ArrayList<>();
        companies.add(responseCompany);
        Mockito.when(companyService.findByStatus(ApprovalStatus.PENDING)).thenReturn(companies);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.getCompanyByStatus("PENDING", initAuthentication()).getCode());
    }

    @Test
    public void approveNewCompany_whenCompanyIdIsNull_thenReturnIdIsEmptyError() {
        thrown.expect(InvalidParamException.class);
        businessCompanyService.approveNewCompany(null, 1L);
    }

    @Test
    public void approveNewCompany_whenUserIsNotRootAdmin_thenReturnNoPermissionToAccessError() {
        thrown.expect(NoPermissionException.class);
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(1L)).thenReturn(null);
        businessCompanyService.approveNewCompany(1L, 1L);
    }

    @Test
    public void approveNewCompany_whenCompanyIdIsValid_thenReturnUpdateFail() {
        UserProfile userProfile = new UserProfile(1L);
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(1L)).thenReturn(userProfile);
        Mockito.when(companyService.updateCompanyStatus(1L)).thenReturn(0);
        try {
            businessCompanyService.approveNewCompany(1L, 1L);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void approveNewCompany_whenUpdatedNewRecord_thenReturnUpdateSuccess() {
        UserProfile userProfile = new UserProfile(1L);
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(1L)).thenReturn(userProfile);
        Mockito.when(companyService.updateCompanyStatus(1L)).thenReturn(1);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.approveNewCompany(1L, 1L).getCode());
    }

    @Test
    public void approveNewCompany_whenCompanyIdIsExisted_thenReturnUpdateSuccess() {
        UserProfile userProfile = new UserProfile(1L);
        Mockito.when(userProfileService.checkUserProfileIsRootAdmin(1L)).thenReturn(userProfile);
        Mockito.when(companyService.updateCompanyStatus(1L)).thenReturn(1);
        Mockito.when(companyService.findById(1L)).thenReturn(new Company(1L));
        Mockito.doNothing().when(pushNotificationService).notifyCompanyApproval(new Company(1L), true);
        Mockito.doNothing().when(boostActorManager).updateCompanyInMongo(new Company(1L));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.approveNewCompany(1L, 1L).getCode());
    }

    @Test
    public void saveCompany_whenBlankLogo_thenThrowEmptyLogo() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setLogo(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EMPTY_LOGO.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenBlankName_thenThrowEmptyName() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setName(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EMPTY_NAME.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenBlankPhone_thenThrowEmptyPhone() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setPhone(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EMPTY_PHONE_NUMBER.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenBlankEmail_thenThrowEmptyEmail() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setEmail(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EMPTY_EMAIL.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthUrlLogo_thenThrowMaxLengthLogoUrl() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setLogo("http://basdlajdslkajdlajdasdasdasdasdasasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsbasdlajdslkajdlajdasdasdasdasdasdasdasdasdasdadsasadasdsasdadasdasdasdadas.com");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_LOGO.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthCompanyName_thenThrowMaxLengthName() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setName("intercontinetal da nang BCASDASDA nasndansdjnaksdakjdka");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_COMPANY_NAME.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthAddress_thenThrowMaxLengthAddress() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setAddress("intercontinetal da nang BCASDASDA nasndansdjnaksdakjdka Da nang");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_ADDRESS.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthPhone_thenThrowMaxLengthPhone() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setPhone("+84 12323123456456456456");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_PHONE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthWeb_thenThrowMaxLengthWeb() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setWeb("http://intercontinetaldsdsdsdsdsasndansdjnaksdakjdkadsdsdsdngintercontinetalddsdsdsdsddjnaksdakjddsdsdsdsdsdsdanang.com");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_WEBPAGE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthAmadeus_thenThrowMaxLengthAmadeus() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setAmadeus("ABCB 1231324234234");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_AMADEUS.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthGalileo_thenThrowMaxLengthGalileo() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setGalileo("ABCB 1231324234234");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_GALILEO.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthWorldspan_thenThrowMaxLengthWorldspan() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setWorldspan("ABCB 1231324234234");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_WORLDSPAN.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthSabre_thenThrowMaxLengthSabre() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setSabre("ABCB 1231324234234");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_SABRE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenOverMaxLengthDescription_thenThrowMaxLengthDescription() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setDescription("intercontinetal da nang BCASDASDA nasndansdjnaksdakjdka intercontinetal da nang BCASDASDA nasndansdjnaksdakjdka intercontinetal da nang BCASDASDA nasndansdjnaksdakjdka intercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdkaintercontinetal da nang BCASDASDA nasndansdjnaksdakjdka");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_EXCEED_MAX_LENGTH_DESCRIPTION.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenWrongFormatWeb_thenThrowHttpWrongFormat() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setWeb("ABCB 1231324234234");
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.HTTP_OR_HTTPS_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenNotFoundCity_thenThrowNotFoundCity() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        Mockito.when(cityService.findValidById(companyReq.getCityId())).thenReturn(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_CITY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenNotFoundHotelType_thenThrowNotFoundHotelType() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        City city = DataUtilsTest.initCity();
        Mockito.when(cityService.findValidById(companyReq.getCityId())).thenReturn(city);
        Mockito.when(hotelTypeService.findById(companyReq.getHotelTypeId())).thenReturn(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_HOTEL_TYPE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenCreateCompanyWithPendingCompany_thenThrowNoPermissionCreateCompany() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        City city = DataUtilsTest.initCity();
        HotelType hotelType = DataUtilsTest.initHotelType();
        Mockito.when(cityService.findValidById(companyReq.getCityId())).thenReturn(city);
        Mockito.when(hotelTypeService.findById(companyReq.getHotelTypeId())).thenReturn(hotelType);
        Mockito.when(staffService.countPendingCompanyOfAdmin(1L)).thenReturn(1);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (NoPermissionException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_HAS_PENDING_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenUpdateCompanyNotFoundCompany_thenThrowNotFoundCompany() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setId(1L);
        City city = DataUtilsTest.initCity();
        HotelType hotelType = DataUtilsTest.initHotelType();
        Mockito.when(cityService.findValidById(companyReq.getCityId())).thenReturn(city);
        Mockito.when(hotelTypeService.findById(companyReq.getHotelTypeId())).thenReturn(hotelType);
        Mockito.when(companyService.findById(companyReq.getId())).thenReturn(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void saveCompany_whenNotAdminUpdateCompany_thenThrowNoPermission() {
        CompanyReq companyReq = DataUtilsTest.initValidSaveCompanyRequest();
        companyReq.setId(1L);
        City city = DataUtilsTest.initCity();
        HotelType hotelType = DataUtilsTest.initHotelType();
        Mockito.when(cityService.findValidById(companyReq.getCityId())).thenReturn(city);
        Mockito.when(hotelTypeService.findById(companyReq.getHotelTypeId())).thenReturn(hotelType);
        Company company = DataUtilsTest.initCompany();
        company.setCompanyId(1L);
        Mockito.when(companyService.findById(companyReq.getId())).thenReturn(company);
        Mockito.when(staffService.findByPendingCompanyAndAdmin(companyReq.getId(), 1L)).thenReturn(null);
        try {
            businessCompanyService.saveCompany(companyReq, initAuthentication());
        } catch (NoPermissionException ex) {
            Assert.assertEquals(ResponseStatus.SAVE_COMPANY_NOT_ADMIN_OF_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getCompanyOfUser_whenUserDoesNotBelongToCompany_thenReturnEmptyList() {
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePath(Mockito.anyString())).thenReturn("image");
        Mockito.when(staffService.findByUserProfileAndStatus(
                1L, ApprovalStatus.APPROVED)).thenReturn(new ArrayList<>());
        BaseResp actualResult = businessCompanyService.getCompanyOfUser(1L, initAuthentication());
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), actualResult.getCode());
        Assert.assertThat(actualResult.getData(), CoreMatchers.instanceOf(List.class));
        List<CompanyShortInformationDTO> data = (List<CompanyShortInformationDTO>) actualResult.getData();
        Assert.assertEquals(0, data.size());
    }

    @Test
    public void getCompanyOfUser_whenUserBelongToCompany_thenReturnCompanies() {
        PowerMockito.mockStatic(ServletUriUtils.class);
        Mockito.when(ServletUriUtils.getRelativePath(Mockito.anyString())).thenReturn("image");
        Company company = DataUtilsTest.initCompany();
        List<Company> companies = Lists.newArrayList(company);
        List<Integer> status = Lists.newArrayList(ApprovalStatus.APPROVED.getCode());
        List<Long> roles = Lists.newArrayList(CompanyRole.ADMIN.getCode(), CompanyRole.HEAD_RECRUITER.getCode(), CompanyRole.RECRUITER.getCode());

        Mockito.when(companyService.findByUserProfileAndRoleAndStatus(1L, roles, status)).thenReturn(companies);
        BaseResp actualResult = businessCompanyService.getCompanyOfUser(1L, initAuthentication());

        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), actualResult.getCode());
        Assert.assertThat(actualResult.getData(), CoreMatchers.instanceOf(List.class));
        List<CompanyShortInformationDTO> data = (List<CompanyShortInformationDTO>) actualResult.getData();
        Assert.assertEquals(1, data.size());
        Assert.assertEquals(company.getCompanyId(), data.get(0).getId());
    }

    @Test
    public void getShortCompany_whenCompanyIdIsNull_thenReturnIdIsEmptyError() {
        thrown.expect(InvalidParamException.class);
        businessCompanyService.getShortCompany(null, initAuthentication());
    }

    @Test
    public void getShortCompany_whenCompanyIdIsValidButNotFoundCompany_thenReturnNotFoundError() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(companyService.findById(1L)).thenReturn(null);
        businessCompanyService.getShortCompany(1L, initAuthentication());
    }

    @Test
    public void getShortCompany_whenNotFoundAdminStaffOfCompany_thenReturnSuccess() {
        Mockito.when(companyService.findById(1L)).thenReturn(new Company(1L));
        Mockito.when(staffService.findStaffOfCompanyByRole(1L, CompanyRole.ADMIN.getCode())).thenReturn(null);
        businessCompanyService.getShortCompany(1L, initAuthentication());
    }

    @Test
    public void getShortCompany_whenFoundAdminStaffOfCompany_thenReturnSuccess() {
        Mockito.when(companyService.findById(1L)).thenReturn(new Company(1L));
        List<Staff> adminStaffs = new ArrayList<>();
        adminStaffs.add(DataUtilsTest.initStaffIsAdmin());
        Mockito.when(staffService.findStaffOfCompanyByRole(1L, CompanyRole.ADMIN.getCode())).thenReturn(adminStaffs);
        businessCompanyService.getShortCompany(1L, initAuthentication());
    }

    private void initMockito(CompanyReq companyReq) {
        City city = DataUtilsTest.initCity();
        city.setCityId(companyReq.getCityId());
        HotelType hotelType = DataUtilsTest.initHotelType();
        hotelType.setHotelTypeId(companyReq.getHotelTypeId());
        Company company = DataUtilsTest.initCompany();
        company.setCompanyId(Objects.nonNull(companyReq.getId()) ? companyReq.getId() : 1L);

        Staff adminCompany = DataUtilsTest.initStaffIsAdmin();

        Mockito.when(cityService.findValidById(companyReq.getCityId())).thenReturn(city);
        Mockito.when(hotelTypeService.findById(companyReq.getHotelTypeId())).thenReturn(hotelType);
        if (Objects.nonNull(companyReq.getId())) {
            Mockito.when(companyService.findById(companyReq.getId())).thenReturn(company);
            Mockito.when(staffService.findByPendingCompanyAndAdmin(companyReq.getId(), adminCompany.getUserFit().getUserProfileId())).thenReturn(adminCompany);
            Mockito.when(companyService.findById(companyReq.getId())).thenReturn(company);
        }

        Mockito.when(userFitService.findById(adminCompany.getUserFit().getUserProfileId())).thenReturn(adminCompany.getUserFit());
        Mockito.when(userFitService.save(adminCompany.getUserFit())).thenReturn(adminCompany.getUserFit());
        if (Objects.nonNull(company.getCompanyId())) {
            Mockito.when(businessValidatorService.checkExistsStaffInApprovedCompany(company.getCompanyId(), adminCompany.getUserFit().getUserProfileId()))
                    .thenReturn(adminCompany);
        }
        Mockito.when(companyService.save(company)).thenReturn(company);
    }

    private void initMockito(Long companyId, Long userProfileId) {
        Mockito.when(companyService.findById(companyId)).thenReturn(new Company(companyId));
        Mockito.when(userFitService.findById(userProfileId)).thenReturn(new UserFit(userProfileId));
        Mockito.when(userFitService.save(new UserFit(userProfileId))).thenReturn(new UserFit(userProfileId));
        Mockito.when(businessValidatorService.checkExistsStaffInApprovedCompany(companyId, userProfileId)).thenReturn(new Staff(new Company(companyId), new UserFit(userProfileId)));

    }

    @Test
    public void searchCompanyByName_whenInputIsValid_thenReturnSuccess() {
        String keyword = "Food";
        int page = 1;
        int size = 1;
        Company company = DataUtilsTest.initCompany();
        company.setStatus(ApprovalStatus.APPROVED);
        List<CompanyDoc> companyDocs = new ArrayList<>();
        companyDocs.add(MongoConverters.convertToCompanyDoc(company));
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyDoc> companyDocPage = new PageImpl<>(companyDocs, pageable, companyDocs.size());
        Mockito.when(companyDocService.searchFullTextByName(keyword, page, size)).thenReturn(companyDocPage);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.searchCompanyByName(keyword, page, size).getCode());
    }

    @Test
    public void joinCompanyRequest_whenLatestJoinRequestIsPending_thenReturnInvalidExceptionError() {
        thrown.expect(InvalidParamException.class);
        Long companyId = 1L;
        Long userProfileId = 1L;
        List<CompanyJoinRequest> companyJoinRequests = new ArrayList<>();
        companyJoinRequests.add(new CompanyJoinRequest(JoinCompanyStatus.PENDING,
                new Company(companyId), new UserFit(userProfileId)));
        Mockito.when(companyJoinRequestService.findByCompanyIdAndUserProfileId(companyId, userProfileId)).thenReturn(companyJoinRequests);
        businessCompanyService.joinCompanyRequest(companyId, userProfileId);
    }

    @Test
    public void joinCompanyRequest_whenLatestJoinRequestIsAuthorized_thenReturnInvalidExceptionError() {
        thrown.expect(InvalidParamException.class);
        Long companyId = 1L;
        Long userProfileId = 1L;
        List<CompanyJoinRequest> companyJoinRequests = new ArrayList<>();
        companyJoinRequests.add(new CompanyJoinRequest(JoinCompanyStatus.AUTHORIZED,
                new Company(companyId), new UserFit(userProfileId)));
        Mockito.when(staffService.countByUserProfileAndCompany(userProfileId, companyId)).thenReturn(1);
        Mockito.when(companyJoinRequestService.findByCompanyIdAndUserProfileId(companyId, userProfileId)).thenReturn(companyJoinRequests);
        businessCompanyService.joinCompanyRequest(companyId, userProfileId);
    }

    @Test
    public void joinCompanyRequest_whenLatestJoinRequestIsDeclined_thenReturnSuccess() {
        Long companyId = 1L;
        Long userProfileId = 1L;
        CompanyJoinRequest companyJoinRequest = new CompanyJoinRequest(JoinCompanyStatus.DECLINED,
                new Company(companyId), new UserFit(userProfileId));
        List<CompanyJoinRequest> companyJoinRequests = new ArrayList<>();
        companyJoinRequests.add(companyJoinRequest);
        CompanyJoinRequest resultJoinRequest = new CompanyJoinRequest(JoinCompanyStatus.PENDING,
                new Company(companyId), new UserFit(userProfileId));
        Mockito.when(companyJoinRequestService.findByCompanyIdAndUserProfileId(companyId, userProfileId)).thenReturn(companyJoinRequests);
        Mockito.when(companyJoinRequestService.save(resultJoinRequest)).thenReturn(resultJoinRequest);
        Mockito.doNothing().when(boostActorManager).saveJoinCompanyRequestInMongoActor(resultJoinRequest);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.joinCompanyRequest(companyId, userProfileId).getCode());
    }

    @Test
    public void joinCompanyRequest_whenSaveFail_thenReturnInvalidExceptionError() {
        thrown.expect(InvalidParamException.class);
        Long companyId = 1L;
        Long userProfileId = 1L;
        CompanyJoinRequest companyJoinRequest = new CompanyJoinRequest(JoinCompanyStatus.PENDING,
                new Company(companyId), new UserFit(userProfileId));
        Mockito.when(companyJoinRequestService.findByCompanyIdAndUserProfileId(companyId, userProfileId)).thenReturn(null);
        Mockito.when(companyJoinRequestService.save(companyJoinRequest)).thenReturn(null);
        businessCompanyService.joinCompanyRequest(companyId, userProfileId);
    }

    @Test
    public void joinCompanyRequest_whenHaveNotJoinRequestYet_thenReturnSuccess() {
        Long companyId = 1L;
        Long userProfileId = 1L;
        CompanyJoinRequest companyJoinRequest = new CompanyJoinRequest(JoinCompanyStatus.PENDING,
                new Company(companyId), new UserFit(userProfileId));
        Mockito.when(companyJoinRequestService.findByCompanyIdAndUserProfileId(companyId, userProfileId)).thenReturn(null);
        Mockito.when(companyJoinRequestService.save(companyJoinRequest)).thenReturn(companyJoinRequest);
        Mockito.doNothing().when(boostActorManager).saveJoinCompanyRequestInMongoActor(companyJoinRequest);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.joinCompanyRequest(companyId, userProfileId).getCode());
    }

    @Test
    public void getJoinCompanyRequest_whenInputIsValid_thenReturnSuccess() {
        Long companyId = 1L;
        long userProfileId = 1L;
        CompanyJoinRequest companyJoinRequest = new CompanyJoinRequest(JoinCompanyStatus.DECLINED,
                new Company(companyId), new UserFit(userProfileId));
        List<CompanyJoinRequest> companyJoinRequests = new ArrayList<>();
        companyJoinRequests.add(companyJoinRequest);
        Mockito.when(companyJoinRequestService.findByCompanyId(companyId)).thenReturn(companyJoinRequests);

        List<Long> userProfileIds = new ArrayList<>();
        userProfileIds.add(userProfileId);
        List<LongCount> countUnreadMessage = new ArrayList<>();
        countUnreadMessage.add(new LongCount(userProfileId, 4));
        Mockito.when(messageDocService.countUnreadMessageByUserProfileCvId(userProfileIds, MessageConstants.RECEIVE_IN_HOTEL_APP, userProfileId)).thenReturn(countUnreadMessage);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.getJoinCompanyRequest(companyId, userProfileId).getCode());
    }

    @Test
    public void getWorkingCompanies_whenInputIsValid_thenReturnSuccess() {
        mockitoGetWorkingCompanies();
        List<Integer> types = Lists.newArrayList(CompanyWorkedType.PENDING_AUTHORIZE_COMPANY, CompanyWorkedType.PENDING_JOIN_COMPANY);
        Authentication authentication = initAuthentication();
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.getWorkingCompanies(types, authentication).getCode());
    }

    @Test
    public void getCompanyProfileInfo_whenStaffHasExistedCompany_thenReturnSuccess() {
        Long companyId = 1L;
        Authentication authentication = initAuthentication();
        List<Staff> owners = Lists.newArrayList(new Staff(1L, new Company(1L, ApprovalStatus.APPROVED),
                new UserFit(1L), new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        mockitoGetCompanyProfileInfo(companyId, authentication, owners, null);
        Assert.assertEquals(companyId, ((CompanyProfileDTO) businessCompanyService.getCompanyProfileInfo(companyId, authentication).getData()).getCompany().getId());
    }

    @Test
    public void getCompanyProfileInfo_whenStaffIsNullCompany_thenReturnSuccess() {
        Long companyId = 1L;
        Authentication authentication = initAuthentication();
        List<Staff> owners = Lists.newArrayList(new Staff(1L, null,
                new UserFit(1L), new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        List<CompanyJoinRequest> companyJoinRequests = Lists.newArrayList(new CompanyJoinRequest(1L, new Company(1L)));
        mockitoGetCompanyProfileInfo(companyId, authentication, owners, companyJoinRequests);
        Assert.assertEquals(companyId, ((CompanyProfileDTO) businessCompanyService.getCompanyProfileInfo(companyId, authentication).getData()).getCompany().getId());
    }

    @Test
    public void getCompanyProfileInfo_whenStaffIsNullCompanyAndCompanyRequestIsNullCompany_thenReturnErrorException() {
        thrown.expect(NoPermissionException.class);
        Long companyId = 1L;
        Authentication authentication = initAuthentication();
        List<Staff> owners = Lists.newArrayList(new Staff(1L, null,
                new UserFit(1L), new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        List<CompanyJoinRequest> companyJoinRequests = Lists.newArrayList(new CompanyJoinRequest(1L));
        mockitoGetCompanyProfileInfo(companyId, authentication, owners, companyJoinRequests);
        businessCompanyService.getCompanyProfileInfo(companyId, authentication);
    }

    @Test
    public void switchCompany_whenHavingPendingJoinRequestCompany_thenReturnSuccess() {
        Long companyId = 1L;
        Authentication authentication = initAuthentication();
        mockitoSwitchCompany(companyId, authentication);
        Assert.assertEquals(companyId, ((CompanyProfileDTO) businessCompanyService.switchCompany(companyId, authentication).getData()).getCompany().getId());
    }

    @Test
    public void searchCompanyByNameForJoinCompany_whenInputIsValid_thenReturnSuccess() {
        String keyword = "abc";
        int page = 0;
        int size = 10;
        Authentication authentication = initAuthentication();
        mockitoSearchCompanyByNameForJoinCompany(keyword, page, size, authentication);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessCompanyService.searchCompanyByNameForJoinCompany(keyword, page, size, authentication).getCode());
    }

    /*============================= Preparation for testing ================================*/

    public void mockitoGetWorkingCompanies() {
        Long userId = 1L;
        List<Company> companies = Lists.newArrayList(new Company(1L, ApprovalStatus.APPROVED), new Company(2L, ApprovalStatus.APPROVED), new Company(3L, ApprovalStatus.APPROVED), new Company(4L, ApprovalStatus.PENDING));
        Object[] item1 = {BigDecimal.valueOf(1), BigDecimal.valueOf(0)}; //first element is companyId, second element is status
        Object[] item2 = {BigDecimal.valueOf(2), BigDecimal.valueOf(1)};
        List<Object[]> companyOfJoinRequests = new ArrayList<>();
        companyOfJoinRequests.add(item1);
        companyOfJoinRequests.add(item2);

        Mockito.when(companyService.findCompanyOfStaffOrJoinRequestByUserId(userId)).thenReturn(companies);
        Mockito.when(companyJoinRequestService.findCompanyIdWithPendingAndApprovedJoinRequestByUserId(userId))
                .thenReturn(companyOfJoinRequests);
    }

    public void mockitoGetCompanyProfileInfo(Long companyId, Authentication authentication, List<Staff> owners, List<CompanyJoinRequest> companyJoinRequests) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = user.getId();

        Mockito.when(staffService.findByUserProfileAndCompany(companyId, userId)).thenReturn(owners);
        Mockito.when(staffService.countByCompany(companyId)).thenReturn(2);
        Mockito.when(locationService.countByCompany(companyId)).thenReturn(2);
        Mockito.when(vacancyService.countOpeningByUserAndCompany(userId, companyId)).thenReturn(1);

        Mockito.when(companyJoinRequestService.findPendingJoinRequestByCompanyAndUserProfile(companyId, userId)).thenReturn(companyJoinRequests);
        Mockito.when(companyService.countPendingJoinedCompanyRequestAndCompanyAuthorizationRequestByUser(userId)).thenReturn(1);
    }

    public void mockitoSwitchCompany(Long companyId, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        List<CompanyJoinRequest> pendingJoinRequestCompany = Lists.newArrayList(new CompanyJoinRequest(1L));
        Mockito.when(companyJoinRequestService.findPendingJoinRequestByCompanyAndUserProfile(companyId, user.getId())).thenReturn(pendingJoinRequestCompany);
        Mockito.when(businessValidatorService.checkExistsStaffInCompany(companyId, user.getId())).thenReturn(new Staff(1L));
        Mockito.doNothing().when(userFitService).updateDefaultCompany(user.getId(), companyId);
        Mockito.doNothing().when(userAccessTokenService).updateCompanyByAccessToken(user.getToken(), companyId);

        List<Staff> owners = Lists.newArrayList(new Staff(1L, new Company(1L, ApprovalStatus.APPROVED),
                new UserFit(1L), new RoleCompany(CompanyRole.ADMIN.getCode(), CompanyRole.ADMIN.getName())));
        mockitoGetCompanyProfileInfo(companyId, authentication, owners, new ArrayList<>());
    }

    public void mockitoSearchCompanyByNameForJoinCompany(String keyword, int page, int size, Authentication authentication) {
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();
        Page<CompanyDoc> companyDocs = new PageImpl<>(Lists.newArrayList(new CompanyDoc(1L)), PageRequest.of(page, size), 1);
        Mockito.when(companyDocService.searchFullTextByNameExceptStaff(keyword, page, size, user.getId())).thenReturn(companyDocs);

    }
}
