package com.qooco.boost.models;

import com.qooco.boost.enumeration.ResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;

@Getter @Setter @NoArgsConstructor
@FieldNameConstants
public class BaseResp<T> {

    private T data;
    private int code = ResponseStatus.SUCCESS.getCode();
    private String message = ResponseStatus.SUCCESS.toString();
    private Date serverTime = new Date();

    public BaseResp(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public BaseResp(T data) {
        this.data = data;
    }

    public BaseResp(T data, ResponseStatus responseStatus) {
        this(responseStatus.getCode(), responseStatus.getDescription());
        this.data = data;
    }

    public BaseResp(ResponseStatus responseStatus) {
        this.code = responseStatus.getCode();
        this.message = responseStatus.getDescription();
    }
}
