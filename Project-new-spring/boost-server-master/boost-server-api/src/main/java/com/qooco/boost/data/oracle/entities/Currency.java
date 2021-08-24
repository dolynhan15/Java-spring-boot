package com.qooco.boost.data.oracle.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author mhvtrung
 */
@Entity
@Table(name = "CURRENCY")
public class Currency implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CURRENCY_SEQUENCE")
    @SequenceGenerator(sequenceName = "CURRENCY_SEQ", allocationSize = 1, name = "CURRENCY_SEQUENCE")
    @Basic(optional = false)
    @NotNull
    @Column(name = "CURRENCY_ID")
    private Long currencyId;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CODE")
    private String code;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "NAME", columnDefinition = "NVARCHAR2")
    private String name;

    @Column(name = "UNIT_PER_USD", columnDefinition = "NUMBER(14)")
    private double unitPerUsd;

    @Column(name = "USD_PER_UNIT", columnDefinition = "NUMBER(18)")
    private Double usdPerUnit;

    @Column(name = "MIN_SALARY", columnDefinition = "NUMBER(10)")
    private long minSalary;

    @Column(name = "MAX_SALARY", columnDefinition = "NUMBER(10)")
    private long maxSalary;

    @Column(name = "SYMBOL")
    private String symbol;

    public Currency() {
    }

    public Currency(String code) {
        this.code = code;
    }

    public Currency(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Currency(Long currencyId, String code, String name) {
        this.currencyId = currencyId;
        this.code = code;
        this.name = name;
    }

    public Currency(Currency currency) {
        if (Objects.nonNull(currency)) {
            currencyId = currency.getCurrencyId();
            code = currency.getCode();
            name = currency.getName();
            unitPerUsd = currency.getUnitPerUsd();
            usdPerUnit = currency.getUsdPerUnit();
            minSalary = currency.getMinSalary();
            maxSalary = currency.getMaxSalary();
            symbol = currency.getSymbol();
        }
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getUnitPerUsd() {
        return unitPerUsd;
    }

    public double getValidUnitPerUsd() {
        if (unitPerUsd == 0) {
            return  1;
        }
        return unitPerUsd;
    }

    public void setUnitPerUsd(double unitPerUsd) {
        this.unitPerUsd = unitPerUsd;
    }

    public Double getUsdPerUnit() {
        return usdPerUnit;
    }

    public void setUsdPerUnit(Double usdPerUnit) {
        this.usdPerUnit = usdPerUnit;
    }

    public long getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(long minSalary) {
        this.minSalary = minSalary;
    }

    public long getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(long maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (currencyId != null ? currencyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return Objects.equals(currencyId, currency.currencyId);
    }

    @Override
    public String toString() {
        return code;
    }

}