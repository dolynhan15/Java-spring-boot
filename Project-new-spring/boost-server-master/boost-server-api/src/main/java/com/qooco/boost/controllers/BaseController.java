package com.qooco.boost.controllers;
import com.qooco.boost.business.SystemLoggerService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class BaseController {
    @Autowired
    private SystemLoggerService systemLoggerService;

    protected void saveRequestBodyToSystemLogger(Object body){
        systemLoggerService.saveSystemLogger(body);
    }

    protected Object success(BaseResp<?> object) {
        systemLoggerService.saveSystemLogger(object, null);
        return new ResponseEntity<BaseResp<?>>(object, HttpStatus.OK);
    }

    protected Object success(ResponseStatus message) {
        BaseResp<?> rsp = new BaseResp<>(message.getCode(), message.getDescription());
        systemLoggerService.saveSystemLogger(rsp, null);
        return new ResponseEntity<BaseResp<?>>(rsp, HttpStatus.OK);
    }

    protected Object error(int code, String message) {
        BaseResp<?> rsp = new BaseResp<>(code, message);
        systemLoggerService.saveSystemLogger(rsp, null);
        return new ResponseEntity<BaseResp<?>>(rsp, HttpStatus.OK);
    }
}
