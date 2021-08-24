package com.qooco.boost.models.user;

import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.user.UserProfileDTO;

public class UserCompanyResp {
    private UserProfileDTO userProfile;

    private CompanyDTO company;

    public UserCompanyResp() {
        super();
    }

    public UserCompanyResp(UserProfileDTO userProfile, CompanyDTO company) {
        this.userProfile = userProfile;
        this.company = company;
    }

    public UserCompanyResp(UserProfileDTO userProfile) {
        this.userProfile = userProfile;
    }

    public UserProfileDTO getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfileDTO userProfile) {
        this.userProfile = userProfile;
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }
}
