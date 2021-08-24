package com.example.demo.model;

import com.example.demo.constant.ResponseStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BaseResp<T> {
    private T data;
    private int code = ResponseStatus.SUCCESS.getCode();
    private String message = ResponseStatus.SUCCESS.getDescription();
    private Date serverTime = new Date();

    public BaseResp(T data) {
        this.data = data;
    }
}
