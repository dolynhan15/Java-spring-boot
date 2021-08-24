package com.qooco.boost.threads.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.data.enumeration.MessageCenterType;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.threads.services.MessageCenterDocActorService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static com.qooco.boost.data.enumeration.MessageCenterType.*;

@Service
public class MessageCenterDocActorServiceImpl implements MessageCenterDocActorService {
    @Autowired
    private MessageCenterDocService service;

    @Override
    public MessageCenterDoc saveForSendAppointment(VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        var doc = service.findByVacancy(vacancy.getId());
        if (Objects.nonNull(doc)) {
            return updateForSendAppointment(doc, senderCv, recipientCv);
        }
        return createForSendAppointment(vacancy, senderCv, recipientCv);
    }

    @Override
    public MessageCenterDoc updateForSendAppointment(MessageCenterDoc doc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        if (Objects.nonNull(doc) && Objects.nonNull(senderCv) && Objects.nonNull(recipientCv)) {
            var managers = new ArrayList<UserProfileCvMessageEmbedded>();
            var candidates = new ArrayList<UserProfileCvMessageEmbedded>();

            Optional.ofNullable(doc.getAppointmentCandidates()).filter(CollectionUtils::isNotEmpty).ifPresent(candidates::addAll);
            Optional.ofNullable(doc.getAppointmentManagers()).filter(CollectionUtils::isNotEmpty).ifPresent(managers::addAll);

            if (candidates.stream().noneMatch(it -> recipientCv.getUserProfileId().equals(it.getUserProfileId()))) {
                candidates.add(new UserProfileCvMessageEmbedded(recipientCv));
            }

            if (managers.stream().noneMatch(it -> senderCv.getUserProfileId().equals(it.getUserProfileId()))) {
                managers.add(new UserProfileCvMessageEmbedded(senderCv));
            }

            doc.setAppointmentCandidates(candidates);
            doc.setAppointmentManagers(managers);
            service.save(doc);
        }
        return doc;
    }

    @Override
    public MessageCenterDoc createForSendAppointment(VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        if (Objects.nonNull(vacancy) && Objects.nonNull(senderCv) && Objects.nonNull(recipientCv)) {
            List<UserProfileCvMessageEmbedded> managers = Lists.newArrayList(new UserProfileCvMessageEmbedded(senderCv));
            List<UserProfileCvMessageEmbedded> candidates = Lists.newArrayList(new UserProfileCvMessageEmbedded(recipientCv));

            MessageCenterDoc doc = new MessageCenterDoc(vacancy);
            doc.setCreatedBy(senderCv);
            doc.setAppointmentManagers(managers);
            doc.setAppointmentCandidates(candidates);
            return service.save(doc);
        }
        return null;
    }

    @Override
    public MessageCenterDoc createForSendApplicant(VacancyEmbedded vacancy, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        return null;
    }

    @Override
    public MessageCenterDoc updateForSendApplicant(MessageCenterDoc doc, UserProfileCvEmbedded senderCv, UserProfileCvEmbedded recipientCv) {
        return null;
    }

    @Override
    public MessageCenterDoc saveForBoostHelper(BoostHelperEmbedded boostHelperEmbedded, UserProfileCvEmbedded customer) {
        return saveForBoostHelper(boostHelperEmbedded, customer, null, BOOST_HELPER_CONVERSATION);
    }

    @Override
    public MessageCenterDoc saveForBoostHelper(BoostHelperEmbedded boostHelperEmbedded, UserProfileCvEmbedded customer, CompanyEmbedded company, MessageCenterType type) {
        var doc = service.findByTypeAndUserOfBoostHelper(type.value(), customer.getUserProfileId(), customer.getUserType());
        return Objects.nonNull(doc) ? updateForBoostHelper(doc, customer) : createForBoostHelper(boostHelperEmbedded, customer, company, type);
    }

    private MessageCenterDoc createForBoostHelper(BoostHelperEmbedded boostHelperEmbedded, UserProfileCvEmbedded customer, CompanyEmbedded company, MessageCenterType type) {
        if (Objects.nonNull(customer)) {
            var messageCenterDoc = MessageCenterDoc.builder()
                    .boostHelper(boostHelperEmbedded)
                    .boostHelperUser(List.of(new UserProfileCvMessageEmbedded(customer)))
                    .type(type.value())
                    .createdDate(new Date())
                    .updatedDate(new Date())
                    .company(company)
                    .isDeleted(false)
                    .build();
            return service.save(messageCenterDoc);
        }
        return null;
    }

    private MessageCenterDoc updateForBoostHelper(MessageCenterDoc doc, UserProfileCvEmbedded recipientCv) {
        if (Objects.nonNull(recipientCv)) {
            var oldBoostHelperUsers = new ArrayList<UserProfileCvMessageEmbedded>();
            Optional.ofNullable(doc.getBoostHelperUser()).filter(CollectionUtils::isNotEmpty).ifPresent(oldBoostHelperUsers::addAll);
            if (oldBoostHelperUsers.stream().noneMatch(it -> recipientCv.getUserProfileId().equals(it.getUserProfileId()))) {
                oldBoostHelperUsers.add(new UserProfileCvMessageEmbedded(recipientCv));
            }
            doc.setBoostHelperUser(oldBoostHelperUsers);
            service.save(doc);
        }
        return doc;
    }

    @Override
    public int getReceiveAppTypeByMessageCenter(MessageBase message) {
        var mgsCenterDoc = service.findById(message.getMessageCenterId());
        UserProfileCvEmbedded recipient = message.getRecipient();
        if (Objects.nonNull(mgsCenterDoc) && Objects.nonNull(recipient)) {
            Long recipientId = recipient.getUserProfileId();
            int recipientInApp = SELECT_APP.value();
            if (AUTHORIZATION_CONVERSATION.value() == mgsCenterDoc.getType()) {
                return recipientInApp;
            } else if (VACANCY_CONVERSATION.value() == mgsCenterDoc.getType()) {
                //TODO: if the user exist in Contact Person, Appointment Managers  And still exist in Applied User, Appointment Candidates => This case in wrong (always RECEIVE_IN_CAREER_APP).
                if (CollectionUtils.isNotEmpty(mgsCenterDoc.getAppliedUserProfiles()) && mgsCenterDoc.getAppliedUserProfiles().stream().anyMatch(
                        u -> Objects.nonNull(recipientId) && u.getUserProfileId().equals(recipientId))) {
                    return PROFILE_APP.value();
                }
                if (CollectionUtils.isNotEmpty(mgsCenterDoc.getAppointmentCandidates()) && mgsCenterDoc.getAppointmentCandidates().stream().anyMatch(
                        u -> Objects.nonNull(recipientId) && u.getUserProfileId().equals(recipientId))) {
                    return PROFILE_APP.value();
                }
                return recipientInApp;
            }
        }
        return 0;
    }
}
