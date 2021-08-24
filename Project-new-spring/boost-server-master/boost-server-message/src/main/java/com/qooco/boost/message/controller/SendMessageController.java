package com.qooco.boost.message.controller;

import com.qooco.boost.core.model.BaseResp;
import com.qooco.boost.message.business.BusinessSendMessageService;
import com.qooco.boost.message.constant.URLConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(tags = "Message API", description = "Message Api")
@RestController
@RequestMapping()
public class SendMessageController {

    @Autowired
    private BusinessSendMessageService businessSendMessageService;

    @ApiOperation(value = "Send a message", httpMethod = "POST", response = BaseResp.class)
    @PostMapping(URLConstant.MESSAGE_PATH)
    public Object sendMessage(@RequestBody Map<String, List<String>> message) {
        BaseResp result = businessSendMessageService.sendMessage(message);
        return success(result);
    }

    private Object success(BaseResp<?> object) {
        return new ResponseEntity<BaseResp<?>>(object, HttpStatus.OK);
    }
}
