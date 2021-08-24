package com.qooco.boost.models.qooco;

import com.qooco.boost.constants.QoocoApiConstants;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 6/19/2018 - 6:31 PM
*/
public class SendVerificationCode {
    private String contact;
    private String locale;
    private String platformname;
    private String platform;

    public SendVerificationCode(SendVerificationCode sendVerificationCode) {
       this.contact = sendVerificationCode.getContact();
       this.locale = sendVerificationCode.getLocale();
       this.platformname = QoocoApiConstants.PLATFORM_BOOST;
       this.platform = QoocoApiConstants.PLATFORM_BOOST;
    }

    public SendVerificationCode(String contact, String locale) {
        this.contact = contact;
        this.locale = locale;
        this.platformname = QoocoApiConstants.PLATFORM_BOOST;
        this.platform = QoocoApiConstants.PLATFORM_BOOST;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }
}
