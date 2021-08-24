package com.qooco.boost.threads.notifications.push;

import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import com.qooco.boost.threads.notifications.requests.PushNotificationMessage;

import java.util.List;

public interface NotifyNewMessageService {
    List<PushNotificationMessage> createPushNotificationMessage(NotifyPushTask pushTask, String locale);
}
