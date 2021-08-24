package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.StaffWorking;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/9/2018 - 10:45 AM
*/
@Repository
public interface StaffWorkingRepository extends Boot2JpaRepository<StaffWorking, Long> {

    @Query("SELECT round(sum(case when sw.endDate > SYS_EXTRACT_UTC(SYSTIMESTAMP) THEN ((cast(SYS_EXTRACT_UTC(SYSTIMESTAMP) as date) - cast(sw.startDate as date)) * 24 * 60) ELSE ((cast(sw.endDate as date) - cast(sw.startDate as date)) * 24 * 60) END)) " +
            "  FROM StaffWorking sw WHERE sw.staff.company.companyId = :companyId " +
            "AND sw.startDate >= :startDate AND sw.endDate <= :endDate GROUP BY sw.staff.company.companyId")
    Integer sumByCompanyInDuration(@Param("companyId") long companyId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(nativeQuery = true,
            value = "with vancancy_seat_on_date AS " +
                    "(select (:startDate + level - 1) as THE_DATE, :staffId AS STAFF_ID from dual connect by level <= :days) " +
                    "select THE_DATE, " +
                    "round(sum(case when THE_DATE <= sw.START_DATE  AND ((THE_DATE + INTERVAL '1' DAY) > sw.END_DATE) THEN ((cast(sw.END_DATE as date) - cast(sw.START_DATE as date)) * 24 * 60) " +
                    "   when THE_DATE <= sw.START_DATE AND ((THE_DATE + INTERVAL '1' DAY) > sw.START_DATE) AND ((THE_DATE + INTERVAL '1' DAY) <= sw.END_DATE) THEN ((cast((THE_DATE + INTERVAL '1' DAY) as date) - cast(sw.START_DATE as date)) * 24 * 60) " +
                    "   when THE_DATE > sw.START_DATE AND ((THE_DATE + INTERVAL '1' DAY) <= sw.START_DATE) AND ((THE_DATE + INTERVAL '1' DAY) > sw.END_DATE) THEN ((cast(sw.END_DATE as date) - cast(THE_DATE as date)) * 24 * 60)  " +
                    "   when THE_DATE > sw.START_DATE AND ((THE_DATE + INTERVAL '1' DAY) < sw.END_DATE) THEN ((cast((THE_DATE + INTERVAL '1' DAY - INTERVAL '1' SECOND) as date) - cast(THE_DATE as date)) * 24 * 60) " +
                    "   ELSE 0 END)) activeTimes  " +
                    "FROM vancancy_seat_on_date cvs " +
                    "LEFT JOIN STAFF_WORKING sw ON cvs.STAFF_ID = sw.STAFF_ID " +
                    "WHERE sw.END_DATE <= :endDate AND sw.START_DATE >= :startDate " +
                    "GROUP BY cvs.the_date ORDER BY cvs.THE_DATE ASC ")
    List<Object[]> sumByStaffInDurationInEachDay(@Param("staffId") long staffId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("days") int days);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM StaffWorking sw WHERE sw.staff.staffId IN :staffIds")
    void deleteAllByStaffId(@Param("staffIds")List<Long> staffIds);
}
