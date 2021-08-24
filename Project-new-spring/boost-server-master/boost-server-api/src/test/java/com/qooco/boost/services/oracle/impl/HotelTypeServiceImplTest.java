package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.HotelType;
import com.qooco.boost.data.oracle.reposistories.HotelTypeRepository;
import com.qooco.boost.data.oracle.services.HotelTypeService;
import com.qooco.boost.data.oracle.services.impl.HotelTypeServiceImpl;
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
public class HotelTypeServiceImplTest {
    @InjectMocks
    private HotelTypeService hotelTypeService = new HotelTypeServiceImpl();

    @Mock
    private HotelTypeRepository hotelTypeRepository = Mockito.mock(HotelTypeRepository.class);

    @Test
    public void getHotelTypes_whenPageIsNegativeAndSizeHasValue_thenReturnAllHotelTypePageResp() {
        List<HotelType> hotelTypeList = new ArrayList<>();
        hotelTypeList.add(new HotelType(1L));
        hotelTypeList.add(new HotelType(2L));
        Page<HotelType> expectedResponse = new PageImpl<>(hotelTypeList, PageRequest.of(0, 2, Sort.Direction.ASC, "hotelTypeName"), hotelTypeList.size());

        Mockito.when(hotelTypeRepository.count()).thenReturn(2L);
        Mockito.when(hotelTypeRepository.findAll(PageRequest.of(0, (int) hotelTypeRepository.count(), Sort.Direction.ASC, "hotelTypeName"))).thenReturn(expectedResponse);

        Assert.assertEquals(expectedResponse.getTotalElements(), hotelTypeService.getHotelTypes(0, 2).getTotalElements());
        Assert.assertEquals(expectedResponse.getTotalElements(), hotelTypeService.getHotelTypes(0, 2).getTotalElements());
    }

    @Test
    public void getHotelTypes_whenPageAndSizeAreValidNumbers_thenReturnHotelTypePageResp() {
        List<HotelType> hotelTypeList = new ArrayList<>();
        hotelTypeList.add(new HotelType(1L));
        hotelTypeList.add(new HotelType(2L));

        PageRequest pageRequest = PageRequest.of(1, 3, Sort.Direction.ASC, "hotelTypeName");
        Page<HotelType> expectedResponse = new PageImpl<>(hotelTypeList, pageRequest, hotelTypeList.size());

        Mockito.when(hotelTypeRepository.findAll(pageRequest)).thenReturn(expectedResponse);

        Page<HotelType> actualResp = hotelTypeService.getHotelTypes(1, 3);
        Assert.assertEquals(3, actualResp.getSize());
    }
}