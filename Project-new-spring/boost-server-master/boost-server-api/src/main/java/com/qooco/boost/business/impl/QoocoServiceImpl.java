package com.qooco.boost.business.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qooco.boost.business.QoocoService;
import com.qooco.boost.constants.Api;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.QoocoApiConstants;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.qooco.*;
import com.qooco.boost.utils.CryptUtils;
import com.qooco.boost.utils.HttpHelper;
import com.qooco.boost.utils.StringUtil;
import lombok.experimental.FieldNameConstants;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/25/2018 - 3:37 PM
*/
@Service
@FieldNameConstants
public class QoocoServiceImpl implements QoocoService {
    private HttpHeaders headers;

    //Service link from other system
    @Value(ApplicationConstant.BOOST_PATA_QOOCO_API_PATH)
    private String qoocoApiPath = "";

    @Override
    public LoginResponse loginToQoocoSystem(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        LoginRequest userLoginRequest = new LoginRequest(username, password);
        Gson gson = new GsonBuilder().create();
        String mapBody = QoocoApiConstants.REPORT_DATA.replace(
                QoocoApiConstants.REPLACEMENT_REQUEST_BODY, gson.toJson(userLoginRequest));
        return HttpHelper.doPost(getQoocoApiPath(Api.SERVICE_LOGIN_SYSTEM_ACCOUNT), mapBody, headers, LoginResponse.class, false);
    }

    @Override
    public LoginResponse socialLogin(SocialLoginReq loginReq) {
        return doPost(getQoocoApiPath(Api.SERVICE_LOGIN_WITH_FACEBOOK_ACCOUNT), loginReq, LoginResponse.class);
    }

    @Override
    public QoocoResponseBase verifyCodeToQoocoSystem(String contact, String code) {
        VerificationCode verificationCode = new VerificationCode(contact, code);
        return doPost(getQoocoApiPath(Api.SERVICE_VERIFY_SIGN_UP_CODE), verificationCode, QoocoResponseBase.class);
    }

    @Override
    public QoocoResponseBase generateCodeToQoocoSystem(String email) {
        SendVerificationCode sendVerificationCode = new SendVerificationCode(email, QoocoApiConstants.LOCALE_US);
        return doPost(getQoocoApiPath(Api.SERVICE_GENERATE_SIGN_UP_CODE), sendVerificationCode, QoocoResponseBase.class);
    }

    private <T> T doPost(String url, Object request, Class<T> clazz){
        if (Objects.isNull(headers)) {
            headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        return doPost(url, request, headers, clazz);
    }

    private <T> T doPost(String url, Object request, HttpHeaders headers, Class<T> clazz){
        Gson gson = new GsonBuilder().create();
        String mapBody = QoocoApiConstants.REPORT_DATA.replace(QoocoApiConstants.REPLACEMENT_REQUEST_BODY, gson.toJson(request));
        return HttpHelper.doPost(url, mapBody, headers, clazz, false);
    }

    @Override
    public BaseResp retrievePassword(String username, String email) {
        String forgotPasswordApiUrl = getRetrievePasswordPath(username, email);
        QoocoForgotPasswordResp qoocoForgotPasswordResp = HttpHelper
                .doGet(forgotPasswordApiUrl, QoocoForgotPasswordResp.class, false);

        return handleRetrievePasswordResponse(qoocoForgotPasswordResp);
    }

    private String getRetrievePasswordPath(String username, String email) {
        if (StringUtils.isNotBlank(username)) {
            return getQoocoApiPath(Api.SERVICE_FORGOT_PASSWORD_USERNAME)
                    .replace(QoocoApiConstants.USERNAME_DATA, username);
        }

        if (StringUtils.isNotBlank(email)) {
            return getQoocoApiPath(Api.SERVICE_FORGOT_PASSWORD_EMAIL)
                    .replace(QoocoApiConstants.EMAIL_DATA, email);
        }
        return StatusConstants.FORGOT_PASSWORD_EMPTY_STRING;
    }

    private BaseResp handleRetrievePasswordResponse(QoocoForgotPasswordResp forgotPasswordResp) {
        if (Objects.isNull(forgotPasswordResp)) {
            throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }

        switch (forgotPasswordResp.getErrorCode()) {
            case QoocoApiConstants.OK_CODE:
                return new BaseResp<>(forgotPasswordResp.getContact(), ResponseStatus.SUCCESS);

            case QoocoApiConstants.FORGOT_PASSWORD_UNKNOWN_CODE:
                throw new InvalidParamException(ResponseStatus.FORGOT_PASSWORD_UNKNOWN);

            case QoocoApiConstants.FORGOT_PASSWORD_USER_NOT_EXIST_CODE:
                throw new InvalidParamException(ResponseStatus.FORGOT_PASSWORD_USER_NOT_EXIST);

            case QoocoApiConstants.FORGOT_PASSWORD_NOT_FOUND_CODE:
                throw new InvalidParamException(ResponseStatus.FORGOT_PASSWORD_NOT_FOUND);

            case QoocoApiConstants.FORGOT_PASSWORD_USER_NAME_NOT_FOUND:
                throw new InvalidParamException(ResponseStatus.USERNAME_NOT_EXIST);

            default:
                throw new InvalidParamException(ResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getQoocoApiPath(String servicePath){
        return  StringUtil.append(qoocoApiPath, servicePath);
    }

    @Override
    public QoocoResponseBase assignAssessment(long userId, long assessmentId) {
        String userIdData = String.valueOf(userId);
        String lessonIdData = String.valueOf(assessmentId);
        String signIdData = CryptUtils.sha1(userIdData.concat(lessonIdData));
        String assignLesson = Api.SERVICE_ASSIGN_LESSON_TO_USER.replace(QoocoApiConstants.USER_ID_DATA, userIdData)
                .replace(QoocoApiConstants.LESSON_ID_DATA, lessonIdData)
                .replace(QoocoApiConstants.SIGN_ID_DATA, signIdData);
        String assignAssessmentUrl = getQoocoApiPath(assignLesson);
        return HttpHelper.doGetParseString(assignAssessmentUrl, QoocoResponseBase.class, false);
    }

}
