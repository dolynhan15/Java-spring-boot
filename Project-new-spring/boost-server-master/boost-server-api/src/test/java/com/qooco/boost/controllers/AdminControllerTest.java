package com.qooco.boost.controllers;

import com.qooco.boost.models.request.AppVersionReq;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.Const.Platform.IOS;
import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.URLConstants.ADMIN_PATH;
import static com.qooco.boost.constants.URLConstants.APP_VERSION_PATH;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.dto.AppVersionDTO.Fields.appVersion;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AdminControllerTest extends BaseMvcTest {
    @Test
    void should_not_get_app_version_without_permission() throws Exception {
        mvc.perform(get(ADMIN_PATH + APP_VERSION_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + code).value(NO_PERMISSION_TO_ACCESS));
    }

    @Test
    void should_not_get_app_version_without_authentication() throws Exception {
        mvc.perform(get(ADMIN_PATH + APP_VERSION_PATH)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_APP_VERSION + "(5,'com.boost.fit','ios',0,to_timestamp('21-MAR-19 07.50.15.450000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('21-MAR-19 09.32.25.563000000 AM','DD-MON-RR HH.MI.SSXFF AM'),50,null)"
    })
    void should_get_app_version_correctly() throws Exception {
        mvc.perform(get(ADMIN_PATH + APP_VERSION_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5")).andExpect(jsonPath(ROOT + data + INDEX0 + appVersion).value(50));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)"
    })
    void should_post_app_version_correctly() throws Exception {
        var appVersionReq = AppVersionReq.builder()
                .appId(PROFILE_APP.appId())
                .appVersion(51)
                .appVersionName("2.0.9")
                .os(IOS.toLowerCase())
                .build();
        mvc.perform(post(ADMIN_PATH + APP_VERSION_PATH)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appVersionReq))
        ).andExpect(jsonPath(ROOT + data + '.' + appVersion).value(appVersionReq.getAppVersion()));
    }

    @Test
    void should_not_post_app_version_without_authentication() throws Exception {
        mvc.perform(post(ADMIN_PATH + APP_VERSION_PATH)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void should_not_post_app_version_without_permission() throws Exception {
        mvc.perform(post(ADMIN_PATH + APP_VERSION_PATH)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AppVersionReq()))
        ).andExpect(jsonPath(ROOT + code).value(NO_PERMISSION_TO_ACCESS));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_APP_VERSION + "(4,'com.boost.profile','ios',0,to_timestamp('05-JUL-18 12.47.08.090000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('05-JUL-18 12.47.08.090000000 PM','DD-MON-RR HH.MI.SSXFF AM'),288,'2.0.6')"
    })
    void should_put_app_version_correctly() throws Exception {
        var appVersionReq = AppVersionReq.builder()
                .appId(PROFILE_APP.appId())
                .appVersion(51)
                .appVersionName("2.0.9")
                .os(IOS.toLowerCase())
                .build();
        mvc.perform(put((ADMIN_PATH + APP_VERSION_PATH) + "/4")
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appVersionReq))
        ).andExpect(jsonPath(ROOT + data + '.' + appVersion).value(appVersionReq.getAppVersion()));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)"
    })
    void should_not_put_app_version_not_found() throws Exception {
        mvc.perform(put((ADMIN_PATH + APP_VERSION_PATH) + "/7")
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AppVersionReq()))
        ).andExpect(jsonPath(ROOT + code).value(NOT_FOUND));
    }

    @Test
    void should_not_put_app_version_without_authentication() throws Exception {
        mvc.perform(put((ADMIN_PATH + APP_VERSION_PATH) + "/7")).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void should_not_put_app_version_without_permission() throws Exception {
        mvc.perform(put((ADMIN_PATH + APP_VERSION_PATH) + "/7")
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new AppVersionReq()))
        ).andExpect(jsonPath(ROOT + code).value(NO_PERMISSION_TO_ACCESS));
    }
}
