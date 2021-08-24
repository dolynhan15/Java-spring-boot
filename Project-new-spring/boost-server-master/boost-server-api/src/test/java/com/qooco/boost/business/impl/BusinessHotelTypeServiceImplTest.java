package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCommonService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.HotelType;
import com.qooco.boost.data.oracle.services.HotelTypeService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResult;
import com.qooco.boost.models.dto.HotelTypeDTO;
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

import static com.qooco.boost.business.impl.BaseUserService.initAuthenticatedUser;

@RunWith(PowerMockRunner.class)
public class BusinessHotelTypeServiceImplTest {
    @InjectMocks
    private BusinessCommonService businessCommonService =  new BusinessCommonServiceImpl();
    @Mock
    private HotelTypeService hotelTypeService = Mockito.mock(HotelTypeService.class);

    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);

    private static AuthenticatedUser authenticatedUser = initAuthenticatedUser();

    @Test
    public void getHotelTypes_whenPageAndSizeAreValidValue_thenReturnHotelTypeResp() {
        int page = 1;
        int size = 2;
        List<HotelType> hotelTypeList = new ArrayList<>();
        hotelTypeList.add(new HotelType(1L));
        hotelTypeList.add(new HotelType(2L));

        Page<HotelType> hotelTypePage = new PageImpl<>(hotelTypeList);
        Mockito.when(hotelTypeService.getHotelTypes(page, size)).thenReturn(hotelTypePage);
        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        List<HotelTypeDTO> countryDTOList = hotelTypePage.getContent().stream().map(it -> new HotelTypeDTO(it, "")).collect(Collectors.toList());
        PagedResult<HotelTypeDTO> result = new PagedResult<>(countryDTOList, page, hotelTypePage.getSize(), hotelTypePage.getTotalPages(), hotelTypePage.getTotalElements(),
                hotelTypePage.hasNext(), hotelTypePage.hasPrevious());

        BaseResp actualResponse = businessCommonService.getHotelTypes(page, size, authentication);
        Assert.assertEquals(result.getSize(), ((PagedResult)actualResponse.getData()).getSize());
    }
}
