package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessConversationService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;

import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.request.ConversationReq;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.qooco.boost.utils.MongoConverters.convertToUserProfileCvEmbedded;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

@Service
public class BusinessConversationServiceImpl implements BusinessConversationService {
    @Autowired
    private MessageCenterDocService messageCenterDocService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private StaffService staffService;

    @Override
    public ConversationBaseService getConversationService() {
        return conversationDocService;
    }

    @Override
    public MessageDocService getMessageDocService() {
        return messageDocService;
    }

    @Override
    public StaffService getStaffService() {
        return staffService;
    }

    @Override
    public BaseResp createConversation(Authentication auth, ConversationReq req) {
        if (Objects.isNull(req.getVacancyId()) && Objects.isNull(req.getUserProfileId())) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        businessValidatorService.checkPermissionOnVacancy(req.getVacancyId(), getCompanyId(auth), getUserId(auth));
        MessageCenterDoc messageCenterDoc = messageCenterDocService.findByVacancy(req.getVacancyId());
        if (Objects.isNull(messageCenterDoc)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_MESSAGE_CENTER);
        }

        UserProfile userProfile = businessValidatorService.checkExistsUserProfile(req.getUserProfileId());
        UserCvDoc userCvDoc = userCvDocService.findByUserProfileId(req.getUserProfileId());

        UserFit userFit = businessValidatorService.checkExistsUserFit(getUserId(auth));
        UserProfileCvEmbedded sender = convertToUserProfileCvEmbedded(userFit);
        UserProfileCvEmbedded recipient = ofNullable(userCvDoc)
                .map(MongoConverters::convertToUserProfileCvEmbedded)
                .orElseGet(() -> convertToUserProfileCvEmbedded(userProfile));

        ConversationDoc conversationDoc = conversationDocService.save(messageCenterDoc, sender, recipient);
        messageCenterDocService.updateFreeChatParticipant(messageCenterDoc.getId(), sender, recipient);
        return new BaseResp<>(new ConversationDTO(conversationDoc, getUserToken(auth), getLocale(auth)));
    }

    @Override
    public BaseResp getById(Authentication auth, String id) {
        if (!ObjectId.isValid(id)) throw new InvalidParamException(ResponseStatus.ID_INVALID);
        ConversationDoc conversationDoc = conversationDocService.findByIdAndUserProfileId(new ObjectId(id), getUserId(auth));
        if (Objects.nonNull(conversationDoc)) {
            Long numberUnread = messageDocService.countUnreadMessageByUserProfileId(conversationDoc.getId(), getUserId(auth), getApp(auth));
            ObjectIdCount idCount = new ObjectIdCount(conversationDoc.getId(), numberUnread);
            List<ConversationDTO> results = getConversation(Lists.newArrayList(conversationDoc), Lists.newArrayList(idCount), getUserToken(auth), getLocale(auth));
            if (CollectionUtils.isNotEmpty(results)) {
                return new BaseResp<>(results.get(0));
            }
        }
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }


    @Override
    public List<ConversationDTO> getConversation(List<ConversationDoc> conversationDocs, List<ObjectIdCount> listCount, String token, String locale) {
        var idMaps = listCount.stream().collect(toUnmodifiableMap(c -> c.getId().toHexString(), ObjectIdCount::getTotal));
        return conversationDocs.stream().map(conversationDoc -> new ConversationDTO(conversationDoc, token, locale))
                .peek(conversation -> conversation.setTotalUnreadMessages(idMaps.getOrDefault(conversation.getId(), conversation.getTotalUnreadMessages())))
                .collect(toUnmodifiableList());
    }

}
