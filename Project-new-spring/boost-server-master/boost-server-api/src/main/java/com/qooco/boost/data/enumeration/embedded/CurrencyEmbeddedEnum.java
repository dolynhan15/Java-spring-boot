package com.qooco.boost.data.enumeration.embedded;

import com.qooco.boost.data.mongo.embedded.CurrencyEmbedded;

public enum CurrencyEmbeddedEnum {
    ID("id"),
    NAME("name"),
    CODE("code"),
    UNIT_PER_USD("unitPerUsd"),
    USD_PER_UNIT("usdPerUnit"),
    MIN_SALARY("minSalary"),
    MAX_SALARY("maxSalary"),
    SYMBOL("symbol");

    public Object getValue(CurrencyEmbedded embedded) {
        switch (this) {
            case ID:
                return embedded.getId();
            case NAME:
                return embedded.getName();
            case CODE:
                return embedded.getCode();
            case UNIT_PER_USD:
                return embedded.getUnitPerUsd();
            case USD_PER_UNIT:
                return embedded.getUsdPerUnit();
            case MIN_SALARY:
                return embedded.getMinSalary();
            case MAX_SALARY:
                return embedded.getMaxSalary();
            case SYMBOL:
                return embedded.getSymbol();
            default:
                return null;
        }
    }

    private final String key;

    public String getKey() {
        return key;
    }

    CurrencyEmbeddedEnum(String key) {
        this.key = key;
    }
}
