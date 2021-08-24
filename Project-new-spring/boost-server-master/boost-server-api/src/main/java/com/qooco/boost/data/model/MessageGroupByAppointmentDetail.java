package com.qooco.boost.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 4/1/2019 - 1:58 PM
*/
@Getter
@Setter
@NoArgsConstructor
public class MessageGroupByAppointmentDetail {
    private long appointmentDetailId;
    private ObjectId conversationId;
    private ObjectId messageCenterId;
}
