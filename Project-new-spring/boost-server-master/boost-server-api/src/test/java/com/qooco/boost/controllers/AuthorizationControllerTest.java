package com.qooco.boost.controllers;

import com.qooco.boost.models.request.authorization.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.Const.Platform.IOS;
import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.controllers.AuthorizationController.HEADER_APP_ID;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthorizationControllerTest extends BaseMvcTest {
    @Test
    void doVerifyCode_whenCodeIsRight_thenReturnSuccess() throws Exception {
        var request = VerifyCodeReq.builder().email("trungmhv@gmail.com").code("1243").build();
        mvc.perform(post(AUTH_PATH + VERIFY_CODE_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS));
    }

    @Test
    void doVerifyCode_whenCodeIsWrong_thenReturnFail() throws Exception {
        var request = VerifyCodeReq.builder().email("trungmhv@gmail.com").code("124322").build();
        mvc.perform(post(AUTH_PATH + VERIFY_CODE_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath(ROOT + code).value(VERIFICATION_CODE_EXPIRED));
    }

    @Test
    void doGenerateCode_whenEmailIsRight_thenReturnSuccess() throws Exception {
        var request = GenerateCodeReq.builder().email("someoneemail@gmail.com").locale("en_US").build();
        mvc.perform(post(AUTH_PATH + GENERATE_CODE_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS));
    }

    @Test
    void doGenerateCode_whenEmailIsWrong_thenReturnEmailWrongFormatError() throws Exception {
        var request = GenerateCodeReq.builder().email("abc@123").locale("en_US").build();
        mvc.perform(post(AUTH_PATH + GENERATE_CODE_PATH).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath(ROOT + code).value(EMAIL_WRONG_FORMAT));
    }

    @Test
    void signUpWithSystemAccount_whenEmailIsNull_thenReturnEmailEmptyError() throws Exception {
        mvc.perform(post(AUTH_PATH + SIGN_UP_WITH_SYSTEM).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(SignUpSystemReq.builder().build())))
                .andExpect(jsonPath(ROOT + code).value(EMAIL_IS_EMPTY));
    }

    @Test
    void signUpWithSystemAccount_whenEmailIsEmpty_thenReturnEmailEmptyError() throws Exception {
        mvc.perform(post(AUTH_PATH + SIGN_UP_WITH_SYSTEM).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(SignUpSystemReq.builder().email("").build())))
                .andExpect(jsonPath(ROOT + code).value(EMAIL_IS_EMPTY));
    }

    @Test
    void signUpWithSystemAccount_whenEmailWrongFormat_thenReturnEmailWrongFormatError() throws Exception {
        mvc.perform(post(AUTH_PATH + SIGN_UP_WITH_SYSTEM).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(SignUpSystemReq.builder().email("test.com").build())))
                .andExpect(jsonPath(ROOT + code).value(EMAIL_WRONG_FORMAT));
    }

    @Test
    void signUpWithSystemAccount() throws Exception {
        var request = SignUpSystemReq.builder().email("test@gmail.com").username("test").password("Abc123").build();
        mvc.perform(post(AUTH_PATH + SIGN_UP_WITH_SYSTEM).content(objectMapper.writeValueAsString(request))
                .header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void doLoginWithSystemAccount_whenValidLoginWithSystemAccount_thenReturnSuccessResponse() throws Exception {
        var request = UserLoginReq.builder().username("phuc1234").password("phuc1234").build();
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).content(objectMapper.writeValueAsString(request))
                .header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS))
                .andExpect(jsonPath("$.data.userProfile.username").value(request.getUsername()));
    }

    @Test
    void doLoginWithSystemAccount_whenInValidLoginWithSystemAccount_thenReturnErrorResponse() throws Exception {
        var request = UserLoginReq.builder().username("phuc12345").password("phuc1234").build();
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).content(objectMapper.writeValueAsString(request))
                .header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON))
                .andExpect(jsonPath(ROOT + code).value(USERNAME_NOT_EXIST));
    }

    @Test
    void doLogin_successful() throws Exception {
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(UserLoginReq.builder().username("abc").password("123").build())))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS));
    }

    @Test
    void doLoginv2_successful() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .clientInfo(ClientInfoReq.builder().appId(PROFILE_APP.appId()).appVersion("2.0.8").platform(IOS).build())
                .userLoginReq(UserLoginReq.builder().username("abc").password("123").build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(SUCCESS));
    }

    @Test
    void doLoginv2_app_id_empty() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .userLoginReq(UserLoginReq.builder().username("abc").password("123").build())
                .clientInfo(ClientInfoReq.builder().appVersion("2.0.8").platform(IOS).build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(APP_ID_IS_EMPTY));
    }

    @Test
    void doLoginv2_app_id_invalid() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .userLoginReq(UserLoginReq.builder().username("abc").password("123").build())
                .clientInfo(ClientInfoReq.builder().appVersion("2.0.8").platform(IOS).appId("rand").build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(APP_ID_INVALID));
    }

    @Test
    void doLoginv2_app_version_empty() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .userLoginReq(UserLoginReq.builder().username("abc").password("123").build())
                .clientInfo(ClientInfoReq.builder().platform(IOS).appId(PROFILE_APP.appId()).build()).build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(APP_VERSION_IS_EMPTY));
    }

    @Test
    void doLoginv2_platform_empty() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .userLoginReq(UserLoginReq.builder().username("abc").password("123").build())
                .clientInfo(ClientInfoReq.builder().appId(PROFILE_APP.appId()).appVersion("2.0.8").build()).build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(PLATFORM_IS_EMPTY));
    }

    @Test
    void doLogin_password_wrong() throws Exception {
        var req = UserLoginReq.builder().username("abc").password("rand").build();
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(PASSWORD_WRONG));
    }

    @Test
    void doLoginv2_password_wrong() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .clientInfo(ClientInfoReq.builder().appId(PROFILE_APP.appId()).appVersion("2.0.8").platform(IOS).build())
                .userLoginReq(UserLoginReq.builder().username("abc").password("rand").build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(PASSWORD_WRONG));
    }

    @Test
    void doLogin_username_empty() throws Exception {
        var req = UserLoginReq.builder().username("").build();
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(USERNAME_EMPTY));
    }

    @Test
    void doLoginv2_username_empty() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .clientInfo(ClientInfoReq.builder().appId(PROFILE_APP.appId()).appVersion("2.0.8").platform(IOS).build())
                .userLoginReq(UserLoginReq.builder().username("").build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(USERNAME_EMPTY));
    }

    @Test
    void doLogin_password_empty() throws Exception {
        var req = UserLoginReq.builder().username("abc").password("").build();
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(PASSWORD_IS_EMPTY));
    }

    @Test
    void doLoginv2_password_empty() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .clientInfo(ClientInfoReq.builder().appId(PROFILE_APP.appId()).appVersion("2.0.8").platform(IOS).build())
                .userLoginReq(UserLoginReq.builder().username("abc").password("").build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(PASSWORD_IS_EMPTY));
    }

    @Test
    void doLogin_username_nonexist() throws Exception {
        var req = UserLoginReq.builder().username("rand").password("123").build();
        mvc.perform(post(AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(USERNAME_NOT_EXIST));
    }

    @Test
    void doLoginv2_username_nonexist() throws Exception {
        var req = UserLoginWithClientInfoReq.builder()
                .clientInfo(ClientInfoReq.builder().appId(PROFILE_APP.appId()).appVersion("2.0.8").platform(IOS).build())
                .userLoginReq(UserLoginReq.builder().username("rand").password("123").build())
                .build();
        mvc.perform(post(VERSION_2 + AUTH_PATH + LOGIN_PATH).header(HEADER_APP_ID, PROFILE_APP.appId()).contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(jsonPath(ROOT + code).value(USERNAME_NOT_EXIST));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(3,201,'Buriram',null,'08',0)",
            INSERT_CITY + "(56,3,'Satuk',null,null,0)"
    })
    void doLogout_successful() throws Exception {
        var request = get(AUTH_PATH + LOGOUT_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER);
        mvc.perform(request).andExpect(jsonPath(ROOT + code).value(SUCCESS));
        mvc.perform(request).andExpect(jsonPath(ROOT + code).value(LOGIN_UNAUTHORIZED));
    }

    @Test
    void doLogout_with_invalid_token() throws Exception {
        mvc.perform(get(AUTH_PATH + LOGOUT_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "0000000000000000000000000000000000000000000000000000000000000000"))
                .andExpect(jsonPath(ROOT + code).value(LOGIN_UNAUTHORIZED));
    }

    @Test
    void doLogout_without_authentication() throws Exception {
        mvc.perform(get(AUTH_PATH + LOGOUT_PATH)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }
}
