package com.qooco.boost.data.mongo.services.embedded;

import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;

import java.util.List;

public interface AppointmentEmbeddedService {
    void update(Long vacancyId, AppointmentEmbedded appointment);

    void delete(List<Long> ids);
}
