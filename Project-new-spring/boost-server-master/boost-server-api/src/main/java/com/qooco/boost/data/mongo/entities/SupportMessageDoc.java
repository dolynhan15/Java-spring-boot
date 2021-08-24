package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.data.mongo.embedded.message.BoostHelperMessage;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static java.util.Optional.ofNullable;

@Getter
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Document(collection = "SupportMessageDoc")
public class SupportMessageDoc extends MessageBase {
    private String content;
    private FileEmbedded fileMessage;
    private UserProfileCvEmbedded createdBy;

    public SupportMessageDoc(ConversationBase conversation, long senderId) {
        super(conversation);
        setSender((UserProfileCvMessageEmbedded) conversation.getParticipant(senderId));
        setRecipient((UserProfileCvMessageEmbedded) conversation.getPartner(senderId));
        ofNullable(getRecipient()).ifPresent(user -> setReceiveInApp(getApp(getRecipient().getUserType())));

        ofNullable(getRecipient()).filter(recipient -> recipient.getUserProfileId().equals(senderId))
                .ifPresentOrElse(
                        it -> setStatus(MessageConstants.MESSAGE_STATUS_SEEN),
                        () -> setStatus(MessageConstants.MESSAGE_STATUS_SENT)
                );
    }

    public SupportMessageDoc(SupportMessageDoc doc) {
        super(doc);
        this.content = doc.getContent();
        this.createdBy = doc.createdBy;
        ofNullable(doc.getFileMessage()).ifPresent(it -> this.fileMessage = it);
    }

    private int getApp(int userType) {
        switch (userType) {
            case UserType.SELECT:
                return SELECT_APP.value();
            case UserType.PROFILE:
                return PROFILE_APP.value();
            default:
                return 0;
        }
    }
}
