package com.qooco.boost.models.transfer;

import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.enumeration.BoostHelperEventType;
import lombok.*;

import java.util.Locale;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoostHelperFitTransfer {
    private UserFit userFit;
    private BoostHelperEventType eventType;
    private Locale locale;
}