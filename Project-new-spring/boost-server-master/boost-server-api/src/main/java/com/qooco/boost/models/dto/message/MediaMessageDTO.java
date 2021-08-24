package com.qooco.boost.models.dto.message;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.FileEmbedded;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MediaMessageDTO {
    private Long id;
    private String name;
    private Long size;
    private Long duration;
    private String url;
    private String thumbnailUrl;
    private String type;
    private String clientId;
    private Date expiredDate;

    public MediaMessageDTO(FileEmbedded embedded) {
        this.id = embedded.getId();
        this.name = embedded.getName();
        this.size = embedded.getSize();
        this.duration = embedded.getDuration();
        this.url = ServletUriUtils.getAbsolutePath(embedded.getUrl());
        this.thumbnailUrl = ServletUriUtils.getAbsolutePath(embedded.getThumbnailUrl());
        this.type = embedded.getType();
        this.clientId = embedded.getClientId();
        this.expiredDate = embedded.getExpiredDate();
    }
}
