package com.qooco.boost.threads.notifications.push.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.constants.Const;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.*;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.SupportMessageDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.UserFitService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.threads.notifications.dto.*;
import com.qooco.boost.threads.notifications.enumeration.PushTask;
import com.qooco.boost.threads.notifications.enumeration.PushType;
import com.qooco.boost.threads.notifications.push.NotifyDataService;
import com.qooco.boost.threads.notifications.requests.NotifyPushTask;
import com.qooco.boost.threads.services.MessageDocActorService;
import com.qooco.boost.threads.services.UserProfileActorService;
import com.qooco.boost.utils.MongoConverters;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

@Service
@RequiredArgsConstructor
public class NotifyDataServiceImpl implements NotifyDataService {
    private final UserProfileService userProfileService;
    private final UserFitService userFitService;
    private final UserCvDocService userCvDocService;
    private final UserProfileActorService userProfileActorService;
    private final MessageDocService messageDocService;
    private final SupportMessageDocService supportMessageDocService;
    private final MessageDocActorService messageDocActorService;

    @Override
    public NotifyData createNotifyData(NotifyPushTask pushTask, String locale) {
        Object pushData = pushTask.getContent();
        if (pushData instanceof MessageBase) {
            return (getPushDataForMessage(pushTask, (MessageBase) pushData, locale));
        }

        if (pushData instanceof ViewProfileDoc) {
            return (getPushDataForViewProfile(pushTask, (ViewProfileDoc) pushData));
        }

        if (pushData instanceof Company) {
            return (getPushDataForCompany(pushTask, (Company) pushData));
        }
        return null;
    }

    @Override
    public List<NotifyData> createNotifiesData(NotifyPushTask pushTask, String locale) {
        Object pushData = pushTask.getContent();
        if (pushData instanceof List && CollectionUtils.isNotEmpty((List) pushData) && ((List) pushData).get(0) instanceof MessageBase) {
            return getPushDataForMessages(pushTask, (List<MessageBase>) pushData, locale);
        }
        return Lists.newArrayList(createNotifyData(pushTask, locale));
    }

