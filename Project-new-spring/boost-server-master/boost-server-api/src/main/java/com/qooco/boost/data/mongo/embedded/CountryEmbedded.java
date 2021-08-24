package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Country;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class CountryEmbedded {
    private Long id;
    private String name;
    private String code;
    private String phoneCode;

    public CountryEmbedded(CountryEmbedded countryEmbedded) {
        if (Objects.nonNull(countryEmbedded)) {
            id = countryEmbedded.getId();
            name = countryEmbedded.getName();
            code = countryEmbedded.getCode();
            phoneCode = countryEmbedded.getPhoneCode();
        }
    }

    public CountryEmbedded(Country country) {
        if (Objects.nonNull(country)) {
            id = country.getCountryId();
            name = country.getCountryName();
            code = country.getCountryCode();
            phoneCode = country.getPhoneCode();
        }
    }
}
