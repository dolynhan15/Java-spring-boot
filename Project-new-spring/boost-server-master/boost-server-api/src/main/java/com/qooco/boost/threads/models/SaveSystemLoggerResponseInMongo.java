package com.qooco.boost.threads.models;

import com.qooco.boost.models.BaseResp;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Setter
@Getter
@NoArgsConstructor
public class SaveSystemLoggerResponseInMongo {
    HttpServletRequest request;
    BaseResp response;
    String stackTrace;

    public SaveSystemLoggerResponseInMongo(HttpServletRequest request, BaseResp response, String stackTrace) {
        this.request = request;
        this.response = response;
        this.stackTrace = stackTrace;
    }
}
