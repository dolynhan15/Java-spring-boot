package com.qooco.boost.data.mongo.embedded.appointment;

import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDetailEmbedded extends AppointmentDetailBaseEmbedded{
    private AppointmentEmbedded appointment;
    private VacancyEmbedded vacancy;
    private UserProfileCvEmbedded candidate;
    private Date appointmentTime;
    private Date createdDate;
    private boolean isDeleted;

    public AppointmentDetailEmbedded(AppointmentDetailDoc doc){
        super(doc);
        this.appointment = doc.getAppointment();
        this.vacancy = doc.getVacancy();
        this.candidate = doc.getCandidate();
        this.appointmentTime = doc.getAppointmentTime();
        this.createdDate = doc.getCreatedDate();
        this.isDeleted = doc.isDeleted();
    }
}
