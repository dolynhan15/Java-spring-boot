package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

public interface BusinessAssessmentService {
    BaseResp getAssessments(int page, int size);

    BaseResp deleteAssessment(Long id);

    BaseResp getAssessmentsByType(int page, int size, Integer type);

    BaseResp getUserQualification(Long userProfileId, String scaleId, boolean isHomepage, Authentication authentication);

    BaseResp syncDataOfEachUser(Long userProfileId);

    BaseResp syncDataFromQooco(Authentication authentication);
}
