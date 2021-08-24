package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.vacancy.VacancyDTO;
import com.qooco.boost.models.request.ClassifyCandidateReq;
import com.qooco.boost.models.request.EditVacancyRequest;
import com.qooco.boost.models.request.VacancyV2Req;
import org.springframework.security.core.Authentication;

public interface BusinessVacancyService {
    BaseResp saveV2(Long id, VacancyV2Req vacancyReq, Authentication authentication);

    BaseResp save(Long id, VacancyV2Req vacancyReq, Authentication authentication);

    BaseResp get(Long id, Authentication authentication);

    BaseResp delete(Long id);

    BaseResp sync(Long id);

    BaseResp getOpeningVacanciesOfCompany(long companyId, long userProfileId, Authentication authentication);

    BaseResp classifyCandidate(ClassifyCandidateReq req, Authentication authentication);

    BaseResp editVacancy(EditVacancyRequest vacancyReq, Authentication authentication);

    BaseResp getOpeningVacancies(long companyId, Long userProfileId, int page, int size, Authentication authentication);

    BaseResp getOpeningVacanciesHavingAppointments(long companyId, Long userProfileId, int page, int size, Authentication authentication);

    BaseResp getCandidatesOfVacancy(long id, int page, int size, Authentication authentication);

    BaseResp suspendVacancy(long id, Integer suspendDays, Authentication authentication);

    BaseResp closeCandidateOfVacancy(long id, long candidateId, Authentication authentication);

    BaseResp<VacancyDTO> declineCandidateOfVacancy(long id, long candidateId, Authentication authentication);

    BaseResp syncAllVacancies(Authentication authentication);
}
