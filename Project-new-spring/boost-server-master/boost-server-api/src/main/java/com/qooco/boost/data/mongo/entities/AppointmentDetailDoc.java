package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.StaffShortEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Setter @Getter @NoArgsConstructor
@FieldNameConstants
@Document(collection = "AppointmentDetailDoc")
public class AppointmentDetailDoc {
    @Id
    private Long id;
    private AppointmentEmbedded appointment;
    private VacancyEmbedded vacancy;
    private UserProfileCvEmbedded candidate;
    private Date appointmentTime;
    private StaffShortEmbedded creator;
    private StaffShortEmbedded updater;
    private Date createdDate;
    private Date updatedDate;
    private boolean isDeleted;
    private Integer status;
}
