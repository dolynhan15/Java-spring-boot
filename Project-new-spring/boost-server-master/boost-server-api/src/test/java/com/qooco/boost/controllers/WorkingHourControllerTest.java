package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class WorkingHourControllerTest extends BaseMvcTest {

    @Test
    void should_not_get_without_authentication() throws Exception {
        mvc.perform(get(PATA_PATH + WORKING_HOUR_PATH + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = INSERT_WORKING_HOUR + "(7,'Half afternoon shift 2','19:00:00','23:00:00',0)" +
            ",(8,'Half night shift','23:00:00','03:00:00',0)")
    void should_get_all_correctly() throws Exception {
        mvc.perform(get(PATA_PATH + WORKING_HOUR_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }
}
