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

class SoftSkillControllerTest extends BaseMvcTest {

    @Test
    void should_not_get_without_authentication() throws Exception {
        mvc.perform(get(PATA_PATH + SOFT_SKILL_PATH + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = INSERT_SOFT_SKILL + "(2,'Self-awareness','Self-awareness',0,1000523108,to_timestamp('13-JUL-18 05.45.08.282000000 PM','DD-MON-RR HH.MI.SSXFF AM'),1000523108,to_timestamp('14-JUL-18 05.45.08.282000000 PM','DD-MON-RR HH.MI.SSXFF AM')),"
            + "(3,'Emotion regulation','Emotion regulation',0,1000523108,to_timestamp('13-JUL-18 05.45.08.282000000 PM','DD-MON-RR HH.MI.SSXFF AM'),1000523108,to_timestamp('14-JUL-18 05.45.08.282000000 PM','DD-MON-RR HH.MI.SSXFF AM'))")
    void should_get_all_correctly() throws Exception {
        mvc.perform(get(PATA_PATH + SOFT_SKILL_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }
}
