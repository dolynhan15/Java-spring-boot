package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.SupportMessageDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.models.dto.vacancy.VacancyShortInformationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.Date;

import static java.util.Optional.ofNullable;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDTO {
    private String id;
    private String conversationId;
    private String messageCenterId;
    private int messageCenterType;
    private Long senderId;
    private Long recipientId;

    private int status;
    private int type;
    private Date createdDate;
    private Date updatedDate;
    private Date timestamp;

    private String content;
    private AppliedMessageDTO appliedMessage;
    private AuthorizationMessageDTO authorizationMessage;
    private AppointmentMessageDTO appointmentMessage;
    private StaffMessageDTO staff;
    private VacancyShortInformationDTO vacancy;
    private MediaMessageDTO mediaMessage;
    private BoostHelperMessageDTO boostHelperMessage;

    private int messageAction;
    private boolean isForNewContact;

    @Deprecated
    @JsonIgnore
    private String secretKey;

    @Deprecated
    private AppointmentMessageDTO appointmentMessageDTO;

    private MessageDTO(MessageBase doc) {
        this.id = doc.getId().toHexString();
        this.conversationId = ofNullable(doc.getConversationId()).map(ObjectId::toHexString).orElse(null);
        this.messageCenterId = ofNullable(doc.getMessageCenterId()).map(ObjectId::toHexString).orElse(null);
        this.messageCenterType = doc.getMessageCenterType();
        this.senderId = ofNullable(doc.getSender()).map(UserProfileBasicEmbedded::getUserProfileId).orElse(null);
        this.recipientId = ofNullable(doc.getRecipient()).map(UserProfileBasicEmbedded::getUserProfileId).orElse(null);
        this.status = doc.getStatus();
        this.type = doc.getType();

        this.secretKey = doc.getSecretKey();
        this.timestamp = this.createdDate = doc.getCreatedDate();
        this.updatedDate = doc.getCreatedDate();

    }

    private void convertMessageDTO(MessageDoc doc, boolean isEncryptMessage, String qoocoDomainPath, String locale) {
        this.isForNewContact = doc.isForNewContact();
        this.content = isEncryptMessage ? CipherKeys.encryptByAES(doc.getContent(), doc.getSecretKey()) : doc.getContent();
        ofNullable(doc.getAppliedMessage()).ifPresent(it -> this.appliedMessage = new AppliedMessageDTO(it, locale));
        ofNullable(doc.getAuthorizationMessage()).ifPresent(it -> this.authorizationMessage = new AuthorizationMessageDTO(it));
        ofNullable(doc.getStaff()).ifPresent(it -> this.staff = new StaffMessageDTO(it, it.getStatus(), locale));
        ofNullable(doc.getAppointmentDetailMessage()).ifPresent(it -> this.appointmentMessageDTO = this.appointmentMessage = new AppointmentMessageDTO(it, locale));
        ofNullable(doc.getVacancyMessage()).ifPresent(it -> this.vacancy = new VacancyShortInformationDTO(it, locale));
        ofNullable(doc.getFileMessage()).ifPresent(it -> this.mediaMessage = new MediaMessageDTO(it));
        ofNullable(doc.getBoostHelperMessage()).ifPresent(it -> this.boostHelperMessage = new BoostHelperMessageDTO(it, qoocoDomainPath, locale));
    }

    private void convertMessageDTO(SupportMessageDoc doc, boolean isEncryptMessage) {
        this.content = isEncryptMessage ? CipherKeys.encryptByAES(doc.getContent(), doc.getSecretKey()) : doc.getContent();
        ofNullable(doc.getFileMessage()).ifPresent(it -> this.mediaMessage = new MediaMessageDTO(it));
    }


    public MessageDTO(MessageBase doc, boolean isEncryptMessage, String qoocoDomainPath, String locale) {
        this(doc);
        if(doc instanceof MessageDoc){
            convertMessageDTO((MessageDoc) doc, isEncryptMessage, qoocoDomainPath, locale);
        } else if (doc instanceof SupportMessageDoc){
            convertMessageDTO((SupportMessageDoc) doc, isEncryptMessage);
        }
    }

    public MessageDTO(MessageBase messageDoc, int messageAction, boolean isEncryptMessage, String qoocoDomainPath, String locale) {
        this(messageDoc, isEncryptMessage, qoocoDomainPath, locale);
        this.messageAction = messageAction;
    }

    public MessageDTO(MessageBase messageDoc, int messageAction, String qoocoDomainPath, String locale) {
        this(messageDoc, messageAction, false, qoocoDomainPath, locale);
        this.messageAction = messageAction;
    }

    public MessageDTO(String conversationId, String messageCenterId, int messageAction) {
        this.conversationId = conversationId;
        this.messageAction = messageAction;
        this.messageCenterId = messageCenterId;
    }

    public MessageDTO(MessageDoc messageDoc, int messageAction, boolean isEncryptMessage, String qoocoDomainPath, String locale) {
        this(messageDoc, isEncryptMessage, qoocoDomainPath, locale);
        this.messageAction = messageAction;
    }

    public MessageDTO(int sessionMessage, Date startOfDay) {
        this.type = sessionMessage;
        this.timestamp = startOfDay;
    }
}
