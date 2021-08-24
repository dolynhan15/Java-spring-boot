package com.qooco.boost.socket.services;

import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.models.dto.message.MessageDTO;

import java.util.List;

public interface SendMessageToClientService {
    void sendMessage(MessageBase doc, String locale);
    void sendMessage(MessageBase doc, boolean isEncrypted, String locale);
    void sendMessage(MessageBase doc, int messageAction, String locale);
    void sendMessage(List<MessageDTO> messages);
    void sendMessage(MessageDTO message);
}
