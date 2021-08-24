package com.qooco.boost.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.message.ConversationDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryConversationResp {
    private List<ConversationDTO> results;
    private boolean hasMoreConversation;

    public HistoryConversationResp(List<ConversationDTO> conversations, boolean hasMoreConversation) {
        this.results = conversations;
        this.hasMoreConversation = hasMoreConversation;
    }

}
