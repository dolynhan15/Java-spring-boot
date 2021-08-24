package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalAssessmentQuestionRepository extends Boot2JpaRepository<PersonalAssessmentQuestion, Long> {
    @Query("SELECT q FROM PersonalAssessmentQuestion q WHERE q.isDeleted = false AND q.personalAssessmentQuality.personalAssessment.id = :personalAssessmentId ORDER BY q.id ASC")
    List<PersonalAssessmentQuestion> getByPersonalAssessmentId(@Param("personalAssessmentId") long personalAssessmentId);
}
