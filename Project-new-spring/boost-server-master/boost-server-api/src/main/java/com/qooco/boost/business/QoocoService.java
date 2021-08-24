package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.qooco.LoginResponse;
import com.qooco.boost.models.qooco.QoocoResponseBase;
import com.qooco.boost.models.qooco.SocialLoginReq;

import javax.validation.constraints.NotNull;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/25/2018 - 3:37 PM
*/
public interface QoocoService {

    LoginResponse loginToQoocoSystem(@NotNull String username, @NotNull String password);

    LoginResponse socialLogin(SocialLoginReq loginReq);

    BaseResp retrievePassword(String username, String email);

    QoocoResponseBase generateCodeToQoocoSystem(@NotNull String email);

    QoocoResponseBase verifyCodeToQoocoSystem(@NotNull String contact, @NotNull String code);

    String getQoocoApiPath(String servicePath);

    QoocoResponseBase assignAssessment(@NotNull long userId, long assessmentId);
}
