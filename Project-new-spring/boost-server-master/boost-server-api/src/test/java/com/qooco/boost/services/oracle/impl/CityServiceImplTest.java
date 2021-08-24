package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.data.oracle.reposistories.CityRepository;
import com.qooco.boost.data.oracle.services.CityService;
import com.qooco.boost.data.oracle.services.impl.CityServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
public class CityServiceImplTest {
    @InjectMocks
    private CityService cityService = new CityServiceImpl();

    @Mock
    private CityRepository cityRepository = Mockito.mock(CityRepository.class);

    @Test
    public void getCities_whenPageAndSizeIsZero_thenReturnGetAllCityInfo() {
        List<City> cityList = new ArrayList<>();
        cityList.add(new City(1L));
        cityList.add(new City(2L));
        cityList.add(new City(3L));
        cityList.add(new City(4L));
        cityList.add(new City(5L));
        cityList.add(new City(6L));
        cityList.add(new City(7L));
        cityList.add(new City(8L));
        cityList.add(new City(9L));
        cityList.add(new City(10L));
        Page<City> expectedResponse = new PageImpl<>(cityList);
        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.ASC, "cityName");

        Mockito.when(cityRepository.countByProvince(new Province(1L))).thenReturn(cityList.size());
        Mockito.when(cityRepository.findAllByProvince(new Province(1L), pageRequest)).thenReturn(expectedResponse);
        Assert.assertEquals(expectedResponse.getTotalElements(), cityService.getCities(0,Integer.MAX_VALUE, 1L).getTotalElements());
    }

    @Test
    public void getCities_whenPageAndSizeAndProvinceIdArePositiveValues_thenReturnCityPageResp() {
        List<City> cityList = new ArrayList<>();
        cityList.add(new City(1L));
        cityList.add(new City(2L));
        Page<City> expectedResponse = new PageImpl<>(cityList, PageRequest.of(1, 2, Sort.Direction.ASC, "cityName"), cityList.size());

        Province province = new Province(1L);
        Mockito.when(cityRepository.findAllByProvince(province, PageRequest.of(1, 2, Sort.Direction.ASC, "cityName"))).thenReturn(expectedResponse);

        Page<City> actualResp = cityService.getCities(1, 2, 1L);
        Assert.assertEquals(expectedResponse.getTotalElements(), actualResp.getTotalElements());
    }
}