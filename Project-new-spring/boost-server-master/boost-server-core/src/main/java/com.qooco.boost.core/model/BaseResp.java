package com.qooco.boost.core.model;

import com.qooco.boost.core.enumeration.ResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BaseResp<T> {

    private T data;
    private int code = ResponseStatus.SUCCESS.getCode();
    private String message = ResponseStatus.SUCCESS.getDescription();

    public BaseResp(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public BaseResp(T data) {
        this.data = data;
    }

    public BaseResp(T data, Integer code, String msg) {
        this(code, msg);
        this.data = data;
    }
}
