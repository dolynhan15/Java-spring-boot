package com.qooco.boost.business;

import com.qooco.boost.business.impl.abstracts.BusinessConversationBaseService;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface BusinessSupportChannelService extends BusinessConversationBaseService<SupportConversationDoc> {
    BaseResp getByUser(Authentication auth);
    BaseResp createByUser(Authentication auth);
    BaseResp restoreSupportConversation(Authentication auth, String conversationId);
    BaseResp archiveSupportConversation(Authentication auth, String conversationId);
    BaseResp deleteSupportConversation(Authentication auth, String conversationId);
    BaseResp getConversations(Authentication auth, Integer appId, Integer status, PageRequest request, String... sorts);
}
