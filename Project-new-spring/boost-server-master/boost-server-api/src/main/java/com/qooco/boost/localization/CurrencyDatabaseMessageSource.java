package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.CurrencyLocalization;
import com.qooco.boost.data.oracle.services.localizations.CurrencyLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceCurrency")
public class CurrencyDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private CurrencyLocalizationService currencyLocalizationService;

    @PostConstruct
    public void init() {

        List<CurrencyLocalization> data = currencyLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewCurrencies(List<CurrencyLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
