package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.model.ObjectIdList;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.Staff;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HideMessageWhenDeleteStaffActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(HideMessageWhenDeleteStaffActor.class);
    public static final String ACTOR_NAME = "hideMessageWhenDeleteStaffActor";

    private MessageDocService messageDocService;
    private MessageCenterDocService messageCenterDocService;
    private ConversationDocService conversationDocService;


    public HideMessageWhenDeleteStaffActor(MessageDocService messageDocService,
                                           MessageCenterDocService messageCenterDocService,
                                           ConversationDocService conversationDocService) {
        this.messageDocService = messageDocService;
        this.messageCenterDocService = messageCenterDocService;
        this.conversationDocService = conversationDocService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Staff) {
            Staff staff = (Staff) message;
            Long companyId = staff.getCompany().getCompanyId();
            Long userFitId = staff.getUserFit().getUserProfileId();
            updateAuthorizationConversation(companyId, userFitId);
            updateVacancyConversation(companyId, userFitId);
        }
    }

    private void updateAuthorizationConversation(long companyId, long userProfileId) {
        MessageCenterDoc messageCenterDoc = messageCenterDocService.findMessageCenterForAuthorizationByCompany(companyId);
        if (Objects.nonNull(messageCenterDoc)) {

            ofNullable(messageCenterDoc.getAdminOfCompany()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> it.removeIf(u -> u.getUserProfileId().equals(userProfileId)));

            ofNullable(messageCenterDoc.getRequestedJoinUsers()).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> it.removeIf(u -> u.getUserProfileId().equals(userProfileId)));

            messageCenterDocService.save(messageCenterDoc);
            setIsDeleteInConversationAndMessage(companyId, userProfileId);
        }
    }

    private void updateVacancyConversation(long companyId, long userProfileId) {
        List<MessageCenterDoc> messageCenters = messageCenterDocService.findMessageCenterForVacancyByContactUserProfileAndCompany(userProfileId, companyId);

        if (CollectionUtils.isNotEmpty(messageCenters)) {
            for (MessageCenterDoc messageCenter : messageCenters) {
                ofNullable(messageCenter.getAppointmentManagers()).filter(CollectionUtils::isNotEmpty)
                        .ifPresent(it -> it.removeIf(u -> u.getUserProfileId().equals(userProfileId)));

                ofNullable(messageCenter.getContactPersons()).filter(CollectionUtils::isNotEmpty)
                        .ifPresent(it -> it.removeIf(u -> u.getUserProfileId().equals(userProfileId)));

                ofNullable(messageCenter.getFreeChatRecruiter()).filter(CollectionUtils::isNotEmpty)
                        .ifPresent(it -> it.removeIf(u -> u.getUserProfileId().equals(userProfileId)));
            }
            messageCenterDocService.save(messageCenters);
            setIsDeleteInConversationAndMessage(companyId, userProfileId);
        }
    }

    private void setIsDeleteInConversationAndMessage(Long companyId, Long userProfileId) {
        List<ObjectIdList> messageCenters = conversationDocService.getConversationsGroupByMessageCenter(companyId, userProfileId);
        if (CollectionUtils.isNotEmpty(messageCenters)) {
            List<ObjectId> ids = new ArrayList<>();
            messageCenters.forEach(messageCenter -> ids.addAll(messageCenter.getResult()));
            if (CollectionUtils.isNotEmpty(ids)) {
                conversationDocService.deleteConversation(ids);
                messageDocService.softDeleteMessageByConversationIds(ids);
            }
        }
    }
}