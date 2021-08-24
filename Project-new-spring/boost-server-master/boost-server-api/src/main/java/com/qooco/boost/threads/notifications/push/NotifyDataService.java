package com.qooco.boost.threads.notifications.push;

import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.threads.notifications.dto.NotifyData;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;

import java.util.List;

public interface NotifyDataService {
    NotifyData createNotifyData(NotifyPushTask pushTask, String locale);
    List<NotifyData> createNotifiesData(NotifyPushTask pushTask, String locale);
    List<NotifyData> createSentMessagesOfUser(AuthenticatedUser authenticatedUser);
}
