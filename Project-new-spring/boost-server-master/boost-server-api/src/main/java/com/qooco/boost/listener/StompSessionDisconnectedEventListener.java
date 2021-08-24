package com.qooco.boost.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class StompSessionDisconnectedEventListener extends StompListenerAbstract implements ApplicationListener<SessionDisconnectEvent> {
    @Override
    public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        saveOnlineStatus(StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage()));
    }
}
