package com.qooco.boost.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.message.MessageCenterFullDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HistoryMessageCenterFullResp {

    private List<MessageCenterFullDTO> results;
    private boolean hasMoreMessageCenter;

    public HistoryMessageCenterFullResp(List<MessageCenterFullDTO> messageCenters, boolean hasMoreMessageCenter) {
        this.results = messageCenters;
        this.hasMoreMessageCenter = hasMoreMessageCenter;
    }
}
