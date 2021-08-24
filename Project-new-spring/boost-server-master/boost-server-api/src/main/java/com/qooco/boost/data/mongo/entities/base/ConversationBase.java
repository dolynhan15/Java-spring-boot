package com.qooco.boost.data.mongo.entities.base;

import com.qooco.boost.data.mongo.embedded.PublicKeyEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
@SuperBuilder(toBuilder = true)
public class ConversationBase {
    @Id
    private ObjectId id;
    private ObjectId messageCenterId;
    private int messageCenterType;
    private Long companyId;
    private List<UserProfileCvMessageEmbedded> participants;

    private UserProfileCvEmbedded createdBy;
    private Date createdDate;
    private Date updatedDate;

    private String secretKey;
    private boolean isDeleted;

    private boolean isDisable;
    private Map<String, PublicKeyEmbedded> userKeys;

    private ConversationBase(List<UserProfileCvEmbedded> participants) {
        if (CollectionUtils.isNotEmpty(participants)) {
            this.participants = new ArrayList<>();
            participants.forEach(it -> {
                if (it instanceof UserProfileCvMessageEmbedded) {
                    this.participants.add((UserProfileCvMessageEmbedded) it);
                } else if (it instanceof UserProfileCvEmbedded) {
                    this.participants.add(new UserProfileCvMessageEmbedded(it));
                }
            });
        }
        this.createdDate = this.updatedDate = DateUtils.toServerTimeForMongo();
    }

    public ConversationBase(MessageCenterDoc messageCenterDoc, List<UserProfileCvEmbedded> participants) {
        this(participants);
        this.messageCenterId = messageCenterDoc.getId();
        this.messageCenterType =  messageCenterDoc.getType();
        this.companyId = messageCenterDoc.getCompanyId();
    }

    public ConversationBase(ObjectId conversationDocId, ObjectId messageCenterId) {
        this.id = conversationDocId;
        this.messageCenterId = messageCenterId;
    }

    public UserProfileCvEmbedded getPartner(Long userProfileId) {
        if (CollectionUtils.isNotEmpty(participants) && Objects.nonNull(userProfileId)) {
            return participants.stream().filter(partner -> !userProfileId.equals(partner.getUserProfileId()))
                    .findFirst().orElse(null);
        }
        return null;
    }

    public UserProfileCvEmbedded getParticipant(Long userProfileId) {
        if (CollectionUtils.isNotEmpty(participants) && Objects.nonNull(userProfileId)) {
            return participants.stream().filter(participant -> userProfileId.equals(participant.getUserProfileId()))
                    .findFirst().orElse(null);
        }
        return null;
    }

    public boolean isParticipant(Long userProfileId) {
        AtomicBoolean isExists = new AtomicBoolean(false);
        Optional.ofNullable(participants)
                .filter(CollectionUtils::isNotEmpty)
                .ifPresent(it -> isExists.set(it.stream().anyMatch(participant -> participant.getUserProfileId().equals(userProfileId))));
        return isExists.get();
    }

    public boolean isParticipant(Long senderUserProfileId, Long recipientUserProfileId) {
        return isParticipant(senderUserProfileId) && isParticipant(recipientUserProfileId);
    }
}
