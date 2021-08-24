package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter @Setter
@Table(name = "CITY")
public class City implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CITY_SEQUENCE")
    @SequenceGenerator(sequenceName = "CITY_SEQ", allocationSize = 1, name = "CITY_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "CITY_ID")
    private Long cityId;

    @JoinColumn(name = "PROVINCE_ID", referencedColumnName = "PROVINCE_ID")
    @ManyToOne(optional = false)
    private Province province;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "CITY_NAME", columnDefinition = "NVARCHAR2")
    private String cityName;

    @Column(name = "LATITUDE", columnDefinition = "NUMBER(3)")
    private Double latitude;

    @Column(name = "LONGITUDE", columnDefinition = "NUMBER(3)")
    private Double longitude;

    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted;

    public City() {
    }

    public City(Long cityId) {
        this.cityId = cityId;
    }

    public City(Long cityId, String cityName) {
        this.cityId = cityId;
        this.cityName = cityName;
    }

    public City(Long cityId, Province province) {
        this.cityId = cityId;
        this.province = province;
    }

    public City(City city) {
        if (Objects.nonNull(city)) {
            cityId = city.getCityId();
            if (Objects.nonNull(city.getProvince())) {
                province = new Province(city.getProvince());
            }
            cityName = city.getCityName();
            latitude = city.getLatitude();
            longitude = city.getLongitude();
            isDeleted = city.isDeleted();
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (cityId != null ? cityId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(cityId, city.cityId);
    }

    @Override
    public String toString() {
        return cityName;
    }

}