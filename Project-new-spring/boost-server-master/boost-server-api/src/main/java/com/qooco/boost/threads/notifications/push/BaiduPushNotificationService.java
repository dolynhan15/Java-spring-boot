package com.qooco.boost.threads.notifications.push;

import com.baidu.yun.push.client.BaiduPushClient;
import com.qooco.boost.threads.notifications.response.PushMessageResponse;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 1:47 PM
 */
public interface BaiduPushNotificationService {
    PushMessageResponse pushNotification(BaiduPushClient pushClient, Object message);
}
