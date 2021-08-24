package com.qooco.boost.models.qooco;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 6/27/2018 - 8:36 AM
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class QoocoForgotPasswordResp extends QoocoResponseBase {

    private String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
