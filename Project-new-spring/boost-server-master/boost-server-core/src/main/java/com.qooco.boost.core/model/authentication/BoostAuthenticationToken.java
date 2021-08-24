package com.qooco.boost.core.model.authentication;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Setter
@Getter
@Accessors(chain = true, fluent = true)
public class BoostAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private String token;
    private String timezone;
    private Integer appVersion;
    private String appVersionName;
    private String locale;

    public BoostAuthenticationToken() {
        super(null, null);
    }
}
