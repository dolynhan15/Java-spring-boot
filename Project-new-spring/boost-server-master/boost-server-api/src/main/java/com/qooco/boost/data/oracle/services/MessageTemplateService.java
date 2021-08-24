package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.MessageTemplate;

import java.util.Date;
import java.util.List;

public interface MessageTemplateService {
    List<MessageTemplate> findAll(Date lastTime, int limit);
}
