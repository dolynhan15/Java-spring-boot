package com.qooco.boost.message.business;

import com.qooco.boost.core.model.BaseResp;

import java.util.List;
import java.util.Map;

public interface BusinessSendMessageService {
    BaseResp sendMessage(Map<String, List<String>> messages);

    BaseResp sendMessage(List<String> destinations, List<String> messages);
}
