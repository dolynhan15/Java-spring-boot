package com.qooco.boost.threads.notifications.requests;

import com.qooco.boost.threads.notifications.enumeration.PushTask;
import com.qooco.boost.threads.notifications.enumeration.PushType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class NotifyPushTask {
    private List<Long> receiverIds;
    private int targetApp;
    private PushTask pushTask;
    private PushType pushType;
    private Object content;
    private boolean isCounted;

    public NotifyPushTask(NotifyPushTask notifyPushTask) {
        this.receiverIds = notifyPushTask.getReceiverIds();
        this.targetApp = notifyPushTask.getTargetApp();
        this.pushTask = notifyPushTask.getPushTask();
        this.pushType = notifyPushTask.getPushType();
        this.content = notifyPushTask.getContent();
        this.isCounted = notifyPushTask.isCounted();
    }
}
