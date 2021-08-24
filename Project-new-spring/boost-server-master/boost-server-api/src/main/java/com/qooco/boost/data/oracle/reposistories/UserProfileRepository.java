package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserProfile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface UserProfileRepository extends Boot2JpaRepository<UserProfile, Long> {
    UserProfile findByUsernameIgnoreCase(String username);

    String FIT_USER_ID_NEED_SYNC = "SELECT upf.* FROM USER_PROFILE upf " +
            "WHERE " +
            "    upf.USER_PROFILE_ID IN ( " +
            "        SELECT DISTINCT up.USER_PROFILE_ID FROM USER_PROFILE up  " +
            "        JOIN STAFF s ON (s.USER_PROFILE_ID = up.USER_PROFILE_ID) " +
            "        UNION " +
            "        SELECT DISTINCT up.USER_PROFILE_ID FROM USER_PROFILE up " +
            "        JOIN COMPANY_JOIN_REQUEST cj ON (cj.USER_PROFILE_ID = up.USER_PROFILE_ID)) " +
            "AND upf.USER_PROFILE_ID NOT IN :exceptedIds";

    //boolean existsById(Long userProfileId);

    @Query(value = "SELECT u FROM UserProfile u WHERE u.userProfileId = :userProfileId AND u.isAdmin = true AND u.isDeleted = false")
    UserProfile findByUserProfileIdAndAdmin(@Param("userProfileId") Long userProfileId);

    @Query("SELECT u FROM UserProfile u WHERE u.userProfileId = :id AND u.isDeleted = false")
    UserProfile findValidById(@Param("id") Long id);

    @Query("SELECT u.userProfileId FROM UserProfile u WHERE u.userProfileId > :userProfileId AND u.isDeleted = false ORDER BY u.userProfileId ASC")
    List<Long> findNextUserProfileIds(@Param("userProfileId") Long userProfileId, Pageable pageable);

    @Query("SELECT u FROM UserProfile u WHERE u.userProfileId IN :ids AND u.isDeleted = false")
    List<UserProfile> findByIds(@Param("ids") List<Long> ids);

    @Query(value = FIT_USER_ID_NEED_SYNC, nativeQuery = true)
    List<UserProfile> findByExceptedUserIds(@Param("exceptedIds")List<Long> exceptedIds);

    @Query("SELECT MAX(u.userProfileId) FROM UserProfile u WHERE u.isDeleted = false ")
    long getMaxUserProfile();
}