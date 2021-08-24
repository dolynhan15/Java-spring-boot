package com.qooco.boost.threads.notifications;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.socket.services.SendNotifyToClientService;
import com.qooco.boost.threads.notifications.dto.NotifyData;
import com.qooco.boost.threads.notifications.push.NotifyDataService;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SocketNotificationActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SocketNotificationActor.class);
    public static final String ACTOR_NAME = "socketNotificationActor";

    private SendNotifyToClientService sendNotifyToClientService;
    private NotifyDataService notifyDataService;

    public SocketNotificationActor(
            SendNotifyToClientService sendNotifyToClientService,
            NotifyDataService notifyDataService
    ) {
        this.sendNotifyToClientService = sendNotifyToClientService;
        this.notifyDataService = notifyDataService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof NotifyPushTask) {
            NotifyPushTask task = (NotifyPushTask) message;
            List<NotifyData> notifiesData = notifyDataService.createNotifiesData(task, null);
            if (CollectionUtils.isNotEmpty(notifiesData)) {
                notifiesData.forEach(nd -> sendNotifyToClientService.sendNotifyMessage(nd));
            }
        } else if (message instanceof AuthenticatedUser) {
            AuthenticatedUser authenticatedUser = ((AuthenticatedUser) message);
            List<NotifyData> notifiesData = notifyDataService.createSentMessagesOfUser(authenticatedUser);
            notifiesData.forEach(nd -> sendNotifyToClientService.sendNotifyMessage(nd));
        }
    }
}
