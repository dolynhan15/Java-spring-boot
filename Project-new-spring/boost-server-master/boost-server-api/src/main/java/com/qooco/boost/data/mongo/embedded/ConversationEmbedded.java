package com.qooco.boost.data.mongo.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ConversationEmbedded {

    private ObjectId id;
    private List<UserProfileCvEmbedded> participants;
    private UserProfileCvEmbedded createdBy;
}
