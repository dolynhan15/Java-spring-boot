package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.VacancyLanguage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:28 PM
 */
@Repository
public interface VacancyLanguageRepository extends Boot2JpaRepository<VacancyLanguage, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VacancyLanguage vl WHERE vl.vacancy.id = :vacancyId")
    void deleteByVacancyId(@Param("vacancyId") Long vacancyId);

    List<VacancyLanguage> findByVacancyId(@Param("vacancyId") Long vacancyId);
}
