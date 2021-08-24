package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Objects;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@FieldNameConstants
public class UserProfileBasicEmbedded {
    private Long userProfileId;
    private String firstName;
    private String lastName;
    private String avatar;
    private String username;
    private int gender;
    private String email;
    private String phone;
    private int userType;

    public UserProfileBasicEmbedded(Long userProfileId, int userType) {
        this.userProfileId = userProfileId;
        this.userType = userType;
    }

    public UserProfileBasicEmbedded(UserProfileEmbedded embedded) {
        this.userProfileId = embedded.getUserProfileId();
        this.firstName = embedded.getFirstName();
        this.lastName = embedded.getLastName();
        this.avatar = embedded.getAvatar();
        this.username = embedded.getUsername();
        this.gender = embedded.getGender();
        this.email = embedded.getEmail();
        this.phone = embedded.getPhone();
        this.userType = embedded.getUserType();
    }

    public UserProfileBasicEmbedded(UserProfileBasicEmbedded embedded) {
        this.userProfileId = embedded.getUserProfileId();
        this.firstName = embedded.getFirstName();
        this.lastName = embedded.getLastName();
        this.avatar = embedded.getAvatar();
        this.username = embedded.getUsername();
        this.gender = embedded.getGender();
        this.email = embedded.getEmail();
        this.phone = embedded.getPhone();
        this.userType = embedded.getUserType();
    }


    public UserProfileBasicEmbedded(UserProfile userProfile, int userType) {
        if (Objects.nonNull(userProfile)) {
            this.userProfileId = userProfile.getUserProfileId();
            this.firstName = userProfile.getFirstName();
            this.lastName = userProfile.getLastName();
            this.avatar = userProfile.getAvatar();
            this.username = userProfile.getUsername();
            Optional.ofNullable(userProfile.getGender()).ifPresent(it -> this.gender = it.ordinal());
            this.email = userProfile.getEmail();
            this.phone = userProfile.getPhoneNumber();
            this.userType = userType;
        }
    }

    public UserProfileBasicEmbedded(UserFit userFit, int userType) {
        if (Objects.nonNull(userFit)) {
            this.userProfileId = userFit.getUserProfileId();
            this.firstName = userFit.getFirstName();
            this.lastName = userFit.getLastName();
            this.avatar = userFit.getAvatar();
            this.username = userFit.getUsername();
            Optional.ofNullable(userFit.getGender()).ifPresent(it -> this.gender = it.ordinal());
            this.email = userFit.getEmail();
            this.phone = userFit.getPhoneNumber();
            this.userType = userType;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProfileBasicEmbedded that = (UserProfileBasicEmbedded) o;
        return Objects.equals(userProfileId, that.userProfileId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userProfileId);
    }
}
