package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.embedded.RoleCompanyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.RoleCompany;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.services.RoleCompanyService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.models.SetRoleInMongo;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.StaffActorService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SetRoleInMessageInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SetRoleInMessageInMongoActor.class);
    public static final String ACTOR_NAME = "setRoleInMessageInMongoActor";

    private MessageDocService messageDocService;
    private RoleCompanyService roleCompanyService;
    private StaffActorService staffActorService;
    private ConversationDocService conversationDocService;
    private MessageCenterDocService messageCenterDocService;
    private PushNotificationService pushNotificationService;
    private SendMessageToClientService sendMessageToClientService;

    public SetRoleInMessageInMongoActor(MessageDocService messageDocService,
                                        RoleCompanyService roleCompanyService,
                                        StaffActorService staffActorService,
                                        ConversationDocService conversationDocService,
                                        MessageCenterDocService messageCenterDocService,
                                        PushNotificationService pushNotificationService,
                                        SendMessageToClientService sendMessageToClientService) {
        this.messageDocService = messageDocService;
        this.roleCompanyService = roleCompanyService;
        this.staffActorService = staffActorService;
        this.messageCenterDocService = messageCenterDocService;
        this.pushNotificationService = pushNotificationService;
        this.conversationDocService = conversationDocService;
        this.sendMessageToClientService = sendMessageToClientService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof SetRoleInMongo) {
            setRoleInMessage((SetRoleInMongo) message);
        }
    }

    private void setRoleInMessage(SetRoleInMongo roleInMongo) {
        Staff staff = roleInMongo.getStaff();
        CompanyRole oldRole = roleInMongo.getOldRole();
        CompanyRole newRole = roleInMongo.getNewRole();
        setRoleInMessage(staff, oldRole, newRole);
    }

    private void setRoleInMessage(Staff staff, CompanyRole oldRole, CompanyRole newRole) {
        MessageDoc existedMessage = messageDocService.findByRecipientAndStaffAndType(staff.getUserFit().getUserProfileId(),
                MongoConverters.convertToStaffEmbedded(staffActorService.updateLazyValue(staff)),
                MessageConstants.ASSIGNMENT_ROLE_MESSAGE);

        RoleCompany roleCompany = roleCompanyService.findByName(newRole.name());
        if (Objects.nonNull(existedMessage)) {
            if (Objects.nonNull(roleCompany)) {
                existedMessage.getStaff().setRoleCompany(new RoleCompanyEmbedded(roleCompany));
                existedMessage.getStaff().setStatus(MessageConstants.MESSAGE_STATUS_SENT);
                existedMessage = messageDocService.save(existedMessage, true);

                pushNotificationService.notifyAssignRoleMessage(existedMessage, true);
                sendMessageToClientService.sendMessage(existedMessage, MessageConstants.UPDATE_MESSAGE_ACTION, null);
            }
            logger.info(StringUtil.append("Set role in message Id =", existedMessage.getId().toString()));
        }

        if (Objects.nonNull(roleCompany)) {
            Long companyId = staff.getCompany().getCompanyId();
            UserProfileCvEmbedded userProfileCvEmbedded = MongoConverters.convertToUserProfileCvEmbedded(staff.getUserFit());
            try {
                conversationDocService.setDisableConversations(companyId, userProfileCvEmbedded.getUserProfileId(), newRole, oldRole);
                conversationDocService.setChangedContactPersonStatus(companyId, userProfileCvEmbedded.getUserProfileId(), newRole, oldRole);
                messageCenterDocService.updateAdminsOfCompany(companyId, userProfileCvEmbedded, oldRole.getName(), newRole.getName());
            } catch (IllegalArgumentException ex) {
                throw new InvalidParamException(ResponseStatus.NOT_FOUND_ROLE);
            }
        }
    }
}
