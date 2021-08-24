package com.qooco.boost.controllers;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailNotifyDoc;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.SUCCESS;
import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.Company.Fields.companyId;
import static com.qooco.boost.data.oracle.entities.Staff.Fields.staffId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.request.DurationRequest.Fields.endDate;
import static com.qooco.boost.models.request.DurationRequest.Fields.startDate;
import static com.qooco.boost.models.request.PageRequest.Fields.page;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AppointmentEventsControllerTest extends BaseMvcTest {
    @Test
    void hasExpiredEvent_shouldNotGetWithoutAuthentication() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + HAS_EXPIRED)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void hasExpiredEvent_shouldReturnFalseWhenCompanyIsNull() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + HAS_EXPIRED).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + code).value(SUCCESS)).andExpect(jsonPath(ROOT + data).value(false));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(213,93,'Aren',null,'03',0),(147,93,'Aceh',null,'10',0),(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(2545,213,'Aren',null,null,0),(863,147,'Alue Cekdoi',null,null,0),(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                + ",(1000531677,'anhdev3','anh','dev 3',0,'/1000531677/avatar/140oGVQx5659969206973160954.jpg',to_date('11-JAN-90','DD-MON-RR'),'+62 123456789',null,null,'1234',null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev3@mailinator.com',0,null,876,5,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$'),(258,'VND','Vietnamese Dong',1,1,2000,2310000000,'₫')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(952,863,0,1000527479,TIMESTAMP '2019-05-10 07:17:49.799000',1000527479,TIMESTAMP '2019-05-10 07:17:49.799000',42438,'test used b',1,0)"
                    + ",(883,2545,0,1000530856,TIMESTAMP '2019-05-06 09:15:24.024000',1000530856,TIMESTAMP '2019-05-06 09:15:24.024000',42438,'02 Aren',1,0)",
            INSERT_VACANCY + "(43676,'/1000530856/vacancy_logo/XCJrGVe21311414266188387385.jpg',42438,88,863,2545,42819,NULL,1,0,1,1,NULL,'short',NULL,1,5,0,1000530856,TIMESTAMP '2019-06-12 04:07:22.219000',1000530856,TIMESTAMP '2019-06-12 12:59:48.219000',7,NULL,1,100000,952,883,NULL,NULL,NULL)",
            INSERT_USER_CURRICULUM_VITAE + "(42986,0,5000000,20000000,1,0,TIMESTAMP '2019-05-16 07:57:04.000000',1,1000531677,258,'[\"https://www.facebook.com\"]',0,1000531677,TIMESTAMP '2019-04-17 01:50:33.479000',1000531677,TIMESTAMP '2019-05-13 07:57:03.075000',0)",
            INSERT_APPOINTMENT + "(2142,888,TIMESTAMP '2019-06-24 10:59:59.000000',42819,1000530856,TIMESTAMP '2019-06-18 06:48:21.000000',1000530856,TIMESTAMP '2019-06-18 06:48:21.000000',0,43676,1,TIMESTAMP '2019-06-17 17:00:00.000000')",
            INSERT_APPOINTMENT_DETAIL + "(2905,2142,42986,TIMESTAMP '2019-06-21 04:15:00.000000',0,TIMESTAMP '2019-06-18 06:48:30.000000',1000530856,TIMESTAMP '2019-06-18 07:11:05.000000',1000530856,12)"
    })
    void hasExpiredEvent_shouldReturnFalseWhenExpiredEventNotified() throws Exception {
        mongoTemplate.save(
                AppointmentDetailNotifyDoc.builder()
                        .id(new ObjectId("5d1f0873a5e72b43e8d7cb3a"))
                        .staff(StaffEmbedded.builder().id(42819L).build())
                        .appointmentDetailIds(ImmutableList.of(2905L, 2466L))
                        .createdDate(iso8601DateFormat.parse("2019-07-05T08:21:07.818Z"))
                        .build()
        );
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + HAS_EXPIRED).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")).andExpect(jsonPath(ROOT + code).value(SUCCESS)).andExpect(jsonPath(ROOT + data).value(false));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(213,93,'Aren',null,'03',0),(147,93,'Aceh',null,'10',0),(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(2545,213,'Aren',null,null,0),(863,147,'Alue Cekdoi',null,null,0),(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                + ",(1000531677,'anhdev3','anh','dev 3',0,'/1000531677/avatar/140oGVQx5659969206973160954.jpg',to_date('11-JAN-90','DD-MON-RR'),'+62 123456789',null,null,'1234',null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('17-APR-19 01.50.01.144000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev3@mailinator.com',0,null,876,5,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$'),(258,'VND','Vietnamese Dong',1,1,2000,2310000000,'₫')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(952,863,0,1000527479,TIMESTAMP '2019-05-10 07:17:49.799000',1000527479,TIMESTAMP '2019-05-10 07:17:49.799000',42438,'test used b',1,0)"
                    + ",(883,2545,0,1000530856,TIMESTAMP '2019-05-06 09:15:24.024000',1000530856,TIMESTAMP '2019-05-06 09:15:24.024000',42438,'02 Aren',1,0)",
            INSERT_VACANCY + "(43676,'/1000530856/vacancy_logo/XCJrGVe21311414266188387385.jpg',42438,88,863,2545,42819,NULL,1,0,1,1,NULL,'short',NULL,1,5,0,1000530856,TIMESTAMP '2019-06-12 04:07:22.219000',1000530856,TIMESTAMP '2019-06-12 12:59:48.219000',7,NULL,1,100000,952,883,NULL,NULL,NULL)",
            INSERT_USER_CURRICULUM_VITAE + "(42986,0,5000000,20000000,1,0,TIMESTAMP '2019-05-16 07:57:04.000000',1,1000531677,258,'[\"https://www.facebook.com\"]',0,1000531677,TIMESTAMP '2019-04-17 01:50:33.479000',1000531677,TIMESTAMP '2019-05-13 07:57:03.075000',0)",
            INSERT_APPOINTMENT + "(2142,888,TIMESTAMP '2019-06-24 10:59:59.000000',42819,1000530856,TIMESTAMP '2019-06-18 06:48:21.000000',1000530856,TIMESTAMP '2019-06-18 06:48:21.000000',0,43676,1,TIMESTAMP '2019-06-17 17:00:00.000000')",
            INSERT_APPOINTMENT_DETAIL + "(2905,2142,42986,TIMESTAMP '2019-06-21 04:15:00.000000',0,TIMESTAMP '2019-06-18 06:48:30.000000',1000530856,TIMESTAMP '2019-06-18 07:11:05.000000',1000530856,12)"
    })
    void hasExpiredEvent_shouldReturnTrueWhenNewExpiredEventComes() throws Exception {
        mongoTemplate.save(
                AppointmentDetailNotifyDoc.builder()
                        .id(new ObjectId("5d1f0873a5e72b43e8d7cb3a"))
                        .staff(StaffEmbedded.builder().id(42819L).build())
                        .appointmentDetailIds(ImmutableList.of(2466L))
                        .createdDate(iso8601DateFormat.parse("2019-07-05T08:21:07.818Z"))
                        .build()
        );
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + HAS_EXPIRED).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")).andExpect(jsonPath(ROOT + code).value(SUCCESS)).andExpect(jsonPath(ROOT + data).value(true));
    }

    @Test
    void getCandidatesOfExpiredEventsForFeedback_shouldNotGetWithoutAuthentication() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + EXPIRED + CANDIDATE_OF_VACANCY)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(93,'Indonesia','ID','+62',0),(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(216,220,'Ba Ria–Vung Tau',null,'84',0),(147,93,'Aceh',null,'10',0),(261,220,'Quang Ngai',null,'52',0),(270,220,'Thua Thien–Hue',null,'86',0),(269,220,'Thanh Hoa',null,'81',0)",
            INSERT_CITY + "(2559,216,'Chau Duc',null,null,0),(863,147,'Alue Cekdoi',null,null,0),(3072,261,'Duc Pho',null,null,0),(3180,270,'A Luoi',null,null,0),(3071,261,'Binh Son',null,null,0),(3155,269,'Bim Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                + ",(1000530398,'ANHDEV1','Anh','dev 1',0,'/1000530398/avatar/fj2oSrjJ3205668640045161851.png',to_date('29-OCT-00','DD-MON-RR'),'+84 234523452345',null,201,'85364875677',null,to_timestamp('21-MAR-19 02.53.56.624000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 02.53.56.624000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev1@mailinator.com',0,null,2559,5,'Anh FIT','Dev 1','/1000530398/avatar/DiRfDCpI7340731528807762880.png',null,'+66 ',to_timestamp('27-MAR-92 01.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school'),(7,'Doctoral or professional degree','Doctoral or professional degree')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$'),(258,'VND','Vietnamese Dong',1,1,2000,2310000000,'₫')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(781,3072,0,1000530856,TIMESTAMP '2019-03-28 07:23:08.451000',1000530856,TIMESTAMP '2019-03-28 07:23:08.451000',42438,'400 Ngo Quyen',1,0)"
                + ",(944,863,1,1000530856,TIMESTAMP '2019-05-10 06:51:12.262000',1000530856,TIMESTAMP '2019-05-10 06:51:12.262000',42438,'hhh',0,0)"
                + ",(941,3155,1,1000530856,TIMESTAMP '2019-05-10 03:57:02.430000',1000530856,TIMESTAMP '2019-05-13 03:14:45.044000',42438,'Lộc Sơn',0,0)",
            INSERT_VACANCY + "(43381,'/1000530856/vacancy_logo/A6JvIMTs8785897294304572530.png',42438,88,3072,3072,42819,NULL,1,0,1,1,NULL,'Wee',NULL,1,5,0,1000530856,TIMESTAMP '2019-04-08 07:14:56.219000',1000530856,TIMESTAMP '2019-04-08 07:14:56.219000',4,NULL,1,100000,781,781,NULL,NULL,NULL)"
                    + ",(43380,'/1000530856/vacancy_logo/ytJsFt393549101943611311429.png',42438,88,3072,3072,42819,NULL,15000000,0,1,1,NULL,'Ddddd',NULL,1,258,0,1000530856,TIMESTAMP '2019-04-08 06:59:19.909000',1000530856,TIMESTAMP '2019-05-27 10:46:18.641000',8,NULL,1,30000000,781,781,NULL,0,42819)"
                    + ",(43484,'\\1000527479\\vacancy_logo\\A0bfJ9Su7138906283448556926.png',42438,88,863,3180,42819,1,1,0,1,1,NULL,'Aaa',NULL,1,5,0,1000527479,TIMESTAMP '2019-05-10 07:56:38.145000',1000527479,TIMESTAMP '2019-05-10 07:56:38.145000',1,TO_CLOB('Aaa'),1,100,944,941,NULL,NULL,NULL)",
            INSERT_USER_CURRICULUM_VITAE + "(42923,0,1,47747,7,1,NULL,1,1000530398,5,'[\"https://www.facebook.com\",\"google.com/ssss\",\"abc.com\"]',0,1000530398,TIMESTAMP '2019-03-27 04:41:31.119000',1000530398,TIMESTAMP '2019-05-13 09:20:04.037000',1)",
            INSERT_APPOINTMENT + "(1509,761,TIMESTAMP '2019-04-11 17:59:59.000000',42819,1000530856,TIMESTAMP '2019-04-09 03:42:46.000000',1000530856,TIMESTAMP '2019-04-09 03:42:46.000000',0,43381,1,TIMESTAMP '2019-04-10 17:00:00.000000')"
                    + ",(1571,761,TIMESTAMP '2019-04-18 17:59:59.000000',42819,1000530856,TIMESTAMP '2019-04-12 03:28:49.000000',1000530856,TIMESTAMP '2019-04-12 03:28:49.000000',0,43380,1,TIMESTAMP '2019-04-11 17:00:00.000000')"
                    + ",(1818,952,TIMESTAMP '2019-05-27 10:59:59.000000',42819,1000530856,TIMESTAMP '2019-05-21 10:28:33.000000',1000530856,TIMESTAMP '2019-05-21 10:28:33.000000',0,43484,1,TIMESTAMP '2019-05-20 17:00:00.000000')",
            INSERT_APPOINTMENT_DETAIL + "(2283,1509,42923,TIMESTAMP '2019-04-11 05:15:00.000000',0,TIMESTAMP '2019-04-09 04:21:21.841000',1000530856,TIMESTAMP '2019-04-09 04:21:21.841000',1000530856,12)"
                    + ",(2332,1571,42923,TIMESTAMP '2019-04-17 06:30:00.000000',0,TIMESTAMP '2019-04-12 03:28:50.614000',1000530856,TIMESTAMP '2019-04-12 03:29:31.172000',1000530856,12)"
                    + ",(2466,1818,42923,TIMESTAMP '2019-05-24 05:30:00.000000',0,TIMESTAMP '2019-05-21 10:28:33.232000',1000530856,TIMESTAMP '2019-05-23 03:10:12.129000',1000530856,12)"
    })
    void getCandidatesOfExpiredEventsForFeedback_shouldGetAllWithPageAndSizeEqualZero() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + EXPIRED + CANDIDATE_OF_VACANCY)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .param(page, "0")
                .param(size, "0")
        ).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(3)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(93,'Indonesia','ID','+62',0),(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(216,220,'Ba Ria–Vung Tau',null,'84',0),(270,220,'Thua Thien–Hue',null,'86',0),(147,93,'Aceh',null,'10',0),(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(2559,216,'Chau Duc',null,null,0),(3180,270,'A Luoi',null,null,0),(863,147,'Alue Cekdoi',null,null,0),(3072,261,'Duc Pho',null,null,0),(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                + ",(1000530398,'ANHDEV1','Anh','dev 1',0,'/1000530398/avatar/fj2oSrjJ3205668640045161851.png',to_date('29-OCT-00','DD-MON-RR'),'+84 234523452345',null,201,'85364875677',null,to_timestamp('21-MAR-19 02.53.56.624000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 02.53.56.624000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev1@mailinator.com',0,null,2559,5,'Anh FIT','Dev 1','/1000530398/avatar/DiRfDCpI7340731528807762880.png',null,'+66 ',to_timestamp('27-MAR-92 01.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school'),(7,'Doctoral or professional degree','Doctoral or professional degree')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$'),(258,'VND','Vietnamese Dong',1,1,2000,2310000000,'₫')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(781,3072,0,1000530856,TIMESTAMP '2019-03-28 07:23:08.451000',1000530856,TIMESTAMP '2019-03-28 07:23:08.451000',42438,'400 Ngo Quyen',1,0)",
            INSERT_LOCATION + "(944,863,1,1000530856,TIMESTAMP '2019-05-10 06:51:12.262000',1000530856,TIMESTAMP '2019-05-10 06:51:12.262000',42438,'hhh',0,0)",
            INSERT_LOCATION + "(941,3155,1,1000530856,TIMESTAMP '2019-05-10 03:57:02.430000',1000530856,TIMESTAMP '2019-05-13 03:14:45.044000',42438,'Lộc Sơn',0,0)",
            INSERT_VACANCY + "(43381,'/1000530856/vacancy_logo/A6JvIMTs8785897294304572530.png',42438,88,3072,3072,42819,NULL,1,0,1,1,NULL,'Wee',NULL,1,5,0,1000530856,TIMESTAMP '2019-04-08 07:14:56.219000',1000530856,TIMESTAMP '2019-04-08 07:14:56.219000',4,NULL,1,100000,781,781,NULL,NULL,NULL)"
                    + ",(43380,'/1000530856/vacancy_logo/ytJsFt393549101943611311429.png',42438,88,3072,3072,42819,NULL,15000000,0,1,1,NULL,'Ddddd',NULL,1,258,0,1000530856,TIMESTAMP '2019-04-08 06:59:19.909000',1000530856,TIMESTAMP '2019-05-27 10:46:18.641000',8,NULL,1,30000000,781,781,NULL,0,42819)"
                    + ",(43484,'\\1000527479\\vacancy_logo\\A0bfJ9Su7138906283448556926.png',42438,88,863,3180,42819,1,1,0,1,1,NULL,'Aaa',NULL,1,5,0,1000527479,TIMESTAMP '2019-05-10 07:56:38.145000',1000527479,TIMESTAMP '2019-05-10 07:56:38.145000',1,TO_CLOB('Aaa'),1,100,944,941,NULL,NULL,NULL)",
            INSERT_USER_CURRICULUM_VITAE + "(42923,0,1,47747,7,1,NULL,1,1000530398,5,'[\"https://www.facebook.com\",\"google.com/ssss\",\"abc.com\"]',0,1000530398,TIMESTAMP '2019-03-27 04:41:31.119000',1000530398,TIMESTAMP '2019-05-13 09:20:04.037000',1)",
            INSERT_APPOINTMENT + "(1509,761,TIMESTAMP '2019-04-11 17:59:59.000000',42819,1000530856,TIMESTAMP '2019-04-09 03:42:46.000000',1000530856,TIMESTAMP '2019-04-09 03:42:46.000000',0,43381,1,TIMESTAMP '2019-04-10 17:00:00.000000')"
                    + ",(1571,761,TIMESTAMP '2019-04-18 17:59:59.000000',42819,1000530856,TIMESTAMP '2019-04-12 03:28:49.000000',1000530856,TIMESTAMP '2019-04-12 03:28:49.000000',0,43380,1,TIMESTAMP '2019-04-11 17:00:00.000000')"
                    + ",(1818,952,TIMESTAMP '2019-05-27 10:59:59.000000',42819,1000530856,TIMESTAMP '2019-05-21 10:28:33.000000',1000530856,TIMESTAMP '2019-05-21 10:28:33.000000',0,43484,1,TIMESTAMP '2019-05-20 17:00:00.000000')",
            INSERT_APPOINTMENT_DETAIL + "(2283,1509,42923,TIMESTAMP '2019-04-11 05:15:00.000000',0,TIMESTAMP '2019-04-09 04:21:21.841000',1000530856,TIMESTAMP '2019-04-09 04:21:21.841000',1000530856,12)"
                    + ",(2332,1571,42923,TIMESTAMP '2019-04-17 06:30:00.000000',0,TIMESTAMP '2019-04-12 03:28:50.614000',1000530856,TIMESTAMP '2019-04-12 03:29:31.172000',1000530856,12)"
                    + ",(2466,1818,42923,TIMESTAMP '2019-05-24 05:30:00.000000',0,TIMESTAMP '2019-05-21 10:28:33.232000',1000530856,TIMESTAMP '2019-05-23 03:10:12.129000',1000530856,12)"
    })
    void getCandidatesOfExpiredEventsForFeedback_shouldGetWithConcretePageAndSize() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + EXPIRED + CANDIDATE_OF_VACANCY)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .param(page, "0")
                .param(size, "2")
        ).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    void getAppointmentEventCount_shouldNotGetWithoutAuthentication() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + COUNT)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)"
    })
    void getAppointmentEventCount_shouldNotGetWithoutParams() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + COUNT).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")).andExpect(status().isBadRequest());
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school')",
            INSERT_CURRENCY + "(258,'VND','Vietnamese Dong',1,1,2000,2310000000,'₫')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
    })
    void getAppointmentEventCount_shouldGet() throws Exception {
        mvc.perform(get(APPOINTMENT_PATH + EVENTS + COUNT)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .param(staffId, "42819")
                .param(companyId, "42438")
                .param(startDate, "0")
                .param(endDate, "")
                .param("timeZone", UTC.getId())).andExpect(jsonPath(ROOT + code).value(SUCCESS));
    }
}