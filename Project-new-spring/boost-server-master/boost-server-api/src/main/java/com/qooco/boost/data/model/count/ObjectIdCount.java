package com.qooco.boost.data.model.count;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class ObjectIdCount {
    private ObjectId id;
    private long total;
}
