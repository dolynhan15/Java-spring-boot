package com.qooco.boost.threads.notifications.push.impl;

import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.oracle.services.views.ViewAccessTokenRolesService;
import com.qooco.boost.threads.notifications.dto.*;
import com.qooco.boost.threads.notifications.push.NotifyDataService;
import com.qooco.boost.threads.notifications.push.NotifyNewMessageService;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import com.qooco.boost.threads.notifications.requests.PushNotificationMessage;
import com.qooco.boost.threads.services.MessageDocActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotifyNewMessageServiceImpl extends NotifyMessageServiceAbstract implements NotifyNewMessageService {
    @Autowired
    private ViewAccessTokenRolesService viewAccessTokenRolesService;
    @Autowired
    private NotifyDataService notifyDataService;
    @Autowired
    private MessageDocActorService messageDocActorService;

    @Value(ApplicationConstant.BOOST_BAIDU_PUSH_NOTIFICATION_MESSAGE_NOTIFY_EXPIRED)
    private long messageNotifyExpired;

    @Value(ApplicationConstant.BOOST_BAIDU_PUSH_NOTIFICATION_IOS_DEPLOY_STATUS)
    private int deployStatus;

    @Override
    public List<PushNotificationMessage> createPushNotificationMessage(NotifyPushTask pushTask, String locale) {
        switch (pushTask.getPushTask()) {
            case PUSH_TO_CAREER_APPLICANT_MESSAGE:
                return createApplicantMessage(pushTask, locale);
            case PUSH_CHAT_NEW_MESSAGE:
                return createNewMessage(pushTask, locale);
            case PUSH_TO_CAREER_VIEW_CANDIDATE_PROFILE:
                return createViewCandidateProfilePushNotification(pushTask, locale);
            case PUSH_TO_HOTEL_RESPONSE_APPLICANT_MESSAGE:
                return createResponseApplicantMessage(pushTask, locale);
            case PUSH_TO_HOTEL_JOIN_COMPANY_REQUEST_MESSAGE:
                return createJoinCompanyRequestMessage(pushTask, locale);
            case PUSH_TO_HOTEL_APPROVAL_JOIN_COMPANY_REQUEST:
                return createJoinCompanyApproval(pushTask, locale);
            case PUSH_TO_HOTEL_ASSIGN_ROLE_MESSAGE:
                return createAssignRoleMessage(pushTask, locale);
            case PUSH_TO_HOTEL_APPROVE_NEW_COMPANY:
                return createCompanyApprovalNotification(pushTask, locale);
            case PUSH_TO_HOTEL_RESPONSE_APPOINTMENT_MESSAGE:
                return createResponseAppointmentMessage(pushTask, locale);
            case PUSH_TO_CAREER_VACANCY_MESSAGE:
                return createVacancyMessage(pushTask, locale);
            default:
                break;
        }
        return new ArrayList<>();
    }

    private List<PushNotificationMessage> createVacancyMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "Change vacancy status message to career";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            title = getTitle(((PushVacancyMessageDTO) customContent.getData()).getSender());
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createResponseAppointmentMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "Response appointment message to hotel";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            title = getTitle(((PushAppointmentDetailResponseDTO) customContent.getData()).getSender());
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createCompanyApprovalNotification(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "Your company is approved by system";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createApplicantMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "You are invited to reference a job";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (customContent.getData() instanceof PushVacancyMessageDTO) {
            title = getTitle(((PushVacancyMessageDTO) customContent.getData()).getSender());
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createJoinCompanyRequestMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "Request to join company";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            title = getTitle(((PushJoinCompanyRequestDTO) customContent.getData()).getSender());
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createJoinCompanyApproval(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            title = getTitle(((PushJoinCompanyApprovalDTO) customContent.getData()).getSender());
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createAssignRoleMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            title = getTitle(((PushAssignRoleDTO) customContent.getData()).getSender());
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createNewMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            MessageDoc messageDoc = (MessageDoc) pushTask.getContent();
            if (messageDoc.getReceiveInApp() == 0) {
                messageDoc.setReceiveInApp(messageDocActorService.getMessageReceiveInApp(messageDoc));
            }
            pushTask.setTargetApp(getTargetApp(messageDoc.getReceiveInApp()));
            title = getTitle(((PushMessageDTO) customContent.getData()).getSender());
            decs = ((PushMessageDTO) customContent.getData()).getContent();
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createResponseApplicantMessage(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        if (pushTask.getContent() instanceof MessageDoc) {
            PushApplicantResponseDTO pushApplicantResponseDTO = (PushApplicantResponseDTO) customContent.getData();
            title = getTitle(pushApplicantResponseDTO.getSender());
            if (pushApplicantResponseDTO.getResponseStatus() == MessageConstants.APPLIED_STATUS_INTERESTED) {
                //TODO: Do localization for all push test
                decs = "I am interested";
            } else if (pushApplicantResponseDTO.getResponseStatus() == MessageConstants.APPLIED_STATUS_NOT_INTERESTED) {
                decs = "I am not interested";
            }
        }
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    private List<PushNotificationMessage> createViewCandidateProfilePushNotification(NotifyPushTask pushTask, String locale) {
        String title = "";
        String decs = "Your profile is viewed by a company";
        NotifyData customContent = notifyDataService.createNotifyData(pushTask, locale);
        return createPushNotificationMessageForSingle(pushTask, title, decs, customContent);
    }

    @Override
    protected long getMessageNotifyExpired() {
        return messageNotifyExpired;
    }

    @Override
    protected int getDeployStatus() {
        return deployStatus;
    }

    @Override
    protected ViewAccessTokenRolesService getViewAccessTokenRolesService() {
        return viewAccessTokenRolesService;
    }

    private int getTargetApp(int receiveInApp) {
        int target = Const.PushTarget.PUSH_TO_HOTEL;
        if ((receiveInApp == MessageConstants.RECEIVE_IN_CAREER_APP)) {
            target = Const.PushTarget.PUSH_TO_CAREER;
        }
        return target;
    }

}
