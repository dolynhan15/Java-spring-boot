package com.qooco.boost.threads.services;

import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;

import java.util.List;

public interface AppointmentDetailActorService {
    void saveAppointmentDetail(Appointment appointment, List<AppointmentDetail> appointmentDetails, String locale);

    void saveAppointmentDetail(Appointment appointment, List<AppointmentDetail> appointmentDetails, Integer cancelReason, String locale);

    void updateAppointmentCandidatesInVacancyDoc(List<AppointmentDetailDoc> detailDocs);

    void updateAppointmentDetail(AppointmentDetail appointmentDetail, String locale);

    void updateAppointmentDetailDoc(AppointmentDetail appointmentDetail, String locale);

}
