package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;

public interface ProfileAttributeService {
    ProfileAttribute findByIdWithLock(Long id);
}
