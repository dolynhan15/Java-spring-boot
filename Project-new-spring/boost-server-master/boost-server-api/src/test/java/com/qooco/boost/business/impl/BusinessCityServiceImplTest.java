package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.data.oracle.services.CityService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.CityDTO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(PowerMockRunner.class)
public class BusinessCityServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();
    @Mock
    private CityService cityService = Mockito.mock(CityService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    @Test
    public void getCities_whenPageAndSizeAreValidValue_thenReturnCityResp() {
        int page = 1;
        int size = 2;
        Long provinceId = 1L;

        List<City> cityList = new ArrayList<>();
        cityList.add(new City(1L, new Province(1L)));
        cityList.add(new City(2L, new Province(1L)));
        Page<City> cityPage = new PageImpl<>(cityList);

        Mockito.when(cityService.getCities(page, size, provinceId)).thenReturn(cityPage);

        List<CityDTO> countryDTOList = cityPage.getContent().stream().map(it -> new CityDTO(it, null)).collect(Collectors.toList());
        PagedResult<CityDTO> result = new PagedResult<>(countryDTOList, page, cityPage.getSize(), cityPage.getTotalPages(), cityPage.getTotalElements(),
                cityPage.hasNext(), cityPage.hasPrevious());
        AuthenticatedUser authenticatedUser = BaseUserService.initAuthenticatedUser();
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        BaseResp actualResponse = businessCommonService.getCities(page, size, provinceId, authentication);
        Assert.assertEquals(result.getSize(), ((PagedResult) actualResponse.getData()).getSize());
    }
}
