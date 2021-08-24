package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.enumeration.CompanyRole;
import com.qooco.boost.data.mongo.embedded.CompanyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AuthorizationMessage;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.CompanyJoinRequest;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.oracle.services.UserFitService;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveJoinCompanyRequestInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveJoinCompanyRequestInMongoActor.class);
    public static final String ACTOR_NAME = "saveJoinCompanyRequestInMongoActor";

    private StaffService staffService;
    private UserFitService userFitService;
    private MessageCenterDocService messageCenterDocService;
    private MessageDocService messageDocService;
    private CompanyService companyService;
    private ConversationDocService conversationDocService;
    private PushNotificationService pushNotificationService;
    private SendMessageToClientService sendMessageToClientService;

    public SaveJoinCompanyRequestInMongoActor(StaffService staffService,
                                              UserFitService userFitService,
                                              MessageCenterDocService messageCenterDocService,
                                              MessageDocService messageDocService,
                                              ConversationDocService conversationDocService,
                                              CompanyService companyService,
                                              PushNotificationService pushNotificationService,
                                              SendMessageToClientService sendMessageToClientService) {
        this.userFitService = userFitService;
        this.staffService = staffService;
        this.messageCenterDocService = messageCenterDocService;
        this.messageDocService = messageDocService;
        this.conversationDocService = conversationDocService;
        this.companyService = companyService;
        this.pushNotificationService = pushNotificationService;
        this.sendMessageToClientService = sendMessageToClientService;
    }

    private List<UserProfileCvEmbedded> initAdminUserProfileCvs(Long companyId) {
        List<UserProfileCvEmbedded> embeddedAdminUserProfileCvs = new ArrayList<>();
        List<Staff> staffs = staffService.findStaffOfCompanyByRole(companyId, CompanyRole.ADMIN.getCode());
        List<UserFit> userFits = staffs.stream().map(Staff::getUserFit).collect(Collectors.toList());
        List<Long> ids = userFits.stream().map(UserFit::getUserProfileId).collect(Collectors.toList());
        List<UserFit> fitUserDocs = userFitService.findByIds(ids);

        UserProfileCvEmbedded userProfileCvEmbedded;
        for (UserFit fitUserDoc : fitUserDocs) {
            userProfileCvEmbedded = MongoConverters.convertToUserProfileCvEmbedded(fitUserDoc);
            embeddedAdminUserProfileCvs.add(userProfileCvEmbedded);
        }
        return embeddedAdminUserProfileCvs;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof CompanyJoinRequest) {
            CompanyJoinRequest joinRequest = ((CompanyJoinRequest) message);
            Company company = companyService.findById(joinRequest.getCompany().getCompanyId());
            Long joinRequestId = joinRequest.getCompanyJoinRequestId();
            UserFit sender = userFitService.findById(joinRequest.getUserFit().getUserProfileId());

            if (Objects.nonNull(joinRequestId)) {
                List<UserProfileCvEmbedded> adminUserProfileCvs = initAdminUserProfileCvs(company.getCompanyId());
                MessageCenterDoc messageCenterDoc = messageCenterDocService.findMessageCenterForAuthorizationByCompany(company.getCompanyId());
                UserProfileCvEmbedded requestedJoinUser = MongoConverters.convertToUserProfileCvEmbedded(sender);

                List<UserProfileCvMessageEmbedded> requestedJoinUsers = new ArrayList<>();

                if (Objects.nonNull(messageCenterDoc)) {
                    requestedJoinUsers.addAll(initRequestedJoinCvEmbedded(messageCenterDoc.getRequestedJoinUsers(), requestedJoinUser));
                } else {
                    messageCenterDoc = new MessageCenterDoc(MongoConverters.convertToCompanyEmbedded(company));
                    messageCenterDoc.setCreatedBy(requestedJoinUser);
                    requestedJoinUsers.add(new UserProfileCvMessageEmbedded(requestedJoinUser));
                }

                messageCenterDoc.setRequestedJoinUsers(requestedJoinUsers);
                List<UserProfileCvMessageEmbedded> adminCVs = adminUserProfileCvs.stream().map(UserProfileCvMessageEmbedded::new).collect(Collectors.toList());
                messageCenterDoc.setAdminOfCompany(adminCVs);
                messageCenterDocService.save(messageCenterDoc);

                List<MessageDoc> messageDocs = initMessageDocs(company, joinRequestId, messageCenterDoc, requestedJoinUser);
                if (!messageDocs.isEmpty()) {
                    messageDocs = messageDocService.save(messageDocs);
                    pushNotificationService.notifyJoinRequestCompanyMessage(messageDocs, true);
                    messageDocs.forEach(ms -> sendMessageToClientService.sendMessage(ms, null));
                    logger.info(StringUtil.append("Save CompanyJoinRequest Id =", joinRequest.getCompanyJoinRequestId().toString()));
                }
            }
        }
    }

    private List<UserProfileCvMessageEmbedded> initRequestedJoinCvEmbedded(List<UserProfileCvMessageEmbedded> userProfileCvEmbeddeds,
                                                                    UserProfileCvEmbedded requestedJoinUser) {
        List<UserProfileCvMessageEmbedded> requestedJoinUsers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userProfileCvEmbeddeds)) {
            requestedJoinUsers.addAll(userProfileCvEmbeddeds);
        }
        if (requestedJoinUsers.stream().noneMatch(r -> r.getUserProfileId().equals(requestedJoinUser.getUserProfileId()))) {
            requestedJoinUsers.add(new UserProfileCvMessageEmbedded(requestedJoinUser));
        }
        return requestedJoinUsers;
    }

    private List<UserProfileCvEmbedded> initAdminProfileCvEmbedded(List<UserProfileCvEmbedded> currentAdmins,
                                                                   List<UserProfileCvEmbedded> newAdmins) {
        List<UserProfileCvEmbedded> adminUsers = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(currentAdmins)) {
            adminUsers.addAll(currentAdmins);
        }
        if (CollectionUtils.isNotEmpty(adminUsers)) {
            List<UserProfileCvEmbedded> notExistedAdmin = newAdmins.stream()
                    .filter(cv -> currentAdmins.stream().noneMatch(ad -> ad.getUserProfileId().equals(cv.getUserProfileId())))
                    .collect(Collectors.toList());
            adminUsers.addAll(notExistedAdmin);
        } else {
            adminUsers.addAll(currentAdmins);
        }
        return adminUsers;
    }

    private List<MessageDoc> initMessageDocs(Company company, Long joinRequestId, MessageCenterDoc messageCenterDoc,
                                             UserProfileCvEmbedded requestedJoinUser) {
        List<MessageDoc> messageDocs = new ArrayList<>();
        CompanyEmbedded companyEmbedded = MongoConverters.convertToCompanyEmbedded(company);
        AuthorizationMessage authorizationMessage = new AuthorizationMessage(joinRequestId, companyEmbedded);
        ConversationDoc conversationDoc;
        for (UserProfileCvEmbedded admin : messageCenterDoc.getAdminOfCompany()) {
            conversationDoc = conversationDocService.save(messageCenterDoc, requestedJoinUser, admin);

            MessageDoc messageDoc= new MessageDoc.MessageDocBuilder(requestedJoinUser, admin, conversationDoc)
                    .withAuthorizationMessage(authorizationMessage).build();
            messageDocs.add(messageDoc);
        }

        return messageDocs;
    }
}
