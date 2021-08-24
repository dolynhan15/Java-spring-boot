package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.ProfileAttribute;
import com.qooco.boost.data.oracle.entities.UserAttribute;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserAttributeService {
    UserAttribute findByUserAttribute(Long userProfileId, ProfileAttribute attribute);
    UserAttribute findByUserAttribute(Long userProfileId, Long attributeId);
    int insert(UserAttribute userAttribute);
    int adjustScore(Long userProfileId, ProfileAttribute attribute, int adjustment);
    int updateNewLevel(List<Long> ids, boolean isNewLevel);
    List<UserAttribute> findNewLevelByUserProfile(Long userProfile);
    Page<UserAttribute> findByUserProfile(Long userProfile, int page, int size);
    Page<UserAttribute> findActiveByUserProfile(Long userProfileId, int page, int size);
    long countTotalAttributeAvailable();
    long countTotalAttributeAvailable(Long userProfileId);
}
