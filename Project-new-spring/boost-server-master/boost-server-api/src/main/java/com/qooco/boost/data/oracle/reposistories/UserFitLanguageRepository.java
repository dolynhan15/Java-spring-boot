package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserFitLanguage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserFitLanguageRepository extends Boot2JpaRepository<UserFitLanguage, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM UserFitLanguage u WHERE u.userFit.userProfileId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserFitLanguage u WHERE u.userFit.userProfileId = :userId")
    List<UserFitLanguage> findByUserId(@Param("userId") Long userId);

    @Query("SELECT u FROM UserFitLanguage u WHERE u.userFit.userProfileId IN :userIds")
    List<UserFitLanguage> findByUserIds(@Param("userIds") List<Long> userIds);
}
