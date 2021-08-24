package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.count.CountByDate;
import com.qooco.boost.data.oracle.entities.AppointmentFeedback;

import java.util.List;
import java.util.Optional;

public interface AppointmentFeedbackService {
    List<AppointmentFeedback> save(Iterable<AppointmentFeedback> appointmentFeedbacks);

    Optional<AppointmentFeedback> findLastFeedbackByVacancyAndCandidate(long vacancyId, long userCvId);

    int countByCompanyInDuration(long companyId, long startDate, long endDate);

    List<CountByDate> countByStaffInDurationInEachDay(long staffId, long startDate, long endDate);

    void deleteAllByStaffId(List<Long> staffId);
}
