package com.qooco.boost.message.listener;

import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Objects;

public abstract class StompListenerAbstract {
    private Logger logger = LogManager.getLogger(StompListenerAbstract.class);

    String saveOnlineStatus(StompHeaderAccessor headerAccessor) {
        if (Objects.nonNull(headerAccessor)) {
            AuthenticatedUser user = (AuthenticatedUser) ((UsernamePasswordAuthenticationToken) headerAccessor.getUser()).getPrincipal();
            String sessionId = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.SESSION_ID_HEADER);

            String simpSubscriptionId = null;
            String destination = null;

            switch (headerAccessor.getMessageType()) {
                case CONNECT:
                case CONNECT_ACK:
                    //TODO: Call API to save
                    //socketConnectionDocService.updateConnected(user.getToken(), user.getUsername(), user.getId(), user.getAppId(), sessionId);
                    break;
                case MESSAGE:
                    break;
                case SUBSCRIBE:
                    destination = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.DESTINATION_HEADER);
                    simpSubscriptionId = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER);

                    //TODO: Call API to save
                    //socketConnectionDocService.updateSubscribe(user.getToken(), simpSubscriptionId, destination);
                    break;
                case UNSUBSCRIBE:
                    simpSubscriptionId = (String) headerAccessor.getHeader(SimpMessageHeaderAccessor.SUBSCRIPTION_ID_HEADER);
                    //TODO: Call API to save
                    //socketConnectionDocService.updateUnSubscribe(user.getToken(), simpSubscriptionId);
                    break;
                case HEARTBEAT:
                    break;
                case DISCONNECT:
                    //TODO: Call API to save
                    //socketConnectionDocService.updateDisconnected(user.getToken(), sessionId);
                    break;
                case DISCONNECT_ACK:
                    break;
                case OTHER:
                    break;
            }

            if (Objects.nonNull(user)) {
                logger.info(headerAccessor.toString());
                logger.info(String.format("%s : %s : %s", headerAccessor.getMessageType().toString(), " : ", user.getUsername(), " : ", user.getToken()));
            }
        }
        return null;
    }
}
