package com.qooco.boost.data.mongo.embedded;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class StaffShortEmbedded {
    @Id
    private Long id;
    private UserProfileBasicEmbedded userProfile;
    private RoleCompanyEmbedded roleCompany;

    public StaffShortEmbedded(StaffShortEmbedded staffShortEmbedded) {
        if (Objects.nonNull(staffShortEmbedded)) {
            id = staffShortEmbedded.getId();
            if (Objects.nonNull(staffShortEmbedded.getUserProfile())) {
                userProfile = new UserProfileBasicEmbedded(staffShortEmbedded.getUserProfile());
            }
            if (Objects.nonNull(staffShortEmbedded.getRoleCompany())) {
                roleCompany = new RoleCompanyEmbedded(staffShortEmbedded.getRoleCompany());
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaffShortEmbedded staff = (StaffShortEmbedded) o;
        return id.equals(staff.getId());
    }
}
