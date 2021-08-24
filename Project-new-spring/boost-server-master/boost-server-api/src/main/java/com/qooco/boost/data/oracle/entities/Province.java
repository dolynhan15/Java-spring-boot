package com.qooco.boost.data.oracle.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Getter @Setter
@Table(name = "PROVINCE")
@FieldNameConstants
public class Province implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PROVINCE_SEQUENCE")
    @SequenceGenerator(sequenceName = "PROVINCE_SEQ", allocationSize = 1, name = "PROVINCE_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "PROVINCE_ID")
    private Long provinceId;

    @JoinColumn(name = "COUNTRY_ID", referencedColumnName = "COUNTRY_ID")
    @ManyToOne(optional = false)
    private Country country;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "NAME", columnDefinition = "NVARCHAR2")
    private String name;

    @Column(name = "TYPE")
    private Integer type;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CODE", columnDefinition = "NVARCHAR2")
    private String code;

    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted;

    public Province() {}

    public Province(Long provinceId) {
        this.provinceId = provinceId;
    }

    public Province(Long provinceId,Country countryId) {
        this.provinceId = provinceId;
        this.country = countryId;
    }

    public Province(Long provinceId, String name, String code, Integer type) {
        this.provinceId = provinceId;
        this.name = name;
        this.code = code;
        this.type = type;
    }

    public Province(Province province) {
        if (Objects.nonNull(province)) {
            provinceId = province.getProvinceId();
            name = province.getName();
            code = province.getCode();
            type = province.getType();
            isDeleted = province.isDeleted();
            if (Objects.nonNull(province.getCountry())) {
                country = new Country(province.getCountry());
            }
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (provinceId != null ? provinceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Province province = (Province) o;
        return Objects.equals(provinceId, province.provinceId);
    }

    @Override
    public String toString() {
        return name;
    }

}