package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.MessageDocActorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/7/2018 - 10:47 AM
*/
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateReceiveInAppMessageActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(UpdateReceiveInAppMessageActor.class);
    public static final String ACTOR_NAME = "updateReceiveInAppMessageActor";

    private MessageDocActorService messageDocActorService;
    private PushNotificationService pushNotificationService;

    public UpdateReceiveInAppMessageActor(MessageDocActorService messageDocActorService,
                                          PushNotificationService pushNotificationService) {
        this.messageDocActorService = messageDocActorService;
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof MessageBase) {
            MessageBase messageDoc = (MessageBase) message;
            messageDocActorService.updateReceiveInApp(messageDoc);
            pushNotificationService.notifyNewMessage(messageDoc, true);
        }
    }
}