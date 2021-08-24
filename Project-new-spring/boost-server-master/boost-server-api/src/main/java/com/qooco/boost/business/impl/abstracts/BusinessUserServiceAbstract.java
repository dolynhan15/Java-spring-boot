package com.qooco.boost.business.impl.abstracts;

import com.qooco.boost.business.BaseBusinessService;
import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.entities.ConversationDoc;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.services.ConversationDocService;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.mongo.services.SupportConversationDocService;
import com.qooco.boost.data.mongo.services.SupportMessageDocService;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.data.utils.CipherKeys;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.sdo.ConversationPublicKeySDO;
import com.qooco.boost.models.user.UserBaseReq;
import com.qooco.boost.models.user.UserUploadKeyReq;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.qooco.boost.constants.AttributeEventType.EVT_FILL_ALL_PROFILE_FIELDS;
import static com.qooco.boost.data.constants.Constants.DEFAULT_LIMITED_CONVERSATION;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public abstract class BusinessUserServiceAbstract implements BaseBusinessService {
    @Autowired
    private UserAccessTokenService userAccessTokenService;
    @Autowired
    private ConversationDocService conversationDocService;
    @Autowired
    private SupportConversationDocService supportConversationDocService;
    @Autowired
    private UserAttributeEventService userAttributeEventService;
    @Autowired
    private UserCurriculumVitaeService userCurriculumVitaeService;
    @Autowired
    private UserPreviousPositionService userPreviousPositionService;
    @Autowired
    private UserPersonalityService userPersonalityService;
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;
    @Autowired
    private UserQualificationService userQualificationService;
    @Autowired
    private UserFitService userFitService;
    @Autowired
    private MessageDocService messageDocService;
    @Autowired
    private SupportMessageDocService supportMessageDocService;

    protected void validateBasicUser(UserBaseReq userBaseReq) {
        if (StringUtils.isBlank(userBaseReq.getFirstName())) {
            throw new InvalidParamException(ResponseStatus.FIRST_NAME_IS_REQUIRED);
        }
        if (StringUtils.isBlank(userBaseReq.getLastName())) {
            throw new InvalidParamException(ResponseStatus.LAST_NAME_IS_REQUIRED);
        }
        if (Objects.isNull(userBaseReq.getBirthday())) {
            throw new InvalidParamException(ResponseStatus.BIRTHDAY_IS_REQUIRED);
        }
    }

    protected void updatePublicKey(AuthenticatedUser authenticatedUser, UserUploadKeyReq req) {
        String token = authenticatedUser.getToken();
        Long userId = authenticatedUser.getId();
        if (Objects.isNull(req) || StringUtils.isBlank(req.getPublicKey())) {
            throw new InvalidParamException(ResponseStatus.NONE_PUBLIC_KEY_IN_PARAM);
        }
        userAccessTokenService.updatePublicKeyByAccessToken(token, req.getPublicKey());
        List<ConversationDoc> conversationDocs = conversationDocService.findNonSecretKeyConversation(userId);
        conversationDocs.forEach(conversationDoc -> {
            String secretKey = CipherKeys.convertToBase64String(Objects.requireNonNull(CipherKeys.generateAESKey()));
            conversationDoc.setSecretKey(secretKey);
        });

        updateBatchSecretKeyForConversations(conversationDocs);
        addAESSecretKeyForNoneSecretKeyMessageByConversationIds(conversationDocs);

        updateForNonePublicKeyConversations(req, token, userId, conversationDocService);
        updateForNonePublicKeyConversations(req, token, userId, supportConversationDocService);
    }

    private void updateForNonePublicKeyConversations(UserUploadKeyReq req, String token, Long userId, ConversationBaseService service) {
        List<ConversationBase> nonePublicKeyConversations = service.findNotHavePublicKeyByToken(userId, token, req.getPublicKey(), DEFAULT_LIMITED_CONVERSATION);
        int size = nonePublicKeyConversations.size();
        while (size > 0) {
            List<ConversationPublicKeySDO> conversationPublicKeySDOs = new ArrayList<>();
            nonePublicKeyConversations.forEach(conversationDoc -> {
                PublicKeyEmbedded publicKeyEmbedded = null;
                if (Objects.nonNull(conversationDoc.getUserKeys())) {
                    publicKeyEmbedded = conversationDoc.getUserKeys().get(token);
                }
                if (Objects.isNull(publicKeyEmbedded) || StringUtils.isBlank(publicKeyEmbedded.getEncryptedPublicKey())) {
                    publicKeyEmbedded = new PublicKeyEmbedded(req.getPublicKey(), conversationDoc.getSecretKey());
                } else if (!publicKeyEmbedded.getPublicKey().equals(req.getPublicKey())) {
                    publicKeyEmbedded = new PublicKeyEmbedded(req.getPublicKey(), conversationDoc.getSecretKey());
                }

                var conversationPublicKeySDO = new ConversationPublicKeySDO(conversationDoc, token, publicKeyEmbedded);
                conversationPublicKeySDOs.add(conversationPublicKeySDO);
            });
            service.updateBatchEncryptionKeyForConversationDocs(conversationPublicKeySDOs);
            nonePublicKeyConversations = service.findNotHavePublicKeyByToken(userId, token, req.getPublicKey(), DEFAULT_LIMITED_CONVERSATION);
            size = nonePublicKeyConversations.size();
        }
    }

    private void updateBatchSecretKeyForConversations(List<ConversationDoc> conversations) {
        conversationDocService.updateBatchSecretKeyForConversations(conversations);
        supportConversationDocService.updateBatchSecretKeyForConversations(conversations);
    }

    private void addAESSecretKeyForNoneSecretKeyMessageByConversationIds(List<ConversationDoc> conversations) {
        messageDocService.addAESSecretKeyForNoneSecretKeyMessageByConversationIds(conversations);
        supportMessageDocService.addAESSecretKeyForNoneSecretKeyMessageByConversationIds(conversations);
    }

    protected void trackingFillAllProfileFields(Long userProfileId) {
        ofNullable(userAttributeEventService).map(it -> it.findByUserAndEvent(userProfileId, EVT_FILL_ALL_PROFILE_FIELDS))
                .map(it -> Optional.<UserCurriculumVitae>empty())
                .orElseGet(() -> ofNullable(userCurriculumVitaeService).map(it -> it.findByUserProfile(new UserProfile(userProfileId))))
                .filter(userCV -> nonNull(userCV.getEducation()))
                .filter(userCV -> nonNull(userCV.getCurriculumVitaeJobs()))
                .filter(userCV -> nonNull(userCV.getMinSalary()))
                .filter(userCV -> nonNull(userCV.getMaxSalary()))
                .filter(userCV -> nonNull(userCV.getCurrency()))
                .filter(userCV -> nonNull(userCV.getUserDesiredHours()))
                .filter(userCV -> nonNull(userCV.getUserBenefits()))
                .filter(userCV -> nonNull(userCV.getUserSoftSkills()))
                .filter(userCV -> nonNull(userCV.getSocialLinks()))
                .filter(userCV -> isNotEmpty(userPreviousPositionService.findByUserProfileId(userProfileId)))
                .filter(userCV -> userCV.isAsap() || nonNull(userCV.getExpectedStartDate()))
                .map(UserCurriculumVitae::getUserProfile)
                .filter(userProfile -> nonNull(userProfile.getFirstName()))
                .filter(userProfile -> nonNull(userProfile.getLastName()))
                .filter(userProfile -> nonNull(userProfile.getAvatar()))
                .filter(userProfile -> nonNull(userProfile.getGender()))
                .filter(userProfile -> nonNull(userProfile.getBirthday()))
                .filter(userProfile -> nonNull(userProfile.getCity()))
                .filter(userProfile -> nonNull(userProfile.getPhoneNumber()))
                .filter(userProfile -> nonNull(userProfile.getUserLanguageList()))
                .filter(userProfile -> nonNull(userProfile.getNationalId()))
                .filter(userProfile -> isNotEmpty(userPersonalityService.findPersonalAssessmentByUserProfile(userProfileId)))
                .filter(userProfile -> isNotEmpty(userQualificationService.findByUserProfileId(userProfileId)))
                .ifPresent(userProfile -> businessProfileAttributeEventService.onAttributeEvent(EVT_FILL_ALL_PROFILE_FIELDS, userProfileId));
    }

    protected void saveDefaultCompany(Long userId, String token, Long companyId) {
        userFitService.updateDefaultCompany(userId, companyId);
        userAccessTokenService.updateCompanyByAccessToken(token, companyId);
    }

    protected long getTotalUnreadMessage(long userProfileId, int receiveInApp) {
        long countInChat = messageDocService.countUserSendUnreadMessageByUserProfileId(userProfileId, receiveInApp);
        long countInSupport = supportMessageDocService.countUserSendUnreadMessageByUserProfileId(userProfileId, receiveInApp);
        return countInChat + countInSupport;
    }
}