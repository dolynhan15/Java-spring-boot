package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.google.common.collect.Lists;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.enumeration.AppointmentStatus;
import com.qooco.boost.data.enumeration.ContactPersonStatus;
import com.qooco.boost.data.mongo.embedded.StaffEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.data.mongo.entities.*;
import com.qooco.boost.data.mongo.services.*;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.data.oracle.services.UserFitService;
import com.qooco.boost.threads.models.messages.AppliedVacancyMessage;
import com.qooco.boost.threads.models.messages.ChangeContactPersonApplicantMessage;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.threads.services.MessageDocActorService;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SendAppliedMessageActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SendAppliedMessageActor.class);
    public static final String ACTOR_NAME = "sendAppliedMessageActor";

    private MessageCenterDocService messageCenterDocService;
    private ConversationDocService conversationDocService;
    private MessageDocService messageDocService;
    private UserFitService userFitService;
    private UserCvDocService userCvDocService;
    private VacancyDocService vacancyDocService;
    private PushNotificationService pushNotificationService;
    private MessageDocActorService messageDocActorService;

    public SendAppliedMessageActor(MessageCenterDocService messageCenterDocService,
                                   ConversationDocService conversationDocService,
                                   MessageDocService messageDocService,
                                   UserFitService userFitService,
                                   UserCvDocService userCvDocService,
                                   PushNotificationService pushNotificationService,
                                   VacancyDocService vacancyDocService,
                                   MessageDocActorService messageDocActorService) {
        this.messageCenterDocService = messageCenterDocService;
        this.conversationDocService = conversationDocService;
        this.messageDocService = messageDocService;
        this.userFitService = userFitService;
        this.userCvDocService = userCvDocService;
        this.pushNotificationService = pushNotificationService;
        this.vacancyDocService = vacancyDocService;
        this.messageDocActorService = messageDocActorService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof AppliedVacancyMessage) {
            sendApplicantMessage((AppliedVacancyMessage) message);
        } else if (message instanceof ChangeContactPersonApplicantMessage) {
            sendChangeContactPersonApplicantMessage((ChangeContactPersonApplicantMessage) message);
        }
    }

    private void sendChangeContactPersonApplicantMessage(ChangeContactPersonApplicantMessage message) {
        Set<MessageDoc> applicantMessageResend = getApplicantMessageNeedResend(message.getOldContactPerson(), message.getVacancyIds());
        if (CollectionUtils.isNotEmpty(applicantMessageResend)) {
            UserProfileCvEmbedded senderCv = new UserProfileCvEmbedded(message.getTargetContactPerson().getUserProfile());
            //It make slow but changing Admin, Head-Recruiter, Recruiter -> Analyst, Unassigned rarely to be used
            applicantMessageResend.forEach(ap -> {
                MessageCenterDoc messageCenterDoc = saveMessageCenterDoc(senderCv, ap.getRecipient(), ap.getAppliedMessage().getVacancy());
                if (Objects.nonNull(messageCenterDoc)) {
                    ConversationDoc conversationDoc = conversationDocService.save(messageCenterDoc, senderCv, ap.getRecipient(), ContactPersonStatus.NEW.getCode());
                    MessageDoc messageDoc = saveMessageDocWithApplicantMessage(conversationDoc, ap.getAppliedMessage(), senderCv, ap.getRecipient());

                    pushNotificationService.notifyApplicantMessage(messageDoc, true);
                    logger.info(StringUtil.append("Save applied message Id =", messageDoc.getId().toHexString()));
                }
            });
            List<ObjectId> messageIds = applicantMessageResend.stream().map(MessageDoc::getId).distinct().collect(Collectors.toList());
            messageDocService.updateApplicantAvailable(messageIds, false);

            List<ObjectId> conversationIds = applicantMessageResend.stream().map(MessageDoc::getConversationId).distinct().collect(Collectors.toList());
            messageDocActorService.saveChangedContactPersonMessageByConversation(conversationIds);
        }

        //DO change contact person
        if (CollectionUtils.isNotEmpty(message.getVacancyIds()) && Objects.nonNull(message.getTargetContactPerson())) {
            vacancyDocService.updateContactPerson(message.getVacancyIds(), message.getTargetContactPerson());
        }
    }

    private Set<MessageDoc> getApplicantMessageNeedResend(StaffEmbedded oldStaff, List<Long> applicantVacancyIds) {
        Long contactUserProfileId = oldStaff.getUserProfile().getUserProfileId();
        //If candidate dont have appointment in a vacancy => Need send applicant again with new contact
        List<Integer> responseStatus = Lists.newArrayList(MessageConstants.APPLIED_STATUS_PENDING, MessageConstants.APPLIED_STATUS_INTERESTED, MessageConstants.APPLIED_STATUS_NOT_INTERESTED);
        List<MessageDoc> applicantMessages = messageDocService.findApplicantMessageBySenderAndTypeAndVacancy(contactUserProfileId, applicantVacancyIds, responseStatus);
        Map<Long, Set<Long>> recipientMap = new HashMap<>();

        applicantMessages.forEach(ap -> {
            Set<Long> vacancyIdSet = recipientMap.get(ap.getRecipient().getUserProfileId());
            if (Objects.nonNull(ap.getAppliedMessage())) {
                if (Objects.isNull(vacancyIdSet)) {
                    vacancyIdSet = new HashSet<>();
                }
                vacancyIdSet.add(ap.getAppliedMessage().getVacancy().getId());
                recipientMap.put(ap.getRecipient().getUserProfileId(), vacancyIdSet);
            }
        });

        Map<Long, List<Long>> recipientHaveAppointmentMap = new HashMap<>();
        recipientMap.forEach((recipientId, vacancyIds) -> {
            List<Long> result = messageDocService.getVacancyHaveAppointmentDetailByRecipient(recipientId, new ArrayList<>(vacancyIds), AppointmentStatus.getAvailableStatus());
            if (CollectionUtils.isNotEmpty(result)) {
                recipientHaveAppointmentMap.put(recipientId, result);
            }
        });

        //If recipient have appointment message -> remove it
        Set<MessageDoc> applicantMessageResend = new HashSet<>(applicantMessages);
        applicantMessages.forEach(ap -> {
            Long candidateId = ap.getRecipient().getUserProfileId();
            Long vacancyId = ap.getAppliedMessage().getVacancy().getId();
            for (Map.Entry<Long, List<Long>> entry : recipientHaveAppointmentMap.entrySet()) {
                Long recipientId = entry.getKey();
                List<Long> vacancyIds = entry.getValue();
                if (recipientId.equals(candidateId) && vacancyIds.contains(vacancyId)) {
                    applicantMessageResend.remove(ap);
                    break;
                }
            }
        });
        return applicantMessageResend;
    }

    private void sendApplicantMessage(AppliedVacancyMessage message) {
        Vacancy vacancy = message.getVacancy();
        VacancyDoc vacancyDoc = vacancyDocService.findById(vacancy.getId());
        UserFit sender = userFitService.findById(message.getSender().getUserProfileId());
        UserCvDoc recipient = userCvDocService.findByUserProfileId(message.getRecipient().getUserProfileId());

        UserProfileCvEmbedded senderCv = MongoConverters.convertToUserProfileCvEmbedded(sender);
        UserProfileCvEmbedded recipientCv = MongoConverters.convertToUserProfileCvEmbedded(recipient);
        VacancyEmbedded vacancyEmbedded = MongoConverters.convertToVacancyEmbedded(vacancyDoc);

        if (Objects.nonNull(sender) && Objects.nonNull(recipient) && Objects.nonNull(vacancyDoc)) {
            MessageCenterDoc messageCenterDoc = saveMessageCenterDoc(senderCv, recipientCv, vacancyEmbedded);
            if (Objects.nonNull(messageCenterDoc)) {
                ConversationDoc conversationDoc = conversationDocService.save(messageCenterDoc, senderCv, recipientCv);
                MessageDoc messageDoc = saveMessageDoc(conversationDoc, vacancyEmbedded, senderCv, recipientCv);
                pushNotificationService.notifyApplicantMessage(messageDoc, true);

                logger.info(StringUtil.append("Save applied message Id =", messageDoc.getId().toHexString()));
            }
        }
    }

    private MessageCenterDoc saveMessageCenterDoc(UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv, VacancyEmbedded vacancy) {

        UserProfileCvMessageEmbedded senderCvMessage = new UserProfileCvMessageEmbedded(senderCv);
        UserProfileCvMessageEmbedded recipientCvMessage = new UserProfileCvMessageEmbedded(recipientCv);

        if (Objects.nonNull(senderCv) && Objects.nonNull(recipientCv) && Objects.nonNull(vacancy)) {
            var messageCenterDoc = messageCenterDocService.findByVacancy(vacancy.getId());

            List<UserProfileCvMessageEmbedded> contactUsers = new ArrayList<>();
            List<UserProfileCvMessageEmbedded> appliedUsers = new ArrayList<>();
            if (Objects.nonNull(messageCenterDoc)) {
                ofNullable(messageCenterDoc.getAppliedUserProfiles()).filter(CollectionUtils::isNotEmpty).ifPresent(appliedUsers::addAll);
                ofNullable(messageCenterDoc.getContactPersons()).filter(CollectionUtils::isNotEmpty).ifPresent(contactUsers::addAll);

                if(appliedUsers.stream().noneMatch(appliedUser -> appliedUser.getUserProfileId().equals(recipientCv.getUserProfileId()))){
                    appliedUsers.add(recipientCvMessage);
                }

                if (contactUsers.stream().noneMatch(contactUser -> senderCv.getUserProfileId().equals(contactUser.getUserProfileId()))) {
                    contactUsers.add(senderCvMessage);
                }
            } else {
                messageCenterDoc = new MessageCenterDoc(vacancy);
                messageCenterDoc.setCreatedBy(senderCv);
                appliedUsers.add(recipientCvMessage);
                contactUsers.add(senderCvMessage);
            }

            messageCenterDoc.setAppliedUserProfiles(appliedUsers);
            messageCenterDoc.setContactPersons(contactUsers);
            return messageCenterDocService.save(messageCenterDoc);
        }
        return null;
    }

    private MessageDoc saveMessageDoc(ConversationDoc conversationDoc,
                                      VacancyEmbedded vacancy,
                                      UserProfileCvEmbedded senderCv,
                                      UserProfileCvEmbedded recipientCv) {
        AppliedMessage appliedMessage = new AppliedMessage(vacancy);
        MessageDoc messageDoc = new MessageDoc.MessageDocBuilder(senderCv, recipientCv, conversationDoc)
                .withApplicantMessage(appliedMessage).build();
        return messageDocService.save(messageDoc, true);
    }


    private MessageDoc saveMessageDocWithApplicantMessage(ConversationDoc conversationDoc,
                                                          AppliedMessage appliedMessage,
                                                          UserProfileCvEmbedded senderCv,
                                                          UserProfileCvEmbedded recipientCv) {
        AppliedMessage appliedMsg = new AppliedMessage(appliedMessage);
        MessageDoc messageDoc = new MessageDoc.MessageDocBuilder(senderCv, recipientCv, conversationDoc)
                .withApplicantMessage(appliedMsg).withIsForNewContact(true).build();
        return messageDocService.save(messageDoc, true);
    }
}