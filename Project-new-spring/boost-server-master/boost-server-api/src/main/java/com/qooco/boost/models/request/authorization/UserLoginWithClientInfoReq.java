package com.qooco.boost.models.request.authorization;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class UserLoginWithClientInfoReq {
    private UserLoginReq userLoginReq;
    private ClientInfoReq clientInfo;
}
