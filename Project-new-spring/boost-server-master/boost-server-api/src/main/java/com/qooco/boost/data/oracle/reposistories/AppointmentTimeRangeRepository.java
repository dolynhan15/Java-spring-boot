package com.qooco.boost.data.oracle.reposistories;

import com.qooco.boost.data.oracle.entities.AppointmentTimeRange;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AppointmentTimeRangeRepository extends Boot2JpaRepository<AppointmentTimeRange, Long> {
    @Query("SELECT ap FROM AppointmentTimeRange ap WHERE ap.id = :id AND ap.isDeleted = false ")
    AppointmentTimeRange findValidById(@Param("id") Long id);

    @Query("SELECT ap FROM AppointmentTimeRange ap WHERE ap.appointment.id IN :appointmentId AND ap.appointment.isDeleted = false " +
            "AND ap.isDeleted = false ORDER BY ap.appointmentTime ASC ")
    List<AppointmentTimeRange> findByAppointmentId(@Param("appointmentId") Long appointmentId);

    @Transactional
    @Modifying
    @Query("DELETE FROM AppointmentTimeRange ap WHERE ap.appointment.id = :appointmentId AND ap.isDeleted = false ")
    void deleteByAppointmentId(@Param("appointmentId") Long appointmentId);
}
