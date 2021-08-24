package com.qooco.boost.localization;

import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentLocalization;
import com.qooco.boost.data.oracle.services.localizations.PersonalAssessmentLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourcePersonalAssessment")
public class PersonalAssessmentDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private PersonalAssessmentLocalizationService personalAssessmentLocalizationService;

    @PostConstruct
    public void init() {

        List<PersonalAssessmentLocalization> data = personalAssessmentLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewPersonalAssessments(List<PersonalAssessmentLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId() + Constants.UNDER_SCORE + localization.getType(), locale, localization.getContent());
            });
        }
    }

}
