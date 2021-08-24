package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.AttributeEvent;
import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.reposistories.ProfileAttributeEventRepository;
import com.qooco.boost.data.oracle.services.ProfileAttributeEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProfileAttributeEventServiceImpl implements ProfileAttributeEventService {
    @Autowired
    private ProfileAttributeEventRepository profileAttributeEventRepository;

    public List<AttributeEvent> findByEventCode(int eventCode) {
        return profileAttributeEventRepository.findByEventCodeAndIsDeletedFalse(eventCode);
    }

    @Override
    public AttributeEvent findByAttributeEventWithLock(ProfileAttribute attribute, int eventCode) {
        return profileAttributeEventRepository.findByAttributeEventWithLock(attribute, eventCode);
    }
}
