package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.UserPreviousPosition;

import java.util.List;

public interface UserPreviousPositionService {
    UserPreviousPosition save(UserPreviousPosition userPreviousPosition);

    List<UserPreviousPosition> save(List<UserPreviousPosition> userPreviousPositions);

    UserPreviousPosition findById(Long id);

    List<UserPreviousPosition> findByUserProfileId(Long userProfileId);

    void deleteUserPreviousPositionById(Long id);

    List<UserPreviousPosition> findByIds(long[] ids);

    Long findOwnerById(Long id);

    List<UserPreviousPosition> findByUserProfileIds(List<Long> userProfileIds);

}
