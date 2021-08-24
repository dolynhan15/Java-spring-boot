package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserLanguage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserLanguageRepository extends Boot2JpaRepository<UserLanguage, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserLanguage u WHERE u.userProfile.userProfileId = :userProfileId")
    void deleteByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("SELECT u FROM UserLanguage u WHERE u.userProfile.userProfileId = :userProfileId")
    List<UserLanguage> findByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("SELECT u FROM UserLanguage u WHERE u.userProfile.userProfileId IN :userProfileIds")
    List<UserLanguage> findByUserProfileIds(@Param("userProfileIds") List<Long> userProfileIds);
}
