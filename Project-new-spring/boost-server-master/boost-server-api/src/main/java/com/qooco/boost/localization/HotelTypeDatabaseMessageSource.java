package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.HotelTypeLocalization;
import com.qooco.boost.data.oracle.services.localizations.HotelTypeLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceHotelType")
public class HotelTypeDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private HotelTypeLocalizationService hotelTypeLocalizationService;

    @PostConstruct
    public void init() {

        List<HotelTypeLocalization> data = hotelTypeLocalizationService.findAll();
        messages = new Messages();
        data.forEach(countryLocalization -> {
            Locale locale = new Locale(countryLocalization.getLocale());
            messages.addMessage(countryLocalization.getId().toString(), locale, countryLocalization.getContent());
        });
    }

    public void addNewHotelTypes(List<HotelTypeLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
