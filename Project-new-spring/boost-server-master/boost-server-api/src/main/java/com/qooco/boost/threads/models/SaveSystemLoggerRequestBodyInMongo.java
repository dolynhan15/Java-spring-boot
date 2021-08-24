package com.qooco.boost.threads.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Setter
@Getter
@NoArgsConstructor
public class SaveSystemLoggerRequestBodyInMongo {
    private HttpServletRequest request;
    private Object requestData;

    public SaveSystemLoggerRequestBodyInMongo(HttpServletRequest request, Object requestData) {
        this.request = request;
        this.requestData = requestData;
    }
}
