package com.qooco.boost.exception;

import com.qooco.boost.enumeration.ResponseStatus;

public class NoPermissionException extends RuntimeException {
    private ResponseStatus status;

    public NoPermissionException(ResponseStatus status) {
        super(status.toString());
        this.status = status;
    }

    public NoPermissionException(ResponseStatus status, Throwable cause) {
        super(status.toString(), cause);
    }

    public NoPermissionException(Throwable cause) {
        super(cause);
    }

    public NoPermissionException(ResponseStatus status, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status.toString(), cause, enableSuppression, writableStackTrace);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
