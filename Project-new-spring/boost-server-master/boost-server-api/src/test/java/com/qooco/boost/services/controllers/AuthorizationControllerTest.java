package com.qooco.boost.services.controllers;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/19/2018 - 1:43 PM
 */

import com.google.gson.Gson;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.request.authorization.VerifyCodeReq;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//@RunWith(SpringRunner.class)
//@AutoConfigureMockMvc
//@SpringBootTest
public class AuthorizationControllerTest {
    //@Autowired
    private MockMvc mvc;

    //@Test
    public void doVerifyCode() throws Exception {
        VerifyCodeReq request = new VerifyCodeReq();
        request.setEmail("trungmhv@gmail.com");
        request.setCode("1234");
        Gson gson = new Gson();
        String json = gson.toJson(request);

        mvc.perform(MockMvcRequestBuilders.post(URLConstants.VERIFY_CODE_PATH).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
