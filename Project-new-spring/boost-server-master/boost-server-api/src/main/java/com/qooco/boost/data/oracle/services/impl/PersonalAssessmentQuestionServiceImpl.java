package com.qooco.boost.data.oracle.services.impl;

import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import com.qooco.boost.data.oracle.reposistories.PersonalAssessmentQuestionRepository;
import com.qooco.boost.data.oracle.services.PersonalAssessmentQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 2:08 PM
*/
@Service
public class PersonalAssessmentQuestionServiceImpl implements PersonalAssessmentQuestionService {
    @Autowired
    private PersonalAssessmentQuestionRepository personalAssessmentQuestionRepository;

    @Override
    public List<PersonalAssessmentQuestion> getByPersonalAssessmentId(long personalAssessmentId) {
        return personalAssessmentQuestionRepository.getByPersonalAssessmentId(personalAssessmentId);
    }
}
