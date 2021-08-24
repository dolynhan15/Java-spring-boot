package com.qooco.boost.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.INVALID_PAGINATION;
import static com.qooco.boost.constants.StatusConstants.TOKEN_MISSING;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.data.oracle.entities.Province.Fields.provinceId;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.PagedResult.Fields.results;
import static com.qooco.boost.models.request.PageRequest.Fields.size;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

class CityControllerTest extends BaseMvcTest {

    @Test
    void should_not_get_without_authentication() throws Exception {
        mvc.perform(get(PATA_PATH + CITY_PATH + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void should_not_get_if_invalid_params() throws Exception {
        mvc.perform(get(PATA_PATH + CITY_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + code).value(INVALID_PAGINATION));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(147,93,'Aceh',null,'10',0)",
            INSERT_CITY + "(844,147,'Kampung Jawa Baru',null,null,0)" +
                    ",(845,147,'Babussalam',null,null,0)"
    })
    void should_get_all_correctly() throws Exception {
        mvc
                .perform(get(PATA_PATH + CITY_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(provinceId, "147"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(2)));
    }

    @Test
    void should_get_none_if_wrong_id() throws Exception {
        mvc
                .perform(get(PATA_PATH + CITY_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(provinceId, "0"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(0)));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(93,'Indonesia','ID','+62',0)",
            INSERT_PROVINCE + "(147,93,'Aceh',null,'10',0)",
            INSERT_CITY + "(844,147,'Kampung Jawa Baru',null,null,0)" +
                    ",(845,147,'Babussalam',null,null,0)"
    })
    void should_get_page_correctly() throws Exception {
        mvc
                .perform(get(PATA_PATH + CITY_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param(provinceId, "147").param(size, "1"))
                .andExpect(jsonPath(ROOT + data + '.' + results, hasSize(1)));
    }
}
