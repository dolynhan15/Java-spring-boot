package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.CityEmbedded;
import com.qooco.boost.data.oracle.entities.City;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/10/2018 - 1:27 PM
*/

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CityDTO {

    private Long id;

    private String name;

    private Double latitude;

    private Double longitude;

    private ProvinceDTO province;

    public CityDTO(long id, String name, Double latitude, Double longitude, ProvinceDTO province) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.province = province;
    }

    public CityDTO(City city, String locale) {
        if (Objects.nonNull(city)) {
            id = city.getCityId();
            if (Objects.nonNull(city.getProvince())) {
                province = new ProvinceDTO(city.getProvince(), locale);
            }
            name = city.getCityName();
            latitude = city.getLatitude();
            longitude = city.getLongitude();
        }
    }

    public CityDTO(CityEmbedded city, String locale) {
        if (Objects.nonNull(city)) {
            id = city.getId();
            if (Objects.nonNull(city.getProvince())) {
                province = new ProvinceDTO(city.getProvince(), locale);
            }
            name = city.getName();
            latitude = city.getLatitude();
            longitude = city.getLongitude();
        }
    }

    public CityDTO(long id) {
        this.id = id;
    }
}
