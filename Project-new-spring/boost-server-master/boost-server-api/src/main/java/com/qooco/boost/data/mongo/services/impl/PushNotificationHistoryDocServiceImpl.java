package com.qooco.boost.data.mongo.services.impl;

import com.qooco.boost.data.mongo.entities.PushNotificationHistoryDoc;
import com.qooco.boost.data.mongo.repositories.PushNotificationHistoryDocRepository;
import com.qooco.boost.data.mongo.services.PushNotificationHistoryDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationHistoryDocServiceImpl implements PushNotificationHistoryDocService {
    @Autowired
    private PushNotificationHistoryDocRepository repository;
    @Override
    public PushNotificationHistoryDoc save(PushNotificationHistoryDoc doc) {
        return repository.save(doc);
    }
}
