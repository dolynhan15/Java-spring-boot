package com.qooco.boost.models.dto.company;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.enumeration.ApprovalStatus;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.models.dto.RoleCompanyBaseDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyProfileDTO {
    @Setter
    @Getter
    private CompanyDTO company;
    @Setter
    @Getter
    private CompanyWidgetDTO widgets;

    @Setter
    @Getter
    private RoleCompanyBaseDTO roleCompany;

    private boolean isEditable;
    @Setter
    @Getter
    private boolean hasPendingCompanyRequest;

    public CompanyProfileDTO(CompanyDTO company, CompanyWidgetDTO widgets, Staff staff, boolean isEditable, boolean hasPendingCompanyRequest, String locale) {
        this.company = company;
        this.widgets = widgets;
        this.isEditable = isEditable;
        this.hasPendingCompanyRequest = hasPendingCompanyRequest;

        //Edit base on the request client team: Admin of pending company is returning Role = null
        if(Objects.nonNull(company) && ApprovalStatus.APPROVED.equals(company.getStatus())) {
            this.roleCompany = Objects.nonNull(staff) && Objects.nonNull(staff.getRole()) ? new RoleCompanyBaseDTO(staff.getRole(), locale) : null;
        }
    }

    public boolean isEditable() {
        return isEditable;
    }

    @JsonProperty("isEditable")
    public void setEditable(boolean asap) {
        isEditable = asap;
    }
}
