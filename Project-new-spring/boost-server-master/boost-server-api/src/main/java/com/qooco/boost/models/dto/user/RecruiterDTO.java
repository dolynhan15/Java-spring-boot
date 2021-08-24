package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.models.dto.company.CompanyDTO;
import com.qooco.boost.models.dto.company.CurrentCompanyDTO;
import com.qooco.boost.models.dto.company.WaitingCompanyDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecruiterDTO {
    private UserFitDTO userProfile;
    private CurrentCompanyDTO currentCompany;
    private int totalVacancy;
    private long totalUnreadMessage;
    private int totalCandidate;
    private int totalAppointment;

    @Deprecated
    private CompanyDTO company;
    @Deprecated
    private WaitingCompanyDTO waitingCompany;

    public RecruiterDTO(UserFitDTO userProfile, CompanyDTO company, WaitingCompanyDTO waitingCompany,
                        int totalVacancy, long totalUnreadMessage, int totalCandidate, int totalAppointment) {
        this.userProfile = userProfile;
        this.company = company;
        this.waitingCompany = waitingCompany;
        this.totalVacancy = totalVacancy;
        this.totalUnreadMessage = totalUnreadMessage;
        this.totalCandidate = totalCandidate;
        this.totalAppointment = totalAppointment;
    }
}
