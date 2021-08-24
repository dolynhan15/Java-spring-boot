package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.AttributeEvent;
import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Optional.ofNullable;
import static javax.persistence.LockModeType.PESSIMISTIC_READ;

@Repository
public interface ProfileAttributeEventRepository extends Boot2JpaRepository<AttributeEvent, Long> {
    List<AttributeEvent> findByEventCodeAndIsDeletedFalse(int eventCode);

    @Lock(PESSIMISTIC_READ)
    AttributeEvent findByAttributeAndEventCode__(ProfileAttribute attribute, int eventCode);

    @Lock(PESSIMISTIC_READ)
    AttributeEvent findFirstByEventCodeAndIsDeletedFalseOrderByIdAsc(int eventCode);

    default AttributeEvent findByAttributeEventWithLock(ProfileAttribute attribute, int eventCode) {
        return ofNullable(attribute).map(it -> findByAttributeAndEventCode__(it, eventCode)).orElseGet(() -> findFirstByEventCodeAndIsDeletedFalseOrderByIdAsc(eventCode));
    }
}
