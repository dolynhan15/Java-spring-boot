package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.UserProfileBasicEmbedded;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseUserDTO {
    private Long id;
    private String username;
    private String avatar;

    public BaseUserDTO(UserProfile userProfile) {
        if (Objects.nonNull(userProfile)) {
            this.id = userProfile.getUserProfileId();
            this.username = userProfile.getUsername();
            this.avatar = ServletUriUtils.getAbsolutePath(userProfile.getAvatar());
        }
    }

    public BaseUserDTO(UserFit userFit) {
        if (Objects.nonNull(userFit)) {
            this.id = userFit.getUserProfileId();
            this.username = userFit.getUsername();
            this.avatar = ServletUriUtils.getAbsolutePath(userFit.getAvatar());
        }
    }

    public BaseUserDTO(UserProfileBasicEmbedded userProfile) {
        if (Objects.nonNull(userProfile)) {
            this.id = userProfile.getUserProfileId();
            this.username = userProfile.getUsername();
            this.avatar = ServletUriUtils.getAbsolutePath(userProfile.getAvatar());
        }
    }
}
