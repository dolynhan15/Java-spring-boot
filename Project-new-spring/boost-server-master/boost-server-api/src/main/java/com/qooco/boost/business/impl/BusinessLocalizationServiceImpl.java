package com.qooco.boost.business.impl;

import com.qooco.boost.business.BusinessLocalizationService;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.oracle.entities.localizations.*;
import com.qooco.boost.data.oracle.services.localizations.*;
import com.qooco.boost.localization.*;
import com.qooco.boost.models.poi.StaticData;
import com.qooco.boost.utils.LocaleConstant;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusinessLocalizationServiceImpl implements BusinessLocalizationService {
    protected Logger logger = LogManager.getLogger(BusinessLocalizationServiceImpl.class);
    @Autowired
    private CountryLocalizationService countryLocalizationService;
    @Autowired
    private CountryDatabaseMessageSource countryDatabaseMessageSource;
    @Autowired
    private ProvinceLocalizationService provinceLocalizationService;
    @Autowired
    private ProvinceDatabaseMessageSource provinceDatabaseMessageSource;
    @Autowired
    private HotelTypeLocalizationService hotelTypeLocalizationService;
    @Autowired
    private HotelTypeDatabaseMessageSource hotelTypeDatabaseMessageSource;
    @Autowired
    private JobLocalizationService jobLocalizationService;
    @Autowired
    private JobDatabaseMessageSource jobDatabaseMessageSource;
    @Autowired
    private BenefitLocalizationService benefitLocalizationService;
    @Autowired
    private BenefitDatabaseMessageSource benefitDatabaseMessageSource;
    @Autowired
    private SoftSkillLocalizationService softSkillLocalizationService;
    @Autowired
    private SoftSkillDatabaseMessageSource skillDatabaseMessageSource;
    @Autowired
    private LanguageLocalizationService languageLocalizationService;
    @Autowired
    private LanguageDatabaseMessageSource languageDatabaseMessageSource;
    @Autowired
    private CurrencyLocalizationService currencyLocalizationService;
    @Autowired
    private CurrencyDatabaseMessageSource currencyDatabaseMessageSource;
    @Autowired
    private EducationLocalizationService educationLocalizationService;
    @Autowired
    private EducationDatabaseMessageSource educationDatabaseMessageSource;
    @Autowired
    private PersonalAssessmentLocalizationService personalAssessmentLocalizationService;
    @Autowired
    private PersonalAssessmentDatabaseMessageSource personalAssessmentDatabaseMessageSource;
    @Autowired
    private PersonalAssessmentQuestionLocalizationService personalAssessmentQuestionLocalizationService;
    @Autowired
    private PersonalAssessmentQuestionDatabaseMessageSource personalAssessmentQuestionDatabaseMessageSource;
    @Autowired
    private PersonalAssessmentQuestionTypeLocalizationService personalAssessmentQuestionTypeLocalizationService;
    @Autowired
    private PersonalAssessmentQuestionTypeDatabaseMessageSource personalAssessmentQuestionTypeDatabaseMessageSource;
    @Override
    public void saveLocalization(List<StaticData> staticData, long createdBy) {
//        saveCountryLocalization(staticData, createdBy);
//        saveProvinceLocalization(staticData, createdBy);
//        saveCityLocalization(staticData);
//        saveHotelTypeLocalization(staticData, createdBy);
//        saveJobLocalization(staticData, createdBy);
//        saveCurrencyLocalization(staticData, createdBy);
//        saveBenefitLocalization(staticData, createdBy);
//        saveLanguageLocalization(staticData, createdBy);
//        saveSoftSkillLocalization(staticData, createdBy);
//        saveEducationLocalization(staticData, createdBy);
//        savePersonalAssessmentQuestionTypeLocalization(staticData, createdBy);
        savePersonalAssessmentLocalization(staticData, createdBy);
//        savePersonalAssessmentQuestionLocalization(staticData, createdBy);
    }

    private void saveCountryLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> countries = staticData.stream().filter(it -> LocaleConstant.EXCEL_COUNTRY_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<CountryLocalization> countryLocalizations = new ArrayList<>();
        countries.forEach(country -> {
            try {
                CountryLocalization countryLocalization = new CountryLocalization(Long.parseLong(country.getKey()), country.getLocale(), country.getContent(), createdBy);
                countryLocalizations.add(countryLocalization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!countryLocalizations.isEmpty()) {
            countryLocalizationService.saveAll(countryLocalizations);
            countryDatabaseMessageSource.addNewCountries(countryLocalizations);
        }
    }

    private void saveCityLocalization(List<StaticData> staticData) {
        List<StaticData> cities = staticData.stream().filter(it -> LocaleConstant.EXCEL_CITY_COLUMN.equals(it.getModule())).collect(Collectors.toList());
    }

    private void saveProvinceLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> provinces = staticData.stream().filter(it -> LocaleConstant.EXCEL_PROVINCE_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<ProvinceLocalization> provinceLocalizations = new ArrayList<>();
        provinces.forEach(province -> {
            try {
                ProvinceLocalization provinceLocalization = new ProvinceLocalization(Long.parseLong(province.getKey()), province.getLocale(), province.getContent(), createdBy);
                provinceLocalizations.add(provinceLocalization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!provinceLocalizations.isEmpty()) {
            provinceLocalizationService.saveAll(provinceLocalizations);
            provinceDatabaseMessageSource.addNewProvinces(provinceLocalizations);
        }
    }

    private void saveHotelTypeLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> hotelTypes = staticData.stream().filter(it -> LocaleConstant.EXCEL_HOTEL_TYPE_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<HotelTypeLocalization> hotelTypeLocalizations = new ArrayList<>();
        hotelTypes.forEach(hotelType -> {
            try {
                HotelTypeLocalization hotelTypeLocalization = new HotelTypeLocalization(Long.parseLong(hotelType.getKey()), hotelType.getLocale(), hotelType.getContent(), createdBy);
                hotelTypeLocalizations.add(hotelTypeLocalization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!hotelTypeLocalizations.isEmpty()) {
            hotelTypeLocalizationService.saveAll(hotelTypeLocalizations);
            hotelTypeDatabaseMessageSource.addNewHotelTypes(hotelTypeLocalizations);
        }
    }
    private void saveJobLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> jobs = staticData.stream().filter(it -> LocaleConstant.EXCEL_JOB_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<JobLocalization> jobLocalizations = new ArrayList<>();
        jobs.forEach(item -> {
            try {
                JobLocalization jobLocalization = new JobLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                jobLocalizations.add(jobLocalization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!jobLocalizations.isEmpty()) {
            jobLocalizationService.saveAll(jobLocalizations);
            jobDatabaseMessageSource.addNewJobs(jobLocalizations);
        }
    }
    private void saveBenefitLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> benefits = staticData.stream().filter(it -> LocaleConstant.EXCEL_BENEFIT_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<BenefitLocalization> localizations = new ArrayList<>();
        benefits.forEach(item -> {
            try {
                BenefitLocalization localization = new BenefitLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            benefitLocalizationService.saveAll(localizations);
            benefitDatabaseMessageSource.addNewBenefits(localizations);
        }
    }
    private void saveSoftSkillLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> softSkills = staticData.stream().filter(it -> LocaleConstant.EXCEL_SOFT_SKILL_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<SoftSkillLocalization> localizations = new ArrayList<>();
        softSkills.forEach(item -> {
            try {
                SoftSkillLocalization localization = new SoftSkillLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            softSkillLocalizationService.saveAll(localizations);
            skillDatabaseMessageSource.addNewSoftSkills(localizations);
        }

    }
    private void saveLanguageLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> languages = staticData.stream().filter(it -> LocaleConstant.EXCEL_LANGUAGE_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<LanguageLocalization> localizations = new ArrayList<>();
        languages.forEach(item -> {
            try {
                LanguageLocalization localization = new LanguageLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            languageLocalizationService.saveAll(localizations);
            languageDatabaseMessageSource.addNewLanguages(localizations);
        }
    }
    private void saveCurrencyLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> currencies = staticData.stream().filter(it -> LocaleConstant.EXCEL_CURRENCY_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<CurrencyLocalization> localizations = new ArrayList<>();
        currencies.forEach(item -> {
            try {
                CurrencyLocalization localization = new CurrencyLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            currencyLocalizationService.saveAll(localizations);
            currencyDatabaseMessageSource.addNewCurrencies(localizations);
        }

    }

    private void saveEducationLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> educations = staticData.stream().filter(it -> LocaleConstant.EXCEL_EDUCATION_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<EducationLocalization> localizations = new ArrayList<>();
        educations.forEach(item -> {
            try {
                EducationLocalization localization = new EducationLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            educationLocalizationService.saveAll(localizations);
            educationDatabaseMessageSource.addNewEducations(localizations);
        }
    }

    private void savePersonalAssessmentQuestionTypeLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> personalAssessmentQuestionTypes = staticData.stream().filter(it -> LocaleConstant.EXCEL_PERSONAL_ASSESSMENT_QUESTION_TYPE_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<PersonalAssessmentQuestionTypeLocalization> localizations = new ArrayList<>();
        personalAssessmentQuestionTypes.forEach(item -> {
            try {
                PersonalAssessmentQuestionTypeLocalization localization = new PersonalAssessmentQuestionTypeLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            personalAssessmentQuestionTypeLocalizationService.saveAll(localizations);
            personalAssessmentQuestionTypeDatabaseMessageSource.addNewPersonalAssessmentQuestionTypes(localizations);
        }
    }
    private void savePersonalAssessmentLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> personalAssessments = staticData.stream().filter(it -> LocaleConstant.EXCEL_PERSONAL_ASSESSMENT_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<PersonalAssessmentLocalization> localizations = new ArrayList<>();
        personalAssessments.forEach(item -> {
            try {
                String[] keys = item.getKey().split(Constants.UNDER_SCORE);
                if (ArrayUtils.isNotEmpty(keys) && keys.length >= 2) {
                    List<String> itemKeys = new ArrayList<>(Arrays.asList(keys).subList(1, keys.length));
                    PersonalAssessmentLocalization localization = new PersonalAssessmentLocalization(
                            Long.parseLong(keys[0]), item.getLocale(), item.getContent(), StringUtils.join(itemKeys, Constants.UNDER_SCORE), createdBy);
                    localizations.add(localization);
                }
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            personalAssessmentLocalizationService.saveAll(localizations);
            personalAssessmentDatabaseMessageSource.addNewPersonalAssessments(localizations);
        }
    }

    private void savePersonalAssessmentQuestionLocalization(List<StaticData> staticData, long createdBy) {
        List<StaticData> personalAssessmentQuestions = staticData.stream().filter(it -> LocaleConstant.EXCEL_PERSONAL_ASSESSMENT_QUESTION_COLUMN.equals(it.getModule())).collect(Collectors.toList());
        List<PersonalAssessmentQuestionLocalization> localizations = new ArrayList<>();
        personalAssessmentQuestions.forEach(item -> {
            try {
                PersonalAssessmentQuestionLocalization localization = new PersonalAssessmentQuestionLocalization(Long.parseLong(item.getKey()), item.getLocale(), item.getContent(), createdBy);
                localizations.add(localization);
            } catch (NumberFormatException ex) {
                logger.error(ex.getMessage());
            }
        });
        if (!localizations.isEmpty()) {
            personalAssessmentQuestionLocalizationService.saveAll(localizations);
            personalAssessmentQuestionDatabaseMessageSource.addNewPersonalAssessmentQuestions(localizations);
        }
    }
}
