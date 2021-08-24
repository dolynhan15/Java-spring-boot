package com.qooco.boost.data.mongo.embedded;

import lombok.*;

@Setter
@Getter
@Builder(builderMethodName = "userProfileCvMessageEmbeddedBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCvMessageEmbedded extends UserProfileCvEmbedded {
    private boolean isDeletedMessage;
    private String description;

    public UserProfileCvMessageEmbedded(UserProfileCvMessageEmbedded embedded) {
        super(embedded);
        this.description = embedded.getDescription();
        this.isDeletedMessage = embedded.isDeletedMessage;
    }

    public UserProfileCvMessageEmbedded(UserProfileCvEmbedded embedded) {
        super(embedded);
    }

    public UserProfileCvMessageEmbedded(UserProfileCvEmbedded userProfileCv, int userTypeFit) {
        super(userProfileCv, userTypeFit);
    }

    public UserProfileCvMessageEmbedded(long userProfileId, int userType) {
        super(userProfileId, userType);
    }

    public UserProfileCvMessageEmbedded(UserProfileEmbedded userProfileEmbedded, int userType) {
        super(userProfileEmbedded);
        setUserType(userType);
    }
}
