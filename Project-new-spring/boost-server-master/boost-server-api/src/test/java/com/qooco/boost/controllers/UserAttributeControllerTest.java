package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.PaginationConstants.DEFAULT_PAGINATION;
import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.ATTRIBUTE_LEVEL_UP;
import static com.qooco.boost.constants.URLConstants.ATTRIBUTE_PATH;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.UserProfile.Fields.userProfileId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.PagedResult.Fields.total;
import static com.qooco.boost.models.dto.attribute.AttributeDTO.Fields.score;
import static com.qooco.boost.models.dto.attribute.AttributeShortDTO.Fields.name;
import static com.qooco.boost.models.request.PageRequest.Fields.page;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class UserAttributeControllerTest extends BaseMvcTest {
    @Test
    void should_not_get_level_up_if_no_authen() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH + ATTRIBUTE_LEVEL_UP)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)"
    })
    void should_get_0_level_up() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH + ATTRIBUTE_LEVEL_UP).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80")).andExpect(jsonPath(ROOT + data, hasSize(0)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,1,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_1_level_up() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH + ATTRIBUTE_LEVEL_UP).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80")).andExpect(jsonPath(ROOT + data, hasSize(1)));
        mvc.perform(get(ATTRIBUTE_PATH + ATTRIBUTE_LEVEL_UP).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80")).andExpect(jsonPath(ROOT + data, hasSize(0)));
    }

    @Test
    void should_not_get_attributes_if_no_authen() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(26,'Critical Observation','Critical Observation',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_page_1() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(size, DEFAULT_PAGINATION))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + name).value("Teamwork"))
                .andExpect(jsonPath(ROOT + data + '.' + total).value(2));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                + ",(26,'Critical Observation','Critical Observation',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_page_2() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(page, "1").param(size, "1"))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + name).value("Critical Observation"))
                .andExpect(jsonPath(ROOT + data + '.' + total).value(2));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,33000,9,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_level_9_score_32000() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(size, DEFAULT_PAGINATION))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + score).value(32000))
                .andExpect(jsonPath(ROOT + data + '.' + total).value(1));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)"
                    + ",(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb','ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb',1,95892,null,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'DE1839CA37240928F3C014A01C337B8A','DE1839CA37240928F3C014A01C337B8A',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2c/W23+Z27jClUeMFtwKnESdsvuNlwd4IHXWHG3dl4u4O2Ifqf73vjswlSBTGCW30DSJkcYkyoTKQ4znHfc62LwREIMdfBwGnkhfmAPhkHQMuAMHzqYj+u6u6PWF35PxR7+ndoB4cI43qiNznOO2KvOyPIBMP0/eEtKKXCQlPITU+vSTz01BDGOUvAyXPkscl5xVQi6zsZ2ncwfPxM+daoOLgrzHXfca+vLr57Emekwg8N5obyWs/MyHlc3ECp1tA40idk07i5HbQjb8gYCofpu0E98NxeyfNB+8Zu4FSuzbgpXreoMQcQ/XzvHSrvzXdomtZfpD3hkENkYDlBa6ywIDAQAB',42438)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_with_user_profile_id_and_has_event_true() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb")
                .param(userProfileId, "1000526864")
                .param("hasLevel", TRUE.toString()))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + name).value("Teamwork"))
                .andExpect(jsonPath(ROOT + data + '.' + total).value(1));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)"
                + ",(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb','ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb',1,95892,null,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'DE1839CA37240928F3C014A01C337B8A','DE1839CA37240928F3C014A01C337B8A',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2c/W23+Z27jClUeMFtwKnESdsvuNlwd4IHXWHG3dl4u4O2Ifqf73vjswlSBTGCW30DSJkcYkyoTKQ4znHfc62LwREIMdfBwGnkhfmAPhkHQMuAMHzqYj+u6u6PWF35PxR7+ndoB4cI43qiNznOO2KvOyPIBMP0/eEtKKXCQlPITU+vSTz01BDGOUvAyXPkscl5xVQi6zsZ2ncwfPxM+daoOLgrzHXfca+vLr57Emekwg8N5obyWs/MyHlc3ECp1tA40idk07i5HbQjb8gYCofpu0E98NxeyfNB+8Zu4FSuzbgpXreoMQcQ/XzvHSrvzXdomtZfpD3hkENkYDlBa6ywIDAQAB',42438)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                + ",(24,'Adaptability','Adaptability',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_with_user_profile_id_and_has_event_false() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb")
                .param(userProfileId, "1000526864")
                .param("hasLevel", FALSE.toString())
                .param(size, DEFAULT_PAGINATION))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + name).value("Adaptability"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)"
                    + ",(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb','ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb',1,95892,null,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'DE1839CA37240928F3C014A01C337B8A','DE1839CA37240928F3C014A01C337B8A',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2c/W23+Z27jClUeMFtwKnESdsvuNlwd4IHXWHG3dl4u4O2Ifqf73vjswlSBTGCW30DSJkcYkyoTKQ4znHfc62LwREIMdfBwGnkhfmAPhkHQMuAMHzqYj+u6u6PWF35PxR7+ndoB4cI43qiNznOO2KvOyPIBMP0/eEtKKXCQlPITU+vSTz01BDGOUvAyXPkscl5xVQi6zsZ2ncwfPxM+daoOLgrzHXfca+vLr57Emekwg8N5obyWs/MyHlc3ECp1tA40idk07i5HbQjb8gYCofpu0E98NxeyfNB+8Zu4FSuzbgpXreoMQcQ/XzvHSrvzXdomtZfpD3hkENkYDlBa6ywIDAQAB',42438)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(24,'Adaptability','Adaptability',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_with_user_profile_id_null_and_has_event_false() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb")
                .param(userProfileId, (String)null)
                .param("hasLevel", FALSE.toString()))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + name).value("Adaptability"))
                .andExpect(jsonPath(ROOT + data + '.' + total).value(2));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)"
                    + ",(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb','ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb',1,95892,null,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('10-MAY-19 03.49.54.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'DE1839CA37240928F3C014A01C337B8A','DE1839CA37240928F3C014A01C337B8A',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2c/W23+Z27jClUeMFtwKnESdsvuNlwd4IHXWHG3dl4u4O2Ifqf73vjswlSBTGCW30DSJkcYkyoTKQ4znHfc62LwREIMdfBwGnkhfmAPhkHQMuAMHzqYj+u6u6PWF35PxR7+ndoB4cI43qiNznOO2KvOyPIBMP0/eEtKKXCQlPITU+vSTz01BDGOUvAyXPkscl5xVQi6zsZ2ncwfPxM+daoOLgrzHXfca+vLr57Emekwg8N5obyWs/MyHlc3ECp1tA40idk07i5HbQjb8gYCofpu0E98NxeyfNB+8Zu4FSuzbgpXreoMQcQ/XzvHSrvzXdomtZfpD3hkENkYDlBa6ywIDAQAB',42438)",
            INSERT_PROFILE_ATTRIBUTE + "(1,'Teamwork','Teamwork attribute defines your ability to work with other people to achieve a common goal',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)"
                    + ",(24,'Adaptability','Adaptability',0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,null)",
            INSERT_USER_ATTRIBUTE + "(1,1000526864,1,300,2,false,0,to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('16-JUL-19 02.57.07.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),NULL,NULL)"
    })
    void should_get_attributes_with_user_profile_id_null_and_has_event_true() throws Exception {
        mvc.perform(get(ATTRIBUTE_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "ce0860091a9b75d668e69e59dafc74136b8e011033fc03f404028c7a913297bb")
                .param(userProfileId, (String)null)
                .param("hasLevel", TRUE.toString()))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + name).value("Adaptability"))
                .andExpect(jsonPath(ROOT + data + '.' + total).value(2));
    }
}
