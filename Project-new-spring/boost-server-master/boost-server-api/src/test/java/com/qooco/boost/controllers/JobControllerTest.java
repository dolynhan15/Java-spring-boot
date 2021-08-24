package com.qooco.boost.controllers;

import com.qooco.boost.models.request.JobReq;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static com.qooco.boost.constants.StatusConstants.*;
import static com.qooco.boost.constants.URLConstants.*;
import static com.qooco.boost.core.enumeration.ApplicationConfig.BOOST_CORE_SECURITY_TOKEN_NAME;
import static com.qooco.boost.models.BaseResp.Fields.code;
import static com.qooco.boost.models.BaseResp.Fields.data;
import static com.qooco.boost.models.dto.JobDTO.Fields.jobName;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class JobControllerTest extends BaseMvcTest {

    @Test
    void get_global_jobs_should_not_get_without_authentication() throws Exception {
        mvc.perform(get(JOB_PATH + GET_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {INSERT_HOTEL_TYPE + "(6,'Resort')",
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(2685,229,'Son Tra',null,null,0)",
            INSERT_COMPANY + "(42419,'Bless Resort','/1000527993/company_logo/bOeIddVV6815707302802418169.png',2685,'30 April to','+84 999999999','blessresort@bless.com','blessresort.com.vn','SDFSDD',null,'SDFSFSDF','SDFSFSF',6,'getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I get home I will send you the link to the video of the video of the video of the video of the video of the video of hei getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I geso home I will send you the link to the video of the video of the video of the video of the video of the video of hei getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I',1,1000527993,to_date('21-MAR-19','DD-MON-RR'),1000527993,to_date('21-MAR-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)"
                    + ",(31,'Catering Sales Manager','Catering Sales Manager', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, 42419)"})
    void get_global_jobs_should_get_correctly() throws Exception {
        mvc.perform(get(JOB_PATH + GET_METHOD).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)).andExpect(jsonPath(ROOT + data, hasSize(1)));
    }

    @Test
    void get_jobs_by_company_should_not_get_without_authentication() throws Exception {
        mvc.perform(get(JOB_PATH + BY_COMPANY_PATH)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    @Sql(statements = {INSERT_HOTEL_TYPE + "(6,'Resort')",
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(2685,229,'Son Tra',null,null,0)",
            INSERT_COMPANY + "(42419,'Bless Resort','/1000527993/company_logo/bOeIddVV6815707302802418169.png',2685,'30 April to','+84 999999999','blessresort@bless.com','blessresort.com.vn','SDFSDD',null,'SDFSFSDF','SDFSFSF',6,'getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I get home I will send you the link to the video of the video of the video of the video of the video of the video of hei getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I geso home I will send you the link to the video of the video of the video of the video of the video of the video of hei getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I',1,1000527993,to_date('21-MAR-19','DD-MON-RR'),1000527993,to_date('21-MAR-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)"
                    + ",(31,'Catering Sales Manager','Catering Sales Manager', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, 42419)"})
    void get_jobs_by_company_should_get_global_jobs_correctly_if_company_id_is_wrong() throws Exception {
        mvc.perform(get(JOB_PATH + BY_COMPANY_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param("id", "0")).andExpect(jsonPath(ROOT + data, hasSize(1)));
    }

    @Test
    @Sql(statements = {INSERT_HOTEL_TYPE + "(6,'Resort')",
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(229,220,'Da Nang',null,'58',0)",
            INSERT_CITY + "(2685,229,'Son Tra',null,null,0)",
            INSERT_COMPANY + "(42419,'Bless Resort','/1000527993/company_logo/bOeIddVV6815707302802418169.png',2685,'30 April to','+84 999999999','blessresort@bless.com','blessresort.com.vn','SDFSDD',null,'SDFSFSDF','SDFSFSF',6,'getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I get home I will send you the link to the video of the video of the video of the video of the video of the video of hei getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I geso home I will send you the link to the video of the video of the video of the video of the video of the video of hei getdefaultheight I have to go to the store and get some rest and feel better soon and that is why I am asking for a friend to talk to you about it when I',1,1000527993,to_date('21-MAR-19','DD-MON-RR'),1000527993,to_date('21-MAR-19','DD-MON-RR'),0)",
            INSERT_JOB + "(88,'BOOST Intern','BOOST Intern', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, NULL)"
            + ",(31,'Catering Sales Manager','Catering Sales Manager', NULL, TIMESTAMP '2018-07-05 12:47:08.090000', NULL, TIMESTAMP '2018-07-05 12:47:08.090000',0, 42419)"})
    void get_jobs_by_company_should_get_correctly_if_company_id_is_valid() throws Exception {
        mvc.perform(get(JOB_PATH + BY_COMPANY_PATH).header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER).param("id", "42419")).andExpect(jsonPath(ROOT + data, hasSize(2)));
    }

    @Test
    void create_new_job_should_not_get_without_authentication() throws Exception {
        mvc.perform(post(JOB_PATH + SAVE_METHOD)).andExpect(jsonPath(ROOT + code).value(TOKEN_MISSING));
    }

    @Test
    void create_new_job_should_not_get_without_body_request() throws Exception {
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_new_job_should_not_get_without_job_is_empty() throws Exception {
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(null))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void create_new_job_should_not_get_without_job_name() throws Exception {
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new JobReq()))
        ).andExpect(jsonPath(ROOT + code).value(JOB_NAME_IS_EMPTY));
    }

    @Test
    void create_new_job_should_not_get_without_job_name_too_short() throws Exception {
        var jobReq = JobReq.builder().jobName("J").build();
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq))
        ).andExpect(jsonPath(ROOT + code).value(JOB_NAME_IS_TOO_SHORT));
    }

    @Test
    void create_new_job_should_not_get_without_job_name_too_long() throws Exception {
        var jobReq = JobReq.builder().jobName("Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.  It has survived not only five centuries").build();
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq))
        ).andExpect(jsonPath(ROOT + code).value(JOB_NAME_IS_TOO_LONG));
    }

    @Test
    void create_new_job_should_not_get_without_job_description_too_short() throws Exception {
        var jobReq = JobReq.builder()
                .jobName("Bartender")
                .jobDescription("A")
                .build();
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq))
        ).andExpect(jsonPath(ROOT + code).value(JOB_DESCRIPTION_IS_TOO_SHORT));
    }

    @Test
    void create_new_job_should_not_get_without_job_description_too_long() throws Exception {
        var jobDescriptionMax = StringUtils.leftPad("", 1001, "a");
        var jobReq = JobReq.builder()
                .jobName("Bartender")
                .jobDescription(jobDescriptionMax)
                .build();
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq))
        ).andExpect(jsonPath(ROOT + code).value(JOB_DESCRIPTION_IS_TOO_LONG));
    }

    @Test
    void create_new_job_should_not_get_without_company_id_is_empty() throws Exception {
        var jobDescriptionMax = StringUtils.leftPad("", 100, "a");
        var jobReq = JobReq.builder()
                .jobName("Bartender")
                .jobDescription(jobDescriptionMax)
                .build();
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                        .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(jobReq))
        ).andExpect(jsonPath(ROOT + code).value(COMPANY_ID_IS_EMPTY));
    }

    @Test
    @Sql(statements = {
            INSERT_COUNTRY + "(220,'Vietnam','VN','+84',0)",
            INSERT_PROVINCE + "(229,220,'Da Nang',null,'58',0),(216,220,'Ba Ria–Vung Tau',null,'84',0)",
            INSERT_CITY + "(2560,216,'Con Dao',null,null,0)",
            INSERT_HOTEL_TYPE + "(3,'Suite')",
            INSERT_COMPANY + "(42421,'GEM YOP TWO COPR','/1000525983/company_logo/tSGwhRr54874016469894128414.jpg',2560,'104 Pham Quang Anh','+84 12345678955','gemyop234@yopmail.com','gemyop24.com','123456W','123456FDFD','1234F','12345',3,'This is the description',1,1000525983,to_date('21-MAR-19','DD-MON-RR'),1000525983,to_date('21-MAR-19','DD-MON-RR'),0)"
    })
    void create_new_job_should_get_correctly() throws Exception {
        var jobDescriptionMax = StringUtils.leftPad("", 100, "a");
        var jobReq = JobReq.builder()
                .jobName("Bartender")
                .jobDescription(jobDescriptionMax)
                .companyId(42421L)
                .build();
        mvc.perform(post(JOB_PATH + SAVE_METHOD)
                .header(BOOST_CORE_SECURITY_TOKEN_NAME.value(), TOKEN_NORMAL_USER)
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(jobReq))
        ).andExpect(jsonPath(ROOT + data + '.' + jobName).value(jobReq.getJobName()));
    }
}
