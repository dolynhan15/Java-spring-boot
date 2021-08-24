package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.Const.Platform.IOS;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static com.qooco.boost.constants.URLConstants.GET_METHOD;
import static com.qooco.boost.constants.URLConstants.APP_VERSION_PATH;
import static com.qooco.boost.constants.URLConstants.BOOST_API;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.dto.AppVersionDTO.Fields.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppVersionControllerTest extends BaseMvcTest {

    @Test
    @Sql(statements = INSERT_APP_VERSION + "(4,'com.boost.profile','ios',0,to_timestamp('05-JUL-18 12.47.08.090000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('05-JUL-18 12.47.08.090000000 PM','DD-MON-RR HH.MI.SSXFF AM'),288,'2.0.6')")
    void should_get_app_version_hasNewVersion_false() throws Exception {
        mvc.perform(get(APP_VERSION_PATH + GET_METHOD)
                .param(appId, PROFILE_APP.appId())
                .param(appVersion, "288")
                .param(os, IOS.toLowerCase())
        ).andExpect(jsonPath(ROOT + data + '.' + hasNewVersion).value(FALSE));
    }

    @Test
    @Sql(statements = INSERT_APP_VERSION + "(4,'com.boost.profile','ios',0,to_timestamp('05-JUL-18 12.47.08.090000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('05-JUL-18 12.47.08.090000000 PM','DD-MON-RR HH.MI.SSXFF AM'),288,'2.0.6')")
    void should_get_app_version_hasNewVersion_true() throws Exception {
        mvc.perform(get(APP_VERSION_PATH + GET_METHOD)
                .param(appId, PROFILE_APP.appId())
                .param(appVersion, "49")
                .param(os, IOS.toLowerCase())
        ).andExpect(jsonPath(ROOT + data + '.' + hasNewVersion).value(TRUE));
    }

    @Test
    void should_not_get_app_version_bad_request() throws Exception {
        mvc.perform(get(APP_VERSION_PATH + GET_METHOD)).andExpect(status().isBadRequest());
    }

    @Test
    void should_get_app_api_version_correctly() throws Exception {
        mvc.perform(get(APP_VERSION_PATH + BOOST_API + GET_METHOD)).andExpect(jsonPath(ROOT + data).value("2.0.8"));
    }
}
