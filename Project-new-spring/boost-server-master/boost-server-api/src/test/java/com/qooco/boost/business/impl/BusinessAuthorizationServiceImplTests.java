package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessAuthorizationService;
import com.qooco.boost.business.QoocoService;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.UserAccessTokenService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.qooco.LoginResponse;
import com.qooco.boost.models.qooco.QoocoResponseBase;
import com.qooco.boost.models.request.authorization.ForgotPasswordReq;
import com.qooco.boost.models.request.authorization.GenerateCodeReq;
import com.qooco.boost.models.request.authorization.VerifyCodeReq;
import com.qooco.boost.utils.HttpHelper;
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

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/25/2018 - 2:42 PM
*/
@PowerMockIgnore({"javax.management.*", "javax.security.*", "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*"})
@RunWith(PowerMockRunner.class)
@PrepareForTest({ServletUriUtils.class, HttpHelper.class})
public class BusinessAuthorizationServiceImplTests {

    @InjectMocks
    private BusinessAuthorizationService businessAuthorizationService = new BusinessAuthorizationServiceImpl();

    @Mock
    private QoocoService qoocoService = Mockito.mock(QoocoService.class);

    @Mock
    private UserProfileService userProfileService = Mockito.mock(UserProfileService.class);

    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);

    @Mock
    private UserAccessTokenService userAccessTokenService = Mockito.mock(UserAccessTokenService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    @Test
    public void loginWithSystemAccount_whenEmptyUsername_thenReturnErrorUsernameEmpty() {
        try {
            businessAuthorizationService.loginWithSystemAccount("", "123113123", "");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            businessAuthorizationService.loginWithSystemAccount("  ", "123113123","");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void loginWithSystemAccount_whenEmptyPassword_thenReturnErrorPasswordEmpty() {
        try {
            businessAuthorizationService.loginWithSystemAccount("abc123", null,"");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            businessAuthorizationService.loginWithSystemAccount("abc123", "  ","");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void loginWithSystemAccount_whenNotExistedUsername_thenReturnUsernameNotExist() {
        LoginResponse notExistedUsername = new LoginResponse();
        notExistedUsername.setErrorCode(QoocoApiConstants.LOGIN_USERNAME_ERROR);
        Mockito.when(qoocoService.loginToQoocoSystem("phuc1234", "phuc1234"))
                .thenReturn(notExistedUsername);
        try {
            businessAuthorizationService.loginWithSystemAccount("phuc1234", "phuc1234","");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_NOT_EXIST.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void loginWithSystemAccount_whenWrongPassword_thenReturnWrongPassword() {
        LoginResponse passwordError = new LoginResponse();
        passwordError.setErrorCode(QoocoApiConstants.LOGIN_PASSWORD_ERROR);
        Mockito.when(qoocoService.loginToQoocoSystem("phuc1234", "phuc1234"))
                .thenReturn(passwordError);
        try {
            businessAuthorizationService.loginWithSystemAccount("phuc1234", "phuc1234","");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_WRONG.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void loginWithSystemAccount_whenServerError_thenReturnLoginServerError() {
        LoginResponse loginServerError = new LoginResponse();
        loginServerError.setErrorCode(QoocoApiConstants.LOGIN_SERVER_ERROR);
        Mockito.when(qoocoService.loginToQoocoSystem("phuc1234", "phuc1234"))
                .thenReturn(loginServerError);
        try {
            businessAuthorizationService.loginWithSystemAccount("phuc1234", "phuc1234","");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), ex.getStatus().getCode());
        }
    }

//    @Test
//    public void loginWithSystemAccount_whenMatchUsernamePassword_thenReturnLoginSuccess() {
//        LoginResponse loginSuccess = new LoginResponse();
//        UserProfile userProfile = new UserProfile(1L, "phuc1234");
//        Company company = new Company(1L);
//        loginSuccess.setUserId(1L);
//        loginSuccess.setUsername("phuc1234");
//        Mockito.when(userProfileService.findById(1L)).thenReturn(userProfile);
//        Mockito.when(companyService.getCompanyByUserProfileId(1L)).thenReturn(company);
//
//        PowerMockito.mockStatic(ServletUriUtils.class);
//        Mockito.when(ServletUriUtils.getAbsolutePath(Mockito.anyObject())).thenReturn("");
//
//        Long userProfileId = userProfile.getUserProfileId();
//        String accessToken = StringUtil.generateAccessToken(userProfile.getUsername(),
//                userProfileId.toString());
//        UserAccessToken userAccessToken = new UserAccessToken(accessToken, accessToken, userProfileId, userProfile);
//        Mockito.when(userAccessTokenService.save(userAccessToken)).thenReturn(userAccessToken);
//        Mockito.when(qoocoService.loginToQoocoSystem("phuc1234", "phuc1234")).thenReturn(loginSuccess);
//
//
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAuthorizationService.loginWithSystemAccount("phuc1234", "phuc1234").getCode());
//    }

    /*=============== START: Unit test for verify verification code =========================*/
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void doVerifyCode_whenEmptyEmail_thenReturnErrorEmailEmpty() {

        VerifyCodeReq request = new VerifyCodeReq();
        try {
            request.setEmail(null);
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex){
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            request.setEmail("");
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex){
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }


        try {
            request.setEmail(" ");
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex){
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doVerifyCode_whenWrongFormatEmail_thenReturnErrorEmailWrongFormat() {
        VerifyCodeReq request = new VerifyCodeReq();
        try {
            request.setEmail("trungmhv.com");
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doVerifyCode_whenEmptyCode_thenReturnErrorCodeEmpty() {
        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trung@gmail.com");

        try {
            request.setCode(null);
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex){
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            request.setCode("");
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex){
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            request.setCode(" ");
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex){
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doVerifyCode_whenCodeNotFound_thenReturnErrorCodeNotFound() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.VERIFICATION_CODE_NOT_FOUND);

        Mockito.when(qoocoService.verifyCodeToQoocoSystem("trungmhv@gmail.com", "1234")).thenReturn(responseBase);

        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trungmhv@gmail.com");
        request.setCode("1234");
        try {
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_NOT_FOUND.getCode(), ex.getStatus().getCode());
        }
      }

    @Test
    public void doVerifyCode_whenCodeNotMatch_thenReturnErrorCodeNotMatch() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.VERIFICATION_CODE_NOT_MATCH);

        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trungmhv@gmail.com");
        request.setCode("1234");

        Mockito.when(qoocoService.verifyCodeToQoocoSystem("trungmhv@gmail.com", "1234")).thenReturn(responseBase);

        try {
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_NOT_MATCH.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doVerifyCode_whenCodeExpired_thenReturnErrorCodeExpired() {

        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.VERIFICATION_CODE_EXPIRED);

        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trungmhv@gmail.com");
        request.setCode("1234");

        Mockito.when(qoocoService.verifyCodeToQoocoSystem("trungmhv@gmail.com", "1234")).thenReturn(responseBase);

        try {
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_EXPIRED.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doVerifyCode_whenServerError_thenReturnErrorFromServer() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.VERIFICATION_CODE_SERVER_ERROR);

        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trungmhv@gmail.com");
        request.setCode("1234");

        Mockito.when(qoocoService.verifyCodeToQoocoSystem("trungmhv@gmail.com", "1234")).thenReturn(responseBase);

        try {
            businessAuthorizationService.doVerifyCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doVerifyCode_whenRightInput_thenReturnSuccess() {

        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.OK_CODE);

        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trungmhv@gmail.com");
        request.setCode("1234");

        Mockito.when(qoocoService.verifyCodeToQoocoSystem("trungmhv@gmail.com", "1234")).thenReturn(responseBase);

        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), (businessAuthorizationService.doVerifyCode(request)).getCode());
    }


    /*=============== END: Unit test for verify verification code =========================*/

    @Test
    public void doGenerateCode_whenEmailIsNull_thenReturnEmailIsEmptyError() {
        GenerateCodeReq request = new GenerateCodeReq();
        try {
            request.setEmail(null);
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            request.setEmail("");
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            request.setEmail("   ");
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsWrongFormat_thenReturnEmailWrongFormatError() {
        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("abc.123");
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsExisted_thenReturnEmailIsExistedError() {
        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("congcan905@gmail.com");
        request.setLocale("en_US");

        QoocoResponseBase response = new QoocoResponseBase();
        response.setExist(true);
        mockitoStatic(response);
        mockito();
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EXISTED.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsCorrect_thenReturnInternalServerError() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.GET_CODE_INTERNAL_SERVER_ERROR);

        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("abc@123.abc");
        request.setLocale("en_US");
        mockitoStatic(responseBase);
        mockito();
        Mockito.when(qoocoService.generateCodeToQoocoSystem("abc@123.abc")).thenReturn(responseBase);
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsCorrect_thenReturnFailedToSendCodeError() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.GET_CODE_FAILED_TO_SEND);

        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("abc@123.abc");
        request.setLocale("en_US");
        mockitoStatic(responseBase);
        mockito();
        Mockito.when(qoocoService.generateCodeToQoocoSystem("abc@123.abc")).thenReturn(responseBase);
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.FAILED_TO_SEND_CODE.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsCorrect_thenReturnNetworkError() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.GET_CODE_NETWORK_ERROR);



        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("abc@123.abc");
        request.setLocale("en_US");
        mockitoStatic(responseBase);
        mockito();
        Mockito.when(qoocoService.generateCodeToQoocoSystem("abc@123.abc")).thenReturn(responseBase);
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.NETWORK_ERROR.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsCorrect_thenReturnBadRequestError() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(ResponseStatus.BAD_REQUEST.getCode());

        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("abc@123.abc");
        request.setLocale("en_US");
        mockitoStatic(responseBase);
        mockito();
        Mockito.when(qoocoService.generateCodeToQoocoSystem("abc@123.abc")).thenReturn(responseBase);
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.BAD_REQUEST.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsCorrect_butApiResponseIsNull_thenReturnBadRequestError() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("abc@123.abc");
        request.setLocale("en_US");
        mockitoStatic(responseBase);
        mockito();
        Mockito.when(qoocoService.generateCodeToQoocoSystem("abc@123.abc")).thenReturn(null);
        try {
            businessAuthorizationService.doGenerateCode(request);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.BAD_REQUEST.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doGenerateCode_whenEmailIsValid_thenReturnGenerateCodeSuccess() {
        QoocoResponseBase responseBase = new QoocoResponseBase();
        responseBase.setErrorCode(QoocoApiConstants.OK_CODE);

        GenerateCodeReq request = new GenerateCodeReq();
        request.setEmail("someemail@gmail.com");
        request.setLocale("en_US");
        mockitoStatic(responseBase);
        mockito();
        Mockito.when(qoocoService.generateCodeToQoocoSystem("someemail@gmail.com")).thenReturn(responseBase);
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAuthorizationService.doGenerateCode(request).getCode());
    }

    @Test
    public void doForgotPassword_whenEmptyUsernameAndEmail_thenReturnEmptyError() {
        try {
            businessAuthorizationService.doForgotPassword(new ForgotPasswordReq("",""));
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_OR_EMAIL_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void doForgotPassword_whenNotEmptyEmail_thenReturnBaseResponse() {
        ForgotPasswordReq forgotPasswordReq = new ForgotPasswordReq("", "test@gmail.com");

        Mockito.when(qoocoService.retrievePassword(forgotPasswordReq.getUsername(), forgotPasswordReq.getEmail()))
                .thenReturn(new BaseResp(ResponseStatus.SUCCESS));

        BaseResp forgotPasswordRes = businessAuthorizationService
                .doForgotPassword(new ForgotPasswordReq(forgotPasswordReq));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), forgotPasswordRes.getCode());
    }

//    @Test
//    public void doLogout_whenRequestRigthToken_thenReturnSuccess(){
//        AuthenticatedUser authenticatedUser = new AuthenticatedUser((long) 1, "trungmhv", "1234", Const.ApplicationId.CAREER_APP, null);
//        Mockito.doNothing().when(userAccessTokenService).deleteByToken(authenticatedUser.getToken());
//        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
//
//        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessAuthorizationService.doLogoutWithSystemAccount(authentication).getCode());
//    }
//
//    @Test
//    public void doLogout_whenRequestRightToken_thenReturnFail(){
//        AuthenticatedUser authenticatedUser = new AuthenticatedUser((long) 1, "trungmhv", "1234", Const.ApplicationId.CAREER_APP, null);
//        Mockito.doThrow(new EntityNotFoundException(ResponseStatus.NOT_FOUND)).when(userAccessTokenService).deleteByToken(authenticatedUser.getToken());
//        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
//        try {
//            businessAuthorizationService.doLogoutWithSystemAccount(authentication);
//        }catch (EntityNotFoundException ex){
//            Assert.assertEquals(ResponseStatus.NOT_FOUND.getCode(), ex.getStatus().getCode());
//        }
//    }

    private void mockitoStatic(QoocoResponseBase response){
        PowerMockito.mockStatic(HttpHelper.class);
        Mockito.when(HttpHelper.doGet(Mockito.anyString(), Mockito.anyObject(), Mockito.anyBoolean())).thenReturn(response);
    }

    private void mockito(){
        Mockito.when(qoocoService.getQoocoApiPath(Mockito.anyString())).thenReturn("");
    }
}
