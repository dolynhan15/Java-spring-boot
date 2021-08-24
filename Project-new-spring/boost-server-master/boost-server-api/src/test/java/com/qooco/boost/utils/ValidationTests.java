package com.qooco.boost.utils;

import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/25/2018 - 1:30 PM
*/

@RunWith(PowerMockRunner.class)
public class ValidationTests {

    @Test
    public void validateUsername_whenValidUsername_thenReturnResponseStatusSuccess() {
        Assert.assertEquals(Validation.validateUsername("phuc1234"), ResponseStatus.SUCCESS);
    }

    @Test
    public void validateUsername_whenInvalidUsername_thenReturnResponseStatusUsernameWrongFormat() {
        try {
            Validation.validateUsername("phuc.1234");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }

        try {
            Validation.validateUsername("phuc");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }

        try {
            Validation.validateUsername("phuc123456789phuc123");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validateUsername_whenEmptyUsername_thenReturnResponseStatusUsernameEmpty() {
        try {
            Validation.validateUsername(null);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.USERNAME_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validateUsername_whenValidPassword_thenReturnResponseStatusSuccess() {
        try {
            Validation.validatePassword("ABC1234");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validatePassword_whenInvalidPassword_thenReturnResponseStatusPasswordWrongFormat() {
        try {
            Validation.validatePassword("phuc.%^1234");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }

        try {
            Validation.validatePassword("1312");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }

        try {
            Validation.validatePassword("phuc123456789phuc123");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validatePassword_whenEmptyPassword_thenReturnResponseStatusPasswordEmpty() {
        try {
            Validation.validatePassword(null);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.PASSWORD_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validateImageExtension_whenRightImageContentType_thenReturnFalse() {
        Assert.assertFalse(Validation.validateImageContentType("image/adb"));
    }

    @Test
    public void validateImageExtension_whenRightImageExtension_thenReturnSuccess() {
        Assert.assertTrue(Validation.validateImageContentType("image/jpeg"));
        Assert.assertTrue(Validation.validateImageContentType("image/jpg"));
        Assert.assertTrue(Validation.validateImageContentType("image/bmp"));
    }

    @Test
    public void validateEmail_whenEmailWrong_thenReturnFail() {
        try {
            Validation.validateEmail(null);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }

        try {
            Validation.validateEmail("jpg@ffd@fdf.com");
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.EMAIL_WRONG_FORMAT.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validateEmail_whenEmailRight_thenReturnSuccess() {
        Assert.assertEquals(Validation.validateEmail("abc@gmail.com"), ResponseStatus.SUCCESS);
    }

    @Test
    public void validateVerifyCode_whenVerifyCodeWrong_thenReturnFail() {
        try {
            Validation.validateVerificationCode(null);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.VERIFICATION_CODE_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void validateVerifyCode_whenVerifyCodeWrong_thenReturnSuccess() {
        Assert.assertEquals(Validation.validateVerificationCode("12334"), ResponseStatus.SUCCESS);
    }

    @Test
    public void validateHttpOrHttps_whenDomainRight_thenReturnSuccess() {
        Assert.assertTrue(Validation.validateHttpOrHttps("https://www.google.com"));
        Assert.assertTrue(Validation.validateHttpOrHttps("http://www.google.com"));
        Assert.assertTrue(Validation.validateHttpOrHttps("www.google.com/dsds/dsds?dsd=dsd.png"));
        Assert.assertTrue(Validation.validateHttpOrHttps("http://google.cfdfdfdffm:8080/uploads/image/1000523106/bg_introduction_35662742282771580256.png"));


    }

    @Test
    public void validateHttpOrHttps_whenDomainWrong_thenReturnSuccess() {
        Assert.assertFalse(Validation.validateHttpOrHttps(null));
        Assert.assertFalse(Validation.validateHttpOrHttps("htt://www.google.com"));
        Assert.assertFalse(Validation.validateHttpOrHttps("://www.google.com"));
    }
}
