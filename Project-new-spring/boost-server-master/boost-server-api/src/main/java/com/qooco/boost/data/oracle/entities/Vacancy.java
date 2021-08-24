/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.qooco.boost.data.oracle.entities;

import com.google.common.collect.Lists;
import com.qooco.boost.constants.Const;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "VACANCY")
public class Vacancy extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "VACANCY_SEQUENCE")
    @SequenceGenerator(sequenceName = "VACANCY_SEQ", allocationSize = 1, name = "VACANCY_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "VACANCY_ID", nullable = false)
    private Long id;

    @Setter
    @Getter
    @Basic(optional = false)
    @Size(min = 1, max = 2000)
    @Column(name = "LOGO", nullable = false)
    private String logo;

    @Setter
    @Getter
    @JoinColumn(name = "COMPANY_ID", nullable = false, referencedColumnName = "COMPANY_ID")
    @ManyToOne(optional = false)
    private Company company;

    @Setter
    @Getter
    @JoinColumn(name = "JOB_ID", referencedColumnName = "JOB_ID")
    @ManyToOne(optional = false)
    private Job job;

    @Deprecated
    @Setter
    @Getter
    @JoinColumn(name = "CITY_ID", referencedColumnName = "CITY_ID")
    @ManyToOne
    private City city;

    @Deprecated
    @Setter
    @Getter
    @JoinColumn(name = "SEARCH_CITY_ID", referencedColumnName = "CITY_ID")
    @ManyToOne
    private City searchCity;

    @Setter
    @Getter
    @JoinColumn(name = "JOB_LOCATION_ID", referencedColumnName = "LOCATION_ID")
    @ManyToOne
    private Location jobLocation;

    @Setter
    @Getter
    @JoinColumn(name = "SEARCH_LOCATION_ID", referencedColumnName = "LOCATION_ID")
    @ManyToOne
    private Location searchLocation;

    @Setter
    @Getter
    @JoinColumn(name = "CONTACT_PERSON_ID", referencedColumnName = "STAFF_ID")
    @ManyToOne(optional = false)
    private Staff contactPerson;

    @Setter
    @Getter
    @JoinColumn(name = "EDUCATION_ID", referencedColumnName = "EDUCATION_ID")
    @ManyToOne()
    private Education education;

    @Setter
    @Getter
    @Column(name = "NUMBER_OF_SEAT")
    private Integer numberOfSeat;

    @Setter
    @Getter
    @Column(name = "SALARY", columnDefinition = "NUMBER")
    private Double salary;

    @Setter
    @Getter
    @Column(name = "SALARY_MAX", columnDefinition = "NUMBER")
    private Double salaryMax;

    @Setter
    @Getter
    @JoinColumn(name = "CURRENCY_ID", referencedColumnName = "CURRENCY_ID")
    @ManyToOne(optional = false)
    private Currency currency;

    @Column(name = "IS_HOUR_SALARY", nullable = false)
    private Boolean isHourSalary;

    @Setter
    @Getter
    @Column(name = "WORKING_TYPE", nullable = false)
    private Boolean workingType;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<VacancyLanguage> vacancyLanguages;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<VacancyDesiredHour> vacancyDesiredHours;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<VacancySoftSkill> vacancySoftSkills;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<VacancyBenefit> vacancyBenefits;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<VacancyAssessmentLevel> vacancyAssessmentLevels;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.ALL)
    private List<Appointment> vacancyAppointments;

    @Setter
    @Getter
    @Column(name = "IS_ASAP")
    private Boolean isAsap;

    @Setter
    @Getter
    @Basic(optional = false)
    @Column(name = "EXPECTED_START_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expectedStartDate;

    @Setter
    @Getter
    @Size(max = 250)
    @Column(name = "SHORT_DESCRIPTION", columnDefinition = "NVARCHAR2")
    private String shortDescription;

    @Setter
    @Getter
    @Lob
    @Size(max = 500)
    @Column(name = "FULL_DESCRIPTION", columnDefinition = "NVARCHAR2")
    private String fullDescription;

    @Setter
    @Getter
    @Column(name = "STATUS", nullable = false)
    private int status;

    @Setter
    @Getter
    @Column(name = "SEARCH_RANGE", nullable = false)
    private int searchRange;


    @Setter
    @Getter
    @Column(name = "START_SUSPEND_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startSuspendDate;

    @Setter
    @Getter
    @Column(name = "SUSPEND_DAYS")
    private Integer suspendDays;

    @Setter
    @Getter
    @JoinColumn(name = "ARCHIVIST_ID", referencedColumnName = "STAFF_ID")
    @ManyToOne
    private Staff archivist;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.REFRESH)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Where(clause = "CANDIDATE_STATUS = 1")
    private List<VacancyCandidate> closedCandidates;

    @Setter
    @Getter
    @OneToMany(mappedBy = "vacancy", cascade = CascadeType.REFRESH)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<VacancySeat> vacancySeats;

    public Vacancy() {
        super();
    }

    public Vacancy(Long id) {
        this.id = id;
    }

    public Vacancy(Long id, Long ownerId) {
        super(ownerId);
        this.id = id;
    }

    public boolean isSuspendedOrInactive() {
        if (Objects.nonNull(this.status) && Const.Vacancy.Status.OPENING == this.status) {
            if (Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
                Date republishedDate = DateUtils.getUtcForOracle(DateUtils.addDays(startSuspendDate, suspendDays));
                return (new Date()).before(republishedDate);
            }
            return false;
        }
        return true;
    }

    public int getVacancyStatus() {
        if (Objects.nonNull(this.status)) {
            if (Const.Vacancy.Status.INACTIVE == this.status) {
                return Const.Vacancy.Status.INACTIVE;
            } else if (Const.Vacancy.Status.PERMANENT_SUSPEND == this.status) {
                return Const.Vacancy.Status.SUSPEND;
            } else if (Const.Vacancy.Status.OPENING == this.status) {
                if (Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
                    Date republishedDate = DateUtils.getUtcForOracle(DateUtils.addDays(startSuspendDate, suspendDays));
                    if ((new Date()).before(republishedDate)) {
                        return Const.Vacancy.Status.SUSPEND;
                    }
                    return Const.Vacancy.Status.OPENING;
                }
            }
        }
        return Const.Vacancy.Status.OPENING;
    }

    public List<Date> getNotAvailableTimeSlotForTemporarySuspend() {
        List<Date> dates = new ArrayList<>();
        if (Const.Vacancy.Status.OPENING == status
                && Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
            Date endSuspendDate = DateUtils.addDays(startSuspendDate, suspendDays);
            Date timeSlot = DateUtils.roundDateToQuarterHour(startSuspendDate);
            do {
                dates.add(timeSlot);
                timeSlot = DateUtils.addSecond(timeSlot, DateUtils.QUARTER_HOUR_IN_SECOND);
            } while (timeSlot.before(endSuspendDate));
        }
        return dates;
    }

    public Date getEndSuspendedDateForTemporarySuspend() {
        if (Const.Vacancy.Status.OPENING == status && Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
            return DateUtils.addDays(startSuspendDate, suspendDays);
        }
        return null;
    }

    public Date getEndSuspendedDateForTemporarySuspendInUtc() {
        if (Const.Vacancy.Status.OPENING == status && Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
            return DateUtils.getUtcForOracle(DateUtils.addDays(startSuspendDate, suspendDays));
        }
        return null;
    }

    public boolean isInSuspendedTime(Date date) {
        Date endDate = getEndSuspendedDateForTemporarySuspendInUtc();
        if (Objects.nonNull(date) && Objects.nonNull(endDate)
                && Objects.nonNull(startSuspendDate) && Objects.nonNull(suspendDays)) {
            if (endDate.getTime() > date.getTime() && DateUtils.getUtcForOracle(startSuspendDate).getTime() <= date.getTime()) {
                return true;
            }
        }
        return false;
    }

    public Vacancy(Vacancy vacancy) {
        if (Objects.nonNull(vacancy)) {
            id = vacancy.getId();
            logo = vacancy.getLogo();
            numberOfSeat = vacancy.getNumberOfSeat();
            salary = vacancy.getSalary();
            salaryMax = vacancy.getSalaryMax();
            isHourSalary = vacancy.getHourSalary();
            workingType = vacancy.getWorkingType();

            if (Objects.nonNull(vacancy.getCompany())) {
                company = new Company(vacancy.getCompany());
            }
            if (Objects.nonNull(vacancy.getJob())) {
                job = new Job(vacancy.getJob());
            }
            if (Objects.nonNull(vacancy.getJobLocation())) {
                jobLocation = new Location(vacancy.getJobLocation());
            }
            if (Objects.nonNull(vacancy.getSearchLocation())) {
                searchLocation = new Location(vacancy.getSearchLocation());
            }
            if (Objects.nonNull(vacancy.getContactPerson())) {
                contactPerson = new Staff(vacancy.getContactPerson());
            }
            if (Objects.nonNull(vacancy.getEducation())) {
                education = new Education(vacancy.getEducation());
            }
            if (Objects.nonNull(vacancy.getCurrency())) {
                currency = new Currency(vacancy.getCurrency());
            }
            if (CollectionUtils.isNotEmpty(vacancy.getVacancyLanguages())) {
                vacancyLanguages = Lists.newArrayList(vacancy.getVacancyLanguages());
            }
            if (CollectionUtils.isNotEmpty(vacancy.getVacancyDesiredHours())) {
                vacancyDesiredHours = Lists.newArrayList(vacancy.getVacancyDesiredHours());
            }
            if (CollectionUtils.isNotEmpty(vacancy.getVacancySoftSkills())) {
                vacancySoftSkills = Lists.newArrayList(vacancy.getVacancySoftSkills());
            }
            if (CollectionUtils.isNotEmpty(vacancy.getVacancyBenefits())) {
                vacancyBenefits = Lists.newArrayList(vacancy.getVacancyBenefits());
            }
            if (CollectionUtils.isNotEmpty(vacancy.getVacancyAssessmentLevels())) {
                vacancyAssessmentLevels = Lists.newArrayList(vacancy.getVacancyAssessmentLevels());
            }
            if (CollectionUtils.isNotEmpty(vacancy.getVacancyAppointments())) {
                vacancyAppointments = Lists.newArrayList(vacancy.getVacancyAppointments());
            }

            isAsap = vacancy.getIsAsap();
            expectedStartDate = vacancy.getExpectedStartDate();
            shortDescription = vacancy.getShortDescription();
            fullDescription = vacancy.getFullDescription();
            status = vacancy.getStatus();
            searchRange = vacancy.getSearchRange();

            startSuspendDate = vacancy.getStartSuspendDate();
            suspendDays = vacancy.getSuspendDays();
            archivist = vacancy.getArchivist();

            if (CollectionUtils.isNotEmpty(vacancy.getClosedCandidates())) {
                closedCandidates = Lists.newArrayList(vacancy.getClosedCandidates());
            }

            this.setCreatedBy(vacancy.getCreatedBy());
            this.setUpdatedBy(vacancy.getUpdatedBy());
            this.setCreatedDate(vacancy.getCreatedDate());
            this.setUpdatedDate(vacancy.getUpdatedDate());
            this.setIsDeleted(vacancy.getIsDeleted());

            if (Objects.nonNull(vacancy.getCity())) {
                city = new City(vacancy.getCity());
            }
            if (Objects.nonNull(vacancy.getSearchCity())) {
                searchCity = new City(vacancy.getSearchCity());
            }
        }
    }

    public Boolean getHourSalary() {
        return isHourSalary;
    }

    public void setHourSalary(Boolean hourSalary) {
        isHourSalary = hourSalary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return Objects.equals(id, vacancy.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return job.toString();
    }

}
