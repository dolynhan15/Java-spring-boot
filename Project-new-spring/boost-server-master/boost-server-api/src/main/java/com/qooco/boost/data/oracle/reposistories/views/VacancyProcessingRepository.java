package com.qooco.boost.data.oracle.reposistories.views;

import com.qooco.boost.data.oracle.entities.VacancyProcessing;
import com.qooco.boost.data.oracle.reposistories.Boot2JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface VacancyProcessingRepository extends Boot2JpaRepository<VacancyProcessing, Long> {

    @Query("SELECT COUNT(vp.id) FROM VacancyProcessing vp WHERE vp.vacancy.company.companyId = :companyId " +
            "AND vp.createdDate >= :startDate AND vp.createdDate <= :endDate ")
    int countByCompanyInDuration(@Param("companyId") long companyId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(nativeQuery = true,
            value = "with vancancy_seat_on_date AS " +
                    "(select (:startDate + level - 1) as THE_DATE, :staffId AS STAFF_ID from dual connect by level <= :days) " +
                    "select THE_DATE, " +
                    "count(CASE WHEN vp.PROCESS_TYPE = 1 AND vp.CREATED_DATE >= THE_DATE AND vp.CREATED_DATE < (THE_DATE + INTERVAL '1' DAY)  THEN 1 end) applied, " +
                    "count(CASE WHEN vp.PROCESS_TYPE = 2 AND vp.CREATED_DATE >= THE_DATE AND vp.CREATED_DATE < (THE_DATE + INTERVAL '1' DAY)  THEN 1 end) rejected " +
                    "FROM vancancy_seat_on_date cvs " +
                    "LEFT JOIN VACANCY_PROCESSING vp ON cvs.STAFF_ID = vp.STAFF_ID " +
                    "WHERE vp.CREATED_DATE < :endDate " +
                    "GROUP BY cvs.the_date ORDER BY cvs.THE_DATE ASC ")
    List<Object[]> countByStaffInDurationInEachDay(@Param("staffId") long staffId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("days") int days);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM VacancyProcessing vp WHERE vp.vacancy.id IN :vacancyIds")
    void deleteAllByVacancyId(@Param("vacancyIds")List<Long> vacancyIds);
}
