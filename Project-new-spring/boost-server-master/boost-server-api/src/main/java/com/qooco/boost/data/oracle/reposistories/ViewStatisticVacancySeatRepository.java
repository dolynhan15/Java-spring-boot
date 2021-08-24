package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.views.ViewStatisticVacancySeat;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ViewStatisticVacancySeatRepository extends Boot2JpaRepository<ViewStatisticVacancySeat, Long> {

    String CLOSED_SEAT_OF_COMPANY = "SELECT vs.STAFF_ID, SUM(vs.COUNT_RESULT) AS CLOSED_SEAT " +
            "FROM VIEW_STATISTIC_VACANCY_SEAT vs " +
            "JOIN STAFF st ON (st.STAFF_ID = vs.STAFF_ID AND st.COMPANY_ID = :companyId) " +
            "WHERE vs.TYPE = 2 AND vs.REPORT_DATE >= :startDate AND vs.REPORT_DATE <= :endDate " +
            "GROUP BY vs.STAFF_ID " +
            "ORDER BY CLOSED_SEAT DESC ";

    @Query(nativeQuery = true, value = "SELECT * FROM ( " + CLOSED_SEAT_OF_COMPANY + ") WHERE ROWNUM <= :limit ")
    List<Object[]> findTopEmployeeByCompany(
            @Param("companyId") Long companyId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("limit") int limit);
}
