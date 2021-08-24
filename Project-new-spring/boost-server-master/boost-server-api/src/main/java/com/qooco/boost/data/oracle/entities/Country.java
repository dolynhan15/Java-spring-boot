package com.qooco.boost.data.oracle.entities;

import com.qooco.boost.data.mongo.embedded.CountryEmbedded;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: trungmhv
 * Date: 7/11/2018 - 9:22 PM
 */
@Entity
@Getter @Setter
@Table(name = "COUNTRY")
@FieldNameConstants
public class Country implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "COUNTRY_SEQUENCE")
    @SequenceGenerator(sequenceName = "COUNTRY_SEQ", allocationSize = 1, name = "COUNTRY_SEQUENCE")
    @Basic(optional = false)
    @Column(name = "COUNTRY_ID", nullable = false)
    private Long countryId;

    @Basic(optional = false)
    @Column(
            name = "COUNTRY_NAME",
            columnDefinition = "NVARCHAR2",
            nullable = false
    )
    private String countryName;

    @Basic(optional = false)
    @Column(
            name = "COUNTRY_CODE",
            length = 10,
            columnDefinition = "VARCHAR2",
            nullable = false
    )
    private String countryCode;

    @Basic(optional = false)
    @Column(
            name = "PHONE_CODE",
            length = 10,
            columnDefinition = "VARCHAR2",
            nullable = false
    )
    private String phoneCode;

    @Column(name = "IS_DELETED", nullable = false)
    private boolean isDeleted;


//    @OneToMany(mappedBy="country", fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
//    private Set<CountryLocalization> countryLocalizations;


    public Country() {
    }

    public Country(Long countryId) {
        this.countryId = countryId;
    }

    public Country(Long countryId, String countryName) {
        this.countryId = countryId;
        this.countryName = countryName;
    }

    public Country(Long countryId, String countryName, String countryCode, String phoneCode) {
        this.countryId = countryId;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.phoneCode = phoneCode;
    }

    public Country(Country country) {
        if (Objects.nonNull(country)) {
            countryId = country.getCountryId();
            countryName = country.getCountryName();
            countryCode = country.getCountryCode();
            phoneCode = country.getPhoneCode();
            isDeleted = country.isDeleted();
        }
    }

    public Country(CountryEmbedded country) {
        if (Objects.nonNull(country)) {
            countryId = country.getId();
            countryName = country.getName();
            countryCode = country.getCode();
            phoneCode = country.getPhoneCode();
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (countryId != null ? countryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return Objects.equals(countryId, country.countryId);
    }

    @Override
    public String toString() {
        return this.countryName + ": " + this.countryCode;
    }
}
