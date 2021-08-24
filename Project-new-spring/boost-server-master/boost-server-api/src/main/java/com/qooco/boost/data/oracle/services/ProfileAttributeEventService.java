package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.AttributeEvent;
import com.qooco.boost.data.oracle.entities.ProfileAttribute;

import java.util.List;

public interface ProfileAttributeEventService {
    List<AttributeEvent> findByEventCode(int eventCode);

    AttributeEvent findByAttributeEventWithLock(ProfileAttribute attribute, int eventCode);
}
