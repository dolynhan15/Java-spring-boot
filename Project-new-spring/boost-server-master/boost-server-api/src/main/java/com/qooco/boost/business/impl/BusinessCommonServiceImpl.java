package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.oracle.services.*;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.*;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BusinessCommonServiceImpl implements BusinessCommonService {
    @Autowired
    private  CityService cityService;
    @Autowired
    private  HotelTypeService hotelTypeService;
    @Autowired
    private  BenefitService benefitService;
    @Autowired
    private  CountryService countryService;
    @Autowired
    private  CurrencyService currencyService;
    @Autowired
    private  EducationService educationService;
    @Autowired
    private  ProvinceService provinceService;
    @Autowired
    private  SoftSkillsService softSkillsService;
    @Autowired
    private  WorkingHourService workingHourService;

    @Override
    public BaseResp getCities(int page, int size, Long provinceId, Authentication authentication) {
        if (Objects.isNull(provinceId)) {
            throw new InvalidParamException(ResponseStatus.INVALID_PAGINATION);
        }
        Page<City> cityPage = cityService.getCities(page, size, provinceId);
        List<CityDTO> cityList = cityPage.getContent().stream().map(it -> new CityDTO(it, getLocale(authentication))).collect(Collectors.toList());
        PagedResult<CityDTO> result = new PagedResult<>(cityList, page, cityPage.getSize(), cityPage.getTotalPages(), cityPage.getTotalElements(),
                cityPage.hasNext(), cityPage.hasPrevious());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getHotelTypes(int page, int size, Authentication authentication) {
        Page<HotelType> hotelTypePage = hotelTypeService.getHotelTypes(page, size);
        List<HotelTypeDTO> hotelTypeList = hotelTypePage.getContent().stream().map(it -> new HotelTypeDTO(it, getLocale(authentication))).collect(Collectors.toList());
        PagedResult<HotelTypeDTO> result = new PagedResult<>(hotelTypeList, page, hotelTypePage.getSize(), hotelTypePage.getTotalPages(), hotelTypePage.getTotalElements(),
                hotelTypePage.hasNext(), hotelTypePage.hasPrevious());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getBenefits(int page, int size, Authentication authentication) {
        Page<Benefit> benefitPage = benefitService.getBenefits(page, size);
        List<BenefitDTO> benefitList = benefitPage.getContent().stream().map(it -> new BenefitDTO(it, getLocale(authentication))).collect(Collectors.toList());
        PagedResult<BenefitDTO> result = new PagedResult<>(benefitList, page, benefitPage.getSize(), benefitPage.getTotalPages(), benefitPage.getTotalElements(),
                benefitPage.hasNext(), benefitPage.hasPrevious());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getCountries(int page, int size, Authentication authentication) {
        Page<Country> countryPage = countryService.getCountries(page, size);
        List<CountryDTO> countryList = countryPage.getContent()
                .stream()
                .map(it -> new CountryDTO(it, getLocale(authentication)))
                .collect(Collectors.toList());
        PagedResult<CountryDTO> result = new PagedResult<>(countryList, page, countryPage.getSize(), countryPage.getTotalPages(), countryPage.getTotalElements(),
                countryPage.hasNext(), countryPage.hasPrevious());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getCurrencies(int page, int pageSize, Authentication authentication) {
        Page<Currency> pageCurrency = currencyService.getCurrencies(page, pageSize);
        List<CurrencyDTO> lstCurrency = pageCurrency.getContent().stream()
                .map(it -> new CurrencyDTO(it, getLocale(authentication))).collect(Collectors.toList());
        PagedResult<CurrencyDTO> result = new PagedResult<>(lstCurrency, page, pageCurrency.getSize(),
                pageCurrency.getTotalPages(), pageCurrency.getTotalElements(), pageCurrency.hasNext(),
                pageCurrency.hasPrevious());

        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getEducations(Authentication authentication) {
        List<Education> educations = educationService.getAll();
        List<EducationDTO> educationDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(educations)) {
            educationDTOs = educations.stream().map(it -> EducationDTO.init(it, getLocale(authentication))).collect(Collectors.toList());
        }
        return new BaseResp<>(educationDTOs);
    }

    @Override
    public BaseResp getProvinces(int page, int size, Long countryId, Authentication authentication) {
        Page<Province> provincePage = provinceService.getProvinces(page, size, countryId);
        List<ProvinceDTO> provinceList = provincePage.getContent().stream().map(it -> new ProvinceDTO(it, getLocale(authentication))).collect(Collectors.toList());
        PagedResult<ProvinceDTO> result = new PagedResult<>(provinceList, page, provincePage.getSize(), provincePage.getTotalPages(), provincePage.getTotalElements(),
                provincePage.hasNext(), provincePage.hasPrevious());
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp getSoftSkills(Authentication authentication) {
        List<SoftSkill> softSkills = softSkillsService.getAll();
        List<SoftSkillDTO> softSkillDTOs = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(softSkills)) {
            softSkillDTOs = softSkills.stream().map(it -> new SoftSkillDTO(it, getLocale(authentication))).collect(Collectors.toList());
        }
        return new BaseResp<>(softSkillDTOs);
    }

    @Override
    public BaseResp getWorkingHours(Authentication authentication) {
        List<WorkingHourDTO> workingHourDTOList = workingHourService.getAll().stream().map(WorkingHourDTO::new).collect(Collectors.toList());
        return new BaseResp<>(workingHourDTOList);
    }
}
