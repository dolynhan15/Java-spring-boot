package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.ProvinceLocalization;
import com.qooco.boost.data.oracle.services.localizations.ProvinceLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceProvince")
public class ProvinceDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private ProvinceLocalizationService provinceLocalizationService;

    @PostConstruct
    public void init() {

        List<ProvinceLocalization> data = provinceLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewProvinces(List<ProvinceLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
