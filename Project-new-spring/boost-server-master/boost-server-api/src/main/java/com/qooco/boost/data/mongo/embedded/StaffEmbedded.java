package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.mongo.embedded.message.MessageStatus;
import com.qooco.boost.data.oracle.entities.Staff;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Builder
@Setter
@Getter
@NoArgsConstructor
public class StaffEmbedded extends MessageStatus {
    private Long id;
    private CompanyEmbedded company;
    private UserProfileEmbedded userProfile;
    private RoleCompanyEmbedded roleCompany;

    public StaffEmbedded(Long id) {
        this.id = id;
    }

    public StaffEmbedded(Long id, CompanyEmbedded company, UserProfileEmbedded userProfile, RoleCompanyEmbedded roleCompany) {
        this.id = id;
        this.company = company;
        this.userProfile = userProfile;
        this.roleCompany = roleCompany;
    }

    public StaffEmbedded(UserProfileEmbedded userProfile) {
        this.userProfile = userProfile;
    }

    public StaffEmbedded(StaffEmbedded staffEmbedded) {
        super(staffEmbedded);
        if (Objects.nonNull(staffEmbedded)) {
            id = staffEmbedded.getId();
            if (Objects.nonNull(staffEmbedded.getCompany())) {
                company = new CompanyEmbedded(staffEmbedded.getCompany());
            }
            if (Objects.nonNull(staffEmbedded.getUserProfile())) {
                userProfile = new UserProfileEmbedded(staffEmbedded.getUserProfile());
            }
            if (Objects.nonNull(staffEmbedded.getRoleCompany())) {
                roleCompany = new RoleCompanyEmbedded(staffEmbedded.getRoleCompany());
            }
        }
    }

    public StaffEmbedded(Staff staff) {
        if (Objects.nonNull(staff)) {
            id = staff.getStaffId();
            if (Objects.nonNull(staff.getCompany())) {
                company = new CompanyEmbedded(staff.getCompany());
            }
            if (Objects.nonNull(staff.getUserFit())) {
                userProfile = new UserProfileEmbedded(staff.getUserFit());
            }
            if (Objects.nonNull(staff.getRole())) {
                roleCompany = new RoleCompanyEmbedded(staff.getRole());
            }
        }
    }
}
