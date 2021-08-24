package com.qooco.boost.business.impl.abstracts;

import com.google.common.collect.ImmutableList;
import com.qooco.boost.business.BaseBusinessService;
import com.qooco.boost.data.model.count.ObjectIdCount;
import com.qooco.boost.data.mongo.entities.base.ConversationBase;
import com.qooco.boost.data.mongo.services.base.ConversationBaseService;
import com.qooco.boost.data.mongo.services.base.MessageBaseService;
import com.qooco.boost.data.oracle.services.StaffService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.RoleCompanyDTO;
import com.qooco.boost.models.dto.company.CompanyBaseDTO;
import com.qooco.boost.models.dto.message.ConversationDTO;
import com.qooco.boost.models.dto.user.UserProfileCvEmbeddedDTO;
import com.qooco.boost.models.response.HistoryConversationResp;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Objects;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.data.enumeration.BoostApplication.SELECT_APP;

public interface BusinessConversationBaseService<T> extends BaseBusinessService {
    ConversationBaseService getConversationService();

    MessageBaseService getMessageDocService();

    StaffService getStaffService();

    List<ConversationDTO> getConversation(List<T> conversations, List<ObjectIdCount> count, String token, String locale);


    default BaseResp getByMessageCenterIdAndTimestamp(Authentication auth, String messageCenterId, Long timestamp, int size) {
        if (!ObjectId.isValid(messageCenterId)) throw new InvalidParamException(ResponseStatus.ID_INVALID);

        var count = getConversationService().countByMessageCenterIdAndUserProfileIdWithTimestamp(new ObjectId(messageCenterId), getUserId(auth), timestamp);
        var conversationDocs = getConversationService().findByMessageCenterIdAndUserProfileIdWithTimestamp(new ObjectId(messageCenterId), getUserId(auth), timestamp, size);

        if (CollectionUtils.isNotEmpty(conversationDocs)) {
            var listCount = getMessageDocService().countUnreadMessageGroupByConversation(new ObjectId(messageCenterId), getUserId(auth), getApp(auth));
            var conversationDTOS = getConversation(conversationDocs, listCount, getUserToken(auth), getLocale(auth));

            //TODO: Need check with userType = 1 (Fit) => return role
            if (getApp(auth) != SELECT_APP.value()) {
                conversationDTOS = getStaffRoleForConversation(conversationDTOS, getUserId(auth), getLocale(auth));
            }

            var hasMoreConversation = false;
            if (count > conversationDocs.size()) {
                hasMoreConversation = true;
            }
            var result = new HistoryConversationResp(conversationDTOS, hasMoreConversation);
            return new BaseResp<>(result);
        }
        return new BaseResp<>(new HistoryConversationResp(ImmutableList.of(), false));
    }

    default BaseResp getById(Authentication auth, String id) {
        if (!ObjectId.isValid(id)) throw new InvalidParamException(ResponseStatus.ID_INVALID);
        var conversationDoc = getConversationService().findByIdAndUserProfileId(new ObjectId(id), getUserId(auth));
        if (Objects.nonNull(conversationDoc)) {
            var numberUnread = getMessageDocService().countUnreadMessageByUserProfileId(((ConversationBase) conversationDoc).getId(), getUserId(auth), getApp(auth));
            var idCount = new ObjectIdCount(((ConversationBase) conversationDoc).getId(), numberUnread);

            List<ConversationDTO> results = getConversation(List.of((T) conversationDoc), List.of(idCount), getUserToken(auth), getLocale(auth));
            if (CollectionUtils.isNotEmpty(results)) {
                return new BaseResp<>(results.get(0));
            }
        }
        return new BaseResp<>(ResponseStatus.SUCCESS);
    }

    private List<ConversationDTO> getStaffRoleForConversation(List<ConversationDTO> conversationDTOS, Long userProfileId, String locale) {
        var result = ImmutableList.copyOf(conversationDTOS);
        var partnerIds = result.stream().map(it -> it.getPartner(userProfileId)).filter(Objects::nonNull).map(UserProfileCvEmbeddedDTO::getId).collect(toImmutableList());
        if (CollectionUtils.isNotEmpty(partnerIds)) {
            var staffs = getStaffService().findByUserProfileAndCompany(result.get(0).getCompanyId(), partnerIds);

            result.stream().map(it -> it.getPartner(userProfileId))
                    .forEach(partner -> staffs.stream().filter(staff -> staff.getUserFit().getUserProfileId().equals(partner.getId()))
                            .findFirst().ifPresent(it -> {
                                partner.setCompany(new CompanyBaseDTO(it.getCompany()));
                                partner.setRole(new RoleCompanyDTO(it.getRole(), locale));
                            })
                    );
        }
        return result;
    }
}
