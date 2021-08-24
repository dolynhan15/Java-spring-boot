package com.qooco.boost.business;

import com.qooco.boost.business.impl.abstracts.BusinessConversationBaseService;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.ConversationReq;
import org.springframework.security.core.Authentication;

public interface BusinessConversationService extends BusinessConversationBaseService<ConversationDoc> {
    BaseResp createConversation(Authentication authentication, ConversationReq req);

    BaseResp getById(Authentication authentication, String id);
}
