package com.qooco.boost.message.business.impl;

import com.qooco.boost.core.enumeration.ResponseStatus;
import com.qooco.boost.core.model.BaseResp;
import com.qooco.boost.message.business.BusinessSendMessageService;
import com.qooco.boost.message.service.SendSocketMessageService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class BusinessSendMessageServiceImpl implements BusinessSendMessageService {
    @Autowired
    private SendSocketMessageService sendSocketMessageService;

    @Override
    public BaseResp sendMessage(Map<String, List<String>> data) {
        if (MapUtils.isNotEmpty(data)) {
            data.forEach((destination, messages) -> messages.forEach(message -> sendSocketMessageService.sendMessage(destination, message)));
            return new BaseResp(ResponseStatus.SUCCESS);
        }
        return new BaseResp(ResponseStatus.BAD_REQUEST);
    }

    @Override
    public BaseResp sendMessage(List<String> destinations, List<String> messages) {
        if (CollectionUtils.isNotEmpty(destinations) && CollectionUtils.isNotEmpty(messages)) {
            sendSocketMessageService.sendMessage(destinations, messages);
            return new BaseResp(ResponseStatus.SUCCESS);
        }
        return new BaseResp(ResponseStatus.BAD_REQUEST);
    }
}
