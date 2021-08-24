package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.VacancyAssessmentLevel;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/10/2018 - 11:56 AM
 */
public interface VacancyAssessmentLevelRepository extends Boot2JpaRepository<VacancyAssessmentLevel, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VacancyAssessmentLevel val WHERE val.vacancy.id = :vacancyId")
    void deleteByVacancyId(@Param("vacancyId") Long vacancyId);

    List<VacancyAssessmentLevel> findByVacancyId(@Param("vacancyId") Long vacancyId);
}
