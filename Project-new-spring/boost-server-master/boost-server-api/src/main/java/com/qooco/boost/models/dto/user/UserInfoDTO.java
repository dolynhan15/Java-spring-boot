package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.models.dto.company.CompanyDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDTO {
    private UserProfileDTO userProfile;
    private CompanyDTO company;
    private String accessToken;
    private String signInId;
    private String sessionId;

    public UserInfoDTO(BaseEntity user, String locale) {
        if(user instanceof UserProfile) {
            this.userProfile = new UserProfileDTO((UserProfile) user, locale);
        } else if (user instanceof UserFit){
            this.userProfile = new UserProfileDTO((UserFit) user, locale);
        }
    }

    public UserInfoDTO(BaseEntity user, Company company, UserAccessToken userAccessToken, String locale) {
        this(user, locale);
        this.company = Objects.nonNull(company) ? new CompanyDTO(company, locale) : null;
        if(Objects.nonNull(userAccessToken)) {
            this.accessToken = userAccessToken.getToken();
            this.signInId = userAccessToken.getSignInId();
            this.sessionId = userAccessToken.getSessionId();
        }
    }
}
