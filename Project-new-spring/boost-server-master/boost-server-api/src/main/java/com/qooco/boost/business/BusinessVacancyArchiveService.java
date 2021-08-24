package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.EditVacancyRequest;
import org.springframework.security.core.Authentication;

public interface BusinessVacancyArchiveService {
    BaseResp getClosedCandidateOfVacancy(long id, int page, int size, Authentication authentication);

    @Deprecated
    BaseResp getSuspendVacancy(int page, int size, Authentication authentication);

    BaseResp getSuspendVacancyWithShortInfo(int page, int size, Authentication authentication);

    @Deprecated
    BaseResp getClosedVacancy(int page, int size, Authentication authentication);

    BaseResp getClosedVacancyWithShortInfo(int page, int size, Authentication authentication);

    BaseResp restoreOrCloneVacancy(Long id, EditVacancyRequest request, Authentication authentication);
}
