package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.PataFile;
import lombok.*;

import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileEmbedded {
    private Long id;
    private String name;
    private Long size;
    private Long duration;
    private String url;
    private String thumbnailUrl;
    private String type;
    private String clientId;
    @Setter
    private Date expiredDate;

    public FileEmbedded(PataFile file, String clientId) {
        this(file);
        this.clientId = clientId;
    }
    private FileEmbedded(PataFile file) {
        this.id = file.getPataFileId();
        this.name = file.getFileName();
        this.size = file.getFileSize();
        this.duration = file.getDuration();
        this.url = file.getUrl();
        this.thumbnailUrl = file.getThumbnailUrl();
        this.type = file.getContentType();
    }
}
