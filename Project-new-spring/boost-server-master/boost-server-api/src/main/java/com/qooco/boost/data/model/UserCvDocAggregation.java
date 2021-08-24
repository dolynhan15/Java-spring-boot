package com.qooco.boost.data.model;

import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class UserCvDocAggregation {
    private Date maxEndDate;
    private Date minStartDate;
    private Long dateDifference;
    private Integer assessmentLevel;
    private List<QualificationEmbedded> qualificationEmbeddedList;
    private List<CompanyEmbedded> preferredHotelsEmbeddedList;

    private Long id;
    private boolean isHourSalary;
    private double minSalary;
    private double maxSalary;
    private boolean isAsap;
    private Date expectedStartDate;
    private boolean isFullTime;
    private List<String> socialLinks;
    private CurrencyEmbedded currency;
    private EducationEmbedded education;
    private List<WorkingHourEmbedded> desiredHours;
    private List<BenefitEmbedded> benefits;
    private List<JobEmbedded> jobs;
    private List<PreviousPositionEmbedded> previousPositions;
    private List<SoftSkillEmbedded> softSkills;
    private List<CompanyEmbedded> preferredHotels;
    private UserProfileEmbedded userProfile;
    private int profileStrength;
    private Date updatedDate;
    private List<QualificationEmbedded> qualifications;
    private boolean hasPersonality;
    private Date startWorking;
    private Date endWorking;
}
