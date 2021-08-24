package com.qooco.boost.listener;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.constants.SocketConstants;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.mongo.services.SocketConnectionDocService;
import com.qooco.boost.threads.notifications.SocketNotificationActor;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Objects;

public abstract class StompListenerAbstract {
    private Logger logger = LogManager.getLogger(StompListenerAbstract.class);
    @Autowired
    private SocketConnectionDocService socketConnectionDocService;
    @Autowired
    private ActorSystem system;

    String saveOnlineStatus(StompHeaderAccessor headerAccessor) {
        if (Objects.nonNull(headerAccessor) && Objects.nonNull(headerAccessor.getUser())) {
            AuthenticatedUser user = (AuthenticatedUser) ((UsernamePasswordAuthenticationToken) headerAccessor.getUser()).getPrincipal();
            String sessionId = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER);

            String simpSubscriptionId;
            String destination;

            switch (headerAccessor.getMessageType()) {
                case CONNECT:
                case CONNECT_ACK:
                    socketConnectionDocService.updateConnected(user.getToken(), user.getUsername(), user.getId(), user.getAppId(), sessionId);
                    break;
                case MESSAGE:
                    break;
                case SUBSCRIBE:
                    destination = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.DESTINATION_HEADER);
                    simpSubscriptionId = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER);
                    socketConnectionDocService.updateSubscribe(user.getToken(), simpSubscriptionId, destination);
                    if(destination.contains(SocketConstants.PRIVATE_NOTIFY_METHOD)){
                        sendMessageToUser(user);
                    }
                    break;
                case UNSUBSCRIBE:
                    simpSubscriptionId = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER);
                    socketConnectionDocService.updateUnSubscribe(user.getToken(), simpSubscriptionId);
                    break;
                case HEARTBEAT:
                    break;
                case DISCONNECT:
                    socketConnectionDocService.updateDisconnected(user.getToken(), sessionId);
                    break;
                case DISCONNECT_ACK:
                    break;
                case OTHER:
                    break;
            }

            if (Objects.nonNull(user)) {
                logger.info(headerAccessor.toString());
                logger.info(StringUtil.append(headerAccessor.getMessageType().toString(), " : ", user.getUsername(), " : ", user.getToken()));
            }
        }
        return null;
    }

    private void sendMessageToUser(AuthenticatedUser user) {
        ActorRef updaterSocket = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(SocketNotificationActor.ACTOR_NAME));
        updaterSocket.tell(user, ActorRef.noSender());
    }
}
