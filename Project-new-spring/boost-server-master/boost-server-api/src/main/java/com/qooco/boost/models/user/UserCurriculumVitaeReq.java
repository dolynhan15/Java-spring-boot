package com.qooco.boost.models.user;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class UserCurriculumVitaeReq extends UserProfileStep {

    private boolean isHourSalary;
    private boolean isAsap;
    private boolean isFullTime;

    @Setter
    @Getter
    private double minSalary;
    @Setter
    @Getter
    private double maxSalary;
    @Setter
    @Getter
    private Date expectedStartDate;
    @Setter
    @Getter
    private String[] socialLinks;
    @Setter
    @Getter
    private String currencyCode;
    @Setter
    @Getter
    private Integer educationId;
    @Setter
    @Getter
    private long[] workHourIds;
    @Setter
    @Getter
    private long[] benefitIds;
    @Setter
    @Getter
    private long[] jobIds;
    @Setter
    @Getter
    private long[] softSkillIds;
    @Setter
    @Getter
    private Long[] preferredHotelIds;
    @Setter
    @Getter
    private String nationalId;
    @Setter
    @Getter
    private List<String> personalPhotos;
    @Setter
    @Getter
    @JsonIgnore
    private Long userProfileId;

    public boolean isHourSalary() {
        return isHourSalary;
    }

    @JsonProperty("isHourSalary")
    public void setHourSalary(boolean hourSalary) {
        isHourSalary = hourSalary;
    }

    public boolean isAsap() {
        return isAsap;
    }

    @JsonProperty("isAsap")
    public void setAsap(boolean asap) {
        isAsap = asap;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    @JsonProperty("isFullTime")
    public void setFullTime(boolean fullTime) {
        this.isFullTime = fullTime;
    }

    public UserCurriculumVitae toUserCurriculumVitae(UserCurriculumVitae userCurriculumVitae) {
        userCurriculumVitae = toUserCurriculumVitaeStepSaveJobProfile(userCurriculumVitae);
        Gson gson = new Gson();
        userCurriculumVitae.setSocialLinks(gson.toJson(socialLinks));
        return userCurriculumVitae;
    }

    public UserCurriculumVitae toUserCurriculumVitaeStepSaveJobProfile(UserCurriculumVitae userCurriculumVitae) {
        Date now = DateUtils.nowUtcForOracle();
        if (userCurriculumVitae == null) {
            userCurriculumVitae = new UserCurriculumVitae();
            userCurriculumVitae.setUpdatedBy(userProfileId);
            userCurriculumVitae.setCreatedBy(userProfileId);
            userCurriculumVitae.setCreatedDate(now);
        }
        userCurriculumVitae.setUpdatedDate(now);
        userCurriculumVitae.setAsap(isAsap);
        if (isAsap) {
            userCurriculumVitae.setExpectedStartDate(null);
        } else {
            if (expectedStartDate == null) {
                userCurriculumVitae.setAsap(true);
                userCurriculumVitae.setExpectedStartDate(null);
            } else {
                userCurriculumVitae.setExpectedStartDate(DateUtils.toUtcForOracle(expectedStartDate));
            }
        }
        userCurriculumVitae.setHourSalary(isHourSalary);
        userCurriculumVitae.setMaxSalary(maxSalary);
        userCurriculumVitae.setMinSalary(minSalary);
        userCurriculumVitae.setFullTime(isFullTime);
        return userCurriculumVitae;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof UserCurriculumVitaeReq;
    }

}
