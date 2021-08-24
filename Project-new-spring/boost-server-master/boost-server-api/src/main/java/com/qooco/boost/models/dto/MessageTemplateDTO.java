package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.oracle.entities.MessageTemplate;
import com.qooco.boost.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor

@Builder
public class MessageTemplateDTO {
    @Getter
    private Long id;
    @Getter
    private String content;

    private boolean isDeleted;
    @Getter
    private Date updatedDate;

    @JsonProperty("isDeleted")
    public boolean isDeleted() {
        return isDeleted;
    }

    public MessageTemplateDTO(MessageTemplate message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.updatedDate = DateUtils.getUtcForOracle(message.getUpdateDate());
        this.isDeleted = message.isDeleted();
    }
}
