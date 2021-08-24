package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.LastRequest;


public interface BusinessMessageTemplateService {
    BaseResp getMessageTemplate(LastRequest lastRequest);
}
