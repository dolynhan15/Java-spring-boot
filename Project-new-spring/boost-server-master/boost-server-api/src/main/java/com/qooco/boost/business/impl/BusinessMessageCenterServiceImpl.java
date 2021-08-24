package com.qooco.boost.business.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.qooco.boost.business.MessageCenterService;
import com.qooco.boost.constants.Const.Vacancy.Status;
import com.qooco.boost.data.enumeration.MessageCenterType;
import com.qooco.boost.data.model.ObjectLatest;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.entities.base.MessageBase;
import com.qooco.boost.data.mongo.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.dto.message.MessageCenterFullDTO;
import com.qooco.boost.models.response.HistoryMessageCenterFullResp;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.data.constants.ConversationDocConstants.shortConversationKeys;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;
import static com.qooco.boost.data.enumeration.MessageCenterType.AUTHORIZATION_CONVERSATION;
import static com.qooco.boost.data.enumeration.MessageCenterType.VACANCY_CONVERSATION;

@Service
public class BusinessMessageCenterServiceImpl implements MessageCenterService {
    @Autowired
    private MessageCenterDocService messageCenterDocService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private SupportConversationDocService supportConversationDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private SupportMessageDocService supportMessageDocService;

    @Override
    public BaseResp findByMessageCenterTypeAndUserProfileFromCareerById(Authentication auth, String id, Boolean isGroup) {
        if (!ObjectId.isValid(id)) throw new InvalidParamException(ResponseStatus.ID_INVALID);
        List<MessageCenterDoc> messageCenterDocs = messageCenterDocService.findByTypeAndUserProfile(MessageCenterType.forCareer(), getUserId(auth), ImmutableList.of(new ObjectId(id)));
        if (CollectionUtils.isNotEmpty(messageCenterDocs)) {
            List<MessageCenterFullDTO> results = getMessageCenterFromCareer(getUserId(auth), messageCenterDocs, getUserToken(auth), isGroup, getLocale(auth));
            if (CollectionUtils.isNotEmpty(results)) {
                return new BaseResp<>(results.get(0));
            }
        }
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp findByMessageCenterTypeAndUserProfileFromCareerWithTimestamp(Authentication auth, Boolean isGroup, Long timestamp, int size) {
        List<MessageCenterDoc> messageCenterDocs = messageCenterDocService.findByTypeAndUserProfileAndTimestamp(MessageCenterType.forCareer(), getUserId(auth), timestamp, size);
        long count = messageCenterDocService.countByTypeAndUserProfileAndTimestamp(MessageCenterType.forCareer(), getUserId(auth), timestamp);
        List<MessageCenterFullDTO> results = getMessageCenterFromCareer(getUserId(auth), messageCenterDocs, getUserToken(auth), isGroup, getLocale(auth));

        boolean hasMoreData = (count > messageCenterDocs.size());
        HistoryMessageCenterFullResp resultData = new HistoryMessageCenterFullResp(results, hasMoreData);
        return new BaseResp<>(resultData);
    }

    private List<MessageCenterFullDTO> getMessageCenterFromCareer(Long userProfileId, List<MessageCenterDoc> messageCenterDocs, String accessToken, Boolean isGroup, String locale) {

        List<MessageCenterFullDTO> results;
        if (isGroup) {
            results = getMessageCenter(userProfileId, messageCenterDocs, locale);
        } else {
            results = getMessageCenterGroupByConversation(userProfileId, messageCenterDocs, accessToken, locale);
        }

        List<ObjectId> messageCenterIds = messageCenterDocs.stream().map(MessageCenterDoc::getId).collect(Collectors.toList());
        List<ObjectId> messageCenterIdsHavingClosedMessage = messageDocService.findMessageCenterHavingClosedMessage(messageCenterIds, userProfileId);
        results.forEach(mc -> mc.setClosed(messageCenterIdsHavingClosedMessage.contains(new ObjectId(mc.getId()))));
        return results;
    }

    private List<UserProfileCvEmbedded> getParticipantInConversations(List<ConversationBase> conversationDocs, ObjectId messageCenterId, Long userProfileId) {
        List<UserProfileCvEmbedded> participants = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(conversationDocs)) {
            conversationDocs.stream().filter(c -> c.getMessageCenterId().equals(messageCenterId)).forEach(c -> {
                Optional<UserProfileCvMessageEmbedded> findCv = c.getParticipants().stream().filter(
                        cv -> !userProfileId.equals(cv.getUserProfileId())).findFirst();
                findCv.ifPresent(participants::add);
            });
        }
        return participants;
    }

