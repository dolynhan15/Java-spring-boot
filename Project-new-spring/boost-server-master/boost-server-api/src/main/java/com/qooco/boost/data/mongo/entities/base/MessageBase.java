package com.qooco.boost.data.mongo.entities.base;

import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.embedded.FileEmbedded;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.data.mongo.embedded.message.BoostHelperMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class MessageBase {

    @Id
    private ObjectId id;
    private ObjectId conversationId;
    private ObjectId messageCenterId;
    private int messageCenterType;

    private UserProfileCvMessageEmbedded sender;
    private UserProfileCvMessageEmbedded recipient;

    private Long companyId;
    private int receiveInApp;

    private int type;
    private int status;

    private Date createdDate;
    private Date updatedDate;

    private boolean isDeleted;
    private String secretKey;

    public String getContent() {
        return null;
    }
    public AppliedMessage getAppliedMessage() {
        return null;
    }
    public AuthorizationMessage getAuthorizationMessage() {
        return null;
    }
    public StaffEmbedded getStaff() {
        return null;
    }
    public AppointmentDetailMessage getAppointmentDetailMessage() {
        return null;
    }
    public VacancyEmbedded getVacancyMessage() {
        return null;
    }
    public FileEmbedded getFileMessage() {
        return null;
    }
    public BoostHelperMessage getBoostHelperMessage() {
        return null;
    }

    public MessageBase(ConversationBase conversationDoc) {
        this.conversationId = conversationDoc.getId();
        this.messageCenterId = conversationDoc.getMessageCenterId();
        this.messageCenterType = conversationDoc.getMessageCenterType();
        this.companyId = conversationDoc.getCompanyId();
        this.secretKey = conversationDoc.getSecretKey();
        this.type = MessageConstants.TEXT_MESSAGE;

        Date now = new Date();
        this.createdDate = now;
        this.updatedDate = now;
        this.isDeleted = false;
    }

    public MessageBase(MessageBase doc) {
        this.id = doc.getId();
        this.secretKey = doc.getSecretKey();
        this.companyId = doc.getCompanyId();
        this.conversationId = doc.getConversationId();
        this.messageCenterId = doc.getMessageCenterId();
        this.sender = doc.getSender();
        this.recipient = doc.getRecipient();
        this.receiveInApp = doc.getReceiveInApp();
        this.status = doc.getStatus();
        this.type = doc.getType();
        this.createdDate = doc.getCreatedDate();
        this.updatedDate = doc.getUpdatedDate();
        this.isDeleted = doc.isDeleted();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageBase that = (MessageBase) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
