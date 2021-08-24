package com.qooco.boost.data.mongo.embedded;

import com.google.common.base.MoreObjects;
import com.qooco.boost.data.oracle.entities.Currency;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@FieldNameConstants
public class CurrencyEmbedded {

    private Long id;
    private String code;
    private String name;
    private double unitPerUsd;
    private Double usdPerUnit;
    private long minSalary;
    private long maxSalary;
    private String symbol;

    public CurrencyEmbedded(CurrencyEmbedded currencyEmbedded) {
        if (Objects.nonNull(currencyEmbedded)) {
            id = currencyEmbedded.getId();
            code = currencyEmbedded.getCode();
            name = currencyEmbedded.getName();
            unitPerUsd = currencyEmbedded.getUnitPerUsd();
            usdPerUnit = currencyEmbedded.getUsdPerUnit();
            minSalary = currencyEmbedded.getMinSalary();
            maxSalary = currencyEmbedded.getMaxSalary();
            symbol = currencyEmbedded.getSymbol();
        }
    }

    public CurrencyEmbedded(Currency currency) {
        if (Objects.nonNull(currency)) {
            this.id = currency.getCurrencyId();
            this.code = currency.getCode();
            this.name = currency.getName();
            this.unitPerUsd = currency.getUnitPerUsd();
            this.usdPerUnit = currency.getUsdPerUnit();
            this.minSalary = currency.getMinSalary();
            this.maxSalary = currency.getMaxSalary();
            this.symbol = currency.getSymbol();
        }
    }

    public double getValidUnitPerUsd() {
        return Math.max(unitPerUsd, 1);

    }

    public double getValidUsdPerUnit() {
        return Math.max(MoreObjects.firstNonNull(usdPerUnit, 0D), 1);
    }
}
