package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.MessageCenterDoc;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageCenterFullDTO extends MessageCenterDTO {
    @Deprecated
    @Setter @Getter
    private ConversationDTO conversation;
    @JsonProperty("isClosed")
    @Setter
    private boolean isClosed;

    public MessageCenterFullDTO(MessageCenterDoc messageCenterDoc, long unreadMessage, int fromApp, long userProfileId, String locale) {
        super(messageCenterDoc, unreadMessage, fromApp, userProfileId, messageCenterDoc.getUpdatedDate(), null, locale);
    }

    public MessageCenterFullDTO(MessageCenterDoc messageCenterDoc, long unreadMessage, int fromApp, long userProfileId, Date lastUpdateTime, List<UserProfileCvEmbedded> participants, String locale) {
        super(messageCenterDoc, unreadMessage, fromApp, userProfileId, lastUpdateTime, participants, locale);
    }

    public MessageCenterFullDTO clone() {
        MessageCenterFullDTO copy = new MessageCenterFullDTO();
        this.conversation = getConversation();
        copy.setCompany(getCompany());
        copy.setId(getId());
        copy.setJoiner(isJoiner());
        copy.setAdmin(isAdmin());
        copy.setClosed(isClosed());
        copy.setLastUpdateTime(getLastUpdateTime());
        copy.setType(getType());
        copy.setUnreadMessage(getUnreadMessage());
        copy.setUserCurriculumVitaes(getUserCurriculumVitaes());
        copy.setVacancy(getVacancy());
        copy.setBoostHelper(getBoostHelper());
        return copy;
    }

    boolean isClosed() {
        return isClosed;
    }

    @Override
    public String toString() {
        return getId().toString();
    }
}
