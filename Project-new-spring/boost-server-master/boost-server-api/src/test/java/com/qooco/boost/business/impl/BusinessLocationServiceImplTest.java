package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessLocationService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Location;
import com.qooco.boost.data.oracle.entities.Staff;
import com.qooco.boost.data.oracle.services.CompanyService;
import com.qooco.boost.data.oracle.services.LocationService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.request.LocationReq;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;

/*
 * Copyright: Falcon Team - AxonActive
 * User: tnlong
 * Date: 8/2/2018 - 4:33 PM
 */
@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"javax.security.*"})
public class BusinessLocationServiceImplTest extends BaseUserService {
    @InjectMocks
    private BusinessLocationService businessLocationService = new BusinessLocationServiceImpl();
    @Mock
    private CompanyService companyService = Mockito.mock(CompanyService.class);
    @Mock
    private LocationService locationService = Mockito.mock(LocationService.class);
    @Mock
    private BusinessValidatorService businessValidatorService = Mockito.mock(BusinessValidatorService.class);
    @Mock
    private Authentication authentication = Mockito.mock(Authentication.class);
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void save_whenNullCompanyId_thenReturnInvalidParamExceptionResp() {
        thrown.expect(InvalidParamException.class);
        businessLocationService.save(new LocationReq(), authentication);
    }

    @Test
    public void save_whenUpdateOldLocationWithNewAddress_thenReturnLocationDTOSuccess() {
        Authentication authentication = initAuthentication();
        LocationReq locationReq = new LocationReq(1L, 1L, 1L, "123 Cach Mang Thang 8");
        Location location = new Location(1L, 1L);
        mockito(locationReq);
        Mockito.when(businessValidatorService.checkExistsLocation(locationReq.getId())).thenReturn(location);
        Assert.assertEquals(location.getLocationId(), ((LocationDTO) businessLocationService.save(locationReq, authentication).getData()).getLocationId());
    }

    @Test
    public void save_whenCreateNewLocationHavingAddress_thenReturnLocationDTOSuccess() {
        Authentication authentication = initAuthentication();
        LocationReq locationReq = new LocationReq(null, 1L, 1L, "123 Cach Mang Thang 8");
        mockito(locationReq);
        Assert.assertEquals(locationReq.getId(), ((LocationDTO) businessLocationService.save(locationReq, authentication).getData()).getLocationId());
    }

    @Test
    public void save_whenCreateNewLocationHavingNullAddress_thenReturnLocationDTOSuccess() {
        Authentication authentication = initAuthentication();
        LocationReq locationReq = new LocationReq(null, 1L, 1L, null);
        mockito(locationReq);
        Assert.assertEquals(locationReq.getCityId(), (long)((LocationDTO) businessLocationService.save(locationReq, authentication).getData()).getCity().getId());
    }

    @Test
    public void getLocation_whenCompanyIdIsNull_thenReturnCompanyIdIsEmptyError() {
        try {
            businessLocationService.getLocation(null, authentication);
        } catch (InvalidParamException ex) {
            Assert.assertEquals(ResponseStatus.COMPANY_ID_IS_EMPTY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getLocation_whenCompanyIdIsNotExistedInSystem_thenReturnNotFoundCompanyError() {
        Mockito.when(companyService.findById(1L)).thenReturn(null);
        try {
            businessLocationService.getLocation(1L, authentication);
        } catch (EntityNotFoundException ex) {
            Assert.assertEquals(ResponseStatus.NOT_FOUND_COMPANY.getCode(), ex.getStatus().getCode());
        }
    }

    @Test
    public void getLocation_whenCannotFindLocationListByCompanyId_thenReturnNotFoundError() {
        thrown.expect(EntityNotFoundException.class);
        Mockito.when(companyService.findById(1L)).thenReturn(new Company(1L));
        Mockito.when(locationService.findByCompanyId(1L)).thenReturn(null);
        businessLocationService.getLocation(1L, null);
    }

    @Test
    public void getLocation_whenFoundLocationListByCompanyId_thenReturnSuccess() {
        Mockito.when(companyService.findById(1L)).thenReturn(new Company(1L));
        List<Location> expectedLocations = new ArrayList<>();
        expectedLocations.add(new Location(1L));
        expectedLocations.add(new Location(2L));
        Mockito.when(locationService.findByCompanyId(1L)).thenReturn(expectedLocations);
        BaseResp expectedResp = new BaseResp<>(expectedLocations);
        Assert.assertEquals(expectedResp.getCode(), businessLocationService.getLocation(1L, initAuthentication()).getCode());
    }

    /* ============================================= Prepare data for unit test =================================================== */
    private void mockito(LocationReq locationReq) {
        AuthenticatedUser authenticatedUser = initAuthenticatedUser();
        Location location = new Location(1L, 2L, "12 Lien Chieu", new City(1L));

        Mockito.when(authentication.getPrincipal()).thenReturn(authenticatedUser);
        Mockito.when(businessValidatorService.checkExistsCompany(locationReq.getCompanyId())).thenReturn(new Company(1L));
        Mockito.when(businessValidatorService.checkExistsCity(locationReq.getCityId())).thenReturn(new City(1L));
        Mockito.when(businessValidatorService.checkExistsStaffInApprovedCompany(locationReq.getCompanyId(), authenticatedUser.getId())).thenReturn(new Staff(1L));
        Mockito.when(businessValidatorService.checkExistsLocation(locationReq.getId())).thenReturn(location);
        Mockito.when(locationService.save(location)).thenReturn(location);
        Mockito.when(locationService.findByCompanyIdAndCityIdAndAddress(locationReq.getCompanyId(), locationReq.getCityId(), locationReq.getAddress())).thenReturn(location);
        Mockito.when(locationService.findByCompanyIdAndCityIdAndNullAddress(locationReq.getCompanyId(), locationReq.getCityId())).thenReturn(location);
    }
}
