package com.qooco.boost.models.dto.currency;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.qooco.boost.data.mongo.embedded.CurrencyEmbedded;
import com.qooco.boost.data.oracle.entities.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 7/10/2018 - 1:28 PM
*/
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrencyDTO extends BaseCurrencyDTO{
    private double unitPerUsd;
    private Double usdPerUnit;
    private long minSalary;
    private long maxSalary;

    public CurrencyDTO(Currency currency, String locale) {
        super(currency, locale);
        if (Objects.nonNull(currency)) {
            this.unitPerUsd = 1;
            this.usdPerUnit = 1d;
            this.minSalary = currency.getMinSalary();
            this.maxSalary = currency.getMaxSalary();
        }
    }

    public CurrencyDTO(CurrencyEmbedded currency, String locale) {
        super(currency, locale);
        if (Objects.nonNull(currency)) {
            this.unitPerUsd = 1;
            this.usdPerUnit = 1d;
            this.minSalary = currency.getMinSalary();
            this.maxSalary = currency.getMaxSalary();
        }
    }
}
