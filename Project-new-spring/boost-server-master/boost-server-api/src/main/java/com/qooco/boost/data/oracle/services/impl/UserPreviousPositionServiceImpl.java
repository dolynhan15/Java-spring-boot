package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.UserPreviousPosition;
import com.qooco.boost.data.oracle.reposistories.UserPreviousPositionRepository;
import com.qooco.boost.data.oracle.services.UserPreviousPositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserPreviousPositionServiceImpl implements UserPreviousPositionService {
    @Autowired
    private UserPreviousPositionRepository repository;

    @Override
    public UserPreviousPosition save(UserPreviousPosition userPreviousPosition) {
        return repository.save(userPreviousPosition);
    }


    @Override
    public List<UserPreviousPosition> save(List<UserPreviousPosition> userPreviousPositions) {
        return repository.saveAll(userPreviousPositions);
    }

    public UserPreviousPosition findById(Long id) {
        return repository.findByUserPreviousPositionId(id);
    }

    @Override
    public List<UserPreviousPosition> findByUserProfileId(Long userProfileId) {
        return repository.findByUserProfileId(userProfileId);
    }

    @Override
    public void deleteUserPreviousPositionById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<UserPreviousPosition> findByIds(long[] ids) {
        if (ids != null && ids.length > 0) {
            return repository.findByUserPreviousPositionIds(ids);
        }
        return new ArrayList<>();
    }

    @Override
    public Long findOwnerById(Long id) {
        return repository.findOwnerById(id);
    }

    @Override
    public List<UserPreviousPosition> findByUserProfileIds(List<Long> userProfileIds) {
        return repository.findByUserProfileIdsIds(userProfileIds);
    }
}
