package com.qooco.boost.threads.notifications.dto;

import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.mongo.embedded.message.AppliedMessage;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.dto.company.CompanyBaseDTO;
import com.qooco.boost.models.dto.company.CompanyShortInformationDTO;
import com.qooco.boost.models.dto.currency.BaseCurrencyDTO;
import com.qooco.boost.models.dto.staff.StaffDTO;
import com.qooco.boost.utils.Converter;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Setter
@Getter
@NoArgsConstructor
public class VacancyDTO {
    private Long id;
    private String logo;
    private CompanyBaseDTO company;
    private JobDTO job;
    private CityDTO city;
    private LocationDTO jobLocation;
    private BaseCurrencyDTO currency;
    private double salary;
    private double salaryMax;
    private boolean isHourSalary;
    private boolean isFullTime;
    private Date updateDate;
    private StaffDTO contactPerson;
    private Integer suspendDays;
    private Date startSuspendDate;
    private int status;

    public VacancyDTO(AppliedMessage appliedMessage, String locale) {
        VacancyEmbedded vacancy = appliedMessage.getVacancy();
        this.id = vacancy.getId();
        this.city = new CityDTO(vacancy.getCity(), locale);
        if (Objects.nonNull(vacancy.getJobLocation())) {
            this.jobLocation = new LocationDTO(vacancy.getJobLocation(), locale);
        }
        this.company = new CompanyShortInformationDTO(vacancy.getCompany());
        this.currency = new BaseCurrencyDTO(vacancy.getCurrency(), locale);
        this.isFullTime = vacancy.isFullTime();
        this.isHourSalary = vacancy.isHourSalary();
        this.job = new JobDTO(vacancy.getJob(), locale);
        this.logo = ServletUriUtils.getAbsolutePath(vacancy.getLogo());
        this.salary = Converter.valueOfDouble(vacancy.getSalary(), 0);
        this.salaryMax = Converter.valueOfDouble(vacancy.getSalaryMax(), 0);
        this.status = vacancy.getStatus();
        Optional.ofNullable(vacancy.getContactPerson()).ifPresent(it -> this.contactPerson = new StaffDTO(it, locale));
    }

    public VacancyDTO(VacancyEmbedded vacancy, String locale) {
        this.id = vacancy.getId();
        this.city = new CityDTO(vacancy.getCity(), locale);
        if (Objects.nonNull(vacancy.getJobLocation())) {
            this.jobLocation = new LocationDTO(vacancy.getJobLocation(), locale);
        }
        this.company = new CompanyShortInformationDTO(vacancy.getCompany());
        this.currency = new BaseCurrencyDTO(vacancy.getCurrency(), locale);
        this.isFullTime = vacancy.isFullTime();
        this.isHourSalary = vacancy.isHourSalary();
        this.job = new JobDTO(vacancy.getJob(), locale);
        this.logo = ServletUriUtils.getAbsolutePath(vacancy.getLogo());
        this.salary = Converter.valueOfDouble(vacancy.getSalary(), 0);
        this.salaryMax = Converter.valueOfDouble(vacancy.getSalaryMax(), 0);
        this.status = vacancy.getStatus();
        this.suspendDays = vacancy.getSuspendDays();
        this.startSuspendDate = vacancy.getStartSuspendDate();
        Optional.ofNullable(vacancy.getContactPerson()).ifPresent(it -> this.contactPerson = new StaffDTO(it, locale));
    }
}
