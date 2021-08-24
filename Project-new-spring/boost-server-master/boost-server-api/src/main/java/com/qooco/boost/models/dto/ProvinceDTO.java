package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.ProvinceEmbedded;
import com.qooco.boost.data.oracle.entities.Province;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.CountryDatabaseMessageSource;
import com.qooco.boost.localization.ProvinceDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: tnlong
 Date: 7/10/2018 - 1:27 PM
*/

@Getter @Setter @NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProvinceDTO {
    private Long id;
    private String name;
    private String code;
    private Integer type;
    private CountryDTO country;

    public ProvinceDTO(Province province, String locale) {
        if (Objects.nonNull(province)) {
            this.id = province.getProvinceId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(ProvinceDatabaseMessageSource.class).getMessage(province.getProvinceId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = province.getName();
            }
            this.code = province.getCode();
            this.type = province.getType();
            if (Objects.nonNull(province.getCountry())) {
                this.country = new CountryDTO(province.getCountry(), locale);
            }
        }
    }

    public ProvinceDTO(ProvinceEmbedded province, String locale) {
        if (Objects.nonNull(province)) {
            this.id = province.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(ProvinceDatabaseMessageSource.class).getMessage(province.getId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = province.getName();
            }
            this.code = province.getCode();
            this.type = province.getType();
            if (Objects.nonNull(province.getCountry())) {
                this.country = new CountryDTO(province.getCountry(), locale);
            }
        }
    }
}
