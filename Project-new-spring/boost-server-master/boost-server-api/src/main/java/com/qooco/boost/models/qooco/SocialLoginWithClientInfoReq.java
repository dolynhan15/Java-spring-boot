package com.qooco.boost.models.qooco;

import com.qooco.boost.models.request.authorization.ClientInfoReq;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SocialLoginWithClientInfoReq {
    private SocialLoginReq loginReq;
    private ClientInfoReq clientInfo;
}
