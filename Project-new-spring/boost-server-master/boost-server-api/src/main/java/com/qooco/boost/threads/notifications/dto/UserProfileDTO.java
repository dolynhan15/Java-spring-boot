package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.enumeration.Gender;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.enumeration.BoostHelperParticipant;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter @Getter
public class UserProfileDTO {
    private Long userProfileId;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String avatar;
    private String username;
    private String email;

    public UserProfileDTO(UserProfileCvEmbedded embedded) {
        if(Objects.nonNull(embedded)) {
            this.userProfileId = embedded.getUserProfileId();
            this.firstName = embedded.getFirstName();
            this.lastName = embedded.getLastName();
            this.gender = Gender.fromValue(embedded.getGender());
            this.username = embedded.getUsername();
            this.email = embedded.getEmail();
            this.avatar = BoostHelperParticipant.getIds().contains(embedded.getUserProfileId())?
                    ServletUriUtils.getAbsoluteResourcePath(embedded.getAvatar())
                    : ServletUriUtils.getAbsolutePath(embedded.getAvatar());
        }
    }
}
