package com.qooco.boost.data.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import java.util.Date;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/29/2018 - 10:19 AM
 */
@Setter @Getter
public class AssessmentTestLatest {
    @Id
    private String id;
    private Date submissionTime;
}
