package com.example.demo.exception;

import com.example.demo.constant.ResponseStatus;
import lombok.Getter;

@Getter
public class InvalidParamException extends RuntimeException {
    private ResponseStatus status;

    public InvalidParamException(ResponseStatus status) {
        super(status.getDescription());
        this.status = status;
    }
}
