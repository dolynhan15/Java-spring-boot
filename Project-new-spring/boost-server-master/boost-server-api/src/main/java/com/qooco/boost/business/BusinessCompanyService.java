package com.qooco.boost.business;

import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.CompanyReq;
import org.springframework.security.core.Authentication;

import javax.validation.constraints.NotNull;
import java.util.List;

public interface BusinessCompanyService {
    BaseResp get(Long companyId, Authentication authentication) throws EntityNotFoundException;

    BaseResp getCompanyByStatus(String status, Authentication authentication);

    BaseResp getWorkingCompanies(List<Integer> types, Authentication authentication);

    BaseResp approveNewCompany(Long companyId, Long userProfileId);

    BaseResp saveCompany(CompanyReq companyReq, Authentication authentication);

    BaseResp getCompanyOfUser(long userProfileId, Authentication authentication);

    BaseResp searchCompanyByName(String keyword, int page, int size);

    BaseResp searchCompanyByNameForJoinCompany(String keyword, int page, int size, Authentication authentication);

    BaseResp joinCompanyRequest(Long companyId, Long userProfileId);

    BaseResp getShortCompany(Long companyId, Authentication authentication);

    BaseResp updateJoinCompanyStatusRequest(String messageId, int status, long userProfileId, Authentication authentication);

    BaseResp getJoinCompanyRequest(Long companyId, long userProfileId);

    BaseResp getCompanyProfileInfo(@NotNull Long companyId, Authentication authentication);

    BaseResp switchCompany(long id, Authentication authentication);

    BaseResp findByCountryAndStatusApproved(long countryId, int page, int size);
}
