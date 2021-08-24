package com.qooco.boost.models.dto.staff;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.BaseDTO;
import com.qooco.boost.models.dto.RoleCompanyBaseDTO;
import com.qooco.boost.models.dto.user.UserFitDTO;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactPersonDTO extends BaseDTO {
    private Long id;
    private UserFitDTO userProfile;
    private RoleCompanyBaseDTO roleCompany;

    public ContactPersonDTO(Staff staff, String locale) {
        if (Objects.nonNull(staff)) {
            setId(staff.getStaffId());
            setUserProfile(new UserFitDTO(staff.getUserFit(), locale));
            setUpdatedDate(DateUtils.getUtcForOracle(staff.getUpdatedDate()));
            this.roleCompany = Objects.nonNull(staff.getRole()) ? new RoleCompanyBaseDTO(staff.getRole(), locale) : null;
        }
    }
}
