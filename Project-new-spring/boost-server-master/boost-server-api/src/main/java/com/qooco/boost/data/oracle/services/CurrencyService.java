package com.qooco.boost.data.oracle.services;

import com.qooco.boost.data.oracle.entities.Currency;
import org.springframework.data.domain.Page;

public interface CurrencyService extends EntryService<Currency, Long> {

    Currency findByCode(String currencyCode);

    Page<Currency> getCurrencies(int page, int size);
}
