package com.qooco.boost.services.oracle.impl;

import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Location;
import com.qooco.boost.data.oracle.reposistories.LocationRepository;
import com.qooco.boost.data.oracle.services.LocationService;
import com.qooco.boost.data.oracle.services.impl.LocationServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/3/2018 - 8:47 AM
 */
@RunWith(PowerMockRunner.class)
public class LocationServiceImplTest {

    @InjectMocks
    private LocationService locationService = new LocationServiceImpl();

    @Mock
    private LocationRepository locationRepository = Mockito.mock(LocationRepository.class);

    @Test
    public void save_whenExistLocation_thenReturnLocationResp() {
        Location location = new Location();
        location.setCity(new City(1L));
        location.setCompany(new Company(1L));

        Mockito.when(locationRepository.save(location))
                .thenReturn(location);

        Location actualLocationResp = locationService.save(location);
        Assert.assertEquals(location, actualLocationResp);
    }

    @Test
    public void save_whenNotExistLocation_thenReturnNullResp() {
        Location location = new Location();

        Mockito.when(locationRepository.save(location))
                .thenReturn(location);

        Location actualLocationResp = locationService.save(location);
        Assert.assertEquals(location, actualLocationResp);
    }

    @Test
    public void findByCompanyAndCityId_whenHaveExistLocation_thenReturnLocationResp() {
        Location location = new Location();
        location.setCity(new City(1L));
        location.setCompany(new Company(1L));

        Mockito.when(locationRepository.findByCompanyIdAndCityIdAndNullAddress(1L, 1L))
                .thenReturn(location);

        Location actualLocationResp = locationService.findByCompanyIdAndCityIdAndNullAddress(1L, 1L);
        Assert.assertEquals(location, actualLocationResp);
    }

    @Test
    public void findByCompanyId_whenCompanyIdIsNotExistedInSystem_thenReturnNullLocationList() {
        Mockito.when(locationRepository.findAllByCompanyCompanyId(1L)).thenReturn(null);
        Assert.assertNull(locationService.findByCompanyId(1L));
    }

    @Test
    public void findByCompanyId_whenCompanyIdExistedInSystem_thenReturnLocationList() {
        List<Location> expectedLocations = new ArrayList<>();
        expectedLocations.add(new Location(1L));
        expectedLocations.add(new Location(2L));
        Mockito.when(locationRepository.findAllByCompanyCompanyId(1L)).thenReturn(expectedLocations);
        Assert.assertEquals(expectedLocations.size(), locationService.findByCompanyId(1L).size());
    }
}