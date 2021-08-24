package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/8/2018 - 5:41 PM
*/
public interface BusinessMatchingCandidateService extends BaseBusinessService{

    BaseResp findMatchingCvForVacancy(long userProfileId, long vacancyId, int size, Authentication authentication);

    BaseResp findMatchingCvForVacancyWithComparison(long userProfileId, long vacancyId, int size, int offset, int sortType, Authentication authentication);

    BaseResp findMatchingCvForVacancyWithAssessmentSort(long userProfileId, long vacancyId, int size, int offset, long assessmentId, Authentication authentication);
}
