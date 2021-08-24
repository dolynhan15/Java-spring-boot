package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.PersonalAssessment;

import java.util.List;

public interface PersonalAssessmentService {
    List<PersonalAssessment> getActivePersonalAssessment();

    PersonalAssessment findById(long id);

}
