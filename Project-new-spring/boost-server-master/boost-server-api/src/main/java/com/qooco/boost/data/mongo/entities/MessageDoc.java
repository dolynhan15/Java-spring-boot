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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "MessageDoc")
public class MessageDoc extends MessageBase {

    private String content;
    private AppliedMessage appliedMessage;
    private AuthorizationMessage authorizationMessage;
    private StaffEmbedded staff;
    private AppointmentDetailMessage appointmentDetailMessage;
    private VacancyEmbedded vacancyMessage;
    private FileEmbedded fileMessage;
    private BoostHelperMessage boostHelperMessage;

    private boolean isForNewContact;

    public MessageDoc(MessageDoc doc) {
        super(doc);
        this.content = doc.getContent();
        ofNullable(doc.getAppliedMessage()).ifPresent(it -> this.appliedMessage = new AppliedMessage(it));
        ofNullable(doc.getAuthorizationMessage()).ifPresent(it -> this.authorizationMessage = new AuthorizationMessage(it));
        ofNullable(doc.getStaff()).ifPresent(it -> this.staff = new StaffEmbedded(it));
        ofNullable(doc.getAppointmentDetailMessage()).ifPresent(it -> this.appointmentDetailMessage = new AppointmentDetailMessage(it));
        ofNullable(doc.getVacancyMessage()).ifPresent(it -> this.vacancyMessage = it);
        ofNullable(doc.getFileMessage()).ifPresent(it -> this.fileMessage = it);
        ofNullable(doc.getBoostHelperMessage()).ifPresent(it -> this.boostHelperMessage = it);
        this.isForNewContact = doc.isForNewContact;
    }

