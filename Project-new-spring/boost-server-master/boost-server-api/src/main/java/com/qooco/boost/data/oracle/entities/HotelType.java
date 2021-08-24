package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "HOTEL_TYPE")
public class HotelType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "HOTEL_TYPE_SEQUENCE")
    @SequenceGenerator(sequenceName = "HOTEL_TYPE_SEQ", allocationSize = 1, name = "HOTEL_TYPE_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "HOTEL_TYPE_ID")
    private Long hotelTypeId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "HOTEL_TYPE_NAME", columnDefinition = "NVARCHAR2")
    private String hotelTypeName;

    public HotelType() {
    }

    public HotelType(Long hotelTypeId) {
        this.hotelTypeId = hotelTypeId;
    }

    public HotelType(Long hotelTypeId, String hotelTypeName) {
        this.hotelTypeId = hotelTypeId;
        this.hotelTypeName = hotelTypeName;

    }

    public HotelType(HotelType hotelType) {
        if (Objects.nonNull(hotelType)) {
            hotelTypeId = hotelType.getHotelTypeId();
            hotelTypeName = hotelType.getHotelTypeName();
        }
    }

    public Long getHotelTypeId() {
        return hotelTypeId;
    }

    public void setHotelTypeId(Long hotelTypeId) {
        this.hotelTypeId = hotelTypeId;
    }

    public String getHotelTypeName() {
        return hotelTypeName;
    }

    public void setHotelTypeName(String hotelTypeName) {
        this.hotelTypeName = hotelTypeName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (hotelTypeId != null ? hotelTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelType hotelType = (HotelType) o;
        return Objects.equals(hotelTypeId, hotelType.hotelTypeId);
    }

    @Override
    public String toString() {
        return hotelTypeName;
    }

}