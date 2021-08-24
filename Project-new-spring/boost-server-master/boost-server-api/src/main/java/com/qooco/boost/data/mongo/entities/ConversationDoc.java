package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
@Document(collection = "ConversationDoc")
public class ConversationDoc extends ConversationBase {
    private int contactPersonStatus;

    public ConversationDoc(MessageCenterDoc messageCenterDoc, List<UserProfileCvEmbedded> participants) {
        super(messageCenterDoc, participants);
    }

    public ConversationDoc(ObjectId conversationDocId, ObjectId messageCenterId) {
      super(conversationDocId, messageCenterId);
    }

    public ConversationDoc(ObjectId id, String secretKey, Map<String, PublicKeyEmbedded> userKeys) {
        super.setId(id);
        super.setSecretKey(secretKey);
        super.setUserKeys(userKeys);
    }
}
