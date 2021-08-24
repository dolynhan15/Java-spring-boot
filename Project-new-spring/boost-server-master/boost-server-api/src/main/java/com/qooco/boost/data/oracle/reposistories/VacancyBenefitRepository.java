package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.VacancyBenefit;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:54 PM
 */
@Repository
public interface VacancyBenefitRepository extends Boot2JpaRepository<VacancyBenefit, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VacancyBenefit vb WHERE vb.vacancy.id = :vacancyId")
    void deleteByVacancyId(@Param("vacancyId") Long vacancyId);

    List<VacancyBenefit> findByVacancyId(@Param("vacancyId") Long vacancyId);
}
