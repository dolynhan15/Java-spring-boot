package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.business.BusinessMessageTemplateService;
import com.qooco.boost.data.oracle.entities.MessageTemplate;
import com.qooco.boost.data.oracle.services.MessageTemplateService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.MessageTemplateDTO;
import com.qooco.boost.models.request.LastRequest;
import com.qooco.boost.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BusinessMessageTemplateServiceImpl implements BusinessMessageTemplateService {

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Override
    public BaseResp getMessageTemplate(LastRequest lastRequest) {
        Date lastTimeRequest = DateUtils.toUtcForOracle(new Date(lastRequest.getLastTime()));
        List<MessageTemplate> messageTemplates = messageTemplateService.findAll(lastTimeRequest, lastRequest.getSize());
        List<MessageTemplateDTO> contents = messageTemplates.stream().map(MessageTemplateDTO::new).collect(ImmutableList.toImmutableList());
        return new BaseResp<>(contents);
    }
}
