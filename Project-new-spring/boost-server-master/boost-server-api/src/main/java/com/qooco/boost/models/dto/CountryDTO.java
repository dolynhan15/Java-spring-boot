package com.qooco.boost.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.CountryEmbedded;
import com.qooco.boost.data.oracle.entities.Country;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.CountryDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CountryDTO {
    private Long id;
    private String name;
    private String code;
    private String phoneCode;

    public CountryDTO(Country country, String locale) {
        if (Objects.nonNull(country)) {
            this.id = country.getCountryId();
            this.code = country.getCountryCode();
            this.phoneCode = country.getPhoneCode();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(CountryDatabaseMessageSource.class).getMessage(country.getCountryId().toString(), locale);
            }
            if (StringUtils.isBlank(this.name)) {
                this.name = country.getCountryName();
            }
        }
    }

    public CountryDTO(CountryEmbedded country, String locale) {
        if (Objects.nonNull(country)) {
            this.id = country.getId();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(CountryDatabaseMessageSource.class).getMessage(country.getId().toString(), locale);
            }
            if (StringUtils.isBlank(this.name)) {
                this.name = country.getName();
            }
            this.code = country.getCode();
            this.phoneCode = country.getPhoneCode();
        }
    }
}
