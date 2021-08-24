package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.AppointmentFeedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentFeedbackRepository extends Boot2JpaRepository<AppointmentFeedback, Long> {

    @Query("SELECT af FROM AppointmentFeedback af WHERE af.appointmentDetail.appointment.vacancy.id = :vacancyId AND af.appointmentDetail.userCurriculumVitae.curriculumVitaeId = :userCvId")
    Page<AppointmentFeedback> findAppointmentFeedbackByVacancyAndCandidate(@Param("vacancyId") long vacancyId, @Param("userCvId") long userCvId, Pageable pageable);


    @Query("SELECT COUNT(af.id) FROM AppointmentFeedback af WHERE af.appointmentDetail.appointment.vacancy.company.companyId = :companyId " +
            "AND af.feedbackDate >= :startDate AND af.feedbackDate <= :endDate ")
    int countByCompanyInDuration(@Param("companyId") long companyId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(nativeQuery = true,
            value = "with vancancy_seat_on_date AS " +
                    "(select (:startDate + level - 1) as THE_DATE, :staffId AS STAFF_ID from dual connect by level <= :days) " +
                    "select THE_DATE, " +
                    "count(CASE WHEN af.FEEDBACK_DATE >= THE_DATE AND af.FEEDBACK_DATE < (THE_DATE + INTERVAL '1' DAY)  THEN 1 end) appointments " +
                    "FROM vancancy_seat_on_date cvs " +
                    "LEFT JOIN APPOINTMENT_FEEDBACK af ON cvs.STAFF_ID = af.STAFF_ID " +
                    "WHERE af.FEEDBACK_DATE < :endDate " +
                    "GROUP BY cvs.the_date ORDER BY cvs.THE_DATE ASC")
    List<Object[]> countByStaffInDurationInEachDay(@Param("staffId") long staffId, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("days") int days);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM AppointmentFeedback af WHERE af.staff.staffId IN :staffIds")
    void deleteAllByStaffId(@Param("staffIds")List<Long> staffIds);
}
