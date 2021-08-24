package com.qooco.boost.data.mongo.embedded;

import com.qooco.boost.data.oracle.entities.Province;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class ProvinceEmbedded {
    private Long id;
    private CountryEmbedded country;
    private String name;
    private Integer type;
    private String code;

    public ProvinceEmbedded(ProvinceEmbedded provinceEmbedded) {
        if (Objects.nonNull(provinceEmbedded)) {
            id = provinceEmbedded.getId();
            if (Objects.nonNull(provinceEmbedded.getCountry())) {
                country = new CountryEmbedded(provinceEmbedded.getCountry());
            }
            name = provinceEmbedded.getName();
            type = provinceEmbedded.getType();
            code = provinceEmbedded.getCode();
        }
    }

    public ProvinceEmbedded(Province province) {
        if (Objects.nonNull(province)) {
            id = province.getProvinceId();
            if (Objects.nonNull(province.getCountry())) {
                country = new CountryEmbedded(province.getCountry());
            }
            name = province.getName();
            type = province.getType();
            code = province.getCode();
        }
    }
}
