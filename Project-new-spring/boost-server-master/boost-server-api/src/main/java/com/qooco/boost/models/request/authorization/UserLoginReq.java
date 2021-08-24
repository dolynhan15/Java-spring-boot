package com.qooco.boost.models.request.authorization;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class UserLoginReq {
    private String username;
    private String password;
}
