package com.qooco.boost.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
public class ObjectLatest {
    private ObjectId id;
    private Date lastUpdateTime;

    public ObjectLatest(ObjectId conversationDocId, Date date) {
        this.id = conversationDocId;
        this.lastUpdateTime = date;
    }
}
