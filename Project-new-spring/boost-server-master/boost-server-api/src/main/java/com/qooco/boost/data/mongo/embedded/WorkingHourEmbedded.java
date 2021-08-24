package com.qooco.boost.data.mongo.embedded;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/7/2018 - 1:48 PM
*/
public class WorkingHourEmbedded {

    private Long id;
    private String description;
    private String startDate;
    private String endDate;
    private boolean isFullTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isFullTime() {
        return isFullTime;
    }

    public void setFullTime(boolean fullTime) {
        isFullTime = fullTime;
    }
}
