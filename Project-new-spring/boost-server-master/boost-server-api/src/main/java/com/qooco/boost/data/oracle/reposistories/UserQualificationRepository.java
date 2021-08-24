package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.UserQualification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface UserQualificationRepository extends Boot2JpaRepository<UserQualification, Long> {
    @Query("SELECT aq FROM UserQualification aq WHERE aq.userProfileId = :userProfileId AND aq.levelValue > 0 AND aq.scaleId like :scaleId AND aq.isDeleted = false ORDER BY aq.submissionTime DESC")
    List<UserQualification> findByUserProfileIdAndScaleId(@Param("userProfileId") Long userProfileId, @Param("scaleId") String scaleId);

    @Query("SELECT aq FROM UserQualification aq WHERE aq.userProfileId = :userProfileId AND aq.levelValue > 0 AND aq.isDeleted = false ORDER BY aq.submissionTime DESC")
    List<UserQualification> findByUserProfileId(@Param("userProfileId") Long userProfileId);

    @Query("SELECT aq FROM UserQualification aq WHERE aq.userProfileId = :userProfileId AND aq.levelValue > 0 AND aq.scaleId like :scaleId AND aq.isDeleted = false ORDER BY aq.submissionTime ASC")
    List<UserQualification> findByUserProfileIdAndScaleIdForHomePage(@Param("userProfileId") Long userProfileId, @Param("scaleId") String scaleId);

    @Query("SELECT aq FROM UserQualification aq WHERE aq.userProfileId = :userProfileId AND aq.levelValue > 0 AND aq.isDeleted = false ORDER BY aq.submissionTime ASC")
    List<UserQualification> findByUserProfileIdForHomePage(@Param("userProfileId") Long userProfileId);

    @Query("SELECT COUNT(aq.id) FROM UserQualification aq WHERE aq.userProfileId = :userProfileId AND aq.levelValue > 0 AND aq.submissionTime > :submissionValidDate AND aq.isDeleted = false")
    int countValidQualification(@Param("userProfileId") Long userProfileId, @Param("submissionValidDate") Date submissionValidDate);

    @Query("SELECT aq FROM UserQualification aq WHERE aq.userProfileId = :userProfileId AND aq.levelValue > 0 AND aq.submissionTime > :submissionValidDate AND aq.isDeleted = false")
    List<UserQualification> findValidQualification(@Param("userProfileId") Long userProfileId, @Param("submissionValidDate") Date submissionValidDate);
}
