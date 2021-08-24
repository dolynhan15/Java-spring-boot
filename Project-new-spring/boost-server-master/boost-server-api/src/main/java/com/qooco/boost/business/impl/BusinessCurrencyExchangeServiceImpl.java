package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessCurrencyExchangeService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.services.CurrencyService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.currencyexchange.OpenExchangeRates;
import com.qooco.boost.utils.HttpHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
public class BusinessCurrencyExchangeServiceImpl implements BusinessCurrencyExchangeService {

    protected Logger logger = LogManager.getLogger(BusinessCurrencyExchangeService.class);

    //Service link from other system
    @Value(ApplicationConstant.OPEN_EXCHANGE_RATE_LATEST_URL)
    private String latestExchangeRateUrl = "";

    private static final String USD = "USD";

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Override
    public OpenExchangeRates getCurrencyExchangeRateFromOpenExchangeRates() {
        return HttpHelper.doGet(latestExchangeRateUrl, OpenExchangeRates.class, true);
    }

    @Override
    public BaseResp syncCurrencyExchangeRates() {
        OpenExchangeRates openExchangeRates = getCurrencyExchangeRateFromOpenExchangeRates();
        Optional.ofNullable(openExchangeRates).ifPresent(exchangeRates -> {
            List<Currency> currencies = currencyService.findAll();
            if (CollectionUtils.isNotEmpty(currencies)) {
                currencies.forEach(currency -> {
                    logger.debug("syncCurrencyExchangeRates: "+currency.getCode()+ "|| rate = "+openExchangeRates.getRates().get(currency.getCode()));
                    if (!USD.equalsIgnoreCase(currency.getCode())) {
                        Double unitPerUsd = openExchangeRates.getRates().get(currency.getCode());
                        Optional.ofNullable(unitPerUsd).ifPresent(it -> {
                            if (currency.getUnitPerUsd() != it && it > 0) {
                                currency.setUnitPerUsd(it);
                                currency.setUsdPerUnit(1/it);
                                syncCurrencyInVacancyDoc(currency);
                                syncCurrencyInUserCvDoc(currency);
                            }
                        });
                    }
                });
                currencyService.save(currencies);
            }
        });
        return new BaseResp();
    }

    private void syncCurrencyInVacancyDoc(Currency currency) {
        logger.debug("syncCurrencyInVacancyDoc: "+currency.getCode());
        AtomicLong vacancyId = new AtomicLong();
        int size;
        do {
            List<VacancyDoc> vacancyDocs = vacancyDocService.findByCurrencyIdGreaterThan(
                    currency.getCurrencyId(), vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);
            vacancyDocService.updateCurrencyAndSalaryInUsd(currency, vacancyDocs);
            vacancyId.set(getMaxIdValue(vacancyDocs.stream().map(VacancyDoc::getId)));
            size = vacancyDocs.size();
        } while (size > 0);
    }

    private void syncCurrencyInUserCvDoc(Currency currency) {
        logger.debug("syncCurrencyInUserCvDoc: "+currency.getCode());
        AtomicLong userCvId = new AtomicLong();
        int size;
        do {
            List<UserCvDoc> userCvDocs = userCvDocService.findOpenVacancyByCurrencyIdGreaterThan(
                    currency.getCurrencyId(), userCvId.get(), Constants.DEFAULT_LIMITED_ITEM);
            userCvDocService.updateCurrencyAndSalaryInUsd(currency, userCvDocs);
            userCvId.set(getMaxIdValue(userCvDocs.stream().map(UserCvDoc::getId)));
            size = userCvDocs.size();
        } while (size > 0);
    }

    private long getMaxIdValue(Stream<Long> ids) {
        return ids.max(Comparator.comparing(Long::valueOf)).orElse(Long.MAX_VALUE);
    }
}
