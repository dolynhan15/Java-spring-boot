package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class BenefitControllerTest extends BaseMvcTest {

    @Test
    void should_not_get_without_authentication() throws Exception {
        mvc.perform(get(PATA_PATH + BENEFIT_PATH + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = "Insert into BENEFIT (BENEFIT_ID,DESCRIPTION,NAME,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED) values " +
            "(1,'Child and Elder care benefits','Child and Elder care benefits',null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),0)," +
            "(2,'Compensation time','Compensation time',null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),0)")
    void should_get_all_correctly() throws Exception {
        mvc.perform(get(PATA_PATH + BENEFIT_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    @Sql(statements = "Insert into BENEFIT (BENEFIT_ID,DESCRIPTION,NAME,CREATED_BY,CREATED_DATE,UPDATED_BY,UPDATED_DATE,IS_DELETED) values " +
            "(1,'Child and Elder care benefits','Child and Elder care benefits',null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),0)," +
            "(2,'Compensation time','Compensation time',null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('13-JUL-18 05.45.08.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),0)")
    void should_get_page_correctly() throws Exception {
        mvc
                .perform(get(PATA_PATH + BENEFIT_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(size, "1"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }
}
