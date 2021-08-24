package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.MessageTemplate;
import com.qooco.boost.data.oracle.reposistories.MessageTemplateRepository;
import com.qooco.boost.data.oracle.services.MessageTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MessageTemplateServiceImpl implements MessageTemplateService {
    @Autowired
    private MessageTemplateRepository repository;

    @Override
    public List<MessageTemplate> findAll(Date lastTime, int limit) {
        return repository.findAll(lastTime, limit);
    }
}
