package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.PersonalAssessmentQuestionLocalization;
import com.qooco.boost.data.oracle.services.localizations.PersonalAssessmentQuestionLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourcePersonalAssessmentQuestion")
public class PersonalAssessmentQuestionDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private PersonalAssessmentQuestionLocalizationService personalAssessmentQuestionLocalizationService;

    @PostConstruct
    public void init() {

        List<PersonalAssessmentQuestionLocalization> data = personalAssessmentQuestionLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewPersonalAssessmentQuestions(List<PersonalAssessmentQuestionLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
