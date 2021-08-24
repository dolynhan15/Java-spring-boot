package com.qooco.boost.controllers;

import com.qooco.boost.models.request.CompanyReq;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.SUCCESS;
import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.Country.Fields.countryId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.dto.company.CompanyBaseDTO.Fields.id;
import static com.qooco.boost.models.dto.company.CompanyBaseDTO.Fields.name;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/25/2018 - 1:36 PM
*/
class CompanyControllerTest extends BaseMvcTest {
    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,2558,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
    })
    void saveCompany_whenSaveCompany_thenReturnResponse() throws Exception {
        var request = CompanyReq.builder()
                .address("123 Cong Hoa, Tan Binh, HCM")
                .amadeus("ABC 1234")
                .cityId(3071L)
                .description("Food center")
                .email("pax@axonactive.com")
                .galileo("ABC 2222")
                .hotelTypeId(7L)
                .logo("http://company12345.png")
                .name("Food")
                .phone("+840101012")
                .sabre("NV 123123")
                .web("http://basd.com")
                .worldspan("NVN 123123").build();
        mvc.perform(post(COMPANY_PATH + SAVE_METHOD).content(objectMapper.writeValueAsString(request))
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .contentType(APPLICATION_JSON))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS))
                .andExpect(jsonPath(ROOT + data + '.' + name).value(request.getName()));
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
    void getCompaniesOfUser_whenGetCompanyOfAdmin_thenReturnResponse() throws Exception {
        mvc.perform(get(COMPANY_PATH + GET_COMPANY_OF_USER).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .contentType(APPLICATION_JSON))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS))
                .andExpect(jsonPath(ROOT + data + INDEX0 + id).value(42438L));
    }

    @Test
    void should_not_find_approved_company_by_country_without_authentication() throws Exception {
        mvc.perform(get(COMPANY_PATH + FIND_APPROVED_COMPANY_BY_COUNTRY)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0),(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0),(2685,229,'Son Tra',null,null,0)",
            INSERT_HOTEL_TYPE + "(1,'Business'),(7,'Bed and Breakfast')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)"
                    + ",(42478,'Pen Hotel','/1000526859/company_logo/CimURpz04888301377098594669.png',2685,'67 Le Manh Trinh','+84 0982131313','info@pen.com','pen.com','BSNSNXKF','PQKDNCNC','NAJCNCC','NWKSNCNC',1,'Hello my name name you name all over again ha ha',1,1000526859,to_date('04-APR-19','DD-MON-RR'),1000526859,to_date('04-APR-19','DD-MON-RR'),0)"
    })
    void should_find_approved_company_by_country_correctly() throws Exception {
        mvc.perform(get(COMPANY_PATH + FIND_APPROVED_COMPANY_BY_COUNTRY).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(countryId, "220"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0),(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0),(2685,229,'Son Tra',null,null,0)",
            INSERT_HOTEL_TYPE + "(1,'Business'),(7,'Bed and Breakfast')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)"
                    + ",(42478,'Pen Hotel','/1000526859/company_logo/CimURpz04888301377098594669.png',2685,'67 Le Manh Trinh','+84 0982131313','info@pen.com','pen.com','BSNSNXKF','PQKDNCNC','NAJCNCC','NWKSNCNC',1,'Hello my name name you name all over again ha ha',1,1000526859,to_date('04-APR-19','DD-MON-RR'),1000526859,to_date('04-APR-19','DD-MON-RR'),0)"
    })
    void should_find_approved_company_by_country_page_correctly() throws Exception {
        mvc.perform(get(COMPANY_PATH + FIND_APPROVED_COMPANY_BY_COUNTRY).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(countryId, "220").param(size, "1"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0),(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0),(2685,229,'Son Tra',null,null,0)",
            INSERT_HOTEL_TYPE + "(1,'Business'),(7,'Bed and Breakfast')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)"
                    + ",(42478,'Pen Hotel','/1000526859/company_logo/CimURpz04888301377098594669.png',2685,'67 Le Manh Trinh','+84 0982131313','info@pen.com','pen.com','BSNSNXKF','PQKDNCNC','NAJCNCC','NWKSNCNC',1,'Hello my name name you name all over again ha ha',0,1000526859,to_date('04-APR-19','DD-MON-RR'),1000526859,to_date('04-APR-19','DD-MON-RR'),0)"
    })
    void should_not_find_unapproved_company_by_country() throws Exception {
        mvc.perform(get(COMPANY_PATH + FIND_APPROVED_COMPANY_BY_COUNTRY).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(countryId, "220"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0),(221,'Thailand','TH','+83',0)",
            INSERT_PROVINCE + "(261,221,'Quang Ngai',null,'52',0),(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0),(2685,229,'Son Tra',null,null,0)",
            INSERT_HOTEL_TYPE + "(1,'Business'),(7,'Bed and Breakfast')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)"
                    + ",(42478,'Pen Hotel','/1000526859/company_logo/CimURpz04888301377098594669.png',2685,'67 Le Manh Trinh','+84 0982131313','info@pen.com','pen.com','BSNSNXKF','PQKDNCNC','NAJCNCC','NWKSNCNC',1,'Hello my name name you name all over again ha ha',1,1000526859,to_date('04-APR-19','DD-MON-RR'),1000526859,to_date('04-APR-19','DD-MON-RR'),0)"
    })
    void should_not_find_approved_company_by_different_country() throws Exception {
        mvc.perform(get(COMPANY_PATH + FIND_APPROVED_COMPANY_BY_COUNTRY).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(countryId, "220"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }
}