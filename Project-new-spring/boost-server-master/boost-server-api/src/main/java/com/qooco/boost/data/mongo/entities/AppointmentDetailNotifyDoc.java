package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "AppointmentDetailNotifyDoc")
public class AppointmentDetailNotifyDoc {

    @Id
    private ObjectId id;
    private StaffEmbedded staff;
    private List<Long> appointmentDetailIds;
    private Date createdDate;

    @Override
    public String toString() {
        return "" + id.toString();
    }
}