    private List<UserProfileCvEmbedded> getParticipantInConversations(ConversationDoc conversationDoc, Long userProfileId) {
        List<UserProfileCvEmbedded> participants = new ArrayList<>();
        if (Objects.nonNull(conversationDoc)) {
            Optional<UserProfileCvMessageEmbedded> findCv = conversationDoc.getParticipants().stream().filter(
                    cv -> !userProfileId.equals(cv.getUserProfileId())).findFirst();
            findCv.ifPresent(participants::add);
        }
        return participants;
    }

    @Override
    public BaseResp findByUserProfileFromHotelById(Authentication auth, String id) {
        if (!ObjectId.isValid(id)) throw new InvalidParamException(ResponseStatus.ID_INVALID);
        var messageCenterDocs = messageCenterDocService.findByUserProfileFromHotel(getUserId(auth), Lists.newArrayList(new ObjectId(id)));
        if (CollectionUtils.isNotEmpty(messageCenterDocs)) {
            var results = getMessageCenterFromHotel(getUserId(auth), messageCenterDocs, getUserToken(auth), getLocale(auth));
            if (CollectionUtils.isNotEmpty(results)) {
                return new BaseResp<>(results.get(0));
            }
        }
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp findByUserProfileFromHotelWithTimestamp(Authentication auth, Long timestamp, int size) {
        var messageCenterDocs = messageCenterDocService.findByUserProfileFromHotelAndTimestamp(getUserId(auth), timestamp, size);
        var count = messageCenterDocService.countByUserProfileFromHotelAndTimestamp(getUserId(auth), timestamp);
        var results = getMessageCenterFromHotel(getUserId(auth), messageCenterDocs, getUserToken(auth), getLocale(auth));

        var hasMoreData = count > results.size();
        var resultData = new HistoryMessageCenterFullResp(results, hasMoreData);
        return new BaseResp<>(resultData);
    }

    @Override
    public BaseResp delete(Authentication auth, String id, boolean isHotelApp) {
        if (!ObjectId.isValid(id)) throw new InvalidParamException(ResponseStatus.ID_INVALID);

        ObjectId messageCenterId = new ObjectId(id);
        int receiveInApp = getReceiveInApp(isHotelApp);
        messageDocService.deleteMessages(messageCenterId, getUserId(auth), receiveInApp);
        conversationDocService.deleteConversation(messageCenterId, getUserId(auth));
        messageCenterDocService.deleteMessageCenterOfUser(messageCenterId, getUserId(auth), isHotelApp);
        return new BaseResp();
    }

    private List<MessageCenterFullDTO> getMessageCenter(Long userProfileId, List<MessageCenterDoc> messageCenterDocs, String locale) {
        var messageCountGroupByMessageCenter = countUnreadMessageOfMessageCenterForProfile(userProfileId);
        var results = new ArrayList<MessageCenterFullDTO>();

        messageCenterDocs.forEach(mc -> {
            long count = getUnreadMessage(mc.getId(), messageCountGroupByMessageCenter);
            var messageCenterFullDTO = new MessageCenterFullDTO(mc, count, PROFILE_APP.value(), userProfileId, locale);
            results.add(messageCenterFullDTO);
        });
        return results;
    }

    private List<ObjectIdCount> countUnreadMessageOfMessageCenterForProfile(long userProfileId) {
        var result = new ArrayList<ObjectIdCount>();
        Optional.of(messageDocService.countUnreadMessageGroupByKey(MessageBase.Fields.messageCenterId, userProfileId, PROFILE_APP.value()))
                .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);

        Optional.of(supportMessageDocService.countUnreadMessageGroupByKey(MessageBase.Fields.messageCenterId, userProfileId, PROFILE_APP.value()))
                .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);

