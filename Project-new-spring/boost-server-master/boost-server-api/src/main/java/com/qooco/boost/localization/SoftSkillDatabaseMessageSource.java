package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.SoftSkillLocalization;
import com.qooco.boost.data.oracle.services.localizations.SoftSkillLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceSoftSkill")
public class SoftSkillDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private SoftSkillLocalizationService softSkillLocalizationService;

    @PostConstruct
    public void init() {

        List<SoftSkillLocalization> data = softSkillLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewSoftSkills(List<SoftSkillLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
