package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class LocationEmbedded {
    private Long id;
    private CityEmbedded city;
    private CompanyEmbedded company;
    private String address;

    public LocationEmbedded(LocationEmbedded locationEmbedded) {
        if (Objects.nonNull(locationEmbedded)) {
            id = locationEmbedded.getId();
            if (Objects.nonNull(locationEmbedded.getCity())) {
                city = new CityEmbedded(locationEmbedded.getCity());
            }
            if (Objects.nonNull(locationEmbedded.getCompany())) {
                company = new CompanyEmbedded(locationEmbedded.getCompany());
            }
            address = locationEmbedded.getAddress();
        }
    }

    public LocationEmbedded(Location location) {
        if (Objects.nonNull(location)) {
            id = location.getLocationId();
            if (Objects.nonNull(location.getCity())) {
                city = new CityEmbedded(location.getCity());
            }
            if (Objects.nonNull(location.getCompany())) {
                company = new CompanyEmbedded(location.getCompany());
            }
            address = location.getAddress();
        }
    }
}
