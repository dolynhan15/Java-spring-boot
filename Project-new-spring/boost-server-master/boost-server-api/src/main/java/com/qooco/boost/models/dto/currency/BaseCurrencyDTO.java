package com.qooco.boost.models.dto.currency;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.CurrencyEmbedded;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.enumeration.ApplicationContextHolder;
import com.qooco.boost.localization.CurrencyDatabaseMessageSource;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 10/1/2018 - 2:35 PM
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseCurrencyDTO {
    private Long currencyId;
    private String code;
    private String name;
    private String symbol;

    public BaseCurrencyDTO(Currency currency, String locale) {
        if (Objects.nonNull(currency)) {
            this.currencyId = currency.getCurrencyId();
            this.code = currency.getCode();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(CurrencyDatabaseMessageSource.class).getMessage(currency.getCurrencyId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = currency.getName();
            }
            this.symbol = currency.getSymbol();
        }
    }

    public BaseCurrencyDTO(CurrencyEmbedded currency, String locale) {
        if (Objects.nonNull(currency)) {
            this.currencyId = currency.getId();
            this.code = currency.getCode();
            ApplicationContext ctx = ApplicationContextHolder.INSTANCE.getCtx();
            if (Objects.nonNull(ctx)) {
                this.name = ctx.getBean(CurrencyDatabaseMessageSource.class).getMessage(currency.getId().toString(), locale);
            }
            if (StringUtils.isBlank(name)) {
                this.name = currency.getName();
            }
            this.symbol = currency.getSymbol();
        }
    }
}
