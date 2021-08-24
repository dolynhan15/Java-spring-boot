package com.qooco.boost.models.request.message;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MediaMessageReq {
    @NotNull
    private MultipartFile file;
    private MultipartFile thumbnail;
    private Long duration;
    private String clientId;
}
