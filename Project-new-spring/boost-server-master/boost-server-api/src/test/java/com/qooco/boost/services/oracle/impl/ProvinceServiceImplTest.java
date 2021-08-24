package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.data.oracle.reposistories.ProvinceRepository;
import com.qooco.boost.data.oracle.services.impl.ProvinceServiceImpl;
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
public class ProvinceServiceImplTest {
    @InjectMocks
    private ProvinceServiceImpl provinceService = new ProvinceServiceImpl();

    @Mock
    private ProvinceRepository provinceRepository = Mockito.mock(ProvinceRepository.class);

    @Test
    public void getProvinces_whenPageAndSizeAndProvinceIdArePositiveValues_thenReturnProvincePageResp() {
        List<Province> provinceList = new ArrayList<>();
        provinceList.add(new Province(1L));
        provinceList.add(new Province(2L));
        Page<Province> expectedResponse = new PageImpl<>(provinceList, PageRequest.of(1, 2, Sort.Direction.ASC, "name"), provinceList.size());

        Country country = new Country(1L);
        Mockito.when(provinceRepository.findAllByCountry(country, PageRequest.of(1, 2, Sort.Direction.ASC, "name"))).thenReturn(expectedResponse);

        Page<Province> actualResp = provinceService.getProvinces(1, 2, 1L);
        Assert.assertEquals(expectedResponse.getTotalElements(), actualResp.getTotalElements());
    }
}