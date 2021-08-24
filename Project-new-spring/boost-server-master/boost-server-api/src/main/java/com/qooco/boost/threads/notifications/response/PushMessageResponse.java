package com.qooco.boost.threads.notifications.response;

import com.baidu.yun.push.model.PushResponse;
import lombok.Getter;
import lombok.Setter;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/31/2018 - 5:28 PM
 */
@Getter @Setter
public class PushMessageResponse extends PushResponse {
    private String messageId;
    private String timerId;
    private long sendTime;
    private long requestId;
    private int errorCode;
    private String errorMsg;
}
