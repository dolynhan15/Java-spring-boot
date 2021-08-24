package com.qooco.boost.models.dto.vacancy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.mongo.embedded.VacancyEmbedded;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.models.dto.CityDTO;
import com.qooco.boost.models.dto.JobDTO;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.dto.company.CompanyBaseDTO;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import com.qooco.boost.utils.Converter;
import com.qooco.boost.utils.ServletUriUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.apache.commons.collections.CollectionUtils;

import java.util.Objects;

@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldNameConstants
public class VacancyBaseDTO {
    @Setter
    @Getter
    private Long id;
    @Setter
    @Getter
    private String logo;
    @Setter
    @Getter
    private CompanyBaseDTO company;
    @Setter
    @Getter
    private JobDTO job;
    @Setter
    @Getter
    private LocationDTO jobLocation;
    @Setter
    @Getter
    private CurrencyDTO currency;
    @Setter
    @Getter
    private double salary;
    @Setter
    @Getter
    private double salaryMax;

    private boolean isHourSalary;
    private boolean isFullTime;

    @Deprecated
    @Setter
    @Getter
    private CityDTO city;

    @Setter
    @Getter
    private Integer numberOfSeat;

    @Setter
    @Getter
    private Integer numberOfClosedCandidate;

    public VacancyBaseDTO(Vacancy vacancy, String locale) {
        this.id = vacancy.getId();
        this.logo = ServletUriUtils.getAbsolutePath(vacancy.getLogo());
        this.company = new CompanyBaseDTO(vacancy.getCompany());
        this.job = new JobDTO(vacancy.getJob(), locale);
        this.jobLocation = new LocationDTO(vacancy.getJobLocation(), locale);
        this.currency = new CurrencyDTO(vacancy.getCurrency(), locale);
        this.salary = Converter.valueOfDouble(vacancy.getSalary(), 0);
        this.salaryMax = Converter.valueOfDouble(vacancy.getSalaryMax(), 0);
        this.isHourSalary = vacancy.getHourSalary();
        this.isFullTime = vacancy.getWorkingType();
        this.numberOfSeat = vacancy.getNumberOfSeat();
        this.numberOfClosedCandidate = CollectionUtils.isNotEmpty(vacancy.getClosedCandidates()) ? vacancy.getClosedCandidates().size() : null;
        this.city = new CityDTO(vacancy.getCity(), locale);
    }

    public VacancyBaseDTO(VacancyEmbedded vacancy, String locale) {
        this.id = vacancy.getId();
        this.logo = ServletUriUtils.getAbsolutePath(vacancy.getLogo());
        this.company = new CompanyBaseDTO(vacancy.getCompany());
        this.job = new JobDTO(vacancy.getJob(), locale);
        this.jobLocation = new LocationDTO(vacancy.getJobLocation(), locale);
        this.currency = new CurrencyDTO(vacancy.getCurrency(), locale);
        this.salary = Converter.valueOfDouble(vacancy.getSalary(), 0);
        this.salaryMax = Converter.valueOfDouble(vacancy.getSalaryMax(), 0);
        this.isHourSalary = vacancy.isHourSalary();
        this.isFullTime = vacancy.isFullTime();
        this.numberOfSeat = vacancy.getNumberOfSeat();
        this.numberOfClosedCandidate = vacancy.getNumberOfClosedCandidate();
        this.city = new CityDTO(vacancy.getCity(), locale);
    }


    @JsonProperty("isHourSalary")
    public void setHourSalary(boolean hourSalary) {
        isHourSalary = hourSalary;
    }

    public boolean isHourSalary() {
        return isHourSalary;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    @JsonProperty("isFullTime")
    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyBaseDTO that = (VacancyBaseDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
