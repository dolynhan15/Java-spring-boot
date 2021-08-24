package com.qooco.boost.exception;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/16/2018 - 8:12 AM
 */

import com.qooco.boost.enumeration.ResponseStatus;

public class FileNotFoundException extends RuntimeException {
    private ResponseStatus status;

    public FileNotFoundException(ResponseStatus status) {
        super(status.toString());
        this.status = status;
    }

    public FileNotFoundException(ResponseStatus status, Throwable cause) {
        super(status.toString(), cause);
        this.status = status;
    }

    public FileNotFoundException(String msg) {
        super(msg);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
