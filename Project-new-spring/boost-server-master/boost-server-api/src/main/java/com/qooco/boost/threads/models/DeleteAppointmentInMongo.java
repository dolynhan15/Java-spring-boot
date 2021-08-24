package com.qooco.boost.threads.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class DeleteAppointmentInMongo {
    private List<Long> appointmentIds;
}
