package com.qooco.boost.models.user;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserProfileStep {
    private Integer profileStep;

    public Integer getProfileStep() {
        return profileStep;
    }

    public void setProfileStep(Integer profileStep) {
        this.profileStep = profileStep;
    }
}
