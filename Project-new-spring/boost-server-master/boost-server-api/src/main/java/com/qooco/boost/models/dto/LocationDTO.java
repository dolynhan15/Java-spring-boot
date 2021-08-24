package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.LocationEmbedded;
import com.qooco.boost.data.oracle.entities.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationDTO {
    private Long locationId;
    private CityDTO city;
    private Long companyId;
    private String address;

    public LocationDTO(Location location, String locale) {
        if (Objects.nonNull(location)) {
            this.locationId = location.getLocationId();
            this.address = location.getAddress();
            if (Objects.nonNull(location.getCompany())) {
                this.companyId = location.getCompany().getCompanyId();
            }
            if (Objects.nonNull(location.getCity())) {
                this.city = new CityDTO(location.getCity(), locale);
            }
        }
    }

    public LocationDTO(LocationEmbedded location, String locale) {
        if (Objects.nonNull(location)) {
            this.locationId = location.getId();
            this.address = location.getAddress();
            if (Objects.nonNull(location.getCompany())) {
                this.companyId = location.getCompany().getId();
            }
            if (Objects.nonNull(location.getCity())) {
                this.city = new CityDTO(location.getCity(), locale);
            }
        }
    }
}
