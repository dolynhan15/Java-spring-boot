package com.qooco.boost.business.impl;

import com.qooco.boost.business.QoocoService;
import com.qooco.boost.controllers.BaseMvcTest;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.request.authorization.ForgotPasswordReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.qooco.boost.constants.StatusConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/26/2018 - 11:19 AM
*/
class QoocoServiceImplTests extends BaseMvcTest {

    @Autowired
    private QoocoService qoocoService;

    @Test
    void loginToQoocoSystem_whenCallQoocoLoginApi_thenReturnLoginResponse() {
        assertNotNull(qoocoService.loginToQoocoSystem("phuc", "123123"));
    }

    @Test
    void verifyCodeToQoocoSystem_whenCallQoocoVerifyCodeApi_thenReturnVerifyCodeResponse() {
        assertNotNull(qoocoService.verifyCodeToQoocoSystem("trungmhv@gmail.com", "1234"));
    }

    @Test
    void retrievePassword_whenCallQoocoRetrievePasswordApi_thenReturnForgotPasswordResponse() {
        assertNotNull(qoocoService.retrievePassword("longntran", "long.tran@axonactive.com"));
    }

    @Test
    void doForgotPassword_whenNotExistUsername_thenReturnNotFoundError() {
        var forgotPasswordReq = new ForgotPasswordReq("not_exist_username", "");

        try {
            qoocoService.retrievePassword(forgotPasswordReq.getUsername(), forgotPasswordReq.getEmail());
        } catch (InvalidParamException ex) {
            assertEquals(FORGOT_PASSWORD_NOT_FOUND_CODE, ex.getStatus().getCode());
        }
    }

    @Test
    void doForgotPassword_whenInputCorrectUsername_thenReturnForgotPasswordResponse() {
        var forgotPasswordReq = new ForgotPasswordReq("longntran22", "");

        var forgotPasswordResp = qoocoService.retrievePassword(
                forgotPasswordReq.getUsername(), forgotPasswordReq.getEmail());
        assertEquals(SUCCESS, forgotPasswordResp.getCode());
    }

    @Test
    void doForgotPassword_whenNotFoundUsername_thenReturnNotFoundError() {
        var forgotPasswordReq = new ForgotPasswordReq("not_found_username", "");

        try {
            qoocoService.retrievePassword(forgotPasswordReq.getUsername(), forgotPasswordReq.getEmail());
        } catch (InvalidParamException ex) {
            assertEquals(FORGOT_PASSWORD_NOT_FOUND_CODE, ex.getStatus().getCode());
        }
    }

    @Test
    void doForgotPassword_whenNotExistEmail_thenReturnNotExistError() {
        var forgotPasswordReq = new ForgotPasswordReq(null, "notexist@gmail.com");

        try {
            qoocoService.retrievePassword(forgotPasswordReq.getUsername(), forgotPasswordReq.getEmail());
        } catch (InvalidParamException ex) {
            assertEquals(FORGOT_PASSWORD_USER_NOT_EXIST_CODE, ex.getStatus().getCode());
        }
    }

    @Test
    void doForgotPassword_whenEmailWasRegisterForManyAccount_thenReturnUnknownError() {
        var forgotPasswordReq = new ForgotPasswordReq(
                "", "long.tran@axonactive.com");

        try {
            qoocoService.retrievePassword(forgotPasswordReq.getUsername(), forgotPasswordReq.getEmail());
        } catch (InvalidParamException ex) {
            assertEquals(FORGOT_PASSWORD_UNKNOWN_CODE, ex.getStatus().getCode());
        }
    }
}
