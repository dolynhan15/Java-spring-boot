package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessSupportChannelService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.constants.SupportChannelStatus;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.embedded.BoostHelperEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.SupportConversationDoc;
import com.qooco.boost.data.mongo.entities.localization.BaseLocaleDoc;
import com.qooco.boost.data.mongo.services.SupportConversationDocService;
import com.qooco.boost.data.mongo.services.SupportMessageDocService;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.data.mongo.services.base.MessageBaseService;
import com.qooco.boost.data.mongo.services.localization.boost.BoostLocaleService;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.data.oracle.services.UserFitService;
import com.qooco.boost.data.oracle.services.UserProfileService;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.request.PageRequest;
import com.qooco.boost.threads.services.MessageCenterDocActorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.of;
import static com.qooco.boost.data.constants.SupportChannelStatus.ARCHIVED;
import static com.qooco.boost.data.constants.SupportChannelStatus.OPENING;
import static com.qooco.boost.data.enumeration.BoostApplication.PROFILE_APP;
import static com.qooco.boost.data.enumeration.MessageCenterType.BOOST_SUPPORT_CHANNEL;
import static com.qooco.boost.enumeration.BoostHelperParticipant.BOOST_SUPPORTER;
import static com.qooco.boost.enumeration.ResponseStatus.SUCCESS;
import static com.qooco.boost.utils.MongoConverters.convertToCompanyEmbedded;
import static com.qooco.boost.utils.MongoConverters.convertToUserProfileCvMessageEmbedded;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;

@Service
@RequiredArgsConstructor
public class BusinessSupportChannelServiceImpl implements BusinessSupportChannelService {
    private final SupportConversationDocService supportConversationDocService;
    private final SupportMessageDocService supportMessageDocService;
    private final UserProfileService profileService;
    private final UserFitService userFitService;
    private final MessageCenterDocActorService messageCenterDocActorService;
    private final BoostLocaleService boostLocaleService;
    private final StaffService staffService;
    private final CompanyService companyService;

    @Override
    public ConversationBaseService getConversationService() {
        return supportConversationDocService;
    }

    @Override
    public MessageBaseService getMessageDocService() {
        return supportMessageDocService;
    }

    @Override
    public StaffService getStaffService() {
        return staffService;
    }

    @Override
    public BaseResp getByUser(Authentication auth) {
        var conversation = supportConversationDocService.findBySenderAndFromApp(getUserId(auth), getUserType(auth), getApp(auth));
        var result = ofNullable(conversation).map(it -> new ConversationDTO(it, getUserToken(auth), getLocale(auth))).orElse(null);
        return new BaseResp<>(result);
    }

    public BaseResp getConversations(Authentication auth, Integer appId, Integer status, PageRequest request, String... sorts) {
        Page<SupportConversationDoc> page = supportConversationDocService.findByAppAndStatusAndLanguageSortBy(appId, status, request.getPage(), request.getSize(), sorts);
        var result = page.getContent().stream().map(it -> new ConversationDTO(it, getUserToken(auth), getLocale(auth))).collect(toUnmodifiableList());
        return new BaseResp<>(new PagedResultV2<>(result, request.getPage(), page));
    }

    @Override
    public BaseResp createByUser(Authentication auth) {
        UserProfileCvEmbedded sender;
        if (getApp(auth) == PROFILE_APP.value()) {
            sender = convertToUserProfileCvMessageEmbedded(profileService.findById(getUserId(auth)));
        } else {
            sender = convertToUserProfileCvMessageEmbedded(userFitService.findById(getUserId(auth)));
        }

        Company company = ofNullable(getCompanyId(auth)).map(it -> companyService.findById(getCompanyId(auth))).orElse(null);
        UserProfileCvEmbedded recipient = createBoostSupportParticipant(BOOST_SUPPORTER);
        var messageCenter = messageCenterDocActorService.saveForBoostHelper(createBoostHelperMessageCenter(), sender, convertToCompanyEmbedded(company), BOOST_SUPPORT_CHANNEL);
        var supportChannel = supportConversationDocService.save(messageCenter, sender, recipient, getApp(auth));
        return new BaseResp<>(new ConversationDTO(supportChannel, getUserToken(auth), getLocale(auth)));
    }

    @Override
    public BaseResp deleteSupportConversation(Authentication auth, String conversationId) {
        //Need discuss more about the message support
        return new BaseResp<>(SUCCESS);
    }

    @Override
    public BaseResp restoreSupportConversation(Authentication auth, String conversationId) {
        supportConversationDocService.updateStatus(conversationId, OPENING);
        //Need discuss more about the message support
        return new BaseResp<>(SUCCESS);
    }

    @Override
    public BaseResp archiveSupportConversation(Authentication auth, String conversationId) {
        supportConversationDocService.updateStatus(conversationId, ARCHIVED);
        //Need discuss more about the message support
        return new BaseResp<>(SUCCESS);
    }

    private BoostHelperEmbedded createBoostHelperMessageCenter() {
        var textMap = boostLocaleService.findById(of(BOOST_SUPPORTER.nameKey(), BOOST_SUPPORTER.descriptionKey())).stream()
                .collect(Collectors.toMap(BaseLocaleDoc::id, it -> it));

        return BoostHelperEmbedded.builder()
                .name(ofNullable(textMap.get(BOOST_SUPPORTER.nameKey())).map(BaseLocaleDoc::content).orElse(""))
                .description(ofNullable(textMap.get(BOOST_SUPPORTER.descriptionKey())).map(BaseLocaleDoc::content).orElse(""))
                .avatar(BOOST_SUPPORTER.avatar())
                .build();
    }

    @Override
    public List<ConversationDTO> getConversation(List<SupportConversationDoc> docs, List<ObjectIdCount> listCount, String token, String locale) {
        var idMaps = listCount.stream().collect(toUnmodifiableMap(c -> c.getId().toHexString(), ObjectIdCount::getTotal));
        return docs.stream().map(conversationDoc -> new ConversationDTO(conversationDoc, token, locale))
                .peek(conversation -> conversation.setTotalUnreadMessages(idMaps.getOrDefault(conversation.getId(), conversation.getTotalUnreadMessages())))
                .collect(toUnmodifiableList());
    }

    private UserProfileCvMessageEmbedded createBoostSupportParticipant(BoostHelperParticipant participant) {
        var textMap = boostLocaleService.findById(of(participant.nameKey(), participant.descriptionKey())).stream()
                .collect(Collectors.toMap(BaseLocaleDoc::id, it -> it));

        var sender = new UserProfileCvMessageEmbedded();
        sender.setUserProfileId(participant.id());
        sender.setUserType(UserType.BOOST_GENERAL_SUPPORTER);
        sender.setFirstName(ofNullable(textMap.get(participant.nameKey())).map(BaseLocaleDoc::content).orElse(""));
        sender.setDescription(ofNullable(textMap.get(participant.descriptionKey())).map(BaseLocaleDoc::content).orElse(""));
        sender.setAvatar(participant.avatar());
        return sender;
    }

}
