package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.LanguageLocalization;
import com.qooco.boost.data.oracle.services.localizations.LanguageLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceLanguage")
public class LanguageDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private LanguageLocalizationService languageLocalizationService;

    @PostConstruct
    public void init() {

        List<LanguageLocalization> data = languageLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewLanguages(List<LanguageLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
