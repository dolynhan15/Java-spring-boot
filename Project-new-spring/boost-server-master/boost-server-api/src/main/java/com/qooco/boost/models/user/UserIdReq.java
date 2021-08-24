package com.qooco.boost.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/22/2018 - 4:58 PM
 */
@Setter
@Getter
@NoArgsConstructor
public class UserIdReq implements Serializable {
    private long userCVId;
}
