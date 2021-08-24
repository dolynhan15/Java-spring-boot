package com.qooco.boost.business;

import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.currencyexchange.OpenExchangeRates;

public interface BusinessCurrencyExchangeService {

    OpenExchangeRates getCurrencyExchangeRateFromOpenExchangeRates();

    BaseResp syncCurrencyExchangeRates();
}
