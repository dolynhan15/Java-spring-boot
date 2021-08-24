package com.qooco.boost.models.request;

import lombok.Getter;

import java.util.Map;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 3:38 PM
*/
@Getter
public class SubmitPersonalAssessmentReq {

    private Map<Long, PersonalAssessmentAnswer> answers;
    private String locale;
}