    @Override
    public List<NotifyData> createSentMessagesOfUser(AuthenticatedUser authenticatedUser) {
        List<Integer> receiveInApps = Lists.newArrayList(getReceiveInApp(authenticatedUser.getAppId()));
        List<MessageDoc> messageDocs = messageDocService.getSentMessagesByUser(authenticatedUser.getId(), Lists.newArrayList(receiveInApps));
        List<SupportMessageDoc> supportMessageDocs = supportMessageDocService.getSentMessagesByUser(authenticatedUser.getId(), Lists.newArrayList(receiveInApps));

        List<MessageBase> messages =  new ArrayList<>();
        Optional.ofNullable(messageDocs).filter(CollectionUtils::isNotEmpty).ifPresent(messages::addAll);
        Optional.ofNullable(supportMessageDocs).filter(CollectionUtils::isNotEmpty).ifPresent(messages::addAll);

        List<NotifyData> notifyPushTasks = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        userIds.add(authenticatedUser.getId());
        messages.forEach(messageDoc -> {
            NotifyPushTask task = new NotifyPushTask();
            switch (messageDoc.getType()) {
                case MessageConstants.TEXT_MESSAGE:
                    task.setPushTask(PushTask.PUSH_CHAT_NEW_MESSAGE);
                    break;
                case MessageConstants.MEDIA_MESSAGE:
                    task.setPushTask(PushTask.PUSH_SEND_FILE_MESSAGE);
                    break;
                case MessageConstants.APPLICANT_MESSAGE:
                    if (messageDoc.getStatus() == MessageConstants.MESSAGE_STATUS_SENT) {
                        task.setPushTask(PushTask.PUSH_TO_CAREER_APPLICANT_MESSAGE);
                    } else {
                        task.setPushTask(PushTask.PUSH_TO_HOTEL_RESPONSE_APPLICANT_MESSAGE);
                    }
                    break;
                case MessageConstants.APPOINTMENT_MESSAGE:
                    if (messageDoc.getStatus() == MessageConstants.MESSAGE_STATUS_SENT) {
                        task.setPushTask(PushTask.PUSH_APPOINTMENT_MESSAGE);
                    } else {
                        task.setPushTask(PushTask.PUSH_TO_HOTEL_RESPONSE_APPOINTMENT_MESSAGE);
                    }
                    break;
                case MessageConstants.AUTHORIZATION_MESSAGE:
                    if (messageDoc.getStatus() == MessageConstants.MESSAGE_STATUS_SENT) {
                        task.setPushTask(PushTask.PUSH_TO_HOTEL_JOIN_COMPANY_REQUEST_MESSAGE);
                    } else {
                        task.setPushTask(PushTask.PUSH_TO_HOTEL_APPROVAL_JOIN_COMPANY_REQUEST);
                    }
                    break;
                case MessageConstants.ASSIGNMENT_ROLE_MESSAGE:
                    task.setPushTask(PushTask.PUSH_TO_HOTEL_ASSIGN_ROLE_MESSAGE);
                    break;
                case MessageConstants.APPOINTMENT_CANCEL_MESSAGE:
                    task.setPushTask(PushTask.PUSH_APPOINTMENT_MESSAGE);
                    break;
                case MessageConstants.APPOINTMENT_APPLICANT_MESSAGE:
                    task.setPushTask(PushTask.PUSH_APPOINTMENT_APPLICANT_MESSAGE);
                    break;
                case MessageConstants.CONGRATULATION_MESSAGE:
                case MessageConstants.SUSPENDED_VACANCY:
                case MessageConstants.INACTIVE_VACANCY:
                    task.setPushTask(PushTask.PUSH_TO_CAREER_VACANCY_MESSAGE);
                    break;
                case MessageConstants.CHANGE_CONTACT_MESSAGE:
                    task.setPushTask(PushTask.PUSH_TO_CAREER_CHANGE_CONTACT_MESSAGE);
                    break;
                case MessageConstants.BOOST_HELPER_MESSAGE:
                    task.setPushTask(PushTask.PUSH_SEND_BOOST_HELPER_MESSAGE);
                    break;
                default:
                    break;
            }

            int receiveInApp = messageDoc.getReceiveInApp();
            if (receiveInApp == 0) {
                receiveInApp = messageDocActorService.getMessageReceiveInApp(messageDoc);
            }
            task.setTargetApp(receiveInApp);
            task.setPushType(PushType.SILENT);
            task.setContent(messageDoc);
            task.setReceiverIds(userIds);
            task.setCounted(false);
            notifyPushTasks.add(getPushDataForMessage(task, messageDoc, authenticatedUser.getLocale()));
        });
        return notifyPushTasks;
    }

    private NotifyData getPushDataForCompany(NotifyPushTask pushTask, Company company) {
        NotifyData customContent = new NotifyData(pushTask.getPushTask().getValue(), pushTask.isCounted());
        if (pushTask.getPushTask() == PushTask.PUSH_TO_HOTEL_APPROVE_NEW_COMPANY) {
            customContent.setData(new CompanyDTO(company));
        }
        return customContent;
    }

    private NotifyData getPushDataForViewProfile(NotifyPushTask pushTask, ViewProfileDoc doc) {
        NotifyData customContent = new NotifyData(pushTask.getPushTask().getValue(), pushTask.isCounted());
        if (pushTask.getPushTask() == PushTask.PUSH_TO_CAREER_VIEW_CANDIDATE_PROFILE) {
            customContent.setData(null);
        }
        return customContent;
    }

