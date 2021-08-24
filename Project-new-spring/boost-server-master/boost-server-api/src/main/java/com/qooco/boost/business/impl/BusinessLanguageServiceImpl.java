package com.qooco.boost.business.impl;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.profiles.BuiltInLanguages;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.qooco.boost.business.BusinessLanguageService;
import com.qooco.boost.data.oracle.services.LanguageService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.PagedResultV2;
import com.qooco.boost.models.dto.LanguageDTO;
import org.apache.commons.lang3.mutable.MutableObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.pemistahl.lingua.api.Language.*;
import static com.github.pemistahl.lingua.api.LanguageDetectorBuilder.fromIsoCodes;
import static com.optimaize.langdetect.i18n.LdLocale.fromString;
import static com.optimaize.langdetect.ngram.NgramExtractors.standard;
import static java.util.Arrays.stream;
import static java.util.Locale.forLanguageTag;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toUnmodifiableList;
import static java.util.stream.Collectors.toUnmodifiableMap;
import static java.util.stream.Stream.concat;

@Service
public class BusinessLanguageServiceImpl implements BusinessLanguageService {
    private static final Map<String, String> OLD_TO_NEW_ISO_CODES = Map.of("in", "id");
    private static final List<String> INDOMALAY_CODES = List.of("in", MALAY.getIsoCode(), INDONESIAN.getIsoCode());

    @Autowired
    private LanguageService languageService;

    @Value("${boost.helper.rasa.locales}")
    private Set<String> supportedLocales;

    private Map<Locale, Locale> supportedSystemLocaleMap;
    private LanguageDetector linguaDetector;
    private com.optimaize.langdetect.LanguageDetector optimaizeDetector;
    private double minimalConfidence = 0.9999d;

    @PostConstruct
    public void onPostContruct() throws Exception {
        var languageToLocales = supportedLocales.stream().collect(toUnmodifiableMap(it -> forLanguageTag(it).getLanguage(), Locale::forLanguageTag, (a, b) -> a));
        supportedSystemLocaleMap = concat(supportedLocales.stream(), languageToLocales.keySet().stream()).distinct()
                .collect(toUnmodifiableMap(Locale::forLanguageTag, it -> languageToLocales.getOrDefault(it, forLanguageTag(it))));

        var langs = supportedLocales.stream().map(it -> forLanguageTag(it).getLanguage()).map(it -> OLD_TO_NEW_ISO_CODES.getOrDefault(it, it)).distinct().collect(toUnmodifiableList());
        var locales = supportedLocales.stream().map(it -> fromString(forLanguageTag(it).toLanguageTag()))
                .map(it -> BuiltInLanguages.getLanguages().contains(it) ? it : fromString(it.getLanguage()))
                .collect(toUnmodifiableList());
        linguaDetector = fromIsoCodes(langs.stream().findFirst().orElse(ENGLISH.getIsoCode()), langs.stream().skip(1).toArray(String[]::new)).withMapDBCache().build();
        optimaizeDetector = LanguageDetectorBuilder.create(standard()).withProfiles(new LanguageProfileReader().readBuiltIn(locales)).build();
    }

    public Locale detectSupportedRasaLocale(String text, Locale... preferedLocales) {
        var langList = new MutableObject<List<DetectedLanguage>>();
        Supplier<List<DetectedLanguage>> langs = () -> ofNullable(langList.getValue()).or(() -> Stream.of(optimaizeDetector.getProbabilities(text)).peek(langList::setValue).findFirst()).get();
        return stream(preferedLocales)
                .filter(loc -> langs.get().stream().anyMatch(lang -> new Locale(lang.getLocale().getLanguage()).getLanguage().equals(loc.getLanguage()))
                        || INDOMALAY_CODES.contains(loc.getLanguage()) && langs.get().stream().anyMatch(lang -> INDOMALAY_CODES.contains(lang.getLocale().getLanguage())))
                .findFirst()
                .orElseGet(() -> Optional.of(linguaDetector.detectLanguageOf(text))
                        .filter(it -> it != UNKNOWN)
                        .filter(it -> it != VIETNAMESE || text.length() > 2 || langs.get().isEmpty() || langs.get().get(0).getProbability() >= minimalConfidence)
                        .map(it -> it != CHINESE ? it.getIsoCode() :
                                langs.get().stream().findFirst().map(DetectedLanguage::getLocale)
                                        .filter(loc -> loc.getLanguage().equals(it.getIsoCode()))
                                        .map(loc -> loc.getLanguage() + loc.getRegion().transform("-"::concat).or(""))
                                        .orElseGet(it::getIsoCode))
                        .map(Locale::forLanguageTag)
                        .or(() -> stream(preferedLocales).findFirst())
                        .orElse(null));
    }

    @Override
    public Locale getSupportedSystemLocale(Locale languageTag) {
        return supportedSystemLocaleMap.get(languageTag);
    }

    @Override
    public BaseResp getLanguages(int page, int size, Authentication authentication) {
        var languagePage = languageService.getLanguages(page, size);
        var languageList = languagePage.getContent().stream().map(it -> new LanguageDTO(it, getLocale(authentication))).collect(Collectors.toList());
        return new BaseResp<>(new PagedResultV2<>(languageList, page, languagePage));
    }
}
