package com.qooco.boost.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;

@Setter @Getter
public class BoostTokenMissingException extends AuthenticationException {
    private Integer code;
    private String msg;

    public BoostTokenMissingException(String msg, Throwable t, Integer code) {
        super(msg, t);
        this.code = code;
        this.msg = msg;
    }

    public BoostTokenMissingException(String msg, Integer code) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
