package com.qooco.boost.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class Conversation {
    private ObjectId id;
    private ObjectId messageId;

    public Conversation(ObjectId id, ObjectId messageId) {
        this.id = id;
        this.messageId = messageId;
    }
}
