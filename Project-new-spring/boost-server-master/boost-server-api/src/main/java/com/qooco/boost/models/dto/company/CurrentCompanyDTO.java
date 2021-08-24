package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.RoleCompanyBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentCompanyDTO {
    private CompanyDTO company;
    @ApiModelProperty(notes = "Using CompanyWorkedType declaration")
    private int type;
    private int numberOfCompany;
    private RoleCompanyBaseDTO roleCompany;

    public CurrentCompanyDTO(CompanyDTO company, Staff staff, int type, String locale) {
        this.company = company;
        this.type = type;
        this.roleCompany = Objects.nonNull(staff) && Objects.nonNull(staff.getRole()) ? new RoleCompanyBaseDTO(staff.getRole(), locale) : null;
    }
}
