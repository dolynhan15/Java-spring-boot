package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.business.BusinessMessageTemplateService;
import com.qooco.boost.data.oracle.entities.MessageTemplate;
import com.qooco.boost.data.oracle.services.MessageTemplateService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.request.LastRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

@RunWith(PowerMockRunner.class)
public class BusinessMessageTemplateServiceTest {
    @InjectMocks
    private BusinessMessageTemplateService businessMessageTemplateService = new BusinessMessageTemplateServiceImpl();
    @Mock
    private MessageTemplateService messageTemplateService = Mockito.mock(MessageTemplateService.class);

    @Test
    public void getMessageTemplate_whenNullCompanyId_thenReturnInvalidParamExceptionResp() {
        MessageTemplate messageTemplate = initMessageTemplate();

        Mockito.when(messageTemplateService.findAll(new Date(0), 1)).thenReturn(ImmutableList.of(messageTemplate));
        Assert.assertEquals(ResponseStatus.SUCCESS.getCode(), businessMessageTemplateService.getMessageTemplate(LastRequest.builder().lastTime(0).limit(1).build()).getCode());
    }

    private MessageTemplate initMessageTemplate() {
        return MessageTemplate.builder().id(1L).languageCode("en").content("This is template message").isDeleted(true).build();
    }
}
