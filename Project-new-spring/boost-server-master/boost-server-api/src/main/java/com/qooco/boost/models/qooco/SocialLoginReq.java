package com.qooco.boost.models.qooco;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SocialLoginReq {
    private String oauthId;
    private String oauthProvider;
    private String app;
    private String nickname;
}
