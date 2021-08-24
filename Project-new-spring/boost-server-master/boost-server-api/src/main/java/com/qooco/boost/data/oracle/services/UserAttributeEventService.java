package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserAttributeEvent;

public interface UserAttributeEventService {
    UserAttributeEvent findByUserAttributeAndEvent(Long userProfileId, Long attributeId, int eventCode);
    UserAttributeEvent findByUserAndEvent(Long userProfileId, int eventCode);
    int insert(UserAttributeEvent userAttributeEvent);
    int adjustCount(Long userProfileId, Long attributeId, int eventCode, int adjustment);
    UserAttributeEvent save(UserAttributeEvent value);
    int updateCountBeforeDate(UserAttributeEvent value);
}
