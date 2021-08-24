package com.qooco.boost.data.model;

import java.util.Date;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/17/2018 - 4:24 PM
 */
public class AssessmentLatest {
    private Long id;
    private Date submissionTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(Date submissionTime) {
        this.submissionTime = submissionTime;
    }
}
