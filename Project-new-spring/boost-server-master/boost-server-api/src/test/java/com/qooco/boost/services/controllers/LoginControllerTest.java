package com.qooco.boost.services.controllers;

import com.google.gson.Gson;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.request.authorization.UserLoginReq;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

//@RunWith(SpringRunner.class)
//@WebMvcTest(LoginController.class)
//@AutoConfigureMockMvc
//@SpringBootTest
public class LoginControllerTest {

    //@Autowired
    private MockMvc mvc;

    //@Test
    public void doLogin() throws Exception {
        UserLoginReq request = new UserLoginReq();
        request.setUsername("");
        request.setPassword("");
        Gson gson = new Gson();
        String json = gson.toJson(request);

        mvc.perform(MockMvcRequestBuilders.post(URLConstants.LOGIN_PATH).content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

//    public static void main(String [] ad){
//        UserLoginReq request = new UserLoginReq();
//        request.setToken("");
//        Gson gson = new Gson();
//        String json = gson.toJson(request);
//    }
}