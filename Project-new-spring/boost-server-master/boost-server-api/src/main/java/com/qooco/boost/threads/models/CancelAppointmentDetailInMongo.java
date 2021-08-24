package com.qooco.boost.threads.models;

import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.oracle.entities.Appointment;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.models.sdo.VacancyCandidateSDO;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CancelAppointmentDetailInMongo {

    private Map<Appointment, List<AppointmentDetail>> cancelEventMap;
    private Integer reason;
    private VacancyDoc vacancyDoc;
    private VacancyCandidateSDO vacancyCandidate;

    private Appointment targetAppointment;
    private List<AppointmentDetail> newAppointmentDetails;

    public CancelAppointmentDetailInMongo() {
        this.cancelEventMap = new HashMap<>();
    }
}
