package com.qooco.boost.controllers;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.business.BusinessCurrencyExchangeService;
import com.qooco.boost.constants.StatusConstants;
import com.qooco.boost.constants.URLConstants;
import com.qooco.boost.models.BasePagerResp;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.*;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import com.qooco.boost.models.request.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Common API: Country, Province, City, ...")
@RestController
@RequestMapping(URLConstants.PATA_PATH)
public class CommonController extends BaseController {

    @Autowired
    private BusinessCommonService businessCommonService;
    @Autowired
    private BusinessCurrencyExchangeService currencyExchangeService;

    @ApiOperation(value = "Get benefit", httpMethod = "GET", response = BenefitResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.BENEFIT_PATH + URLConstants.GET_METHOD)
    public Object getBenefits(@Valid PageRequest request, Authentication authentication) {
        BaseResp benefits = businessCommonService.getBenefits(request.getPage(), request.getSize(), authentication);
        return success(benefits);
    }

    @ApiOperation(value = "Get country", httpMethod = "GET", response = CountryResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.COUNTRY_PATH + URLConstants.GET_METHOD)
    public Object getCountries(@Valid PageRequest request, Authentication authentication) {
        BaseResp countries = businessCommonService.getCountries(request.getPage(), request.getSize(), authentication);
        return success(countries);
    }

    @ApiOperation(value = "Get provinces", httpMethod = "GET", response = ProvinceResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.PROVINCE_PATH + URLConstants.GET_METHOD)
    public Object getProvinces(@Valid PageRequest request,
                               @RequestParam(value = "countryId", required = false) Long countryId,
                               Authentication authentication) {
        BaseResp provinces = businessCommonService.getProvinces(request.getPage(), request.getSize(), countryId, authentication);
        return success(provinces);
    }

    @ApiOperation(value = "Get cities", httpMethod = "GET", response = CityResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.CITY_PATH + URLConstants.GET_METHOD)
    public Object getCities(@Valid PageRequest request,
                            @RequestParam(value = "provinceId", required = false) Long provinceId,
                            Authentication authentication) {
        BaseResp cities = businessCommonService.getCities(request.getPage(), request.getSize(), provinceId, authentication);
        return success(cities);
    }

    @ApiOperation(value = "Get currency", httpMethod = "GET", response = PageCurrency.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.CURRENCY_PATH + URLConstants.GET_METHOD)
    public Object getCurrencies(@Valid PageRequest request, Authentication authentication) {
        BaseResp currencies = businessCommonService.getCurrencies(request.getPage(), request.getSize(), authentication);
        return success(currencies);
    }

    @ApiOperation(value = "Sync currencies rate", httpMethod = "PATCH", response = BaseResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @PatchMapping(URLConstants.CURRENCY_PATH)
    public Object syncCurrencyRates() {
        return success(currencyExchangeService.syncCurrencyExchangeRates());
    }

    @ApiOperation(value = "Get educations", httpMethod = "GET", response = EducationResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.EDUCATION_PATH + URLConstants.GET_METHOD)
    public Object getEducations(Authentication authentication) {
        BaseResp result = businessCommonService.getEducations(authentication);
        return success(result);
    }

    @ApiOperation(value = "Get hotel types", httpMethod = "GET", response = HotelTypesResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.HOTEL_TYPE_PATH + URLConstants.GET_METHOD)
    public Object getHotelTypes(@Valid PageRequest request, Authentication authentication) {
        BaseResp hotelTypes = businessCommonService.getHotelTypes(request.getPage(), request.getSize(), authentication);
        return success(hotelTypes);
    }

    @ApiOperation(value = "Get soft skills", httpMethod = "GET", response = SoftSkillResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.SOFT_SKILL_PATH + URLConstants.GET_METHOD)
    public Object get(Authentication authentication) {
        BaseResp result = businessCommonService.getSoftSkills(authentication);
        return success(result);
    }

    @ApiOperation(value = "Get Working Hours", httpMethod = "GET", response = WorkingHourResp.class, notes = NOTE)
    @PreAuthorize("isAuthenticated()")
    @GetMapping(URLConstants.WORKING_HOUR_PATH + URLConstants.GET_METHOD)
    public Object getWorkingHours(Authentication authentication) {
        BaseResp workingHoursResp = businessCommonService.getWorkingHours(authentication);
        return success(workingHoursResp);
    }

    private static final String NOTE = "Response code description:"
            + "<br>" + StatusConstants.SUCCESS + " : " + StatusConstants.SUCCESS_MESSAGE
            + "<br>" + StatusConstants.BAD_REQUEST + " : " + StatusConstants.BAD_REQUEST_MESSAGE;

    class WorkingHourResp extends BaseResp<List<WorkingHourDTO>> {
    }

    private class SoftSkillResp extends BaseResp<List<SoftSkillDTO>> {
    }

    private class HotelTypesResp extends BaseResp<List<HotelTypeDTO>> {
    }


    private class EducationResp extends BaseResp<List<EducationDTO>> {
    }

    private class PageCurrency extends BaseResp<PagedResult<CurrencyDTO>> {
    }

    private class CityResp extends BaseResp<PagedResult<CityDTO>> {
    }

    private class ProvinceResp extends BaseResp<PagedResult<ProvinceDTO>> {
    }

    private class CountryResp extends BaseResp<PagedResult<CountryDTO>> {
    }

    private class BenefitResp extends BasePagerResp<List<BenefitDTO>> {
    }
}
