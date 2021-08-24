package com.qooco.boost.data.oracle.reposistories;/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/4/2018 - 2:32 PM
 */

import com.qooco.boost.data.oracle.entities.UserPreviousPosition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserPreviousPositionRepository extends Boot2JpaRepository<UserPreviousPosition, Long> {

    @Query("SELECT p FROM UserPreviousPosition p WHERE p.id = :id")
    UserPreviousPosition findByUserPreviousPositionId(@Param("id") long id);

    @Query("SELECT p.createdBy FROM UserPreviousPosition p WHERE p.id = :id")
    Long findOwnerById(@Param("id") long id);

    @Query("SELECT p FROM UserPreviousPosition p WHERE p.createdBy = :userProfileId and p.isDeleted = false ")
    List<UserPreviousPosition> findByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("SELECT p FROM UserPreviousPosition p WHERE p.id IN :ids AND p.isDeleted = false")
    List<UserPreviousPosition> findByUserPreviousPositionIds(@Param("ids") long[] ids);

    @Query("SELECT p FROM UserPreviousPosition p WHERE p.createdBy IN :userProfileIds")
    List<UserPreviousPosition> findByUserProfileIdsIds(@Param("userProfileIds") List<Long> userProfileIds);
}