        return result;
    }

    @Deprecated
    private List<MessageCenterFullDTO> getMessageCenterGroupByConversation(Long userProfileId, List<MessageCenterDoc> messageCenterDocs, String accessToken, String locale) {

        List<ObjectIdCount> messageCountGroupByConversation = messageDocService.countUnreadMessageGroupByKey(MessageBase.Fields.conversationId, userProfileId, PROFILE_APP.value());
        List<ObjectId> conversationIds = messageCountGroupByConversation.stream().map(ObjectIdCount::getId).collect(Collectors.toList());
        List<ObjectLatest> messageLatestUpdated = messageDocService.getLatestUpdatedDateByConversationIds(conversationIds, userProfileId, PROFILE_APP.value());
        List<ObjectId> messageCenterIds = messageCenterDocs.stream().map(MessageCenterDoc::getId).collect(Collectors.toList());
        List<ConversationDoc> conversationDocs = conversationDocService.findByMessageCenterIdAndUserProfileId(messageCenterIds, userProfileId);

        List<MessageCenterFullDTO> results = new ArrayList<>();
        for (ConversationDoc conversationDoc : conversationDocs) {
            messageCenterDocs.stream().filter(mc -> mc.getId().equals(conversationDoc.getMessageCenterId())).findFirst()
                    .ifPresent(mc -> {
                        Date latestUpdatedDate = getLatestUpdatedDateInConversation(conversationDoc, messageLatestUpdated);
                        long count = getUnreadMessage(conversationDoc.getId(), messageCountGroupByConversation);
                        List<UserProfileCvEmbedded> participants = getParticipantInConversations(conversationDoc, userProfileId);
                        MessageCenterFullDTO messageCenterFullDTO = new MessageCenterFullDTO(mc, count, PROFILE_APP.value(), userProfileId, latestUpdatedDate, participants, locale);
                        messageCenterFullDTO.setConversation(new ConversationDTO(conversationDoc, accessToken, locale));
                        results.add(messageCenterFullDTO);
                    });
        }
        return results.stream().filter(mc -> Objects.nonNull(mc) && Objects.nonNull(mc.getConversation())).collect(Collectors.toList());
    }

    private List<MessageCenterFullDTO> getMessageCenterFromHotel(Long userProfileId, List<MessageCenterDoc> messageCenterDocs, String accessToken, String locale) {

        var conversationDocs = getConversationOfMessageCenterSelect(messageCenterDocs, userProfileId);
        var messageCountDocs = countUnreadMessageGroupByMessageCenterForSelect(conversationDocs, userProfileId);
        var messageLatestUpdated = getLatestUpdatedDateByMessageCenterForSelect(messageCenterDocs, userProfileId);

        List<MessageCenterFullDTO> results = new ArrayList<>();
        for (MessageCenterDoc messageCenterDoc : messageCenterDocs) {
            long count = getUnreadMessage(messageCenterDoc.getId(), messageCountDocs);
            var latestUpdatedDate = getLatestUpdatedDate(messageCenterDoc, messageLatestUpdated);
            var participants = getParticipantInConversations(conversationDocs, messageCenterDoc.getId(), userProfileId);

            var messageCenterFullDTO = new MessageCenterFullDTO(
                    messageCenterDoc,
                    count,
                    SELECT_APP.value(),
                    userProfileId,
                    latestUpdatedDate,
                    participants,
                    locale);

            AtomicBoolean isJoiner = new AtomicBoolean(false);
            if (messageCenterDoc.getType() == AUTHORIZATION_CONVERSATION.value()) {
                conversationDocs.stream().filter(
                        c -> Objects.nonNull(c.getCreatedBy())
                                && userProfileId.equals(c.getCreatedBy().getUserProfileId())
                                && messageCenterDoc.getId().equals(c.getMessageCenterId())
                                && messageCenterDoc.getType() == AUTHORIZATION_CONVERSATION.value())
                        .findFirst().ifPresent(it -> {
                    isJoiner.set(true);
                    messageCenterFullDTO.setConversation(new ConversationDTO((ConversationDoc) it, locale, locale));
                });

            }

            boolean isAdmin = false;
            if (CollectionUtils.isNotEmpty(messageCenterDoc.getAdminOfCompany())) {
                isAdmin = messageCenterDoc.getAdminOfCompany().stream().anyMatch(ad -> ad.getUserProfileId().equals(userProfileId));
            }
            messageCenterFullDTO.setAdmin(isAdmin);
            messageCenterFullDTO.setClosed(Optional.of(messageCenterDoc).map(it -> it.getType() == VACANCY_CONVERSATION.value() && it.getVacancy().getStatus() == Status.INACTIVE).orElse(false));
            results.add(messageCenterFullDTO);
        }
        return results;
    }

    private List<ConversationBase> getConversationOfMessageCenterSelect(List<MessageCenterDoc> messageCenterDocs, Long userProfileId) {
        List<ConversationBase> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(messageCenterDocs)) {
            var messageCenterIds = messageCenterDocs.stream().map(MessageCenterDoc::getId).collect(toImmutableList());
            Optional.of(conversationDocService.findByMessageCenterIdAndUserProfileId(messageCenterIds, userProfileId, shortConversationKeys()))
                    .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);
            Optional.of(supportConversationDocService.findByMessageCenterIdAndUserProfileId(messageCenterIds, userProfileId, shortConversationKeys()))
                    .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);
        }
        return result;
    }

    private List<ObjectIdCount> countUnreadMessageGroupByMessageCenterForSelect(List<ConversationBase> conversations, Long userProfileId) {
        List<ObjectIdCount> result = new ArrayList<>();
        Optional.of(messageDocService.countUnreadMessageGroupByMessageCenter(conversations.stream().
                filter(it -> it instanceof ConversationDoc)
                .map(ConversationBase::getId).collect(toImmutableList()), userProfileId, SELECT_APP.value()))
                .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);

        Optional.of(supportMessageDocService.countUnreadMessageGroupByMessageCenter(conversations.stream().
                filter(it -> it instanceof SupportConversationDoc)
                .map(ConversationBase::getId).collect(toImmutableList()), userProfileId, SELECT_APP.value()))
                .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);
        return result;
    }

    private List<ObjectLatest> getLatestUpdatedDateByMessageCenterForSelect(List<MessageCenterDoc> messageCenters, Long userProfileId) {
        List<ObjectLatest> result = new ArrayList<>();

        Optional.of(messageDocService.getLatestUpdatedDateByMessageCenterIds(messageCenters.stream().
                filter(it -> it.getType() != MessageCenterType.BOOST_SUPPORT_CHANNEL.value())
                .map(MessageCenterDoc::getId).collect(toImmutableList()), userProfileId, SELECT_APP.value()))
                .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);

        Optional.of(supportMessageDocService.getLatestUpdatedDateByMessageCenterIds(messageCenters.stream().
                filter(it -> it.getType() == MessageCenterType.BOOST_SUPPORT_CHANNEL.value())
                .map(MessageCenterDoc::getId).collect(toImmutableList()), userProfileId, SELECT_APP.value()))
                .filter(CollectionUtils::isNotEmpty).ifPresent(result::addAll);
        return result;
    }


    private long getUnreadMessage(ObjectId id, List<ObjectIdCount> source) {
        return source.stream().filter(cv -> Objects.nonNull(cv.getId()) && cv.getId().equals(id)).findFirst()
                .map(ObjectIdCount::getTotal).orElse(0L);
    }

    private Date getLatestUpdatedDate(MessageCenterDoc messageCenterDoc, List<ObjectLatest> messageLatestUpdated) {
        if (Objects.nonNull(messageCenterDoc)) {
            if (CollectionUtils.isNotEmpty(messageLatestUpdated)) {
                Optional<ObjectLatest> objectLatest = messageLatestUpdated.stream()
                        .filter(cv -> Objects.nonNull(cv.getId()) && cv.getId().equals(messageCenterDoc.getId()))
                        .findFirst();
                return objectLatest.map(ObjectLatest::getLastUpdateTime).orElse(messageCenterDoc.getUpdatedDate());
            } else {
                return messageCenterDoc.getUpdatedDate();
            }
        }
        return null;
    }

    private Date getLatestUpdatedDateInConversation(ConversationDoc conversationDoc, List<ObjectLatest> messageLatestUpdated) {
        if (CollectionUtils.isNotEmpty(messageLatestUpdated)) {
            Optional<ObjectLatest> objectLatest = messageLatestUpdated.stream()
                    .filter(cv -> Objects.nonNull(cv.getId()) && cv.getId().equals(conversationDoc.getId()))
                    .findFirst();
            return objectLatest.map(ObjectLatest::getLastUpdateTime).orElse(null);
        }
        return null;
    }

    private int getReceiveInApp(boolean isHotelApp) {
        return isHotelApp ? SELECT_APP.value() : PROFILE_APP.value();
    }
}
