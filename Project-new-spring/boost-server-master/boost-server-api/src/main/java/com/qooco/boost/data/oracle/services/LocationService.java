package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.model.count.CountByCompany;
import com.qooco.boost.data.oracle.entities.Location;
import org.springframework.data.domain.Page;

import java.util.List;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/24/2018 - 2:48 PM
*/
public interface LocationService {
    Location findById(Long id);

    Location findByCompanyIdAndCityIdAndAddress(Long companyId, Long cityId, String address);

    List<Location> findByCompanyIdAndCityId(Long companyId, List<Long> cityIds);

    List<Location> findByIds(List<Long> id);

    Location save(Location location);

    List<Location> save(List<Location> locations);

    Location findByCompanyIdAndCityIdAndNullAddress(Long companyId, Long locationId);

    List<Location> findByIdsAndCompanyId(Long companyId, List<Long> ids);

    List<Location> findByCompanyId(Long companyId);

    int countByCompany(Long companyId);

    List<CountByCompany> findAllByCompanyCompanyIds(List<Long> companyIds);

    Page<Location> findByCompanyId(long companyId, int page, int size);

    void updateNonePrimaryForCompany(long companyId);

}