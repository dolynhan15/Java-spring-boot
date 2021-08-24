package com.qooco.boost.data.mongo.entities;


import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import lombok.AllArgsConstructor;
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

@Document(collection = "UserCvDoc")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class UserCvDoc {
    @Id
    private Long id;
    private boolean isHourSalary;
    private double minSalary;
    private double maxSalary;
    @Indexed(direction = IndexDirection.ASCENDING)
    private double minSalaryUsd;
    @Indexed(direction = IndexDirection.DESCENDING)
    private double maxSalaryUsd;
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

    public UserCvDoc(Long id) {
        this.id = id;
    }

}
