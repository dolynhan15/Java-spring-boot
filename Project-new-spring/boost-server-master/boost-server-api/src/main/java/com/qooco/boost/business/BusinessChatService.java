package com.qooco.boost.business;

import com.qooco.boost.business.impl.abstracts.BusinessChatBaseService;
import com.qooco.boost.models.request.message.HistoryMessageResp;
import org.springframework.security.core.Authentication;

public interface BusinessChatService extends BusinessChatBaseService {
    HistoryMessageResp getAllMessagesInPersonalConversation(Authentication authentication, String conversationId, Long eventId, Long timestamp, int size);
}