    private List<NotifyData> getPushDataForMessages(NotifyPushTask pushTask, List<MessageBase> messageDoc, String locale) {
        List<NotifyData> customContents = new ArrayList<>();
        if (pushTask.getPushTask() == PushTask.PUSH_APPOINTMENT_MESSAGE) {
            messageDoc.forEach(md -> {
                NotifyData notifyData = new NotifyData(pushTask.getPushTask().getValue(), pushTask.isCounted());
                createNotifyDataForAppointment(md, notifyData, locale);
                notifyData.setTarget(SELECT_APP.appId(), Lists.newArrayList(md.getSender().getUserProfileId()));
                notifyData.setTarget(PROFILE_APP.appId(), Lists.newArrayList(md.getRecipient().getUserProfileId()));
                customContents.add(notifyData);
            });

        }
        return customContents;
    }

    private void createNotifyDataForAppointment(MessageBase md, NotifyData notifyData, String loale) {
        updateSenderForMessageDoc(md);
        //Using Applicant message DTO for Appointment applicant message
        if (md.getType() == MessageConstants.APPOINTMENT_APPLICANT_MESSAGE) {
            notifyData.setType(PushTask.PUSH_APPOINTMENT_APPLICANT_MESSAGE.getValue());
            notifyData.setData(new PushVacancyMessageDTO(md, loale));
        } else {
            notifyData.setData(new PushAppointmentDetailMessageDTO(md, loale));
        }
    }

    private void createNotifyDataForVacancy(MessageBase md, NotifyData notifyData, String locale) {
        updateSenderForMessageDoc(md);
        //Using Applicant message DTO for Appointment applicant message
        if (md.getType() == MessageConstants.SUSPENDED_VACANCY
                || md.getType() == MessageConstants.INACTIVE_VACANCY
                || md.getType() == MessageConstants.CONGRATULATION_MESSAGE) {
            notifyData.setType(PushTask.PUSH_TO_CAREER_VACANCY_MESSAGE.getValue());
        }
        notifyData.setData(new PushVacancyMessageDTO(md, locale));
    }

    private NotifyData getPushDataForMessage(NotifyPushTask pushTask, MessageBase messageDoc, String locale) {
        NotifyData customContent = new NotifyData(pushTask.getPushTask().getValue(), pushTask.isCounted());

        switch (pushTask.getPushTask()) {
            case PUSH_APPOINTMENT_MESSAGE:
            case PUSH_APPOINTMENT_APPLICANT_MESSAGE:
                createNotifyDataForAppointment(messageDoc, customContent, locale);
                pushTask.setReceiverIds(Lists.newArrayList(messageDoc.getRecipient().getUserProfileId()));
                break;
            case PUSH_TO_CAREER_APPLICANT_MESSAGE:
                updateSenderForMessageDoc(messageDoc);
                customContent.setData(new PushVacancyMessageDTO(messageDoc, locale));
                break;
            case PUSH_TO_CAREER_VIEW_CANDIDATE_PROFILE:
                break;
            case PUSH_TO_HOTEL_RESPONSE_APPLICANT_MESSAGE:
                updateRecipientForMessageDoc(messageDoc);
                customContent.setData(new PushApplicantResponseDTO(messageDoc));
                break;
            case PUSH_TO_HOTEL_JOIN_COMPANY_REQUEST_MESSAGE:
                updateSenderForMessageDoc(messageDoc);
                customContent.setData(new PushJoinCompanyRequestDTO(messageDoc));
                break;
            case PUSH_TO_HOTEL_APPROVAL_JOIN_COMPANY_REQUEST:
                updateRecipientForMessageDoc(messageDoc);
                customContent.setData(new PushJoinCompanyApprovalDTO(messageDoc));
                break;
            case PUSH_TO_HOTEL_ASSIGN_ROLE_MESSAGE:
                updateSenderForMessageDoc(messageDoc);
                customContent.setData(new PushAssignRoleDTO(messageDoc, locale));
                break;
            case PUSH_TO_HOTEL_APPROVE_NEW_COMPANY:
                break;
            case PUSH_TO_HOTEL_RESPONSE_APPOINTMENT_MESSAGE:
                updateRecipientForMessageDoc(messageDoc);
                customContent.setData(new PushAppointmentDetailResponseDTO(messageDoc, locale));
                break;
            case PUSH_TO_CAREER_VACANCY_MESSAGE:
                createNotifyDataForVacancy(messageDoc, customContent, locale);
                pushTask.setReceiverIds(Lists.newArrayList(messageDoc.getRecipient().getUserProfileId()));
                break;
            case PUSH_TO_CAREER_CHANGE_CONTACT_MESSAGE:
                pushTask.setReceiverIds(Lists.newArrayList(messageDoc.getRecipient().getUserProfileId()));
                break;
            case PUSH_SEND_BOOST_HELPER_MESSAGE:
                customContent.setData(new PushBoostHelperMessageDTO(messageDoc));
                break;

            case PUSH_CHAT_NEW_MESSAGE:
                updateSenderForMessageDoc(messageDoc);
                customContent.setData(new PushMessageDTO(messageDoc));
                break;
            case PUSH_SEND_FILE_MESSAGE:
                updateSenderForMessageDoc(messageDoc);
                customContent.setData(new PushMediaMessageDTO(messageDoc));
                break;
        }

        customContent.setCounted(pushTask.isCounted());
        customContent.setTarget(getAppId(pushTask.getTargetApp()), pushTask.getReceiverIds());
        return customContent;
    }

