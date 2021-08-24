package com.qooco.boost.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class StompUnSubscribeEventListener extends StompListenerAbstract implements ApplicationListener<SessionUnsubscribeEvent> {
    @Override
    public void onApplicationEvent(SessionUnsubscribeEvent sessionUnSubscribeEvent) {
        saveOnlineStatus(StompHeaderAccessor.wrap(sessionUnSubscribeEvent.getMessage()));
    }
}
