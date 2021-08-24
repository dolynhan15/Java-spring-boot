package com.qooco.boost.exception;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/25/2018 - 12:31 PM
 */

import com.qooco.boost.enumeration.ResponseStatus;

public class EntityNotFoundException extends RuntimeException {
    private ResponseStatus status;

    public EntityNotFoundException(ResponseStatus status) {
        super(status.toString());
        this.status = status;
    }
    public EntityNotFoundException(String msg) {
        super(msg);
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public void setStatus(ResponseStatus status) {
        this.status = status;
    }
}
