package com.qooco.boost.data.mongo.embedded.assessment;

import java.util.Date;

/*
 * Copyright: Falcon Team - AxonActive
 * User: ntlong
 * Date: 8/13/2018 - 10:24 AM
 */
public class AssessmentTestHistoryEmbedded {
    private Integer id;
    private Long userProfileId;
    private Long assessmentId;
    private AssessmentLevelEmbedded level;
    private String name;
    private String description;
    private double min;
    private double max;
    private String scaleId;
    private double score;
    private Date updatedDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public AssessmentLevelEmbedded getLevel() {
        return level;
    }

    public void setLevel(AssessmentLevelEmbedded level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public String getScaleId() {
        return scaleId;
    }

    public void setScaleId(String scaleId) {
        this.scaleId = scaleId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }
}