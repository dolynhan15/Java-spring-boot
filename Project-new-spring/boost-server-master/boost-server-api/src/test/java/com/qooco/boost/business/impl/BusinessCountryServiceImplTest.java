package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.services.CountryService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.CountryDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.qooco.boost.business.impl.BaseUserService.initAuthentication;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:11 PM
 */
@RunWith(PowerMockRunner.class)
public class BusinessCountryServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();

    @Mock
    private CountryService countryService = Mockito.mock(CountryService.class);

    @Test
    public void getCountries_whenPageAndSizeAreValidValue_thenReturnCountryResp() {
        int page = 1;
        int size = 10;
        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1L));
        countryList.add(new Country(2L));

        Page<Country> countryPage = new PageImpl<>(countryList);
        Mockito.when(countryService.getCountries(page, size)).thenReturn(countryPage);
        List<CountryDTO> countryDTOList = countryPage.getContent().stream().map(it -> new CountryDTO(it, null)).collect(Collectors.toList());
        PagedResult<CountryDTO> result = new PagedResult<>(countryDTOList, page, countryPage.getSize(), countryPage.getTotalPages(), countryPage.getTotalElements(),
                countryPage.hasNext(), countryPage.hasPrevious());

        BaseResp actualResponse = businessCommonService.getCountries(page, size, initAuthentication());
        Assert.assertEquals(result.getSize(), ((PagedResult)actualResponse.getData()).getSize());
    }
}
