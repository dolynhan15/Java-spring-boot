package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.reposistories.PersonalAssessmentRepository;
import com.qooco.boost.data.oracle.services.PersonalAssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 2:06 PM
*/
@Service
public class PersonalAssessmentServiceImpl implements PersonalAssessmentService {

    @Autowired
    private PersonalAssessmentRepository personalAssessmentRepository;

    @Override
    public List<PersonalAssessment> getActivePersonalAssessment() {
        return personalAssessmentRepository.getActiveAPersonalAssessment();
    }

    @Override
    public PersonalAssessment findById(long id) {
        return personalAssessmentRepository.findValidById(id);
    }
}
