package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessFitUserService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.enumeration.Gender;
import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.SupportConversationDocService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.user.FitUserReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import com.qooco.boost.threads.BoostActorManager;
import org.bson.types.ObjectId;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*", "javax.crypto"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({CipherKeys.class})
public class BusinessFitUserServiceImplTest extends BaseUserService {
    @InjectMocks
    private BusinessFitUserService businessFitUserService = new BusinessFitUserServiceImpl();
    @Mock
    private UserFitService userFitService = Mockito.mock(UserFitService.class);
    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);
    @Mock
    private LanguageService languageService = Mockito.mock(LanguageService.class);
    @Mock
    private UserFitLanguageService userFitLanguageService = Mockito.mock(UserFitLanguageService.class);
    @Mock
    private StaffService staffService = Mockito.mock(StaffService.class);
    @Mock
    private CompanyJoinRequestService companyJoinRequestService = Mockito.mock(CompanyJoinRequestService.class);
    @Mock
    private VacancyService vacancyService = Mockito.mock(VacancyService.class);
    @Mock
    private MessageDocService messageDocService = Mockito.mock(MessageDocService.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);
    @Mock
    private BoostActorManager boostActorManager = Mockito.mock(BoostActorManager.class);
    @Mock
    private UserAccessTokenService userAccessTokenService = Mockito.mock(UserAccessTokenService.class);
    @Mock
    private ConversationDocService conversationDocService = Mockito.mock(ConversationDocService.class);
    @Mock
    private SupportConversationDocService supportConversationDocService = Mockito.mock(SupportConversationDocService.class);
    @Mock
    private AppointmentDetailService appointmentDetailService = Mockito.mock(AppointmentDetailService.class);
    @Rule
    private ExpectedException thrown = ExpectedException.none();

    @Test
    public void saveBasicFitUser_whenUserIdIsNull_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        FitUserReq fitUserReq = new FitUserReq();
        businessFitUserService.saveBasicFitUser(fitUserReq, initAuthentication());
    }

    @Test
    public void saveBasicFitUser_whenExistedUserIsNullAndUserFirstNameReqIsBlank_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        FitUserReq fitUserReq = new FitUserReq(1L);
        mockitoBasicFitUser(fitUserReq);
        Mockito.when(userFitService.findById(fitUserReq.getId())).thenReturn(null);
        businessFitUserService.saveBasicFitUser(fitUserReq, initAuthentication());
    }

    @Test
    public void saveBasicFitUser_whenUserLastNameReqIsBlank_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        FitUserReq fitUserReq = new FitUserReq(1L);
        fitUserReq.setFirstName("Marc");
        mockitoBasicFitUser(fitUserReq);
        businessFitUserService.saveBasicFitUser(fitUserReq, initAuthentication());
    }

    @Test
    public void saveBasicFitUser_whenUserBirthdayReqIsBlank_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        FitUserReq fitUserReq = new FitUserReq(1L);
        fitUserReq.setFirstName("Marc");
        fitUserReq.setLastName("Donal");
        mockitoBasicFitUser(fitUserReq);
        businessFitUserService.saveBasicFitUser(fitUserReq, initAuthentication());
    }

    @Test
    public void saveBasicFitUser_whenInputsAreValid_thenReturnSuccess() {
        FitUserReq fitUserReq = new FitUserReq(1L);
        fitUserReq.setAvatar("http://192.168.78.48:8080/1000526864/avatar/7CybnbNl4815763378175889137.jpg");
        fitUserReq.setGender(Gender.OTHER);
        fitUserReq.setFirstName("Ly");
        fitUserReq.setLastName("Do");
        fitUserReq.setBirthday(new Date(826675200000L));
        fitUserReq.setProfileStep(6);
        mockitoBasicFitUser(fitUserReq);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessFitUserService.saveBasicFitUser(fitUserReq, initAuthentication()).getCode());
    }

    @Test
    public void saveAdvancedFitUser_whenNativeLanguageIdsSizeLessThanInput_thenReturnErrorException() {
        thrown.expect(EntityNotFoundException.class);
        FitUserReq fitUserReq = new FitUserReq(1L);
        long[] nativeLanguages = {1L, 2L};
        fitUserReq.setNativeLanguageIds(nativeLanguages);
        mockitoAdvancedFitUser(fitUserReq);
        Mockito.when(languageService.findByIds(fitUserReq.getNativeLanguageIds())).thenReturn(Lists.newArrayList(new Language(1L)));
        businessFitUserService.saveAdvancedFitUser(fitUserReq, initAuthentication());
    }

    @Test
    public void saveAdvancedFitUser_whenInputsAreValid_thenReturnSuccess() {
        FitUserReq fitUserReq = new FitUserReq(1L);
        long[] nativeLanguages = {1L, 2L};
        fitUserReq.setNativeLanguageIds(nativeLanguages);
        fitUserReq.setPhoneNumber("0980878427");
        fitUserReq.setAddress("123 Do Quang");
        fitUserReq.setNationalId("DFT20182");
        fitUserReq.setPersonalPhotos(Lists.newArrayList("http://192.168.78.48:8080/1000526864/avatar/7CybnbNl4815763378175889137.jpg"));
        fitUserReq.setProfileStep(6);
        mockitoAdvancedFitUser(fitUserReq);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessFitUserService.saveAdvancedFitUser(fitUserReq, initAuthentication()).getCode());
    }

