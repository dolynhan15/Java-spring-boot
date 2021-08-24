package com.qooco.boost.exception;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/25/2018 - 12:33 PM
 */

import com.qooco.boost.enumeration.ResponseStatus;
import org.springframework.security.core.AuthenticationException;

public class UnauthorizedException extends AuthenticationException {
    private ResponseStatus status;

    public UnauthorizedException(ResponseStatus status) {
        super(status.toString());
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
