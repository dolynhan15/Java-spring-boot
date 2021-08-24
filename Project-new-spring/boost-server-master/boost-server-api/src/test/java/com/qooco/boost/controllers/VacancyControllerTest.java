package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.URLConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.dto.vacancy.VacancyBaseDTO.Fields.id;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class VacancyControllerTest extends BaseMvcTest {
    @Test
    void should_not_decline_candidate_without_authentication() throws Exception {
        mvc.perform(patch(VACANCY_PATH + "/{vacancy}" + CANDIDATE_OF_VACANCY + "/{candidate}" + DECLINE, 43561, 42923)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void should_not_decline_candidate_without_permission() throws Exception {
        mvc.perform(patch(VACANCY_PATH + "/{vacancy}" + CANDIDATE_OF_VACANCY + "/{candidate}" + DECLINE, 43460, 42923).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + code).value(NO_PERMISSION_TO_ACCESS));
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
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)"
    })
    void should_not_decline_candidate_not_found_vacancy() throws Exception {
        mvc.perform(patch(VACANCY_PATH + "/{vacancy}" + CANDIDATE_OF_VACANCY + "/{candidate}" + DECLINE, 43569, 42923).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")).andExpect(jsonPath(ROOT + code).value(NOT_FOUND_VACANCY));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(147,93,'Aceh',null,'10',0),(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(863,147,'Alue Cekdoi',null,null,0),(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(952,863,0,1000527479,TIMESTAMP '2019-05-10 07:17:49.799000',1000527479,TIMESTAMP '2019-05-10 07:17:49.799000',42438,'test used b',1,0)",
            INSERT_LOCATION + "(953,863,0,1000527479,TIMESTAMP '2019-05-10 07:19:00.435000',1000530856,TIMESTAMP '2019-05-13 07:30:59.599000',42438,'test used llllll',1,0)",
            INSERT_VACANCY + "(43561,'/1000523111/vacancy_logo/637FYAQn7453599144611720620.jpg',42438,88,863,863,42819,NULL,1,0,1,1,NULL,'qee',NULL,1,5,0,1000523111,TIMESTAMP '2019-05-28 04:42:24.631000',1000523111,TIMESTAMP '2019-05-28 04:42:24.631000',1,NULL,1,100000,953,952,NULL,NULL,NULL)"
    })
    void should_not_decline_candidate_not_found_appointment() throws Exception {
        mvc.perform(patch(VACANCY_PATH + "/{vacancy}" + CANDIDATE_OF_VACANCY + "/{candidate}" + DECLINE, 43561, 42923).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")).andExpect(jsonPath(ROOT + code).value(NOT_FOUND_APPOINTMENT_DETAIL));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(93,'Indonesia','ID','+62',0),(201,'Thailand','TH','+66',0)",
            INSERT_PROVINCE + "(147,93,'Aceh',null,'10',0),(155,93,'East Java',null,'05',0),(216,220,'Ba Ria–Vung Tau',null,'84',0),(261,220,'Quang Ngai',null,'52',0),(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(874,147,'Bireuen',null,null,0),(1352,155,'Awang-awang',null,null,0),(2559,216,'Chau Duc',null,null,0),(3071,261,'Binh Son',null,null,0),(2685,229,'Son Tra',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                + ",(1000531876,'anhdev8',null,null,null,null,null,null,null,null,null,null,to_timestamp('26-APR-19 04.50.14.963000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('26-APR-19 04.50.14.963000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev8@mailinator.com',0,42478,null,null,null,null,null,null,null,null,null,null,null)"
                + ",(1000530398,'ANHDEV1','Anh','dev 1',0,'/1000530398/avatar/fj2oSrjJ3205668640045161851.png',to_date('29-OCT-00','DD-MON-RR'),'+84 234523452345',null,201,'85364875677',null,to_timestamp('21-MAR-19 02.53.56.624000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 02.53.56.624000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev1@mailinator.com',0,null,2559,5,'Anh FIT','Dev 1','/1000530398/avatar/DiRfDCpI7340731528807762880.png',null,'+66 ',to_timestamp('27-MAR-92 01.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(1,'Business'),(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(7,'Doctoral or professional degree','Doctoral or professional degree')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)"
                    + ",(42478,'Pen Hotel','/1000526859/company_logo/CimURpz04888301377098594669.png',2685,'67 Le Manh Trinh','+84 0982131313','info@pen.com','pen.com','BSNSNXKF','PQKDNCNC','NAJCNCC','NWKSNCNC',1,'Hello my name name you name all over again ha ha',1,1000526859,to_date('04-APR-19','DD-MON-RR'),1000526859,to_date('04-APR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)"
                    + ",(42957,1000531876,42438,1,1000530856,to_date('02-MAY-19','DD-MON-RR'),1000530856,to_date('23-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(903,874,1,1000527993,TIMESTAMP '2019-05-07 06:32:41.201000',1000527993,TIMESTAMP '2019-05-07 06:32:41.201000',42438,'13',0,0)"
                    + ",(904,1352,0,1000527993,TIMESTAMP '2019-05-07 06:32:53.902000',1000530856,TIMESTAMP '2019-05-10 04:31:59.889000',42438,'14ttttttt',1,0)",
            INSERT_VACANCY + "(43460,'/1000530856/vacancy_logo/x4U9oJLm2217439705522210974.jpg',42438,88,1352,874,42819,NULL,1,0,1,1,NULL,'hshud7d',NULL,1,5,0,1000530856,TIMESTAMP '2019-05-08 03:22:14.220000',1000530856,TIMESTAMP '2019-05-08 03:22:14.220000',8,NULL,1,100000,904,903,NULL,NULL,NULL)",
            INSERT_USER_CURRICULUM_VITAE + "(42923,0,1,47747,7,1,NULL,1,1000530398,5,'[\"https://www.facebook.com\",\"google.com/ssss\",\"abc.com\"]',0,1000530398,TIMESTAMP '2019-03-27 04:41:31.119000',1000530398,TIMESTAMP '2019-05-13 09:20:04.037000',1)",
            INSERT_VACANCY_CANDIDATE + "(79,43460,1,0,1000531876,TIMESTAMP '2019-05-29 07:35:45.794000',1000531876,TIMESTAMP '2019-05-29 07:35:45.794000',42923,42957)"})
    void should_not_decline_candidate_already_closed() throws Exception {
        mvc.perform(patch(VACANCY_PATH + "/{vacancy}" + CANDIDATE_OF_VACANCY + "/{candidate}" + DECLINE, 43460, 42923).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")).andExpect(jsonPath(ROOT + code).value(CANDIDATE_IS_ALREADY_CLOSED));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(217,220,'Bac Giang',null,'32',0),(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(2567,217,'Hiep Hoa',null,null,0),(3072,261,'Duc Pho',null,null,0),(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)"
                + ",(1000531676,'anhdev2','Anh','Dev 2',1,'/1000531676/avatar/FzRx9BHl911992500999888093.jpg',to_date('11-JAN-90','DD-MON-RR'),'+84 123123123',null,null,'3434234777',null,to_timestamp('17-APR-19 01.44.32.002000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('17-APR-19 01.44.32.002000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'anhdev2@mailinator.com',0,null,2567,6,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(3,'College','College, no degree')",
            INSERT_CURRENCY + "(5,'USD','USD',1,1,1,100000,'$'),(7,'KRW','South Korean won',1,1,1,112000000,'₩')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)",
            INSERT_LOCATION + "(781,3072,0,1000530856,TIMESTAMP '2019-03-28 07:23:08.451000',1000530856,TIMESTAMP '2019-03-28 07:23:08.451000',42438,'400 Ngo Quyen',1,0)",
            INSERT_VACANCY + "(43381,'/1000530856/vacancy_logo/A6JvIMTs8785897294304572530.png',42438,88,3072,3072,42819,NULL,1,0,1,1,NULL,'Wee',NULL,1,5,0,1000530856,TIMESTAMP '2019-04-08 07:14:56.219000',1000530856,TIMESTAMP '2019-04-08 07:14:56.219000',4,NULL,1,100000,781,781,NULL,NULL,NULL)",
            INSERT_USER_CURRICULUM_VITAE + "(42985,1,1,112000000,3,0,TIMESTAMP '2019-05-20 06:54:19.000000',0,1000531676,7,'[\"https://www.facebook.com\",\"https://www.google.com\"]',0,1000531676,TIMESTAMP '2019-04-17 01:45:57.373000',1000531676,TIMESTAMP '2019-05-14 06:54:17.466000',1)",
            INSERT_APPOINTMENT + "(1629,761,TIMESTAMP '2019-04-24 10:59:59.000000',42819,1000530856,TIMESTAMP '2019-04-18 06:54:03.000000',1000530856,TIMESTAMP '2019-04-18 06:54:03.000000',0,43381,1,TIMESTAMP '2019-04-17 17:00:00.000000')",
            INSERT_APPOINTMENT_DETAIL + "(2372,1629,42985,TIMESTAMP '2019-04-22 04:00:00.000000',0,TIMESTAMP '2019-04-18 06:54:15.960000',1000530856,TIMESTAMP '2019-04-18 06:54:50.760000',1000530856,12)"
    })
    void should_decline_candidate_correctly() throws Exception {
        var request = patch(VACANCY_PATH + "/{vacancy}" + CANDIDATE_OF_VACANCY + "/{candidate}" + DECLINE, 43381, 42985).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42");
        mvc.perform(request).andExpect(jsonPath(ROOT + code).value(SUCCESS)).andExpect(jsonPath(ROOT + data + '.' + id).value(43381));
        mvc.perform(request).andExpect(jsonPath(ROOT + code).value(SUCCESS)).andExpect(jsonPath(ROOT + data + '.' + id).value(43381));
    }
}
