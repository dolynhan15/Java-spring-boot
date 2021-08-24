package com.qooco.boost.business;

import com.qooco.boost.business.impl.abstracts.BusinessChatBaseService;
import com.qooco.boost.models.request.message.HistoryMessageResp;
import org.springframework.security.core.Authentication;

public interface BusinessChatSupportService extends BusinessChatBaseService {
    HistoryMessageResp getAllMessagesInPersonalConversation(Authentication authentication, String conversationId, Long timestamp, int size);
}
