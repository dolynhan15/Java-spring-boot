package com.qooco.boost.data.mongo.embedded.appointment;

import com.qooco.boost.data.mongo.entities.AppointmentDetailDoc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AppointmentDetailBaseEmbedded {
    private Long id;
    private int status;

    public AppointmentDetailBaseEmbedded(AppointmentDetailDoc doc) {
        this.id = doc.getId();
        this.status = doc.getStatus();
    }
}
