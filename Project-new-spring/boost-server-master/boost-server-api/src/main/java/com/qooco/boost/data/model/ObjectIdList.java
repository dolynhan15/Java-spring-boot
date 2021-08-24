package com.qooco.boost.data.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
public class ObjectIdList {
    private ObjectId id;
    private List<ObjectId> result;
}
