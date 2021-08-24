package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionTypeLocalization;
import com.qooco.boost.data.oracle.services.localizations.PersonalAssessmentQuestionTypeLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourcePersonalAssessmentQuestionType")
public class PersonalAssessmentQuestionTypeDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private PersonalAssessmentQuestionTypeLocalizationService personalAssessmentQuestionTypeLocalizationService;

    @PostConstruct
    public void init() {

        List<PersonalAssessmentQuestionTypeLocalization> data = personalAssessmentQuestionTypeLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewPersonalAssessmentQuestionTypes(List<PersonalAssessmentQuestionTypeLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
