package com.qooco.boost.models.request.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 6/19/2018 - 10:53 AM
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @SuperBuilder
public class SignUpSystemReq {
    private String email;
    private String username;
    private String password;
}
