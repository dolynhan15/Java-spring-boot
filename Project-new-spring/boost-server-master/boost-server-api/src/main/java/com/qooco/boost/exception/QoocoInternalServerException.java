package com.qooco.boost.exception;

import com.qooco.boost.enumeration.ResponseStatus;

public class QoocoInternalServerException extends RuntimeException {
    private ResponseStatus status;

    public QoocoInternalServerException(ResponseStatus status) {
        super(status.toString());
        this.status = status;
    }

    public QoocoInternalServerException(ResponseStatus status, Throwable cause) {
        super(status.toString(), cause);
    }

    public QoocoInternalServerException(Throwable cause) {
        super(cause);
    }

    public QoocoInternalServerException(ResponseStatus status, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(status.toString(), cause, enableSuppression, writableStackTrace);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
