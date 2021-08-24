package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.data.oracle.services.ProvinceService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.ProvinceDTO;
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

@RunWith(PowerMockRunner.class)
public class BusinessProvinceServiceImplTest {

    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();
    @Mock
    private ProvinceService provinceService = Mockito.mock(ProvinceService.class);

    @Test
    public void getProvinces_whenPageAndSizeAreValidValue_thenReturnCityResp() {
        int page = 1;
        int size = 2;
        Long countryId = 1L;

        List<Province> provinceList = new ArrayList<>();
        provinceList.add(new Province(1L, new Country(1L)));
        provinceList.add(new Province(2L, new Country(1L)));
        Page<Province> provincePage = new PageImpl<>(provinceList);

        Mockito.when(provinceService.getProvinces(page, size, countryId)).thenReturn(provincePage);

        List<ProvinceDTO> countryDTOList = provincePage.getContent().stream().map(it -> new ProvinceDTO(it, null)).collect(Collectors.toList());
        PagedResult<ProvinceDTO> result = new PagedResult<>(countryDTOList, page, provincePage.getSize(), provincePage.getTotalPages(), provincePage.getTotalElements(),
                provincePage.hasNext(), provincePage.hasPrevious());

        BaseResp actualResponse = businessCommonService.getProvinces(page, size, countryId, initAuthentication());
        Assert.assertEquals(result.getSize(), ((PagedResult)actualResponse.getData()).getSize());
    }
}
