package com.qooco.boost.message.service;

import java.util.List;

public interface SendSocketMessageService {
    void sendMessage(String destination, String message);

    void sendMessage(List<String> destinations, List<String> messages);
}
