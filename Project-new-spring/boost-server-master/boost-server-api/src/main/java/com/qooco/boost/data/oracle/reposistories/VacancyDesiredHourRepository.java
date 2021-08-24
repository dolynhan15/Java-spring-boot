package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.VacancyDesiredHour;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 8/6/2018 - 4:50 PM
 */
@Repository
public interface VacancyDesiredHourRepository extends Boot2JpaRepository<VacancyDesiredHour, Long> {
    @Transactional
    @Modifying
    @Query("DELETE FROM VacancyDesiredHour vdh WHERE vdh.vacancy.id = :vacancyId")
    void deleteByVacancyId(@Param("vacancyId") Long vacancyId);

    List<VacancyDesiredHour> findByVacancyId(@Param("vacancyId") Long vacancyId);
}
