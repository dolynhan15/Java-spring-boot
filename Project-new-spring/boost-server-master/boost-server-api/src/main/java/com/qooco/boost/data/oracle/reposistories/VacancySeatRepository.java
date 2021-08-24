package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.VacancySeat;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface VacancySeatRepository extends Boot2JpaRepository<VacancySeat, Long> {
    @Query(nativeQuery = true,
            value = "with vancancy_seat_on_date AS " +
                    "(select (:startDate + level - 1) as THE_DATE, :companyId AS COMPANY_ID from dual connect by level <= :days) "+
                    "select THE_DATE, " +
                    "count(CASE WHEN vs.STATUS = 2 AND vs.CLOSED_DATE IS NOT NULL AND vs.CLOSED_DATE >= THE_DATE AND vs.CLOSED_DATE < (THE_DATE + INTERVAL '1' DAY)  THEN 1 end) closedSeats, " +
                    "count(CASE WHEN " +
                    "   ((vs.STATUS = 1 AND vs.CREATED_DATE < (THE_DATE + INTERVAL '1' DAY)) " +
                    "   OR (vs.STATUS = 3 AND vs.SUSPEND_FROM_DATE IS NOT NULL AND vs.SUSPEND_FROM_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE) " +
                    "   OR (vs.STATUS = 4 AND vs.SUSPEND_FROM_DATE IS NOT NULL AND vs.SUSPEND_FROM_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE) " +
                    "   OR (vs.STATUS = 5 AND vs.DELETED_DATE IS NOT NULL AND vs.DELETED_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE) " +
                    "   OR (vs.STATUS = 2 AND vs.CLOSED_DATE IS NOT NULL AND vs.CLOSED_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE))  THEN 1 end) openSeats " +
                    "FROM vancancy_seat_on_date cvs " +
                    "LEFT JOIN COMPANY c ON cvs.COMPANY_ID = c.COMPANY_ID " +
                    "LEFT JOIN VACANCY v ON c.COMPANY_ID = v.COMPANY_ID " +
                    "LEFT JOIN VACANCY_SEAT vs ON vs.VACANCY_ID = v.VACANCY_ID " +
                    "WHERE vs.CREATED_DATE < :endDate " +
                    "GROUP BY cvs.the_date ORDER BY cvs.THE_DATE ASC ")
    List<Object[]> countVacancySeatInRangeByCompany(@Param("companyId") long companyId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("days") int days);

    @Query(nativeQuery = true,
            value = "with vancancy_seat_on_date AS " +
                    "(select (:startDate + level - 1) as THE_DATE, :staffId AS STAFF_ID from dual connect by level <= :days) "+
                    "select THE_DATE, " +
                    "count(CASE WHEN vs.STATUS = 2 AND vs.CLOSED_DATE IS NOT NULL AND vs.CLOSED_DATE >= THE_DATE AND vs.CLOSED_DATE < (THE_DATE + INTERVAL '1' DAY)  THEN 1 end) closedSeats " +
                    "FROM vancancy_seat_on_date cvs " +
                    "LEFT JOIN VACANCY_SEAT vs ON cvs.STAFF_ID = vs.CLOSED_STAFF_ID " +
                    "WHERE vs.CREATED_DATE < :endDate  " +
                    "GROUP BY cvs.the_date ORDER BY cvs.THE_DATE ASC ")
    List<Object[]> countClosedSeatInRangeByStaff(@Param("staffId") long staffId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("days") int days);

    @Query(nativeQuery = true,
            value = "with vancancy_seat_on_date AS " +
                    "(select (:startDate + level - 1) as THE_DATE, :staffId AS STAFF_ID from dual connect by level <= :days) "+
                    "select THE_DATE, " +
                    "count(CASE WHEN " +
                    "   ((vs.STATUS = 1 AND vs.CREATED_DATE < (THE_DATE + INTERVAL '1' DAY)) " +
                    "   OR (vs.STATUS = 3 AND vs.SUSPEND_FROM_DATE IS NOT NULL AND vs.SUSPEND_FROM_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE) " +
                    "   OR (vs.STATUS = 4 AND vs.SUSPEND_FROM_DATE IS NOT NULL AND vs.SUSPEND_FROM_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE) " +
                    "   OR (vs.STATUS = 5 AND vs.DELETED_DATE IS NOT NULL AND vs.DELETED_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE) " +
                    "   OR (vs.STATUS = 2 AND vs.CLOSED_DATE IS NOT NULL AND vs.CLOSED_DATE > (THE_DATE + INTERVAL '1' DAY) AND vs.CREATED_DATE < THE_DATE))  THEN 1 end) openSeats " +
                    "FROM vancancy_seat_on_date cvs " +
                    "LEFT JOIN VACANCY_SEAT vs ON cvs.STAFF_ID = vs.RESPONSIBLE_STAFF_ID " +
                    "WHERE vs.CREATED_DATE < :endDate " +
                    "GROUP BY cvs.the_date ORDER BY cvs.THE_DATE ASC ")
    List<Object[]> countOpenSeatInRangeByStaff(@Param("staffId") long staffId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("days") int days);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM VacancySeat vs WHERE vs.vacancy.id IN :vacancyIds")
    void deleteAllByVacancyId(@Param("vacancyIds") List<Long> vacancyIds);
}
