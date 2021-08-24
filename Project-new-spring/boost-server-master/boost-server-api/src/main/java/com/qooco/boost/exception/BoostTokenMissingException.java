package com.qooco.boost.exception;

import com.qooco.boost.enumeration.ResponseStatus;
import org.springframework.security.core.AuthenticationException;

/**
 * Thrown when token cannot be found in the request header
 * @author pascal alma
 */

public class BoostTokenMissingException extends AuthenticationException {

    ResponseStatus status;

    public BoostTokenMissingException(ResponseStatus status) {
        super(status.getDescription());
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