    public MessageDoc(MessageBase doc) {
        super(doc);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public MessageDoc(MessageDocBuilder builder) {
        super(builder.conversationDoc);
        this.appliedMessage = builder.appliedMessage;
        this.appointmentDetailMessage = builder.appointmentDetailMessage;
        this.authorizationMessage = builder.authorizationMessage;
        this.staff = builder.staff;
        this.vacancyMessage = builder.vacancyMessage;
        this.fileMessage = builder.fileMessage;
        this.boostHelperMessage = builder.boostHelperMessage;
        this.content = builder.content;
        this.isForNewContact = builder.isForNewContact;

        super.setSender(new UserProfileCvMessageEmbedded(builder.sender));
        super.setRecipient(new UserProfileCvMessageEmbedded(builder.recipient));
        super.setType(builder.type);
        super.setReceiveInApp(builder.receiveInApp);
        super.setStatus(builder.status);
    }

    public static class MessageDocBuilder {

        private ConversationBase conversationDoc;
        private UserProfileCvEmbedded sender;
        private UserProfileCvEmbedded recipient;

        private int receiveInApp;
        private int type;

        private AppliedMessage appliedMessage;
        private AuthorizationMessage authorizationMessage;
        private StaffEmbedded staff;
        private AppointmentDetailMessage appointmentDetailMessage;
        private VacancyEmbedded vacancyMessage;
        private FileEmbedded fileMessage;
        private BoostHelperMessage boostHelperMessage;
        private String content;
        private boolean isForNewContact;
        private int status;

        public MessageDocBuilder(ConversationBase conversationDoc, long senderId) {
            this.conversationDoc = conversationDoc;
            this.sender = conversationDoc.getParticipant(senderId);
            this.recipient = conversationDoc.getPartner(senderId);
            this.isForNewContact = false;
            this.status = MessageConstants.MESSAGE_STATUS_SENT;
        }

        public MessageDocBuilder(UserProfileCvEmbedded sender, UserProfileCvEmbedded recipient, ConversationBase conversationDoc) {
            this.conversationDoc = conversationDoc;
            this.sender = sender;
            this.recipient = recipient;
            this.isForNewContact = false;
            this.status = MessageConstants.MESSAGE_STATUS_SENT;
        }

        public MessageDocBuilder withApplicantMessage(AppliedMessage appliedMessage) {
            clearMessageData();
            this.appliedMessage = appliedMessage;
            this.type = MessageConstants.APPLICANT_MESSAGE;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withBoostHelperMessage(BoostHelperMessage boostHelperMessage, int receiveInApp) {
            clearMessageData();
            this.boostHelperMessage = boostHelperMessage;
            this.type = MessageConstants.BOOST_HELPER_MESSAGE;
            this.receiveInApp = receiveInApp;
            return this;
        }

        public MessageDocBuilder withIsForNewContact(boolean isForNewContact) {
            this.isForNewContact = isForNewContact;
            return this;
        }

        public MessageDocBuilder withStatus(int status) {
            this.status = status;
            return this;
        }

        public MessageDocBuilder withApplicantAppointmentMessage(AppliedMessage appliedMessage) {
            clearMessageData();
            this.appliedMessage = appliedMessage;
            this.type = MessageConstants.APPOINTMENT_APPLICANT_MESSAGE;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withTextMessage(String text) {
            clearMessageData();
            this.content = text;
            this.type = MessageConstants.TEXT_MESSAGE;
            this.receiveInApp = getReceiveInApp();
            return this;
        }

        public MessageDocBuilder withChangeContactMessage() {
            clearMessageData();
            this.type = MessageConstants.CHANGE_CONTACT_MESSAGE;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withAuthorizationMessage(AuthorizationMessage authorizationMessage) {
            clearMessageData();
            this.type = MessageConstants.AUTHORIZATION_MESSAGE;
            this.authorizationMessage = authorizationMessage;
            this.receiveInApp = MessageConstants.RECEIVE_IN_HOTEL_APP;
            return this;
        }

        public MessageDocBuilder withAssignRoleMessage(StaffEmbedded staff) {
            clearMessageData();
            this.type = MessageConstants.ASSIGNMENT_ROLE_MESSAGE;
            this.staff = staff;
            this.receiveInApp = MessageConstants.RECEIVE_IN_HOTEL_APP;
            return this;
        }

        public MessageDocBuilder withAppointmentMessage(AppointmentDetailMessage appointmentMessage) {
            clearMessageData();
            this.type = MessageConstants.APPOINTMENT_MESSAGE;
            this.appointmentDetailMessage = appointmentMessage;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withCancelAppointmentMessage(AppointmentDetailMessage appointmentMessage) {
            clearMessageData();
            this.type = MessageConstants.APPOINTMENT_CANCEL_MESSAGE;
            this.appointmentDetailMessage = appointmentMessage;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withCongratulationMessage(VacancyEmbedded vacancyMessage) {
            clearMessageData();
            this.type = MessageConstants.CONGRATULATION_MESSAGE;
            this.vacancyMessage = vacancyMessage;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withInactiveVacancyMessage(VacancyEmbedded vacancyMessage) {
            clearMessageData();
            this.type = MessageConstants.INACTIVE_VACANCY;
            this.vacancyMessage = vacancyMessage;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withSuspendedVacancyMessage(VacancyEmbedded vacancyMessage) {
            clearMessageData();
            this.type = MessageConstants.SUSPENDED_VACANCY;
            this.vacancyMessage = vacancyMessage;
            this.receiveInApp = MessageConstants.RECEIVE_IN_CAREER_APP;
            return this;
        }

        public MessageDocBuilder withFileMessage(FileEmbedded fileMessage) {
            clearMessageData();
            this.type = MessageConstants.MEDIA_MESSAGE;
            this.fileMessage = fileMessage;
            this.receiveInApp = getReceiveInApp();
            return this;
        }

        private int getReceiveInApp() {
            if (sender.getUserType() == UserType.SELECT && recipient.getUserType() == UserType.SELECT) {
                return MessageConstants.RECEIVE_IN_HOTEL_APP;
            } else if (sender.getUserType() == UserType.PROFILE && recipient.getUserType() == UserType.SELECT) {
                return MessageConstants.RECEIVE_IN_HOTEL_APP;
            } else if (sender.getUserType() == UserType.SELECT && recipient.getUserType() == UserType.PROFILE) {
                return MessageConstants.RECEIVE_IN_CAREER_APP;
            }
            return 0;
        }

        private void clearMessageData() {
            this.appliedMessage = null;
            this.authorizationMessage = null;
            this.staff = null;
            this.appointmentDetailMessage = null;
            this.vacancyMessage = null;
            this.fileMessage = null;
            this.content = null;
        }

        public MessageDoc build() {
            return new MessageDoc(this);
        }
    }
}
