package com.qooco.boost.data.mongo.services;

import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;

import java.util.Date;
import java.util.List;

public interface AppointmentDetailDocService extends DocService<AppointmentDetailDoc, Long> {
    List<AppointmentDetailDoc> findByAppointmentId(Long appointmentId);

    List<AppointmentDetailDoc> findNoneVacancy(int limit, List<Long> exceptId);

    List<AppointmentDetailDoc> findByVacancyIdAndCandidateCVId(Long vacancyId, List<Long> userProfileCVIds);

    List<AppointmentDetailDoc> findNoDateTimeRange(int limit, List<Long> exceptId);

    void updateDateTimeRangeAndType(long id, List<Date> dateRanges, List<Date> timeRanges, int type, Date fromDate, Date toDate);

    void updateStatusOfAppointmentDetailDoc(long id, Integer status);

    void updateAppointmentTimeAndStatus(long id, Date time, int status);

    boolean updateBatchVacancies(List<AppointmentDetailDoc> appointmentDetailDocs);
}
