package com.qooco.boost.listener;

import com.qooco.boost.enumeration.ApplicationContextHolder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContextHolder
                .INSTANCE
                .setApplicationContext(event.getApplicationContext());
    }
}
