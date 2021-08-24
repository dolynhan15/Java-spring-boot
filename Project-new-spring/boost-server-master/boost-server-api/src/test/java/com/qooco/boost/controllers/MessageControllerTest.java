package com.qooco.boost.controllers;

import com.qooco.boost.models.request.LastRequest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.MESSAGE_PATH;
import static com.qooco.boost.constants.URLConstants.TEMPLATE_MESSAGE;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class MessageControllerTest extends BaseMvcTest {

    @Test
    void get_message_template_should_not_get_without_authentication() throws Exception {
        mvc.perform(get(MESSAGE_PATH + TEMPLATE_MESSAGE)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = "Insert into MESSAGE_TEMPLATE (ID,LANGUAGE_CODE,CONTENT,IS_DELETED,CREATED_DATE,UPDATED_DATE) values " +
            "(1,'en','Thank you for applying for a position with our company. After careful consideration, however…',0,to_timestamp('09-JUL-19 02.53.10.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('09-JUL-19 02.53.11.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'))," +
            "(2,'en','Dear #name. I’m sorry to inform you about the misfortune event.',0,to_timestamp('09-JUL-19 02.53.10.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('09-JUL-19 02.53.11.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'))," +
            "(3,'en','We appreciate your interest in applying for employment, dear #Name. Your application was very strong, and you were among the four finalists',0,to_timestamp('09-JUL-19 02.53.10.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'),to_timestamp('09-JUL-19 02.53.11.000000000 PM','DD-MON-RR HH.MI.SSXFF AM'))")
    void get_message_template_should_correctly() throws Exception {
        var pageReq = LastRequest.builder().lastTime(0).limit(
                3).build();
        mvc.perform(get(MESSAGE_PATH + TEMPLATE_MESSAGE)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pageReq))
        ).andExpect(jsonPath(ROOT + data, hasSize(3)));
    }
}
