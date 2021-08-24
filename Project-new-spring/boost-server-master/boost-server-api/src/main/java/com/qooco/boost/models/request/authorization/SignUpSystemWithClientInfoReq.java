package com.qooco.boost.models.request.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SignUpSystemWithClientInfoReq {
    private SignUpSystemReq signUpSystemReq;
    private ClientInfoReq clientInfo;
}
