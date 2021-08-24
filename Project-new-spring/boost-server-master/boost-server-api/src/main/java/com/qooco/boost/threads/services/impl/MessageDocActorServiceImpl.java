package com.qooco.boost.threads.services.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.constants.Const;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.embedded.message.AppointmentDetailMessage;
import com.qooco.boost.data.mongo.entities.*;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.services.*;
import com.qooco.boost.data.oracle.entities.AppointmentDetail;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.entities.VacancyCandidate;
import com.qooco.boost.data.oracle.services.VacancyCandidateService;
import com.qooco.boost.models.dto.message.MessageDTO;
import com.qooco.boost.models.sdo.VacancyCandidateSDO;
import com.qooco.boost.socket.services.SendMessageToClientService;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.MessageCenterDocActorService;
import com.qooco.boost.threads.services.MessageDocActorService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.Const.Vacancy.CandidateStatus.RECRUITED;

@Service
public class MessageDocActorServiceImpl implements MessageDocActorService {
    protected Logger logger = LogManager.getLogger(MessageDocActorServiceImpl.class);
    @Autowired
    private MessageCenterDocActorService messageCenterDocActorService;
    @Autowired
    private MessageCenterDocService messageCenterDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private SupportMessageDocService supportMessageDocService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private SendMessageToClientService sendMessageToClientService;
    @Autowired
    private PushNotificationService pushNotificationService;
    @Autowired
    private VacancyCandidateService vacancyCandidateService;
    @Autowired
    private UserCvDocService userCvDocService;

    @Value(ApplicationConstant.BOOST_PATA_QOOCO_DOMAIN_PATH)
    private String qoocoDomainPath = "";

    @Override
    public int getMessageReceiveInApp(MessageBase message) {
        if (message.getReceiveInApp() != 0) {
            return message.getReceiveInApp();
        }
        return messageCenterDocActorService.getReceiveAppTypeByMessageCenter(message);
    }

    @Override
    public void updateReceiveInApp(MessageBase message) {
        int recipientInApp = messageCenterDocActorService.getReceiveAppTypeByMessageCenter(message);
        message.setReceiveInApp(recipientInApp);
        if(message instanceof  MessageDoc){
            messageDocService.save((MessageDoc) message, false);
        }else if (message instanceof SupportMessageDoc){
            supportMessageDocService.save((SupportMessageDoc) message, false);
        }
    }

    @Override
    public List<MessageDoc> saveMessageForAppointmentDetails(List<AppointmentDetailDoc> docs) {
        return docs.stream().flatMap(it -> saveMessageForAppointmentDetail(it).stream()).collect(toImmutableList());
    }

