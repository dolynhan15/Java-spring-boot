package com.qooco.boost.core.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.AuthenticationException;

@Setter @Getter
public class UnauthorizedException extends AuthenticationException {
    private Integer code;
    private String msg;

    public UnauthorizedException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }


}
