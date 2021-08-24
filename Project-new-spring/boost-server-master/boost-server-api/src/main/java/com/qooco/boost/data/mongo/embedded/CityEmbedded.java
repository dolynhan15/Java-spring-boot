package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.City;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Setter
@Getter
@NoArgsConstructor
@FieldNameConstants
public class CityEmbedded {

    private Long id;
    private ProvinceEmbedded province;
    private String name;
    private Double latitude;
    private Double longitude;

    public CityEmbedded(CityEmbedded cityEmbedded) {
        if (Objects.nonNull(cityEmbedded)) {
            id = cityEmbedded.getId();
            ofNullable(cityEmbedded.getProvince()).ifPresent(it -> this.province = new ProvinceEmbedded(it));
            name = cityEmbedded.getName();
            latitude = cityEmbedded.getLatitude();
            longitude = cityEmbedded.getLongitude();
        }
    }

    public CityEmbedded(City city) {
        if (Objects.nonNull(city)) {
            id = city.getCityId();
            ofNullable(city.getProvince()).ifPresent(it -> this.province = new ProvinceEmbedded(it));
            name = city.getCityName();
            latitude = city.getLatitude();
            longitude = city.getLongitude();
        }
    }

    public Long getProvinceId(){
        return ofNullable(this.province).map(ProvinceEmbedded::getId).orElse(null);
    }

    public Long getCountryId(){
        return ofNullable(this.province).map(it -> Optional.ofNullable(it.getCountry()).map(CountryEmbedded::getId).orElse(null)).orElse(null);
    }
}
