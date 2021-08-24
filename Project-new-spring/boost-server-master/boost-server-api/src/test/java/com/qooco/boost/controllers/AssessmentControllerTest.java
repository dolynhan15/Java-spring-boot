package com.qooco.boost.controllers;

import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.mongo.entities.AssessmentTestHistoryDoc;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static com.qooco.boost.constants.StatusConstants.SUCCESS;
import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.Assessment.Fields.type;
import static com.qooco.boost.data.oracle.entities.UserProfile.Fields.userProfileId;
import static com.qooco.boost.data.oracle.entities.UserQualification.Fields.assessmentId;
import static com.qooco.boost.data.oracle.entities.UserQualification.Fields.scaleId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.dto.assessment.AssessmentHistoryDTO.Fields.testHistories;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class AssessmentControllerTest extends BaseMvcTest {
    @Test
    void should_not_delete_if_no_authen() throws Exception {
        mvc.perform(delete(ASSESSMENT_PATH + "/244273")).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)")
    void should_delete_correctly() throws Exception {
        mvc.perform(delete(ASSESSMENT_PATH + "/244273").header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + code).value(SUCCESS));
        mvc.perform(get(ASSESSMENT_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(0)));
    }

    @Test
    void should_not_get_if_no_authen() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)"
                    + ",(241755,'Assessment Demo - try before you buy',null,'/lessonData/qoocoTalk/images/packages_big/ltfb_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5732,48292,1400,600)",
            INSERT_ASSESSMENT_LEVEL + "(41597,'BC1','1',null,to_date('21-MAR-19','DD-MON-RR'),null,to_date('21-MAR-19','DD-MON-RR'),0,'BC','adult','1',244273,0)"
                + ",(41603,'BC2','2',null,to_date('21-MAR-19','DD-MON-RR'),null,to_date('21-MAR-19','DD-MON-RR'),0,'BC','adult','2',241755,0)"
    })
    void should_get_all_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)"
                    + ",(241755,'Assessment Demo - try before you buy',null,'/lessonData/qoocoTalk/images/packages_big/ltfb_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5732,48292,1400,600)",
            INSERT_ASSESSMENT_LEVEL + "(41597,'BC1','1',null,to_date('21-MAR-19','DD-MON-RR'),null,to_date('21-MAR-19','DD-MON-RR'),0,'BC','adult','1',244273,0)"
                    + ",(41603,'BC2','2',null,to_date('21-MAR-19','DD-MON-RR'),null,to_date('21-MAR-19','DD-MON-RR'),0,'BC','adult','2',241755,0)"
    })
    void should_get_page_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(size, "1")).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }

    @Test
    void should_get_none_if_wrong_type() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(type, "0")).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(0)));
    }

    @Test
    void should_not_get_own_qualification_if_no_authen() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(3,201,'Buriram',null,'08',0)",
            INSERT_CITY + "(56,3,'Satuk',null,null,0)"
    })
    void should_get_none_if_no_own_qualifications() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bfa611e0ecd443b487f1e392f46a67ba206acdf6b294d9223b6ec7b3df36ef60")).andExpect(jsonPath(ROOT + data, hasSize(0)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)"
                    + ",(241755,'Assessment Demo - try before you buy',null,'/lessonData/qoocoTalk/images/packages_big/ltfb_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5732,48292,1400,600)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_USER_QUALIFICATION + "(42264,'Assessment Demo - try before you buy','NULL',null,to_timestamp('10-MAY-19 08.10.05.923000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'cefr',241755,2,to_timestamp('21-NOV-18 12.37.12.762000000 PM','DD-MON-RR HH.MI.SSXFF AM'))",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"})
    void should_get_own_qualifications_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80")).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"
    })
    void should_get_own_qualifications_by_scaleid_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(scaleId, "BC")).andExpect(jsonPath(ROOT + data, hasSize(1)));
    }

    @Test
    void should_not_get_qualification_homepage_if_no_authen() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION_HOMEPAGE + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)"
                    + ",(241755,'Assessment Demo - try before you buy',null,'/lessonData/qoocoTalk/images/packages_big/ltfb_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5732,48292,1400,600)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_USER_QUALIFICATION + "(42264,'Assessment Demo - try before you buy','NULL',null,to_timestamp('10-MAY-19 08.10.05.923000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'cefr',241755,2,to_timestamp('21-NOV-18 12.37.12.762000000 PM','DD-MON-RR HH.MI.SSXFF AM'))",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"})
    void should_get_own_qualifications_homepage_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION_HOMEPAGE + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80")).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"
    })
    void should_get_own_qualifications_homepage_by_scaleid_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION_HOMEPAGE + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(scaleId, "BC")).andExpect(jsonPath(ROOT + data, hasSize(1)));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)"
    })
    void should_get_own_qualifications_history_correctly() throws Exception {
        Stream.of(
                AssessmentTestHistoryDoc.builder()
                        .id("1000526864_241755_1542775236019a")
                        .userProfileId(1000526864L)
                        .assessmentId(241755L)
                        .level(AssessmentLevelEmbedded.builder().scaleId("cefr").assessmentLevel(2).build())
                        .assessmentName("Assessment Demo - try before you buy")
                        .minLevel(2)
                        .maxLevel(2)
                        .scaleId("cefr")
                        .score(77)
                        .duration(51311L)
                        .updatedDate(iso8601DateFormat.parse("2019-05-10T08:14:56.793Z"))
                        .submissionTime(iso8601DateFormat.parse("2018-11-21T04:40:36.019Z"))
                        .updatedDateByItSelf(iso8601DateFormat.parse("2019-05-10T08:14:56.793Z"))
                        .build(),
                AssessmentTestHistoryDoc.builder()
                        .id("1000526864_241755_1557475688282b")
                        .userProfileId(1000526864L)
                        .assessmentId(241755L)
                        .level(AssessmentLevelEmbedded.builder().scaleId("BC").assessmentLevel(2).build())
                        .assessmentName("Assessment Demo - try before you buy")
                        .minLevel(2)
                        .maxLevel(2)
                        .scaleId("BC")
                        .score(71)
                        .duration(91615L)
                        .updatedDate(iso8601DateFormat.parse("2019-05-10T08:14:56.793Z"))
                        .submissionTime(iso8601DateFormat.parse("2019-05-10T08:08:08.282Z"))
                        .updatedDateByItSelf(iso8601DateFormat.parse("2019-05-10T08:14:56.793Z"))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc
                .perform(get(ASSESSMENT_PATH + ASSESSMENT_TEST_HISTORY + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(assessmentId, "241755"))
                .andExpect(jsonPath(ROOT + data + '.' + testHistories, hasSize(2)));
    }

    @Test
    void should_get_empty_if_no_own_qualifications_history() throws Exception {
        mvc
                .perform(get(ASSESSMENT_PATH + ASSESSMENT_TEST_HISTORY + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80").param(assessmentId, "234771"))
                .andExpect(jsonPath(ROOT + data + '.' + testHistories).doesNotExist());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(211,93,'Buntok',null,'17',0)",
            INSERT_CITY + "(2543,211,'Buntok',null,null,0)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)"
    })
    void should_get_qualifications_history_correctly() throws Exception {
        Stream.of(
                AssessmentTestHistoryDoc.builder()
                        .id("1000526864_241755_1542775032762")
                        .userProfileId(1000526864L)
                        .assessmentId(241755L)
                        .level(AssessmentLevelEmbedded.builder().scaleId("cefr").assessmentLevel(2).build())
                        .assessmentName("Assessment Demo - try before you buy")
                        .minLevel(2)
                        .maxLevel(2)
                        .scaleId("cefr")
                        .score(83)
                        .duration(80880L)
                        .updatedDate(iso8601DateFormat.parse("2019-05-10T08:14:56.793Z"))
                        .submissionTime(iso8601DateFormat.parse("2018-11-21T04:37:12.762Z"))
                        .updatedDateByItSelf(iso8601DateFormat.parse("2019-05-10T08:14:56.793Z")).build(),
                AssessmentTestHistoryDoc.builder()
                        .id("1000526864_241755_1560330621439")
                        .userProfileId(1000526864L)
                        .assessmentId(241755L)
                        .level(AssessmentLevelEmbedded.builder()
                                .scaleId("BC")
                                .mappingId("adult")
                                .levelName("BC2")
                                .levelDescription("2")
                                .assessmentLevel(2)
                                .build())
                        .assessmentName("Assessment Demo - try before you buy")
                        .minLevel(2)
                        .maxLevel(2)
                        .scaleId("BC")
                        .score(0)
                        .duration(110098L)
                        .updatedDate(iso8601DateFormat.parse("2019-06-13T04:07:48.389Z"))
                        .submissionTime(iso8601DateFormat.parse("2019-06-12T09:10:21.439Z"))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc
                .perform(get(ASSESSMENT_PATH + ASSESSMENT_TEST_HISTORY + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(userProfileId, "1000526864").param(assessmentId, "241755"))
                .andExpect(jsonPath(ROOT + data + '.' + testHistories, hasSize(2)));
    }

    @Test
    void should_get_empty_if_no_qualifications_history() throws Exception {
        mvc
                .perform(get(ASSESSMENT_PATH + ASSESSMENT_TEST_HISTORY + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(userProfileId, "1000526864").param(assessmentId, "234771"))
                .andExpect(jsonPath(ROOT + data + '.' + testHistories).doesNotExist());
    }

    @Test
    void should_not_get_qualifications_if_no_authen() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void should_get_none_if_no_user_param_and_no_own_qualification() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bfa611e0ecd443b487f1e392f46a67ba206acdf6b294d9223b6ec7b3df36ef60")).andExpect(jsonPath(ROOT + data, hasSize(0)));
    }

    @Test
    @Sql(statements = {
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)"
                    + ",(241755,'Assessment Demo - try before you buy',null,'/lessonData/qoocoTalk/images/packages_big/ltfb_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5732,48292,1400,600)",
            INSERT_USER_PROFILE + "(1000526864,'GEMYOP10','Gem','Yop Profile 10',2,'/1000526864/avatar/tYglpdYH8107484260595382735.jpg',to_date('16-JUN-95','DD-MON-RR'),'+62 93366965',null,null,'CA 2548',null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 08.13.30.109000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'gemtruong10@yopmail.com',0,null,2543,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000526864,'f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80','f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80',1,94583,null,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000526864,to_timestamp('28-MAR-19 11.01.23.845000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,'12B89AF32B77F72B880A1195665DBF0C','12B89AF32B77F72B880A1195665DBF0C',4,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnnYH+DaREtWo15kQ46pzVQK5qNunMBMkdNA9Lqk65rlZ+Jv36LuUt9GOYOMkpPy0MUmjopI01xNttEzoiIPUtjkO6zLf28BwyaHdNqzFozyTLw+CjY4N5mzfr2r21IWxbibFcTx6B126GZz8C16VRrJvOYpsMwaAuSLwhn5rBtB4gDbFGLLIvFxBgqZIUe8Uh8BWKInXIHLkbV/jLdnA3GbmrcPaFuKS3mLMyuAkP1+h5WA6ySmgdjBRHsKWcSF88UV6ePXnYzFJOnbXFR6BF4/AonCPUWG0gNLm9S2EIiE9XYN04mhk3f3Sm+wGoM94PttVtyb54k3q0eUH76Dr3QIDAQAB',null)",
            INSERT_USER_QUALIFICATION + "(42264,'Assessment Demo - try before you buy','NULL',null,to_timestamp('10-MAY-19 08.10.05.923000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'cefr',241755,2,to_timestamp('21-NOV-18 12.37.12.762000000 PM','DD-MON-RR HH.MI.SSXFF AM'))",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"})
    void should_get_own_qualifications_if_no_user_param() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "f81adce53de69327fc442d1526ae3f94ec80e27a222c2a0108c89427dd815a80")).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(3,201,'Buriram',null,'08',0)",
            INSERT_CITY + "(56,3,'Satuk',null,null,0)",
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)"
                    + ",(241755,'Assessment Demo - try before you buy',null,'/lessonData/qoocoTalk/images/packages_big/ltfb_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5732,48292,1400,600)",
            INSERT_USER_QUALIFICATION + "(42264,'Assessment Demo - try before you buy','NULL',null,to_timestamp('10-MAY-19 08.10.05.923000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'cefr',241755,2,to_timestamp('21-NOV-18 12.37.12.762000000 PM','DD-MON-RR HH.MI.SSXFF AM'))",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"})
    void should_get_qualifications_correctly() throws Exception {
        mvc.perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(userProfileId, "1000526864")).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(3,201,'Buriram',null,'08',0)",
            INSERT_CITY + "(56,3,'Satuk',null,null,0)",
            INSERT_ASSESSMENT + "(244273,'Assessment Demo - for test purpose',null,'/lessonData/qoocoTalk/images/packages_big/ltfo_level_assessment.jpg',0,null,to_date('01-JUN-19','DD-MON-RR'),null,to_date('01-JUN-19','DD-MON-RR'),0,'BC','adult',null,5733,48293,1400,600)",
            INSERT_USER_QUALIFICATION + "(42265,'Assessment Demo - for test purpose','NULL',null,to_timestamp('10-MAY-19 08.14.21.421000000 AM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('10-MAY-19 08.16.56.854000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000526864,1000526864,0,1000526864,'BC',244273,2,to_timestamp('10-MAY-19 04.11.42.137000000 PM','DD-MON-RR HH.MI.SSXFF AM'))"
    })
    void should_get_qualifications_by_scaleid_correctly() throws Exception {
        mvc
                .perform(get(ASSESSMENT_PATH + ASSESSMENT_QUALIFICATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(userProfileId, "1000526864").param(scaleId, "BC"))
                .andExpect(jsonPath(ROOT + data, hasSize(1)));
    }
}
