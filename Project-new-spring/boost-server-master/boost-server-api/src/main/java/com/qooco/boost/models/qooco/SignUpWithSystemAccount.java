package com.qooco.boost.models.qooco;

import com.google.gson.annotations.SerializedName;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 6/20/2018 - 9:55 AM
 */
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class SignUpWithSystemAccount extends QoocoResponseBase {
    private long userId;

    @SerializedName("signinId")
    private String signInId;

    private int verificationRequired;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSignInId() {
        return signInId;
    }

    public void setSignInId(String signInId) {
        this.signInId = signInId;
    }

    public int getVerificationRequired() {
        return verificationRequired;
    }

    public void setVerificationRequired(int verificationRequired) {
        this.verificationRequired = verificationRequired;
    }
}
