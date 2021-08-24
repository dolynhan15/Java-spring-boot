package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.entities.UserPersonality;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserPersonalityRepository extends Boot2JpaRepository<UserPersonality, Long> {

    @Query("SELECT distinct up.personalAssessmentQuestion.personalAssessmentQuality.personalAssessment.id FROM UserPersonality up WHERE up.createdBy = :userProfileId AND up.isDeleted = false")
    List<Long> getPersonalAssessmentIdByUserProfileId(@Param("userProfileId") long userProfileId);

    @Query("SELECT up FROM UserPersonality up WHERE up.createdBy = :userProfileId AND up.personalAssessmentId = :personalAssessmentId AND up.isDeleted = false")
    List<UserPersonality> getByUserProfileIdAndPersonalAssessmentId(@Param("userProfileId") long userProfileId, @Param("personalAssessmentId") Long personalAssessmentId);

    @Query("SELECT up FROM UserPersonality up WHERE up.createdBy = :userProfileId AND up.isDeleted = false")
    List<UserPersonality> findByUserProfileId(@Param("userProfileId") long userProfileId);

    @Query("SELECT count(distinct up.personalAssessmentQuestion.personalAssessmentQuality.personalAssessment.id) FROM UserPersonality up WHERE up.createdBy = :userProfileId AND up.personalAssessmentQuestion.personalAssessmentQuality.personalAssessment.id = :personalAssessmentId AND up.isDeleted = false")
    int countPersonalAssessmentByUserProfileId(@Param("userProfileId")long userProfileId, @Param("personalAssessmentId") long personalAssessmentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM UserPersonality up WHERE up.createdBy = :userProfileId AND up.personalAssessmentId = :personalAssessmentId")
    void removeOldResult(@Param("userProfileId") long userProfileId, @Param("personalAssessmentId") long personalAssessmentId);

//    @Query("SELECT pa FROM UserPersonality up JOIN up.personalAssessmentQuestion q JOIN up.personalAssessmentQuestion.personalAssessmentQuality pq JOIN up.personalAssessmentQuestion.personalAssessmentQuality.personalAssessment pa WHERE up.createdBy = :userProfileId AND up.isDeleted = false GROUP BY pa")
    @Query("SELECT distinct up.personalAssessmentQuestion.personalAssessmentQuality.personalAssessment FROM UserPersonality up WHERE up.createdBy = :userProfileId AND up.isDeleted = false ")
    List<PersonalAssessment> findPersonalAssessmentByUserProfile(@Param("userProfileId") long userProfileId);
}
