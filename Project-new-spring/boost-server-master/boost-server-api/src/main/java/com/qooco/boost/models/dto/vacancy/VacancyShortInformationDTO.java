package com.qooco.boost.models.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.dto.company.CompanyShortInformationDTO;
import com.qooco.boost.models.dto.staff.StaffDTO;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VacancyShortInformationDTO extends VacancyBaseDTO {
    private Integer status;
    private Date updateDate;
    private Date currentDate;
    private Date createdDate;
    private StaffDTO contactPerson;

    private Integer suspendDays;
    private Date startSuspendDate;

    public VacancyShortInformationDTO(Vacancy vacancy, String locale) {
        super(vacancy, locale);
        this.status = vacancy.getStatus();
        this.updateDate = DateUtils.getUtcForOracle(vacancy.getUpdatedDate());
        this.createdDate = DateUtils.getUtcForOracle(vacancy.getCreatedDate());
        this.contactPerson = new StaffDTO(vacancy.getContactPerson(), locale);
        ofNullable(vacancy.getStartSuspendDate()).ifPresent(it -> this.startSuspendDate = DateUtils.getUtcForOracle(it));
        this.suspendDays = vacancy.getSuspendDays();
        this.currentDate =  new Date();
    }

    public VacancyShortInformationDTO(VacancyEmbedded vacancy, String locale) {
        super(vacancy, locale);
        this.status = vacancy.getStatus();
        this.setCompany(new CompanyShortInformationDTO(vacancy.getCompany()));
        this.contactPerson = new StaffDTO(vacancy.getContactPerson(), locale);
        ofNullable(vacancy.getStartSuspendDate()).ifPresent(it -> this.startSuspendDate = (it));
        this.suspendDays = vacancy.getSuspendDays();
        this.currentDate = new Date();
    }

    public VacancyShortInformationDTO(Long vacancyId) {
        this.setId(vacancyId);
    }
}
