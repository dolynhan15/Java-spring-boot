package com.qooco.boost.data.mongo.embedded;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@FieldNameConstants
@SuperBuilder(builderMethodName = "userProfileCvEmbeddedBuilder", toBuilder = true)
public class UserProfileCvEmbedded extends UserProfileEmbedded {

    private Long userProfileCvId;
    private boolean isHourSalary;
    private double minSalary;
    private double maxSalary;
    private boolean isAsap;
    private Date expectedStartDate;
    private boolean isFullTime;
    private CurrencyEmbedded currency;
    private EducationEmbedded education;
    private List<JobEmbedded> jobs;
    private List<CompanyEmbedded> preferredHotels;
    private int profileStrength;
    private boolean hasPersonality;

    public UserProfileCvEmbedded(Long userProfileId, int userType) {
        super(userProfileId, userType);
    }

    public UserProfileCvEmbedded(UserProfileEmbedded embedded) {
        super(embedded);
    }

    public UserProfileCvEmbedded(UserProfileBasicEmbedded userProfileBasicEmbedded) {
        super(userProfileBasicEmbedded);
    }

    public UserProfileCvEmbedded(UserProfileCvEmbedded userCV) {
        super(userCV);
        this.userProfileCvId = userCV.getUserProfileCvId();
        this.isHourSalary = userCV.isHourSalary();
        this.minSalary = userCV.getMinSalary();
        this.maxSalary = userCV.getMaxSalary();
        this.isAsap = userCV.isAsap();
        this.expectedStartDate = userCV.getExpectedStartDate();
        this.isFullTime = userCV.isFullTime();
        this.currency = userCV.getCurrency();
        this.education = userCV.getEducation();
        this.jobs = userCV.getJobs();
        this.profileStrength = userCV.getProfileStrength();
        this.hasPersonality = userCV.isHasPersonality();
        this.preferredHotels = userCV.getPreferredHotels();
    }

    public UserProfileCvEmbedded(UserProfileCvEmbedded userCV, int type) {
        this(userCV);
        setUserType(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserProfileCvEmbedded that = (UserProfileCvEmbedded) o;
        return Objects.equals(userProfileCvId, that.userProfileCvId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), userProfileCvId);
    }
}
