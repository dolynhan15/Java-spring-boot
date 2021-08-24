package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author tnlong
 */
@Entity
@Table(name = "LOCATION")
public class Location extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @NotNull
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "LOCATION_SEQUENCE")
    @SequenceGenerator(sequenceName = "LOCATION_SEQ", allocationSize = 1, name = "LOCATION_SEQUENCE")
    @Setter @Getter
    @Column(name = "LOCATION_ID")
    private Long locationId;

    @Setter @Getter
    @JoinColumn(name = "CITY_ID", referencedColumnName = "CITY_ID")
    @ManyToOne(optional = false)
    private City city;

    @Setter @Getter
    @JoinColumn(name = "COMPANY_ID", referencedColumnName = "COMPANY_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private Company company;

    @Setter @Getter
    @Size(max = 255)
    @Column(name = "ADDRESS", columnDefinition = "NVARCHAR2")
    private String address;
    @Column(name = "IS_USED")
    private boolean isUsed;
    @Column(name = "IS_PRIMARY")
    private boolean isPrimary;

    public Location() {
        super();
    }

    public Location(Location location){
        if(Objects.nonNull(location)){
            this.locationId = location.getLocationId();
            this.setCity(new City(location.getCity()));
            this.setCompany(new Company(location.getCompany()));
            this.address = location.getAddress();
        }

    }
    public Location(City cityId) {
        this.city = cityId;
    }

    public Location(Long locationId) {
        this.locationId = locationId;
    }

    public Location(Long locationId, Long ownerId) {
        super(ownerId);
        this.locationId = locationId;
    }

    public Location(Long locationId, Long ownerId, String address) {
        this(locationId, ownerId);
        this.address = address;
    }

    public Location(Long locationId, Long ownerId, String address, City city) {
        this(locationId, ownerId, address);
        this.city = city;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (locationId != null ? locationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Objects.equals(locationId, location.locationId);
    }

    @Override
    public String toString() {
        return city.getCityName();
    }

    public boolean getIsUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public boolean getIsPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }
}
