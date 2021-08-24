package com.qooco.boost.controllers;

import com.qooco.boost.models.user.UserCurriculumVitaeReq;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.SUCCESS;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.UserCurriculumVitae.Fields.userProfile;
import static com.qooco.boost.data.oracle.entities.UserProfile.Fields.userProfileId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.dto.user.BaseUserCVDTO.Fields.id;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/12/2018 - 8:54 AM
*/
class BusinessUserCurriculumVitaeControllerTest extends BaseMvcTest {

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(2545,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2545,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                    + ",(1000531677,'anhdev3','anh','dev 3',0,'/1000531677/avatar/140oGVQx5659969206973160954.jpg',to_date('11-JAN-90','DD-MON-RR'),'+62 123456789',null,null,'1234',null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev3@mailinator.com',0,null,876,5,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_CURRENCY + "(258,'USD','USD',1,1,1,100000,'$')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school')",
            INSERT_WORKING_HOUR + "(1,'Morning shift','07:00:00','15:00:00',1)",
            INSERT_BENEFIT + "(1,'Child and Elder care benefits','Child and Elder care benefits',null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),0)",
    })
    void saveUserCurriculumVitaeService() throws Exception {
        var saveRequest = UserCurriculumVitaeReq.builder()
                .userProfileId(1000530856L)
                .isFullTime(true)
                .educationId(1)
                .workHourIds(new long[] {1})
                .currencyCode("USD")
                .jobIds(new long[] {88})
                .minSalary(1)
                .maxSalary(10)
                .isAsap(true)
                .isHourSalary(true)
                .benefitIds(new long[] {1})
                .build();
        mvc.perform(post(USER_PATH + USER_CV_PATH + SAVE_METHOD).contentType(APPLICATION_JSON)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .content(objectMapper.writeValueAsString(saveRequest)))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(2545,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2545,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                    + ",(1000531677,'anhdev3','anh','dev 3',0,'/1000531677/avatar/140oGVQx5659969206973160954.jpg',to_date('11-JAN-90','DD-MON-RR'),'+62 123456789',null,null,'1234',null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev3@mailinator.com',0,null,876,5,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school')",
            INSERT_CURRENCY + "(258,'USD','USD',1,1,1,100000,'$')",
            INSERT_USER_CURRICULUM_VITAE + "(42986,0,5000000,20000000,1,0,TIMESTAMP '2019-05-16 07:57:04.000000',1,1000531677,258,'[\"https://www.facebook.com\"]',0,1000531677,TIMESTAMP '2019-04-17 01:50:33.479000',1000531677,TIMESTAMP '2019-05-13 07:57:03.075000',0)"
    })
    void getUserCurriculumVitaeService_whenGetUserCv_thenReturnUserCv() throws Exception {
        mvc.perform(get(USER_PATH +  USER_CV_PATH + GET_METHOD)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .param(userProfileId, "1000530856"))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS))
                .andExpect(jsonPath(ROOT + data + '.' + userProfile + '.' + id).value(1000530856L));
    }

}