    //TODO: Next time shouldn't use this beacause message have enough information
    private MessageBase updateSenderForMessageDoc(MessageBase messageDoc) {
        UserProfileCvMessageEmbedded sender = messageDoc.getSender();
        if (StringUtils.isNotBlank(sender.getUsername())) return messageDoc;

        sender = getUserProfileCvEmbedded(sender);
        if (Objects.nonNull(sender)) {
            messageDoc.setSender(sender);
            messageDocService.updateSender(messageDoc.getId(), sender);
        }
        return messageDoc;
    }

    //TODO: Next time shouldn't use this beacause message have enough information
    private MessageBase updateRecipientForMessageDoc(MessageBase messageDoc) {
        UserProfileCvMessageEmbedded recipient = messageDoc.getRecipient();
        if (StringUtils.isNotBlank(recipient.getUsername())) return messageDoc;

        recipient = getUserProfileCvEmbedded(messageDoc.getRecipient());
        if (Objects.nonNull(recipient)) {
            messageDoc.setRecipient(recipient);
            messageDocService.updateRecipient(messageDoc.getId(), recipient);
        }
        return messageDoc;
    }

    private UserProfileCvMessageEmbedded getUserProfileCvEmbedded(UserProfileCvMessageEmbedded cvEmbedded) {
        UserCvDoc sender = userCvDocService.findByUserProfileId(cvEmbedded.getUserProfileId());
        if (Objects.nonNull(sender)) {
            return MongoConverters.convertToUserProfileCvMessageEmbedded(sender);
        }

        if (UserType.SELECT == cvEmbedded.getUserType()) {
            UserFit userFit = userFitService.findById(cvEmbedded.getUserProfileId());
            userFit = userProfileActorService.updateLazyValue(userFit);
            return MongoConverters.convertToUserProfileCvMessageEmbedded(userFit);
        } else if (UserType.PROFILE == cvEmbedded.getUserType()){
            UserProfile userProfile = userProfileService.findById(cvEmbedded.getUserProfileId());
            userProfile = userProfileActorService.updateLazyValue(userProfile);
            return MongoConverters.convertToUserProfileCvMessageEmbedded(userProfile);
        } else return cvEmbedded;
    }

    private String getAppId(int target) {
        switch (target) {
            case Const.PushTarget.PUSH_TO_HOTEL:
                return SELECT_APP.appId();
            case Const.PushTarget.PUSH_TO_CAREER:
                return PROFILE_APP.appId();
            default:
                return null;
        }
    }

    private int getReceiveInApp(String appId) {
        if(SELECT_APP.appId().equals(appId)){
            return Const.PushTarget.PUSH_TO_HOTEL;
        } else if(PROFILE_APP.appId().equals(appId)){
            return Const.PushTarget.PUSH_TO_CAREER;
        }
        return 0;
    }
}
