package com.qooco.boost.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 1/22/2019 - 1:51 PM
*/
@Getter @Setter @NoArgsConstructor
public class UserUploadKeyReq {
    private String publicKey;

    public UserUploadKeyReq(String publicKey) {
        this.publicKey = publicKey;
    }
}
