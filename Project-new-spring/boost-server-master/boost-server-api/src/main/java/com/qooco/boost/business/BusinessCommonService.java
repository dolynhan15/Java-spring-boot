package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

public interface BusinessCommonService extends BaseBusinessService {
    BaseResp getCountries(int page, int size, Authentication authentication);
    BaseResp getProvinces(int page, int size, Long countryId, Authentication authentication);
    BaseResp getCities(int page, int size, Long provinceId, Authentication authentication);
    BaseResp getBenefits(int page, int size, Authentication authentication);
    BaseResp getCurrencies(int page, int pageSize, Authentication authentication);
    BaseResp getHotelTypes(int page, int size, Authentication authentication);
    BaseResp getSoftSkills(Authentication authentication);
    BaseResp getWorkingHours(Authentication authentication);
    BaseResp getEducations(Authentication authentication);
}
