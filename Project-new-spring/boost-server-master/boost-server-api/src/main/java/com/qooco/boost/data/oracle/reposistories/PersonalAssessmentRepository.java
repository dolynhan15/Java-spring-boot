package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalAssessmentRepository extends Boot2JpaRepository<PersonalAssessment, Long> {

    @Query("SELECT pa FROM PersonalAssessment pa WHERE pa.isDeleted = false")
    List<PersonalAssessment> getActiveAPersonalAssessment();

    @Query("SELECT pa FROM PersonalAssessment pa WHERE pa.id = :id AND pa.isDeleted = false")
    PersonalAssessment findValidById(@Param("id") Long id);
}
