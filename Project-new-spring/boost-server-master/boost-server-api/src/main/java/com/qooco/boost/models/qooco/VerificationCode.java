package com.qooco.boost.models.qooco;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 6/20/2018 - 7:51 AM
 */

public class VerificationCode {
    private String contact;
    private String code;

    public VerificationCode(String contact, String code) {
        this.contact = contact;
        this.code = code;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
