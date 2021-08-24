package com.qooco.boost.business;
/*
 * Copyright: Falcon Team - AxonActive
 * User: mhvtrung
 * Date: 7/11/2018 - 6:00 PM
 */

import com.qooco.boost.models.BaseResp;
import org.springframework.security.core.Authentication;

import java.util.Locale;

public interface BusinessLanguageService extends BaseBusinessService {
    BaseResp getLanguages(int page, int size, Authentication authentication);
    Locale detectSupportedRasaLocale(String text, Locale... preferedLocales);
    Locale getSupportedSystemLocale(Locale languageTag);
}
