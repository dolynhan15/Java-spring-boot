package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.reposistories.ProfileAttributeRepository;
import com.qooco.boost.data.oracle.services.ProfileAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileAttributeServiceImpl implements ProfileAttributeService {
    @Autowired
    private ProfileAttributeRepository profileAttributeRepository;

    public ProfileAttribute findByIdWithLock(Long id) {
        return profileAttributeRepository.findByIdWithLock(id);
    }
}
