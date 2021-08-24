package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.entities.UserAttributeEvent;
import com.qooco.boost.data.oracle.reposistories.UserAttributeEventRepository;
import com.qooco.boost.data.oracle.services.ProfileAttributeEventService;
import com.qooco.boost.data.oracle.services.UserAttributeEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAttributeEventServiceImpl implements UserAttributeEventService {
    @Autowired
    private UserAttributeEventRepository userAttributeEventRepository;

    @Autowired
    private ProfileAttributeEventService profileAttributeEventService;

    public UserAttributeEvent findByUserAttributeAndEvent(Long userProfileId, ProfileAttribute attribute, int eventCode) {
        return userAttributeEventRepository.findByUserAttributeAndEvent(userProfileId, attribute, eventCode);
    }

    public UserAttributeEvent findByUserAttributeAndEvent(Long userProfileId, Long attributeId, int eventCode) {
        return userAttributeEventRepository.findByUserAttributeAndEvent(userProfileId, attributeId, eventCode);
    }

    public UserAttributeEvent findByUserAndEvent(Long userProfileId, int eventCode) {
        return userAttributeEventRepository.findByUserAndEvent(userProfileId, eventCode);
    }

    @Transactional
    public int adjustCount(Long userProfileId, Long attributeId, int eventCode, int adjustment) {
        return userAttributeEventRepository.adjustCountByUserAttributeAndEvent(userProfileId, attributeId, eventCode, adjustment);
    }

    @Override
    public UserAttributeEvent save(UserAttributeEvent value) {
        return userAttributeEventRepository.save(value);
    }

    @Transactional
    public int updateCountBeforeDate(UserAttributeEvent value) {
        return userAttributeEventRepository.updateCountBeforeDate(value);
    }

    @Transactional
    public int insert(UserAttributeEvent uae) {
        profileAttributeEventService.findByAttributeEventWithLock(uae.getAttribute(), uae.getEventCode());
        return userAttributeEventRepository.insert(uae);
    }
}
