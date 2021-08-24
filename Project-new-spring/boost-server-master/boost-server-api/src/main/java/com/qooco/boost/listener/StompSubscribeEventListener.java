package com.qooco.boost.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class StompSubscribeEventListener extends StompListenerAbstract implements ApplicationListener<SessionSubscribeEvent> {
    @Override
    public void onApplicationEvent(SessionSubscribeEvent sessionSubscribeEvent) {
        saveOnlineStatus(StompHeaderAccessor.wrap(sessionSubscribeEvent.getMessage()));
    }
}
