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
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.LocationDTO;
import com.qooco.boost.models.dto.LocationFullDTO;
import com.qooco.boost.models.request.LocationReq;
import com.qooco.boost.utils.DateUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class BusinessLocationServiceImpl implements BusinessLocationService {

    @Autowired
    private LocationService locationService;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private BusinessValidatorService businessValidatorService;

    @Override
    public BaseResp save(LocationReq locationReq, Authentication authentication) {
        if (Objects.isNull(locationReq.getCompanyId())) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }

        Long updateOwner = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        businessValidatorService.checkExistsStaffInApprovedCompany(locationReq.getCompanyId(), updateOwner);

        Company company = businessValidatorService.checkExistsCompany(locationReq.getCompanyId());
        City city = businessValidatorService.checkExistsCity(locationReq.getCityId());

        Location location;
        if (Objects.nonNull(locationReq.getId())) {
            location = businessValidatorService.checkExistsLocation(locationReq.getId());
            if (location.getIsUsed() || location.getIsPrimary()) {
                throw new NoPermissionException(ResponseStatus.IN_USING);
            }

            Location existsLocation = checkExistsLocation(locationReq);
            if(Objects.nonNull(existsLocation)){
                throw new InvalidParamException(ResponseStatus.LOCATION_IS_EXIST);
            }

            location.setAddress(locationReq.getAddress());
            location.setCity(city);
            location.setUpdatedDate(DateUtils.nowUtc());
            location.setUpdatedBy(updateOwner);
            locationService.save(location);
        } else {
            location = checkExistsLocation(locationReq);
            if (Objects.isNull(location)) {
                location = new Location(null, updateOwner);
                locationReq.toEntity(location);
                location.setCompany(company);
                location.setCity(city);
                locationService.save(location);
            }
        }

        LocationDTO locationDTO = new LocationDTO(location, ((AuthenticatedUser) authentication.getPrincipal()).getLocale());
        locationDTO.setCompanyId(locationReq.getCompanyId());
        return new BaseResp<>(locationDTO);
    }

    private Location checkExistsLocation(LocationReq locationReq) {
        Location location;
        String address = locationReq.getAddress();
        if (Strings.isNotBlank(address)) {
            address = address.trim().toLowerCase();
            location = locationService.findByCompanyIdAndCityIdAndAddress(locationReq.getCompanyId(), locationReq.getCityId(), address);
        } else {
            location = locationService.findByCompanyIdAndCityIdAndNullAddress(locationReq.getCompanyId(), locationReq.getCityId());
        }
        return location;
    }

    @Override
    public BaseResp getLocationByCompany(long companyId, int page, int size, Authentication authentication) {
        Long updateOwner = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        Staff userStaff = businessValidatorService.checkExistsStaffInCompany(companyId, updateOwner);
        Page<Location> locations = locationService.findByCompanyId(companyId, page, size);
        List<LocationDTO> locationDTOs = locations.getContent().stream().map(l -> new LocationFullDTO(l, userStaff, ((AuthenticatedUser) authentication.getPrincipal()).getLocale())).collect(Collectors.toList());
        PagedResultV2 result = new PagedResultV2<>(locationDTOs, page, locations);
        return new BaseResp<>(result);
    }

    @Override
    public BaseResp deleteLocation(long id, Authentication authentication) {
        Long updateOwner = ((AuthenticatedUser) authentication.getPrincipal()).getId();
        Location location = businessValidatorService.checkExistsLocation(id);
        businessValidatorService.checkExistsStaffInCompany(location.getCompany().getCompanyId(), updateOwner);
        if (location.getIsPrimary() || location.getIsUsed()) {
            throw new NoPermissionException(ResponseStatus.NO_PERMISSION_TO_DELETE);
        }
        location.setIsDeleted(true);
        locationService.save(location);
        return new BaseResp();
    }

    @Override
    public BaseResp getLocation(Long companyId, Authentication authentication) {
        if (Objects.isNull(companyId)) {
            throw new InvalidParamException(ResponseStatus.COMPANY_ID_IS_EMPTY);
        }
        Company foundCompany = companyService.findById(companyId);
        if (Objects.isNull(foundCompany)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_COMPANY);
        }
        List<Location> locations = locationService.findByCompanyId(companyId);
        if (Objects.isNull(locations)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_LOCATION);
        }
        List<LocationDTO> expectedLocations = locations.stream().map(it -> new LocationDTO(it, ((AuthenticatedUser) authentication.getPrincipal()).getLocale())).collect(Collectors.toList());
        return new BaseResp<>(expectedLocations);
    }
}
