package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.PaginationConstants.DEFAULT_PAGE_NUMBER;
import static com.qooco.boost.constants.PaginationConstants.DEFAULT_PAGE_SIZE;
import static com.qooco.boost.constants.StatusConstants.SUCCESS;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.Company.Fields.companyId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.request.PageRequest.Fields.page;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/6/2018 - 2:19 PM
*/
class StaffControllerTest extends BaseMvcTest {
    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(261,220,'Quang Ngai',null,'52',0)",
            INSERT_CITY + "(3071,261,'Binh Son',null,null,0)",
            INSERT_USER_PROFILE + "(1000530856,'BAOYOPMAIL','Bao','Yopmail',null,'/1000530856/avatar/0myIGlaI1090913876551058712.png',to_date('06-MAY-92','DD-MON-RR'),'+84 967876537',null,220,'32323232',null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('25-MAR-19 07.35.56.154000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'bao@yopmail.com',0,42438,3071,5,'Bao','Yopmail','/1000530856/avatar/sY3Zfz8q8978729256518814090.png',null,'+84 938758641',to_timestamp('02-NOV-92 08.55.58.828000000 AM','DD-MON-RR HH.MI.SSXFF AM'),'XT-023341','41 âm phủ',1)",
            INSERT_USER_ACCESS_TOKEN + "(1000530856,'cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42','cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42',1,96658,null,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000530856,to_timestamp('30-MAY-19 03.57.44.237000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000530856,'76AAF0EC5FF15CD6A919C685D2749FF6','76AAF0EC5FF15CD6A919C685D2749FF6',3,'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzR7ADa8uPAlS6QGwo8/oa5jyj8+masHWYnd+tDRdHpjheLN6q6oJV9rkEKAs8qNMs5VT8/1VHe6dp3LBPWjasypCxOYrjDp5EMzFqhu3MBzz13aQW9dsRwVHFkWU940MR7/xRHN/FtwPJCYyMJnvnl2SA1teXyQozBy8udEZwCklsT7KtjFguSvAhBl4gbKrY4nXdoRIjNvDvcBeiqftDn05fcWMdJ2ofQQHDs9E9uo/0Vzgc4RCxqRxpPeh4O5pawxYNCTa4or1YCykuOTXaZZIAzCbYWen7AE0ZWDyvpHVwE3cGwh7hxSTxMsD39D2F1lXY0gGSOgZ/S1emMWQ3QIDAQAB',42438)",
            INSERT_HOTEL_TYPE + "(7,'Bed and Breakfast')",
            INSERT_ROLE_COMPANY + "(1,'ADMIN',1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1,to_timestamp('28-AUG-18 12.00.00.000000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin')",
            INSERT_EDUCATION + "(1,'Less than high school','Less than high school')",
            INSERT_CURRENCY + "(258,'VND','Vietnamese Dong',1,1,2000,2310000000,'₫')",
            INSERT_COMPANY + "(42438,'Bao Yop mail','/1000530856/company_logo/ru3llMlZ8446071453383095942.png',3071,'45 Lý Tự Trọng.  âbv aaaa','+84 12345678901','bao@gmail.com','google.com','1253','48QWE','78908','QQQQ',7,'There are many variations of passages of Lorem Ipsum available, but the majority have suffered alteration in some form, by injected humour, or randomised words which don''t look even slightly believable. If you are going to use a passage of Lorem Ipsum, you need to be sure there isn''t anything embarrassing hidden in the middle of text. All the Lorem Ipsum generators on the Internet tend to repeat predefined chunks as necessary, making this the first true generator on the Internet. It uses a dictionary of over 200 Latin words, combined with a handful of model sentence structures, to generate Lorem Ipsum which looks reasonable. The generated Lorem Ipsum is therefore always free from repetition,',1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530856,to_date('25-MAR-19','DD-MON-RR'),0)",
            INSERT_STAFF + "(42819,1000530856,42438,1,1000530856,to_date('25-MAR-19','DD-MON-RR'),1000530858,to_date('07-MAY-19','DD-MON-RR'),0)",
    })
    void getStaffOfCompany_whenGetStaffOfCompany_thenReturnListStaff() throws Exception {
        mvc.perform(get(COMPANY_PATH + STAFF + GET_METHOD)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "cb8d31b5086b1a088e0303d3330893b6f918f3f21f06d3b8a3ced27c8ac4de42")
                .param(companyId, "42438")
                .param(page, DEFAULT_PAGE_NUMBER)
                .param(size, DEFAULT_PAGE_SIZE))
                .andExpect(jsonPath(ROOT + code).value(SUCCESS))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }
}