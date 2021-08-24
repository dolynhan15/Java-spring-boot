package com.qooco.boost.message.impl;

import com.qooco.boost.message.service.SendSocketMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendSocketMessageServiceImpl implements SendSocketMessageService {
    protected Logger logger = LogManager.getLogger(SendSocketMessageServiceImpl.class);
    @Autowired
    private SimpMessagingTemplate template;

    @Override
    public void sendMessage(String destination, String message) {
        this.template.convertAndSend(destination, message);
        logger.info(String.format("Channel %s", destination));
        logger.info(String.format("Data %s", message));
    }

    @Override
    public void sendMessage(List<String> destinations, List<String> messages) {
        if (CollectionUtils.isNotEmpty(destinations) && CollectionUtils.isNotEmpty(messages)){
            destinations.forEach(destination -> messages.forEach(message -> sendMessage(destination, message)));
        }
    }
}
