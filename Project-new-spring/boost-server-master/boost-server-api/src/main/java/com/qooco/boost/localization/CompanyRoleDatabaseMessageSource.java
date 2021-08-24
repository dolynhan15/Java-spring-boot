package com.qooco.boost.localization;

import com.qooco.boost.data.oracle.entities.localizations.CompanyRoleLocalization;
import com.qooco.boost.data.oracle.services.localizations.CompanyRoleLocalizationService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;

@Component("messageSourceCompanyRole")
public class CompanyRoleDatabaseMessageSource extends DBMessageSource {

    @Autowired
    private CompanyRoleLocalizationService companyRoleLocalizationService;

    @PostConstruct
    public void init() {

        List<CompanyRoleLocalization> data = companyRoleLocalizationService.findAll();
        messages = new Messages();
        data.forEach(localization -> {
            Locale locale = new Locale(localization.getLocale());
            messages.addMessage(localization.getId().toString(), locale, localization.getContent());
        });
    }

    public void addNewCompanyRoles(List<CompanyRoleLocalization> data) {
        initMessageIfNullable();
        if (CollectionUtils.isNotEmpty(data)) {
            data.forEach(localization -> {
                Locale locale = new Locale(localization.getLocale());
                messages.addMessage(localization.getId().toString(), locale, localization.getContent());
            });
        }
    }

}
