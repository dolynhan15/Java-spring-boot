package com.qooco.boost.data.enumeration.doc;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/31/2018 - 11:07 AM
 */
@AllArgsConstructor
public enum PushNotificationDocEnum {
    ID("id"),
    REQUEST_ID("requestId"),
    ERROR_CODE("errorCode"),
    ERROR_MSG("errorMsg"),
    TYPE_MSG("typeMsg"),
    PUSH_MSG_TO_SINGLE("pushMsgToSingle"),
    PUSH_MSG_TO_ALL("pushMsgToAll"),
    PUSH_BATCH_UNI_MSG("pushBatchUniMsg");

    @Getter
    private final String key;
}
