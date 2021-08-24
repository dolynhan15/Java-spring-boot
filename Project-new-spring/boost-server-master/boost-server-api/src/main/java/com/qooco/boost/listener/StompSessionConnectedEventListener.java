package com.qooco.boost.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class StompSessionConnectedEventListener extends StompListenerAbstract implements ApplicationListener<SessionConnectedEvent> {
    @Override
    public void onApplicationEvent(SessionConnectedEvent sessionConnectedEvent) {
        saveOnlineStatus(StompHeaderAccessor.wrap(sessionConnectedEvent.getMessage()));
    }
}
