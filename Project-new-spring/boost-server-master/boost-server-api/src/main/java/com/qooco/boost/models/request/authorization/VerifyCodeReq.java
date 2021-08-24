package com.qooco.boost.models.request.authorization;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@SuperBuilder
@NoArgsConstructor
public class VerifyCodeReq {

    private String email;
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @Override
    public int hashCode() {
        return Objects.hash(email, code);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VerifyCodeReq that = (VerifyCodeReq) o;
        return email.equals(that.email) &&
                code.equals(that.code);
    }
}