//    @Test
//    public void getRecruiterInfo_whenCompanyAndJoinRequestAreValid_thenReturnSuccess() {
//        mockitoGetRecruiterInfo();
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessFitUserService.getRecruiterInfo(1L).getCode());
//    }
//
//    @Test
//    public void getRecruiterInfo_whenJoinRequestCreatedDateAfterCompanyCreatedDate_thenReturnSuccess() {
//        Long userId = 1L;
//        mockitoGetRecruiterInfo();
//        Page<CompanyJoinRequest> companyJoinRequests = new PageImpl<>(
//                Lists.newArrayList(new CompanyJoinRequest(1L, new Date(1552176000000L), new Company(1L, "Bright Hotel"))),
//                PageRequest.of(0, 1), 1);
//        Mockito.when(companyJoinRequestService.findLastRequest(userId, 0, 1)).thenReturn(companyJoinRequests);
//        Page<Company> companies = new PageImpl<>(Lists.newArrayList(new Company(1L, new Date(1551398400000L))), PageRequest.of(0, 1), 1);
//        Mockito.when(companyService.findLastPendingCompanyByUser(userId, 0, 1)).thenReturn(companies);
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessFitUserService.getRecruiterInfo(1L).getCode());
//    }
//
//    @Test
//    public void getRecruiterInfo_whenCompanyIsNull_thenReturnSuccess() {
//        Long userId = 1L;
//        mockitoGetRecruiterInfo();
//        Page<Company> companies = new PageImpl<>(Lists.newArrayList(), PageRequest.of(0, 1), 0);
//        Mockito.when(companyService.findLastPendingCompanyByUser(userId, 0, 1)).thenReturn(companies);
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessFitUserService.getRecruiterInfo(1L).getCode());
//    }
//
//    @Test
//    public void getRecruiterInfo_whenJoinRequestIsNull_thenReturnSuccess() {
//        Long userId = 1L;
//        mockitoGetRecruiterInfo();
//        Page<CompanyJoinRequest> companyJoinRequests = new PageImpl<>(Lists.newArrayList(), PageRequest.of(0, 1), 0);
//        Mockito.when(companyJoinRequestService.findLastRequest(userId, 0, 1)).thenReturn(companyJoinRequests);
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessFitUserService.getRecruiterInfo(1L).getCode());
//    }

    @Test
    public void uploadPublicKey_whenPublicKeyIsNull_thenReturnErrorException() {
        thrown.expect(InvalidParamException.class);
        AuthenticatedUser user = initAuthenticatedUser();
        UserUploadKeyReq keyReq = new UserUploadKeyReq();
        businessFitUserService.uploadPublicKey(user, keyReq);
    }
//
//    @Test
//    public void uploadPublicKey_whenInputsAreValid_thenReturnSuccess() {
//        AuthenticatedUser user = initAuthenticatedUser();
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4aHJYB3j0JKy5iGZfnfMvfwA3AI";
//        UserUploadKeyReq keyReq = new UserUploadKeyReq(publicKey);
//        mockitoUploadPublicKey();
//        businessFitUserService.uploadPublicKey(user, keyReq);
//    }

