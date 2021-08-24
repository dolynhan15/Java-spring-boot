package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.SubmitPersonalAssessmentReq;
import org.springframework.security.core.Authentication;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 2:20 PM
*/
public interface BusinessPersonalAssessmentService {
    BaseResp getPersonalAssessment(Long userId, String locale, Authentication authentication);

    BaseResp savePersonalAssessmentAnswer(long userId, long assessmentId, SubmitPersonalAssessmentReq personalAssessmentTest);

    BaseResp savePersonalAssessmentAnswerV2(long userId, long assessmentId, SubmitPersonalAssessmentReq personalAssessmentTest);

    BaseResp getQuestions(Long id, String locale, Long userProfileId);

    BaseResp getPersonalTestResult(Long userProfileId, String locale, Long personalAssessmentId, Authentication authentication);

    BaseResp getPersonalTestResultV2(Long userProfileId, String locale, Long personalAssessmentId, Authentication authentication);
}
