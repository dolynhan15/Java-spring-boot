package com.qooco.boost.threads.notifications;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedAbstractActor;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.mongo.entities.PushNotificationDoc;
import com.qooco.boost.data.mongo.services.PushNotificationDocService;
import com.qooco.boost.threads.notifications.enumeration.BaiduErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/30/2018 - 11:58 AM
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class PushNotificationResendActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(PushNotificationResendActor.class);
    public static final String ACTOR_NAME = "pushNotificationResendActor";

    public static final int resentPushErrorCodes[] = {
            BaiduErrorCode.INTERNAL_SERVER_ERROR.getErrorCode(),
            BaiduErrorCode.REQUEST_TIME_EXPIRES_TIMEOUT.getErrorCode(),
            BaiduErrorCode.CHANNEL_TOKEN_TIMEOUT.getErrorCode(),
            BaiduErrorCode.QUOTA_USE_UP_PAYMENT_REQUIRED.getErrorCode()
    };

    @Autowired
    private ActorSystem system;

    private PushNotificationDocService pushNotificationDocService;

    public PushNotificationResendActor(PushNotificationDocService pushNotificationDocService) {
        this.pushNotificationDocService = pushNotificationDocService;
    }

    @Override
    public void onReceive(Object message){
        List<PushNotificationDoc> resentPushs = pushNotificationDocService.findByRequest(resentPushErrorCodes);
        if (CollectionUtils.isNotEmpty(resentPushs)) {
            ActorRef updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                    .props(PushNotificationActor.ACTOR_NAME));
            for (PushNotificationDoc push : resentPushs) {
                updater.tell(push, ActorRef.noSender());
            }
        }
    }
}
