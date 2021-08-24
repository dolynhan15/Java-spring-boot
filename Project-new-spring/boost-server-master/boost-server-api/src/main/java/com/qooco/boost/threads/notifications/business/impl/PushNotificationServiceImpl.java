package com.qooco.boost.threads.notifications.business.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.threads.notifications.PushNotificationActor;
import com.qooco.boost.threads.notifications.SocketNotificationActor;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.notifications.enumeration.PushTask;
import com.qooco.boost.threads.services.MessageDocActorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

@Service
public class PushNotificationServiceImpl extends PushNotificationServiceAbstract implements PushNotificationService {
    @Autowired
    private ActorSystem system;
    @Autowired
    private MessageDocActorService messageDocActorService;

    private ActorRef updaterBaidu;
    private ActorRef updaterSocket;

    @Value(ApplicationConstant.BOOST_PUSH_NOTIFICATION_SILENT_ONLY)
    private boolean silentOnly;

    @Value(ApplicationConstant.BOOST_BAIDU_PUSH_NOTIFICATION_ENABLE)
    private boolean baiduEnable;


    @Value(ApplicationConstant.BOOST_NOTIFICATION_REALTIME_TYPE)
    private int sendRealTimeNotifyType;

    @PostConstruct
    public void init() {
        updaterBaidu = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(PushNotificationActor.ACTOR_NAME));

        updaterSocket = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(SocketNotificationActor.ACTOR_NAME));
    }

    @Override
    public void notifyApplicantMessage(MessageDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getRecipient())) {
            sendRealTimeNotificationToCareer(message.getRecipient().getUserProfileId(), PushTask.PUSH_TO_CAREER_APPLICANT_MESSAGE, message, isCounted);
        }
    }

    @Override
    public void notifyViewCandidateProfile(ViewProfileDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getCandidate())) {
            sendRealTimeNotificationToCareer(message.getCandidate().getUserProfileId(), PushTask.PUSH_TO_CAREER_VIEW_CANDIDATE_PROFILE, message, isCounted);
        }
    }

    @Override
    public void notifyApplicantResponseMessage(MessageDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getSender())) {
            sendRealTimeNotificationToHotel(message.getSender().getUserProfileId(), PushTask.PUSH_TO_HOTEL_RESPONSE_APPLICANT_MESSAGE, message, isCounted);
        }
    }

    @Override
    public void notifyAppointmentResponseMessage(MessageDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getSender())) {
            sendRealTimeNotificationToHotel(message.getSender().getUserProfileId(), PushTask.PUSH_TO_HOTEL_RESPONSE_APPOINTMENT_MESSAGE, message, isCounted);
        }
    }

    @Override
    public void notifyAppointmentMessage(List<MessageDoc> messages, boolean isCounted) {
        if (CollectionUtils.isNotEmpty(messages)) {
            sendRealTimeNotification(Const.PushTarget.PUSH_TO_ALL, PushTask.PUSH_APPOINTMENT_MESSAGE, messages, isCounted);
        }
    }

    @Override
    public void notifyJoinRequestCompanyMessage(List<MessageDoc> messages, boolean isCounted) {
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(message -> {
                if (Objects.nonNull(message.getRecipient())) {
                    sendRealTimeNotificationToHotel(message.getRecipient().getUserProfileId(), PushTask.PUSH_TO_HOTEL_JOIN_COMPANY_REQUEST_MESSAGE, message, isCounted);
                }
            });
        }
    }

    @Override
    public void notifyJoinCompanyApproval(MessageDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getSender())) {
            sendRealTimeNotificationToHotel(message.getSender().getUserProfileId(), PushTask.PUSH_TO_HOTEL_APPROVAL_JOIN_COMPANY_REQUEST, message, isCounted);
        }
    }

    @Override
    public void notifyAssignRoleMessage(MessageDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getRecipient())) {
            sendRealTimeNotificationToHotel(message.getRecipient().getUserProfileId(), PushTask.PUSH_TO_HOTEL_ASSIGN_ROLE_MESSAGE, message, isCounted);
        }
    }

    @Override
    public void notifyVacancyMessage(List<MessageDoc> messages, boolean isCounted) {
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(message -> {
                if (Objects.nonNull(message.getRecipient())) {
                    sendRealTimeNotificationToCareer(message.getRecipient().getUserProfileId(), PushTask.PUSH_TO_CAREER_VACANCY_MESSAGE, message, isCounted);
                }
            });
        }
    }

    @Override
    public void notifyChangedContactPersonMessage(List<MessageDoc> messages, boolean isCounted) {
        if (CollectionUtils.isNotEmpty(messages)) {
            messages.forEach(message -> {
                if (Objects.nonNull(message.getRecipient())) {
                    sendRealTimeNotificationToCareer(message.getRecipient().getUserProfileId(), PushTask.PUSH_TO_CAREER_CHANGE_CONTACT_MESSAGE, message, isCounted);
                }
            });
        }
    }

    @Override
    public void notifyCompanyApproval(Company company, boolean isCounted) {
        if (Objects.nonNull(company)) {
            sendRealTimeNotificationToHotel(company.getCreatedBy(), PushTask.PUSH_TO_HOTEL_ASSIGN_ROLE_MESSAGE, company, isCounted);
        }
    }

    @Override
    public void notifyNewMessage(MessageBase message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getRecipient())) {
            sendRealTimeNotification(
                    message.getRecipient().getUserProfileId(),
                    getTargetAppForChatMessage(message),
                    PushTask.PUSH_CHAT_NEW_MESSAGE,
                    message, isCounted);
        }
    }

    @Override
    public void notifyBoostHelperMessage(MessageDoc message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getRecipient())) {
            sendRealTimeNotification(
                    message.getRecipient().getUserProfileId(),
                    getTargetAppForChatMessage(message),
                    PushTask.PUSH_SEND_BOOST_HELPER_MESSAGE,
                    message, isCounted);
        }
    }

    @Override
    public void notifySendFileMessage(MessageBase message, boolean isCounted) {
        if (Objects.nonNull(message) && Objects.nonNull(message.getRecipient())) {
            sendRealTimeNotification(
                    message.getRecipient().getUserProfileId(),
                    getTargetAppForChatMessage(message),
                    PushTask.PUSH_SEND_FILE_MESSAGE,
                    message, isCounted);
        }
    }

    @Override
    boolean isSilentPush() {
        return silentOnly;
    }

    @Override
    ActorRef getPushNotificationActor() {
        return updaterBaidu;
    }

    @Override
    ActorRef getSocketNotificationActor() {
        return updaterSocket;
    }

    @Override
    int getSendRealTimeNotifyType() {
        return sendRealTimeNotifyType;
    }

    private int getTargetAppForChatMessage(MessageBase doc) {
        int receiveInApp = doc.getReceiveInApp();
        if (receiveInApp == 0) {
            receiveInApp = messageDocActorService.getMessageReceiveInApp(doc);
        }
        return getTargetApp(receiveInApp);
    }
}
