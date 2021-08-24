package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.constants.Const;
import com.qooco.boost.data.oracle.entities.Vacancy;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.Objects;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
public class VacancyEmbedded {
    private Long id;
    private String logo;
    private CompanyEmbedded company;
    private JobEmbedded job;
    private StaffEmbedded contactPerson;
    private EducationEmbedded education;
    private Integer numberOfSeat;
    private Integer numberOfClosedCandidate;
    private Double salary;
    private Double salaryMax;
    private CurrencyEmbedded currency;
    private boolean isHourSalary;
    private boolean isFullTime;
    private boolean isAsap;
    private Date expectedStartDate;
    private LocationEmbedded jobLocation;
    private LocationEmbedded searchLocation;
    private Integer searchRange;

    @Deprecated
    private CityEmbedded city;
    @Deprecated
    private CityEmbedded searchCity;

    private Integer suspendDays;
    private Date startSuspendDate;
    private int status;

    public VacancyEmbedded(Vacancy vacancy) {
        if (Objects.nonNull(vacancy)) {
            this.id = vacancy.getId();
            this.logo = vacancy.getLogo();
            ofNullable(vacancy.getCompany()).ifPresent(it -> this.company = new CompanyEmbedded(it));
            ofNullable(vacancy.getJob()).ifPresent(it -> this.job = new JobEmbedded(it));
            ofNullable(vacancy.getContactPerson()).ifPresent(it -> this.contactPerson = new StaffEmbedded(it));
            ofNullable(vacancy.getEducation()).ifPresent(it -> this.education = new EducationEmbedded(it));

            this.numberOfSeat = vacancy.getNumberOfSeat();
            ofNullable(vacancy.getClosedCandidates()).ifPresent(it -> this.numberOfClosedCandidate = it.size());

            this.salary = vacancy.getSalary();
            this.salaryMax = vacancy.getSalaryMax();
            ofNullable(vacancy.getCurrency()).ifPresent(it -> this.currency = new CurrencyEmbedded(it));

            this.isHourSalary = vacancy.getHourSalary();
            this.isFullTime = vacancy.getWorkingType();
            this.isAsap = vacancy.getIsAsap();
            this.expectedStartDate = DateUtils.toServerTimeForMongo(vacancy.getExpectedStartDate());
            ofNullable(vacancy.getJobLocation()).ifPresent(it -> this.jobLocation = new LocationEmbedded(it));
            ofNullable(vacancy.getSearchLocation()).ifPresent(it -> this.searchLocation = new LocationEmbedded(it));

            this.searchRange = vacancy.getSearchRange();
            this.suspendDays = vacancy.getSuspendDays();
            this.startSuspendDate = vacancy.getStartSuspendDate();
        }
    }

    public int getVacancyStatus() {
        if (Const.Vacancy.Status.INACTIVE == this.status) {
            return Const.Vacancy.Status.INACTIVE;
        } else if (Const.Vacancy.Status.PERMANENT_SUSPEND == this.status) {
            return Const.Vacancy.Status.SUSPEND;
        } else if (Const.Vacancy.Status.OPENING == this.status) {
            if (Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
                Date republishedDate = DateUtils.getUtcForOracle(DateUtils.addDays(startSuspendDate, suspendDays));
                if ((new Date()).after(republishedDate)) {
                    return Const.Vacancy.Status.OPENING;
                }
                return Const.Vacancy.Status.SUSPEND;
            }
        }
        return Const.Vacancy.Status.OPENING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VacancyEmbedded that = (VacancyEmbedded) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public VacancyEmbedded(VacancyEmbedded vacancyEmbedded) {
        if (Objects.nonNull(vacancyEmbedded)) {
            this.id = vacancyEmbedded.getId();
            this.logo = vacancyEmbedded.getLogo();
            if (Objects.nonNull(vacancyEmbedded.getCompany())) {
                this.company = new CompanyEmbedded(vacancyEmbedded.getCompany());
            }
            if (Objects.nonNull(vacancyEmbedded.getJob())) {
                this.job = new JobEmbedded(vacancyEmbedded.getJob());
            }
            if (Objects.nonNull(vacancyEmbedded.getContactPerson())) {
                this.contactPerson = new StaffEmbedded(vacancyEmbedded.getContactPerson());
            }
            if (Objects.nonNull(vacancyEmbedded.getEducation())) {
                this.education = new EducationEmbedded(vacancyEmbedded.getEducation());
            }
            this.numberOfSeat = vacancyEmbedded.getNumberOfSeat();
            this.salary = vacancyEmbedded.getSalary();
            this.salaryMax = vacancyEmbedded.getSalaryMax();
            if (Objects.nonNull(vacancyEmbedded.getCurrency())) {
                this.currency = new CurrencyEmbedded(vacancyEmbedded.getCurrency());
            }
            this.isHourSalary = vacancyEmbedded.isHourSalary();
            this.isFullTime = vacancyEmbedded.isFullTime();
            this.isAsap = vacancyEmbedded.isAsap();
            this.expectedStartDate = vacancyEmbedded.getExpectedStartDate();
            if (Objects.nonNull(vacancyEmbedded.getJobLocation())) {
                this.jobLocation = new LocationEmbedded(vacancyEmbedded.getJobLocation());
            }
            if (Objects.nonNull(vacancyEmbedded.getSearchLocation())) {
                this.searchLocation = new LocationEmbedded(vacancyEmbedded.getSearchLocation());
            }
            this.searchRange = vacancyEmbedded.getSearchRange();

            this.suspendDays = vacancyEmbedded.getSuspendDays();
            this.startSuspendDate = vacancyEmbedded.getStartSuspendDate();
            this.numberOfClosedCandidate = vacancyEmbedded.getNumberOfClosedCandidate();
            this.status = vacancyEmbedded.getStatus();
        }
    }
}
