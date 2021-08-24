package com.qooco.boost.models.sdo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedDateSDO {
    private Long userId;
    private Long referralCodeId;
    private Date createdDateEvent;
}
