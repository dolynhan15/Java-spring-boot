package com.qooco.boost.data.oracle.services.impl;


import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.entities.UserAttribute;
import com.qooco.boost.data.oracle.reposistories.ProfileAttributeRepository;
import com.qooco.boost.data.oracle.reposistories.UserAttributeRepository;
import com.qooco.boost.data.oracle.services.ProfileAttributeService;
import com.qooco.boost.data.oracle.services.UserAttributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserAttributeServiceImpl implements UserAttributeService {
    @Autowired
    private ProfileAttributeService profileAttributeService;

    @Autowired
    private UserAttributeRepository userAttributeRepository;

    @Autowired
    private ProfileAttributeRepository profileAttributeRepository;

    @Override
    public UserAttribute findByUserAttribute(Long userProfileId, ProfileAttribute attribute) {
        return userAttributeRepository.findByUserAttribute(userProfileId, attribute);
    }

    @Override
    public UserAttribute findByUserAttribute(Long userProfileId, Long attributeId) {
        return userAttributeRepository.findByUserAttribute(userProfileId, profileAttributeRepository.findById(attributeId).orElse(null));
    }

    @Transactional
    public int insert(UserAttribute ua) {
        ProfileAttribute attribute = profileAttributeService.findByIdWithLock(ua.getAttribute().getId());
        return userAttributeRepository.insert(ua);
    }

    @Transactional
    public int adjustScore(Long userProfileId, ProfileAttribute attribute, int adjustment) {
        return userAttributeRepository.adjustScore(userProfileId, attribute.getId(), adjustment);
    }

    @Override
    @Transactional
    public int updateNewLevel(List<Long> ids, boolean isNewLevel) {
        return userAttributeRepository.updateNewLevel(ids, isNewLevel);
    }

    @Override
    public List<UserAttribute> findNewLevelByUserProfile(Long userProfile) {
        return userAttributeRepository.findByNewLevel(userProfile, true);
    }

    @Override
    public Page<UserAttribute> findByUserProfile(Long userProfile, int page, int size) {
        return userAttributeRepository.findByUserProfile(userProfile, PageRequest.of(page, size));
    }

    @Override
    public Page<UserAttribute> findActiveByUserProfile(Long userProfileId, int page, int size) {
        return userAttributeRepository.findActiveByUserProfile(userProfileId, PageRequest.of(page, size));
    }

    @Override
    public long countTotalAttributeAvailable() {
        return profileAttributeRepository.countByIsDeletedFalse();
    }

    @Override
    public long countTotalAttributeAvailable(Long userProfileId) {
        return userAttributeRepository.countByUserProfile(userProfileId);
    }
}
