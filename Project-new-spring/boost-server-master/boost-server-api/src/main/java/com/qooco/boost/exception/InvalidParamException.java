package com.qooco.boost.exception;

import com.qooco.boost.enumeration.ResponseStatus;
import org.apache.commons.lang3.StringUtils;

import static java.util.Optional.ofNullable;

public class InvalidParamException extends RuntimeException {
    private ResponseStatus status;

    public InvalidParamException(ResponseStatus status) {
        super(status.toString());
        this.status = status;
    }

    public InvalidParamException(ResponseStatus status, String message) {
        super(status.getCode() + " - " + ofNullable(message).filter(StringUtils::isNotEmpty).orElseGet(status::getDescription));
        this.status = status;
    }

    public InvalidParamException(ResponseStatus status, Throwable cause) {
        super(status.toString(), cause);
    }

    public InvalidParamException(Throwable cause) {
        super(cause);
    }

    public InvalidParamException(ResponseStatus status, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status.toString(), cause, enableSuppression, writableStackTrace);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
