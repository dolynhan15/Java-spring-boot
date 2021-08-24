package com.qooco.boost.socket.services;

import com.qooco.boost.threads.notifications.dto.NotifyData;

import java.util.List;

public interface SendNotifyToClientService {
    void sendNotifyMessage(List<NotifyData> notifiesData);
    void sendNotifyMessage(NotifyData notifyData);
}
