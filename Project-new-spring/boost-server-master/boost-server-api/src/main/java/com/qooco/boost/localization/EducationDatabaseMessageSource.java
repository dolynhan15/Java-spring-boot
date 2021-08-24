package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.EducationLocalization;
import com.qooco.boost.data.oracle.services.localizations.EducationLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceEducation")
public class EducationDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private EducationLocalizationService educationLocalizationService;

    @PostConstruct
    public void init() {

        List<EducationLocalization> data = educationLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewEducations(List<EducationLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
