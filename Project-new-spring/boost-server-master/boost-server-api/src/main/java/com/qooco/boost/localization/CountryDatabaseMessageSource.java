package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.CountryLocalization;
import com.qooco.boost.data.oracle.services.localizations.CountryLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceCountry")
public class CountryDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private CountryLocalizationService countryLocalizationService;

    @PostConstruct
    public void init() {

        List<CountryLocalization> data = countryLocalizationService.findAll();
        messages = new Messages();
        data.forEach(countryLocalization -> {
            Locale locale = new Locale(countryLocalization.getLocale());
            messages.addMessage(countryLocalization.getId().toString(), locale, countryLocalization.getContent());
        });
    }

    public void addNewCountries(List<CountryLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
