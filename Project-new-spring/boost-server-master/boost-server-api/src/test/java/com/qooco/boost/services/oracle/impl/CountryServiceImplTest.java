package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.reposistories.CountryRepository;
import com.qooco.boost.data.oracle.services.CountryService;
import com.qooco.boost.data.oracle.services.impl.CountryServiceImpl;
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

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:12 PM
 */
@RunWith(PowerMockRunner.class)
public class CountryServiceImplTest {
    @InjectMocks
    private CountryService countryService = new CountryServiceImpl();

    @Mock
    private CountryRepository countryRepository = Mockito.mock(CountryRepository.class);

    @Test
    public void getCountries_whenPageIsNegativeAndSizeHasValue_thenReturnAllCountryPageResp() {
        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1L));
        countryList.add(new Country(2L));

        Mockito.when(countryRepository.count()).thenReturn(2L);

        Page<Country> expectedResponse = new PageImpl<>(countryList, PageRequest.of(0, 2, Sort.Direction.ASC, "countryName"), countryList.size());
        Mockito.when(countryRepository.findAll(PageRequest.of(0, (int) countryRepository.count(), Sort.Direction.ASC, "countryName"))).thenReturn(expectedResponse);

        Page<Country> actualResp = countryService.getCountries(0, 2);
        Assert.assertEquals(2L, actualResp.getTotalElements());
    }

    @Test
    public void getCountries_whenPageAndSizeAreValidNumbers_thenReturnCountryPageResp() {
        List<Country> countryList = new ArrayList<>();
        countryList.add(new Country(1L));
        countryList.add(new Country(2L));

        PageRequest pageRequest = PageRequest.of(1, 3, Sort.Direction.ASC, "countryName");
        Page<Country> expectedResponse = new PageImpl<>(countryList, pageRequest, countryList.size());

        Mockito.when(countryRepository.findAll(pageRequest)).thenReturn(expectedResponse);

        Page<Country> actualResp = countryService.getCountries(1, 3);
        Assert.assertEquals(3, actualResp.getSize());
    }
}