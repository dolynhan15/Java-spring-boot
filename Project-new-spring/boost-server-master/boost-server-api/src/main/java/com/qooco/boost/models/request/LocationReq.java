package com.qooco.boost.models.request;

import com.qooco.boost.data.oracle.entities.City;
import com.qooco.boost.data.oracle.entities.Company;
import com.qooco.boost.data.oracle.entities.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/23/2018 - 2:30 PM
*/
@Setter
@Getter
@NoArgsConstructor
public class LocationReq {
    private Long id;
    private long cityId;
    private Long companyId;
    private String address;

    public LocationReq(long cityId, Long companyId) {
        this.cityId = cityId;
        this.companyId = companyId;
    }

    public LocationReq(Long id, long cityId, Long companyId, String address) {
        this(cityId, companyId);
        this.id = id;
        this.address = address;
    }

    public Location toEntity(Location location) {
        location.setLocationId(id);
        location.setCity(new City(cityId));
        location.setCompany(new Company(companyId));
        if (Strings.isNotBlank(address)) {
            location.setAddress(address.trim());
        }
        return location;
    }
}
