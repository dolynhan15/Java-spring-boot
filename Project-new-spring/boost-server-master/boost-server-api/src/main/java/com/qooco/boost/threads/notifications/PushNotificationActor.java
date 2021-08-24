package com.qooco.boost.threads.notifications;

import akka.actor.UntypedAbstractActor;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.model.PushRequest;
import com.qooco.boost.data.mongo.entities.PushNotificationDoc;
import com.qooco.boost.data.mongo.entities.PushNotificationHistoryDoc;
import com.qooco.boost.data.mongo.services.PushNotificationDocService;
import com.qooco.boost.data.mongo.services.PushNotificationHistoryDocService;
import com.qooco.boost.threads.notifications.enumeration.BaiduErrorCode;
import com.qooco.boost.threads.notifications.push.BaiduPushNotificationService;
import com.qooco.boost.threads.notifications.push.NotifyNewMessageService;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import com.qooco.boost.threads.notifications.requests.PushNotificationMessage;
import com.qooco.boost.threads.notifications.response.PushMessageResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PushNotificationActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(PushNotificationActor.class);
    public static final String ACTOR_NAME = "pushNotificationActor";


    private BaiduPushNotificationProvider pushNotificationProvider;
    private BaiduPushNotificationService baiduPushNotificationService;
    private NotifyNewMessageService notifyNewMessageService;
    private PushNotificationDocService pushNotificationDocService;
    private PushNotificationHistoryDocService pushNotificationHistoryDocService;

    public PushNotificationActor(BaiduPushNotificationProvider pushNotificationProvider,
                                 BaiduPushNotificationService baiduPushNotificationService,
                                 NotifyNewMessageService notifyNewMessageService,
                                 PushNotificationDocService pushNotificationDocService,
                                 PushNotificationHistoryDocService pushNotificationHistoryDocService) {
        this.pushNotificationProvider = pushNotificationProvider;
        this.baiduPushNotificationService = baiduPushNotificationService;
        this.notifyNewMessageService = notifyNewMessageService;
        this.pushNotificationDocService = pushNotificationDocService;
        this.pushNotificationHistoryDocService = pushNotificationHistoryDocService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof NotifyPushTask) {
            List<PushNotificationMessage> notifications = notifyNewMessageService.createPushNotificationMessage((NotifyPushTask) message, null);
            doSendPushNotification(notifications);
        } else if (message instanceof PushNotificationMessage) {
            doSendPushNotification((PushNotificationMessage) message);
        } else if (message instanceof PushNotificationDoc) {
            doResendPushNotification((PushNotificationDoc) message);
        }
    }

    private void doSendPushNotification(List<PushNotificationMessage> requests) {
        if (CollectionUtils.isNotEmpty(requests)) {
            for (PushNotificationMessage request : requests) {
                doSendPushNotification(request);
            }
        }
    }

    private void doSendPushNotification(PushNotificationMessage request) {
        PushRequest push = (PushRequest) request.getMessage();
        if (Objects.nonNull(push)) {
            BaiduPushClient pushClient = pushNotificationProvider.getBaiduPushClient(request.getTargetApp(), push.getDevice());
            PushMessageResponse response = baiduPushNotificationService.pushNotification(pushClient, push);
            savePushNotification(push, response);
        }
    }

    private void doResendPushNotification(PushNotificationDoc pushMsg) {
        PushRequest push = null;
        if (Objects.nonNull(pushMsg.getPushMsgToAll())) {
            push = pushMsg.getPushMsgToAll();
        } else if (Objects.nonNull(pushMsg.getPushMsgToSingle())) {
            push = pushMsg.getPushMsgToSingle();
        } else if (Objects.nonNull(pushMsg.getPushBatchUniMsg())) {
            push = pushMsg.getPushBatchUniMsg();
        }
        if (Objects.nonNull(push)) {
            BaiduPushClient pushClient = pushNotificationProvider.getBaiduPushClient(pushMsg.getTargetApp(), push.getDevice());
            PushMessageResponse response = baiduPushNotificationService.pushNotification(pushClient, push);
            savePushNotification(push, response);
            pushNotificationDocService.deleteByRequestId(pushMsg.getRequestId());
        }
    }

    private void savePushNotification(PushRequest push, PushMessageResponse response) {
        if (Objects.nonNull(response) && response.getErrorCode() == BaiduErrorCode.SUCCESS.getErrorCode()) {
            pushNotificationHistoryDocService.save(new PushNotificationHistoryDoc(push, response));
        } else {
            pushNotificationDocService.save(new PushNotificationDoc(push, response));
        }
    }
}
