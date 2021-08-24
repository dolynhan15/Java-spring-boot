package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.constants.SupportChannelStatus;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
@Document(collection = "SupportConversationDoc")
@SuperBuilder(toBuilder = true)
public class SupportConversationDoc extends ConversationBase {

    private String channelId;
    private UserProfileCvEmbedded customer;
    private CompanyEmbedded company;

    //1: opening; 2: Archive;
    private int status;
    private int fromApp;

    public SupportConversationDoc(MessageCenterDoc messageCenter, UserProfileCvEmbedded sender, UserProfileCvEmbedded recipient, int fromApp) {
        super(messageCenter, List.of(sender, recipient));
        this.customer = sender;
        this.status = SupportChannelStatus.OPENING;
        this.company = messageCenter.getCompany();
        this.fromApp = fromApp;
    }
}
