package com.qooco.boost.models.qooco;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse extends QoocoResponseBase {

    @SerializedName("UserId")
    private long userId;
    private String username;
    private String nickname;
    private String email;
    private String userRole;
    private String signinId;
    @SerializedName("SessionId")
    private String sessionId;
    private int successfulLoginCount;
    private int verificationRequired;
    private int phoneVerified;
    private int emailVerified;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getSigninId() {
        return signinId;
    }

    public void setSigninId(String signinId) {
        this.signinId = signinId;
    }

    public int getSuccessfulLoginCount() {
        return successfulLoginCount;
    }

    public void setSuccessfulLoginCount(int successfulLoginCount) {
        this.successfulLoginCount = successfulLoginCount;
    }

    public int getVerificationRequired() {
        return verificationRequired;
    }

    public void setVerificationRequired(int verificationRequired) {
        this.verificationRequired = verificationRequired;
    }

    public int getPhoneVerified() {
        return phoneVerified;
    }

    public void setPhoneVerified(int phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public int getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(int emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
