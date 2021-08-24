package com.qooco.boost.models.transfer;

import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.enumeration.BoostHelperEventType;
import lombok.*;

import java.util.Locale;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoostHelperProfileTransfer {
    private UserProfile userProfile;
    private BoostHelperEventType eventType;
    private String referralCode;
    private Locale locale;
}