package com.qooco.boost.models.sdo;

import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
public class ConversationPublicKeySDO {
    private ObjectId conversationId;
    private String accessToken;
    private PublicKeyEmbedded publicKey;

    public ConversationPublicKeySDO(ConversationBase conversationDoc, String accessToken, PublicKeyEmbedded publicKeyEmbedded) {
        this.conversationId = conversationDoc.getId();
        this.accessToken = accessToken;
        this.publicKey = publicKeyEmbedded;
    }
}
