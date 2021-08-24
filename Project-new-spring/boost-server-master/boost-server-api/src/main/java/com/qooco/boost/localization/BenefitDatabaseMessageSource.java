package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.BenefitLocalization;
import com.qooco.boost.data.oracle.services.localizations.BenefitLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceBenefit")
public class BenefitDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private BenefitLocalizationService benefitLocalizationService;

    @PostConstruct
    public void init() {

        List<BenefitLocalization> data = benefitLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewBenefits(List<BenefitLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