//    @Test
//    public void uploadPublicKey_whenSecretKeyIsNullAndDifferPublicKey_thenReturnSuccess() {
//        AuthenticatedUser user = initAuthenticatedUser();
//        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4aHJYB3j0JKy5iGZfnfMvfwA3AI";
//        String aesKey = "nNkKq73jVu3UZZDT";
//        UserUploadKeyReq keyReq = new UserUploadKeyReq(publicKey);
//        mockitoUploadPublicKey();
//        ObjectId conversationId = new ObjectId("5c22ede7f11a2e447e61cd64");
//        PublicKeyEmbedded publicKeyEmbedded = new PublicKeyEmbedded(publicKey, aesKey);
//        publicKeyEmbedded.setEncryptedPublicKey("Rt5H5cMZzl8fc6cfb5ahkU74U5C9XJIa05NYm2Gf20e");
//        Map<String, PublicKeyEmbedded> userKeys = new HashMap<>();
//        userKeys.put(user.getToken(), publicKeyEmbedded);
//        List<ConversationDoc> conversationDocs = Lists.newArrayList(new ConversationDoc(conversationId, "", userKeys));
//        Mockito.when(conversationDocService.findNotHavePublicKeyByToken(user.getId(), user.getToken(), publicKey, 1)).thenReturn(conversationDocs);
//        businessFitUserService.uploadPublicKey(user, keyReq);
//    }

    /*====================================== Prepare for testing ========================================= */
    private void mockitoBasicFitUser(FitUserReq fitUserReq) {
        UserFit userFit = new UserFit(1L);
        UserProfile userProfile = new UserProfile(1L, "lyly123", "lyly123@yopmail.com");

        Mockito.when(userFitService.findById(fitUserReq.getId())).thenReturn(userFit);
        Mockito.when(userProfileService.findById(fitUserReq.getId())).thenReturn(userProfile);
        Mockito.when(userFitService.save(userFit)).thenReturn(userFit);
        Mockito.doNothing().when(boostActorManager).saveUserCvInMongo(userFit);
    }

    private void mockitoAdvancedFitUser(FitUserReq fitUserReq) {
        UserFit fitUserDoc = new UserFit(1L);
        Mockito.when(businessValidatorService.checkExistsUserFit(fitUserReq.getId())).thenReturn(fitUserDoc);
        Mockito.when(languageService.findByIds(fitUserReq.getNativeLanguageIds())).thenReturn(Lists.newArrayList(new Language(1L), new Language(2L)));
        Mockito.when(businessValidatorService.checkExistsCountry(fitUserReq.getCountryId())).thenReturn(new Country(1L));
        Mockito.when(userFitService.save(fitUserDoc)).thenReturn(fitUserDoc);
        Mockito.doNothing().when(userFitLanguageService).deleteUserLangByUserId(fitUserDoc.getUserProfileId());
        Mockito.doNothing().when(boostActorManager).saveUserCvInMongo(fitUserDoc);
    }

    private void mockitoGetRecruiterInfo() {
        Long userId = 1L;
        UserFit fitUserDoc = new UserFit(userId);
        Mockito.when(businessValidatorService.checkExistsUserFit(userId)).thenReturn(fitUserDoc);
        Mockito.when(staffService.findByUserProfileAndStatus(userId, ApprovalStatus.APPROVED)).thenReturn(Lists.newArrayList(new Staff(1L, new Company(1L))));
        Page<CompanyJoinRequest> companyJoinRequests = new PageImpl<>(
                Lists.newArrayList(new CompanyJoinRequest(1L, new Date(1552176000000L), new Company(1L, "Bright Hotel"))),
                PageRequest.of(0, 1), 1);
        Mockito.when(companyJoinRequestService.findLastRequest(userId, 0, 1)).thenReturn(companyJoinRequests);
        Page<Company> companies = new PageImpl<>(Lists.newArrayList(new Company(1L)), PageRequest.of(0, 1), 1);
        Mockito.when(companyService.findLastPendingCompanyCreatedByUser(userId, 0, 1)).thenReturn(companies);
        Mockito.when(vacancyService.countOpenVacancyByUserAndCompany(userId, 1L)).thenReturn(1);
        Mockito.when(messageDocService.countUserSendUnreadMessageByUserProfileId(userId, MessageConstants.RECEIVE_IN_HOTEL_APP)).thenReturn(1L);
        List<Long> roles = CompanyRole.valueOf(CompanyRole.ADMIN.getName()).getRolesSmallerOrAdminRole();
        Mockito.when(appointmentDetailService.countAvailableByUserProfileIdAndCompanyIdAndStatusesAndRoles(userId, 1L, AppointmentStatus.getAcceptedStatus(), roles)).thenReturn(1);
    }

    private void mockitoUploadPublicKey() {
        Long userId = 1L;
        String token = "1234";
        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoOMa9EQNKM7ePKaUxVZMW1BILIgda8ub+wED+6kLIWT1VfAqGfJzWzU9H+OmUPJu6K8lcsd/ffAL6tt17k3Gz+dklUUWhK2Vkz48yAdUBMmy52u44d0ULAnfbAQVjL3zRbEeyGHVls8OPQPxlcCBM/19ghDcjTB6PfIoCrNlbb0lxJB6lyzy6J936rvhhjsAKKKD1hvSaVdHU7k130n7RTu6I73PyIl6WaAAvApFSO18uRtUXkTQnbPr9iPeo/bMadtmz7uP9jxH9lAPaM9rk7szCOkgv7JztX+YyPNDvRP3/Bcy1ekkV/xWudk1Cnx0+ijf4WHnNeOlIuGF9IAH5QIDAQAB";
        String aesKey = "nNkKq73jVu3UZZDT";
        ObjectId conversationId = new ObjectId("5c22ede7f11a2e447e61cd64");
        PublicKeyEmbedded publicKeyEmbedded = new PublicKeyEmbedded(publicKey, aesKey);
        Map<String, PublicKeyEmbedded> userKeys = new HashMap<>();
        userKeys.put(token, publicKeyEmbedded);
        Mockito.doNothing().when(userAccessTokenService).updatePublicKeyByAccessToken(token, publicKey);
        List<ConversationDoc> conversationDocs = Lists.newArrayList(new ConversationDoc(conversationId, aesKey, userKeys));
        List<SupportConversationDoc> supportConversationDocs = Lists.newArrayList(new SupportConversationDoc().toBuilder().id(conversationId).secretKey(aesKey).userKeys(userKeys).build());
        Mockito.when(conversationDocService.findNotHavePublicKeyByToken(userId, token, publicKey, 1)).thenReturn(conversationDocs);
        Mockito.when(supportConversationDocService.findNotHavePublicKeyByToken(userId, token, publicKey, 1)).thenReturn(supportConversationDocs);
        PowerMockito.mockStatic(CipherKeys.class);
    }
}
