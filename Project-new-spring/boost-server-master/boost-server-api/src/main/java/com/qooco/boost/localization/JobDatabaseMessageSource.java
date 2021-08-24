package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.JobLocalization;
import com.qooco.boost.data.oracle.services.localizations.JobLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceJob")
public class JobDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private JobLocalizationService jobLocalizationService;

    @PostConstruct
    public void init() {

        List<JobLocalization> data = jobLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewJobs(List<JobLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
