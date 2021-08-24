package com.qooco.boost.data.mongo.entities;

import com.qooco.boost.constants.Const;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentSlotEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Document(collection = "VacancyDoc")
@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
public class VacancyDoc {

    @Id
    private Long id;
    private String logo;
    private CompanyEmbedded company;
    private JobEmbedded job;

    @Deprecated
    private CityEmbedded city;
    @Deprecated
    private CityEmbedded searchCity;

    private LocationEmbedded jobLocation;
    private LocationEmbedded searchLocation;

    private StaffEmbedded contactPerson;
    private EducationEmbedded education;
    private Integer numberOfSeat;
    private double salary;
    private double salaryMax;
    @Indexed(direction = IndexDirection.ASCENDING)
    private double salaryUsd;
    @Indexed(direction = IndexDirection.DESCENDING)
    private double salaryMaxUsd;
    private CurrencyEmbedded currency;
    private boolean isHourSalary;
    private boolean isFullTime;
    private boolean isAsap;
    private Date expectedStartDate;
    private String shortDescription;
    private String fullDescription;
    private Integer status;
    private int searchRange;
    private Date startSuspendDate;
    private Integer suspendDays;
    private Date endSuspendDate;
    private StaffEmbedded archivist;

    private List<LanguageEmbedded> nativeLanguages;
    private List<LanguageEmbedded> languages;
    private List<WorkingHourEmbedded> desiredHours;
    private List<BenefitEmbedded> benefits;
    private List<SoftSkillEmbedded> softSkills;
    private List<QualificationEmbedded> qualifications;
    private List<AppointmentEmbedded> appointments;
    private List<UserProfileCvEmbedded> closedCandidates;
    private List<Long> closedCandidateIdsOnClonedVacancy;

    private Date updatedDate;
    private StaffEmbedded createdByStaff;

    private long numberOfCandidate;
    private List<UserProfileCvEmbedded> candidateProfiles;
    private List<Long> appliedUserCvId;
    private List<RejectedUserCvEmbedded> rejectedUserCv;
    private Map<Long, AppointmentSlotEmbedded> appointmentCandidates;
    private Map<Long, List<AppointmentSlotEmbedded>> appointmentSlots;


    public boolean isSuspendedOrInactive() {
        if (Objects.nonNull(this.status) && Const.Vacancy.Status.OPENING == this.status) {
            if (Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
                Date republishedDate = DateUtils.addDays(startSuspendDate, suspendDays);
                return (new Date()).before(republishedDate);
            }
            return false;
        }
        return true;
    }

    public int getVacancyStatus() {
        if(Objects.nonNull(this.status)) {
            if (Const.Vacancy.Status.INACTIVE == this.status) {
                return Const.Vacancy.Status.INACTIVE;
            } else if (Const.Vacancy.Status.PERMANENT_SUSPEND == this.status) {
                return Const.Vacancy.Status.SUSPEND;
            } else if (Const.Vacancy.Status.OPENING == this.status) {
                if (Objects.nonNull(this.startSuspendDate) && Objects.nonNull(this.suspendDays)) {
                    Date republishedDate = DateUtils.addDays(startSuspendDate, suspendDays);
                    if ((new Date()).after(republishedDate)) {
                        return Const.Vacancy.Status.OPENING;
                    }
                    return Const.Vacancy.Status.SUSPEND;
                }
            }
        }
        return Const.Vacancy.Status.OPENING;
    }

    @Override
    public String toString() {
        return "" + id.toString();
    }
}
