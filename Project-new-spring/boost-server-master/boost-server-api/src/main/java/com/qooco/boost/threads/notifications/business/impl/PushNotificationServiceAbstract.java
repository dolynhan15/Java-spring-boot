package com.qooco.boost.threads.notifications.business.impl;

import akka.actor.ActorRef;
import com.google.common.collect.Lists;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.threads.notifications.enumeration.PushTask;
import com.qooco.boost.threads.notifications.enumeration.PushType;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

public abstract class PushNotificationServiceAbstract {
    private static final int SEND_REALTIME_NOTIFY_VIA_SOCKET = 1;
    private static final int SEND_REALTIME_NOTIFY_VIA_BAIDU = 2;


    abstract ActorRef getPushNotificationActor();

    abstract ActorRef getSocketNotificationActor();

    abstract int getSendRealTimeNotifyType();

    abstract boolean isSilentPush();

    void sendRealTimeNotificationToHotel(Long receiverId, PushTask pushTask, Object message, boolean isCounted) {
        sendRealTimeNotification(receiverId, Const.PushTarget.PUSH_TO_HOTEL, pushTask, message, isCounted);
    }

    void sendRealTimeNotificationToCareer(Long receiverId, PushTask pushTask, Object message, boolean isCounted) {
        sendRealTimeNotification(receiverId, Const.PushTarget.PUSH_TO_CAREER, pushTask, message, isCounted);
    }

    void sendRealTimeNotification(Long receiverId, int target, PushTask pushTask, Object message, boolean isCounted) {
        if (Objects.nonNull(receiverId)) {
            NotifyPushTask task = new NotifyPushTask();
            task.setReceiverIds(Lists.newArrayList(receiverId));

            task.setTargetApp(target);
            task.setPushTask(pushTask);
            task.setPushType(PushType.SILENT);
            task.setContent(message);
            task.setCounted(isCounted);
            sendReadTimeNotify(task);
//            pushNotify(task);
        }
    }

    void sendRealTimeNotification(int target, PushTask pushTask, List<MessageDoc> messages, boolean isCounted) {
        if (CollectionUtils.isNotEmpty(messages)) {
            NotifyPushTask task = new NotifyPushTask();
            task.setTargetApp(target);
            task.setPushTask(pushTask);
            task.setPushType(PushType.SILENT);
            task.setContent(messages);
            task.setCounted(isCounted);
            sendReadTimeNotify(task);
//            pushNotify(task);
        }
    }

    private void sendReadTimeNotify(NotifyPushTask task) {
        if (getSendRealTimeNotifyType() == SEND_REALTIME_NOTIFY_VIA_SOCKET) {
            getSocketNotificationActor().tell(task, ActorRef.noSender());
        }
    }

    private void pushNotify(NotifyPushTask notifyPushTask) {
        NotifyPushTask pushNotifyPushTask = new NotifyPushTask(notifyPushTask);
        pushNotifyPushTask.setPushType(isSilentPush() ? PushType.SILENT : PushType.NOTIFICATION);
        getPushNotificationActor().tell(pushNotifyPushTask, ActorRef.noSender());
    }

    protected int getTargetApp(int receiveInApp) {
        int target = Const.PushTarget.PUSH_TO_HOTEL;
        if ((receiveInApp == MessageConstants.RECEIVE_IN_CAREER_APP)) {
            target = Const.PushTarget.PUSH_TO_CAREER;
        }
        return target;
    }
}