    @Override
    public List<MessageDoc> saveMessageForAppointmentDetail(AppointmentDetailDoc doc) {
        if (Objects.nonNull(doc)) {
            logger.info(StringUtil.append("Save AppointmentDetailDoc to message id = ", doc.getId().toString()));
            AppointmentStatus status = AppointmentStatus.convertFromValue(doc.getStatus());

            MessageDoc messageDoc = messageDocService.findAppointmentDetailMessage(doc.getId());

            if (Objects.isNull(messageDoc)) {
                return doCreateAndSaveAppointmentMessage(doc);
            }
            // Save appointment detail doc and message doc to Cancel Or Changed in case Update appointment detail
            AppointmentDetailMessage detailMessage = messageDoc.getAppointmentDetailMessage();

            if (Objects.nonNull(status)) {
                if (status == AppointmentStatus.CANCELED || status == AppointmentStatus.CHANGED) {
                    return doUpdateAndCreateCanceledAppointmentMessage(doc, messageDoc);
                }

                if (AppointmentStatus.ACCEPTED.getValue() == status.getValue()) {
                    detailMessage.setResponseStatus(MessageConstants.APPOINTMENT_STATUS_ACCEPTED);
                    detailMessage.setStatus(MessageConstants.MESSAGE_STATUS_SENT);
                    messageDoc.setStatus(MessageConstants.MESSAGE_STATUS_SEEN);
                }
                detailMessage.setAppointmentDetailStatus(status.getValue());
                detailMessage.setAppointment(doc.getAppointment());
                detailMessage.setAppointmentTime(doc.getAppointmentTime());
                messageDoc.setAppointmentDetailMessage(detailMessage);


                messageDoc = messageDocService.save(messageDoc, true);
                logger.info(StringUtil.append("Do createConversation AppointmentDetailDoc id = ", doc.getId().toString(), " in message id = ", messageDoc.getId().toHexString()));
                return Lists.newArrayList(messageDoc);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<MessageDoc> saveChangedContactPersonMessage(List<AppointmentDetail> appointmentDetails) {
        if (CollectionUtils.isNotEmpty(appointmentDetails)) {
            List<Long> appointmentDetailIds = appointmentDetails.stream().map(AppointmentDetail::getId).collect(Collectors.toList());
            List<ObjectIdCount> countMessages = messageDocService.countMessageGroupByConversation(appointmentDetailIds);
            List<ObjectId> conversationIds = countMessages.stream().map(ObjectIdCount::getId).distinct().collect(Collectors.toList());
            return saveChangedContactPersonMessageByConversation(conversationIds);
        }
        return ImmutableList.of();
    }

    @Override
    public List<MessageDoc> saveChangedContactPersonMessageByConversation(List<ObjectId> conversationIds) {
        List<ConversationDoc> conversationDocs = Optional.of(conversationIds)
                .filter(CollectionUtils::isNotEmpty)
                .map(conversationDocService::findAllById)
                .orElseGet(ImmutableList::of);

        if (CollectionUtils.isNotEmpty(conversationDocs)) {
            List<MessageDoc> result = conversationIds.stream()
                    .map(it -> conversationDocs.stream().filter(con -> it.equals(con.getId())).findFirst().orElse(null))
                    .filter(Objects::nonNull)
                    .flatMap(conversationDoc -> Stream.of(conversationDoc.getPartner(conversationDoc.getCreatedBy().getUserProfileId()))
                            .filter(Objects::nonNull)
                            .map(recipient -> new MessageDoc.MessageDocBuilder(conversationDoc.getCreatedBy(), recipient, conversationDoc).withChangeContactMessage().build()))
                    .collect(toImmutableList());

            result = messageDocService.save(result);
            result.stream()
                    .peek(msg -> logger.info(StringUtil.append("Send changed contact person to candidate with Id =", msg.getId().toString())))
                    .forEach(it -> sendMessageToClientService.sendMessage(it, null));
            pushNotificationService.notifyChangedContactPersonMessage(result, true);
            return result;
        }
        return ImmutableList.of();
    }

    @Override
    public List<MessageDoc> saveMessageForSuspendedOrInactiveVacancy(VacancyDoc vacancyDoc, int reason, String locale) {
        if (Objects.nonNull(vacancyDoc)) {
            MessageCenterDoc messageCenterDoc = messageCenterDocService.findByVacancy(vacancyDoc.getId());
            if (Objects.nonNull(messageCenterDoc)) {
                List<ConversationDoc> conversationDocs = conversationDocService.findByMessageCenterId(messageCenterDoc.getId());
                if (CollectionUtils.isNotEmpty(conversationDocs)) {
                    List<VacancyCandidate> recruitedCandidates = vacancyCandidateService.findByVacancyAndStatus(vacancyDoc.getId(), ImmutableList.of(RECRUITED));
                    List<Long> closedUserIds = recruitedCandidates.stream().map(vc -> vc.getCandidate().getUserProfile().getUserProfileId()).collect(Collectors.toList());

                    List<MessageDoc> messageDocs = new ArrayList<>();
                    List<ObjectId> conversationIdsToLock = new ArrayList<>();
                    conversationDocs.forEach(conversationDoc -> {
                        UserProfileCvEmbedded senderCv = conversationDoc.getParticipants().get(0);
                        UserProfileCvEmbedded recipientCv = conversationDoc.getParticipants().get(1);
                        if (CollectionUtils.isEmpty(closedUserIds) || !closedUserIds.contains(recipientCv.getUserProfileId())) {
                            messageDocs.add(createMessageForSuspendedOrInactiveVacancy(vacancyDoc, conversationDoc, senderCv, recipientCv, reason));
                            conversationIdsToLock.add(conversationDoc.getId());
                        }
                    });

                    List<MessageDoc> result = messageDocService.save(messageDocs);
                    if (reason == Const.Vacancy.CancelAppointmentReason.INACTIVE) {
                        conversationDocService.setDisableConversations(conversationIdsToLock, true);
                    }
                    sendMessageSuspendVacancy(result, locale);
                    return result;
                }
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<MessageDoc> saveMessageForRecruitedVacancyCandidate(VacancyCandidateSDO vacancyCandidate) {
        Vacancy vacancy = vacancyCandidate.getVacancy();
        Long candidateId = vacancyCandidate.getCandidateId();
        List<MessageDoc> savedMessages = new ArrayList<>();
        if (Objects.nonNull(vacancy)) {
            MessageCenterDoc messageCenterDoc = messageCenterDocService.findByVacancy(vacancy.getId());
            UserCvDoc userCvDoc = userCvDocService.findById(candidateId);
            if (Objects.nonNull(messageCenterDoc) && Objects.nonNull(userCvDoc)) {
                List<ConversationDoc> conversationDocs = conversationDocService.findByMessageCenterIdAndUserProfileId(
                        messageCenterDoc.getId(), userCvDoc.getUserProfile().getUserProfileId());
                if (CollectionUtils.isNotEmpty(conversationDocs)) {
                    List<MessageDoc> messageDocs = conversationDocs.stream()
                            .map(conversationDoc -> new MessageDoc.MessageDocBuilder(
                                    conversationDoc.getPartner(userCvDoc.getUserProfile().getUserProfileId()),
                                    conversationDoc.getParticipant(userCvDoc.getUserProfile().getUserProfileId()),
                                    conversationDoc).withCongratulationMessage(new VacancyEmbedded(vacancy)).build())
                            .collect(Collectors.toList());

                    messageDocs = messageDocService.save(messageDocs);
                    if (Objects.nonNull(messageDocs)) {
                        savedMessages.addAll(messageDocs);
                        messageDocs.stream()
                                .peek(msg -> logger.info(StringUtil.append("Send Congratulation message to candidate with Id =", msg.getId().toString())))
                                .forEach(it -> sendMessageToClientService.sendMessage(it, null));
                    }
                }
            }
        }
        return savedMessages;
    }

    private List<MessageDoc> doCreateAndSaveAppointmentMessage(AppointmentDetailDoc doc) {
        return doCreateAndSaveAppointmentMessage(doc, MessageConstants.APPOINTMENT_MESSAGE, AppointmentStatus.PENDING.getValue(), MessageConstants.APPOINTMENT_STATUS_PENDING);
    }

    private List<MessageDoc> doUpdateAndCreateCanceledAppointmentMessage(AppointmentDetailDoc doc, MessageDoc messageDoc) {
        AppointmentDetailMessage detailMessage = messageDoc.getAppointmentDetailMessage();

        detailMessage.setAppointmentDetailStatus(AppointmentStatus.CANCELED.getValue());
        messageDoc.setAppointmentDetailMessage(detailMessage);
        //If profile don't have action on canceled appointment => delete it
        messageDoc.setDeleted(detailMessage.getResponseStatus() == AppointmentStatus.PENDING.getValue());
        messageDoc = messageDocService.save(messageDoc, true);
        List<MessageDoc> result = Lists.newArrayList(messageDoc);
        logger.info(StringUtil.append("Do createConversation AppointmentDetailDoc id = ", doc.getId().toString(), " in message id = ", messageDoc.getId().toHexString()));

        List<MessageDoc> messageNews = doCreateAndSaveAppointmentMessage(doc, MessageConstants.APPOINTMENT_CANCEL_MESSAGE, detailMessage.getAppointmentDetailStatus(), detailMessage.getResponseStatus());
        if (CollectionUtils.isNotEmpty(messageNews)) {
            result.addAll(messageNews);
        }
        return result;
    }

    private List<MessageDoc> doCreateAndSaveAppointmentMessage(AppointmentDetailDoc doc, int messageType, int detailStatus, int responseStatus) {
        UserProfileCvEmbedded senderCv = new UserProfileCvEmbedded(doc.getAppointment().getManager().getUserProfile());
        UserProfileCvEmbedded recipientCv = doc.getCandidate();


        List<Integer> status = Lists.newArrayList(MessageConstants.APPLICANT_MESSAGE, MessageConstants.APPOINTMENT_APPLICANT_MESSAGE);
        MessageCenterDoc messageCenterDoc = messageCenterDocActorService.saveForSendAppointment(doc.getVacancy(), senderCv, recipientCv);
        ConversationDoc conversationDoc = conversationDocService.save(messageCenterDoc, senderCv, recipientCv);
        MessageDoc exists = messageDocService.findByConversationIdAndTypes(conversationDoc.getId(), status);

        MessageDoc applicationMessage = null;
        List<MessageDoc> messageDocs = new ArrayList<>();
        if (Objects.isNull(exists)) {
            applicationMessage = createAppointmentApplicantMessage(conversationDoc, doc.getVacancy(), senderCv, recipientCv);
            messageDocs.add(applicationMessage);
        }

        MessageDoc messageDoc = createMessageWithAppointmentDetail(conversationDoc, doc, messageType, detailStatus, responseStatus, senderCv, recipientCv);
        if (Objects.nonNull(applicationMessage)) {
            messageDoc.getCreatedDate().setTime(applicationMessage.getCreatedDate().getTime() + 1);
            messageDoc.getUpdatedDate().setTime(applicationMessage.getUpdatedDate().getTime() + 1);
        }
        messageDocs.add(messageDoc);

        messageDocs = messageDocService.save(messageDocs);
        return messageDocs;
    }

    private MessageDoc createAppointmentApplicantMessage(ConversationDoc conversationDoc, VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        AppliedMessage appliedMessage = new AppliedMessage(vacancy);
        appliedMessage.setResponseStatus(MessageConstants.APPLIED_STATUS_INTERESTED);
        return new MessageDoc.MessageDocBuilder(senderCv, recipientCv, conversationDoc)
                .withApplicantAppointmentMessage(appliedMessage).build();
    }

    private MessageDoc createMessageWithAppointmentDetail(ConversationDoc conversationDoc, AppointmentDetailDoc doc, int messageType,
                                                          int detailStatus, int responseStatus, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {

        AppointmentDetailMessage detailMessage = new AppointmentDetailMessage();
        detailMessage.setId(doc.getId());
        detailMessage.setVacancy(doc.getVacancy());
        detailMessage.setAppointment(doc.getAppointment());
        detailMessage.setAppointmentTime(doc.getAppointmentTime());
        detailMessage.setAppointmentDetailStatus(detailStatus);
        detailMessage.setResponseStatus(responseStatus);
        detailMessage.setStatus(MessageConstants.MESSAGE_STATUS_SENT);

        if (messageType == MessageConstants.APPOINTMENT_MESSAGE) {
            return new MessageDoc.MessageDocBuilder(senderCv, recipientCv, conversationDoc)
                    .withAppointmentMessage(detailMessage).build();
        }
        return new MessageDoc.MessageDocBuilder(senderCv, recipientCv, conversationDoc)
                .withCancelAppointmentMessage(detailMessage).build();
    }

    private MessageDoc createMessageForSuspendedOrInactiveVacancy(VacancyDoc vacancyDoc,
                                                                  ConversationDoc conversationDoc,
                                                                  UserProfileCvEmbedded senderCv,
                                                                  UserProfileCvEmbedded recipientCv,
                                                                  int reason) {
        VacancyEmbedded vacancyEmbedded = MongoConverters.convertToVacancyEmbedded(vacancyDoc);
        MessageDoc.MessageDocBuilder messageDocBuilder = new MessageDoc.MessageDocBuilder(senderCv, recipientCv, conversationDoc);
        if (reason == Const.Vacancy.CancelAppointmentReason.INACTIVE) {
            messageDocBuilder.withInactiveVacancyMessage(vacancyEmbedded);
        } else {
            messageDocBuilder.withSuspendedVacancyMessage(vacancyEmbedded);
        }
        return messageDocBuilder.build();
    }

    private void sendMessageSuspendVacancy(List<MessageDoc> docs, String locale) {
        //Send message to conversation via socket
        List<MessageDTO> messageDTOs = new ArrayList<>();
        docs.forEach(d -> messageDTOs.add(new MessageDTO(d, MessageConstants.SUBMIT_MESSAGE_ACTION, qoocoDomainPath, locale)));
        sendMessageToClientService.sendMessage(messageDTOs);
        pushNotificationService.notifyVacancyMessage(docs, true);

        // TODO Send push to enable/disable chat.
    }

}
