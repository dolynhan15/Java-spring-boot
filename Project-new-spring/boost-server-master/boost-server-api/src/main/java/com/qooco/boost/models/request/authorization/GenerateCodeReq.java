package com.qooco.boost.models.request.authorization;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
public class GenerateCodeReq {
    private String email;
    private String locale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerateCodeReq that = (GenerateCodeReq) o;
        return Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
