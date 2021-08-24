package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.reposistories.UserProfileRepository;
import com.qooco.boost.data.oracle.services.UserProfileService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileServiceImpl implements UserProfileService {

    @Autowired
    private UserProfileRepository repository;

    @Override
    public UserProfile save(UserProfile userProfile) {
        return repository.save(userProfile);
    }

    @Override
    public List<UserProfile> save(List<UserProfile> userProfiles) {
        return repository.saveAll(userProfiles);
    }

    @Override
    public UserProfile findByUsername(String username) {
        return repository.findByUsernameIgnoreCase(username);
    }

    @Override
    public UserProfile findById(Long id) {
        return repository.findValidById(id);
    }

    @Override
    public boolean isExist(Long userProfileId) {
        return repository.existsById(userProfileId);
    }

    @Override
    public UserProfile checkUserProfileIsRootAdmin(Long userProfileId) {
        return repository.findByUserProfileIdAndAdmin(userProfileId);
    }

    @Override
    public List<Long> findNextUserProfileIds(Long userProfileId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.Direction.ASC, "userProfileId");
        return repository.findNextUserProfileIds(userProfileId, pageable);
    }

    @Override
    public List<UserProfile> findByIds(List<Long> userProfileIds) {
        return repository.findByIds(userProfileIds);
    }

    @Override
    public List<UserProfile> findByExceptedUserIds(List<Long> exceptedIds) {
        if (CollectionUtils.isNotEmpty(exceptedIds)) {
            return repository.findByExceptedUserIds(exceptedIds);
        }
        return repository.findAll();
    }

    @Override
    public long getMaxUserProfileId() {
        return repository.getMaxUserProfile();
    }
}
