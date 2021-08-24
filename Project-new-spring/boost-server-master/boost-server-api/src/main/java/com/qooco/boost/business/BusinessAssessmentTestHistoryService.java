package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

public interface BusinessAssessmentTestHistoryService {
    BaseResp getTestHistoryByAssessment(Long userProfileId, Long assessmentId, String timeZone, Authentication authentication);
}
