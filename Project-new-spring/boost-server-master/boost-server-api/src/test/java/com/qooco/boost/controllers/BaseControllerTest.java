//package com.qooco.boost.controllers;/*
// * Copyright: Falcon Team - AxonActive
// * User: mhvtrung
// * Date: 6/22/2018 - 4:55 PM
// */
//
//import com.google.gson.Gson;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
//
//public class BaseControllerTest {
//
//    @Autowired
//    protected static MockMvc mvc;
//
//    protected ResultActions doTest(String api, Object request) throws Exception{
//        Gson gson = new Gson();
//        String json = gson.toJson(request);
//
//        return mvc.perform(MockMvcRequestBuilders.post(api).content(json)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    protected ResultActions doTestForPostMethod(MockMvc mvc, String api, Object request) throws Exception{
//        Gson gson = new Gson();
//        String json = gson.toJson(request);
//
//        return mvc.perform(MockMvcRequestBuilders.post(api).content(json)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//
//    protected ResultActions doTestForGetMethod(MockMvc mvc, String api, String id) throws Exception{
//        return mvc.perform(MockMvcRequestBuilders.get(api)
//                .param("userProfileId", id))
//                .andExpect(MockMvcResultMatchers.status().isOk());
//    }
//}
