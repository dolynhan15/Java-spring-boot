package com.qooco.boost.controllers;

import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;
import java.util.stream.Stream;

import static com.qooco.boost.constants.StatusConstants.NO_PERMISSION_TO_ACCESS;
import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.CONVERSATION;
import static com.qooco.boost.constants.URLConstants.CUSTOMER_CARE_PATH;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.mongo.entities.SupportConversationDoc.Fields.fromApp;
import static com.qooco.boost.data.mongo.entities.SupportConversationDoc.Fields.status;
import static com.qooco.boost.data.mongo.entities.base.ConversationBase.Fields.updatedDate;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.dto.message.ConversationDTO.Fields.id;
import static com.qooco.boost.models.request.PageRequest.Fields.page;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CustomerCareControllerTest extends BaseMvcTest {

    @Test
    void should_not_get_conversations_unauthenticated() throws Exception {
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void should_not_get_conversations_no_permission() throws Exception {
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + code).value(NO_PERMISSION_TO_ACCESS));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_by_page_size_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param(size, "1"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000001"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_by_page_size0_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param(size, "0"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_by_page_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param(size, "1").param(page, "1"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000002"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_by_app_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param(fromApp, String.valueOf(SELECT_APP.value())))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000002"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_by_status_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .status(1)
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .status(2)
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param(status, "2"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000002"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId())
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId())
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5")).andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_sort_by_app_desc_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param("sort", fromApp + " desc"))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000001"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_sort_by_app_asc_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param("sort", fromApp))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000002"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_sort_by_updated_date_desc_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param("sort", updatedDate + " desc"))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000001"));
    }

    @Test
    @Sql(statements = {
            INSERT_USER_PROFILE + "(1000527753,'DEVADMIN',null,null,null,null,null,null,null,null,null,null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),null,to_timestamp('21-MAR-19 03.03.46.503000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'devadmin@yopmail.com',1,null,null,null,null,null,null,null,null,null,null,null,null)",
            INSERT_USER_ACCESS_TOKEN + "(1000527753,'bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5','bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5',1,94739,null,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,1000527753,to_timestamp('02-APR-19 07.18.21.562000000 AM','DD-MON-RR HH.MI.SSXFF AM'),1000527753,'FC84287F6691D4799D1CF3E84E5C57F5','FC84287F6691D4799D1CF3E84E5C57F5',0,null,null)",
            INSERT_CLIENT_INFO + "('bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5', 'com.qooco.boost.webadmin', '1.0', 'x', 'x', 'x', 'x', 1000528950, to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), to_timestamp('21-MAR-19 04.05.32.794000000 AM','DD-MON-RR HH.MI.SSXFF AM'), 'x')"
    })
    void should_get_conversations_sort_by_updated_date_asc_correctly() throws Exception {
        Stream.of(
                SupportConversationDoc.builder().fromApp(PROFILE_APP.value())
                        .id(new ObjectId("000000000000000000000001"))
                        .isDeleted(false)
                        .updatedDate(new Date())
                        .build(),
                SupportConversationDoc.builder().fromApp(SELECT_APP.value())
                        .id(new ObjectId("000000000000000000000002"))
                        .isDeleted(false)
                        .updatedDate(addDays(new Date(), -1))
                        .build()
        ).forEach(mongoTemplate::save);
        mvc.perform(get(CUSTOMER_CARE_PATH + CONVERSATION).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), "bd35d5c0f62e6906441e75f582ec49d7df2cab79cc3a30772291a3635b9fb9c5").param("sort", updatedDate))
                .andExpect(jsonPath(ROOT + data + '.' + results + INDEX0 + id).value("000000000000000000000002"));
    }
}
