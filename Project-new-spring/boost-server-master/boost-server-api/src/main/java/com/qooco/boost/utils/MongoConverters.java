package com.qooco.boost.utils;

import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentEmbedded;
import com.qooco.boost.data.mongo.embedded.appointment.AppointmentSlotEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.data.mongo.entities.*;
import com.qooco.boost.data.oracle.entities.Currency;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.data.utils.IdGeneration;
import com.qooco.boost.models.qooco.sync.levelTestHistory.TestHistory;
import com.qooco.boost.models.qooco.sync.levelTestScales.Level;
import com.qooco.boost.models.qooco.sync.levelTestScales.LevelTestScale;
import com.qooco.boost.models.qooco.sync.leveltestwizards.Answer;
import com.qooco.boost.models.qooco.sync.leveltestwizards.Question;
import com.qooco.boost.models.qooco.sync.leveltestwizards.TestByValue;
import com.qooco.boost.models.qooco.sync.leveltestwizards.WizardSkin;
import com.qooco.boost.models.qooco.sync.ownedPackage.Package;
import com.qooco.boost.threads.models.SaveAppointmentInMongo;
import com.qooco.boost.threads.models.SaveQualificationInMongo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.codehaus.plexus.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public final class MongoConverters {

    public static CompanyDoc convertToCompanyDoc(Company company) {
        CompanyDoc companyDoc = new CompanyDoc();
        if (nonNull(company)) {
            companyDoc.setId(company.getCompanyId());
            companyDoc.setName(company.getCompanyName());
            companyDoc.setLogo(company.getLogo());
            companyDoc.setDescription(company.getDescription());
            companyDoc.setAddress(company.getAddress());
            companyDoc.setPhone(company.getPhone());
            companyDoc.setEmail(company.getEmail());
            companyDoc.setWeb(company.getWeb());
            companyDoc.setAmadeus(company.getAmadeus());
            companyDoc.setGalileo(company.getGalileo());
            companyDoc.setWorldspan(company.getWorldspan());
            companyDoc.setSabre(company.getSabre());
            companyDoc.setStatus(company.getStatus().getCode());
            companyDoc.setUpdatedDate(DateUtils.toServerTimeForMongo(company.getUpdatedDate()));
            ofNullable(company.getCity()).ifPresent(it -> companyDoc.setCity(convertToCityEmbedded(it)));
            ofNullable(company.getHotelType()).ifPresent(it -> companyDoc.setHotelType(convertToHotelTypeEmbedded(it)));
        }
        return companyDoc;
    }

    public static CompanyEmbedded convertToCompanyEmbedded(Company company) {
        CompanyEmbedded companyEmbedded = null;
        if (nonNull(company)) {
            companyEmbedded = CompanyEmbedded.builder()
                    .id(company.getCompanyId())
                    .name(company.getCompanyName())
                    .logo(company.getLogo())
                    .address(company.getAddress())
                    .phone(company.getPhone())
                    .email(company.getEmail())
                    .web(company.getWeb())
                    .amadeus(company.getAmadeus())
                    .galileo(company.getGalileo())
                    .worldspan(company.getWorldspan())
                    .sabre(company.getSabre())
                    .build();
        }
        return companyEmbedded;
    }

    public static UserCvDoc convertToUserCvDoc(UserCurriculumVitae curriculumVitae,
                                               List<UserPreviousPosition> previousPositions,
                                               List<SaveQualificationInMongo> saveQualificationInMongos) {
        UserCvDoc userCvDoc = new UserCvDoc();
        if (nonNull(curriculumVitae)) {
            userCvDoc.setId(curriculumVitae.getCurriculumVitaeId());
            userCvDoc.setFullTime(curriculumVitae.isFullTime());
            userCvDoc.setHourSalary(curriculumVitae.isHourSalary());
            userCvDoc.setMaxSalary(curriculumVitae.getMaxSalary());
            userCvDoc.setMinSalary(curriculumVitae.getMinSalary());
            if (Objects.nonNull(curriculumVitae.getCurrency())) {
                double usdRate = curriculumVitae.getCurrency().getValidUnitPerUsd();
                double salaryUsd = curriculumVitae.getMinSalary() / usdRate;
                userCvDoc.setMinSalaryUsd(salaryUsd);
                double salaryMaxUsd = curriculumVitae.getMaxSalary() / usdRate;
                userCvDoc.setMaxSalaryUsd(salaryMaxUsd);
            }
            userCvDoc.setAsap(curriculumVitae.isAsap());

            ofNullable(curriculumVitae.getCurrency()).ifPresent(it -> userCvDoc.setCurrency(convertToCurrencyEmbedded(it)));
            ofNullable(curriculumVitae.getEducation()).ifPresent(it -> userCvDoc.setEducation(convertToEducationEmbedded(it)));
            ofNullable(curriculumVitae.getExpectedStartDate()).ifPresent(it -> userCvDoc.setExpectedStartDate(DateUtils.toServerTimeForMongo(it)));
            ofNullable(curriculumVitae.getSocialLinks()).filter(StringUtils::isNotBlank).ifPresent(it -> userCvDoc.setSocialLinks(StringUtil.convertToList(it)));
            ofNullable(curriculumVitae.getUserProfile()).ifPresent(it -> userCvDoc.setUserProfile(convertToUserProfileEmbedded(it)));
            ofNullable(curriculumVitae.getUpdatedDate()).ifPresent(it -> userCvDoc.setUpdatedDate(DateUtils.toServerTimeForMongo(it)));

            ofNullable(curriculumVitae.getUserBenefits())
                    .ifPresent(it -> userCvDoc.setBenefits(it.stream().map(ub -> convertToBenefitEmbedded(ub.getBenefit())).collect(toImmutableList())));
            ofNullable(curriculumVitae.getUserDesiredHours())
                    .ifPresent(it -> userCvDoc.setDesiredHours(it.stream().map(ub -> convertToWorkingHourEmbedded(ub.getWorkingHour())).collect(toImmutableList())));
            ofNullable(curriculumVitae.getCurriculumVitaeJobs())
                    .ifPresent(it -> userCvDoc.setJobs(it.stream().map(ub -> convertToJobEmbedded(ub.getJob())).collect(toImmutableList())));
            ofNullable(curriculumVitae.getUserSoftSkills())
                    .ifPresent(it -> userCvDoc.setSoftSkills(it.stream().map(ub -> convertToSoftSkillEmbedded(ub.getSoftSkill())).collect(toImmutableList())));
            ofNullable(saveQualificationInMongos).filter(CollectionUtils::isNotEmpty)
                    .ifPresent(it -> userCvDoc.setQualifications(it.stream().map(MongoConverters::convertToQualificationEmbedded).collect(toImmutableList())));

            ofNullable(previousPositions).filter(CollectionUtils::isNotEmpty).ifPresent(it -> {
                userCvDoc.setPreviousPositions(it.stream().map(MongoConverters::convertToPreviousPositionEmbedded).collect(toImmutableList()));
                it.stream().map(UserPreviousPosition::getStartDate).min(Date::compareTo).ifPresent(userCvDoc::setStartWorking);

                if (it.stream().noneMatch(p -> Objects.isNull(p.getEndDate()))) {
                    it.stream().map(UserPreviousPosition::getEndDate).max(Date::compareTo).ifPresent(userCvDoc::setEndWorking);
                }
            });
            userCvDoc.setPreferredHotels(ofNullable(curriculumVitae.getPreferredHotels()).map(it -> it.stream().map(item -> new CompanyEmbedded(item.getHotel())).collect(toList())).orElse(List.of()));

            userCvDoc.setProfileStrength(UserProfileUtils.calculateProfileStrength(userCvDoc, null));

        }
        return userCvDoc;
    }

    public static VacancyDoc convertToVacancyDoc(Vacancy vacancy, List<UserCvDoc> userCvDocs) {
        VacancyDoc vacancyDoc = new VacancyDoc();
        if (nonNull(vacancy)) {
            vacancyDoc.setId(vacancy.getId());
            vacancyDoc.setAsap(vacancy.getIsAsap());
            vacancyDoc.setFullDescription(vacancy.getFullDescription());
            vacancyDoc.setFullTime(vacancy.getWorkingType());
            vacancyDoc.setHourSalary(vacancy.getHourSalary());
            vacancyDoc.setNumberOfSeat(vacancy.getNumberOfSeat());
            vacancyDoc.setSearchRange(vacancy.getSearchRange());
            vacancyDoc.setLogo(vacancy.getLogo());
            vacancyDoc.setSalary(Converter.valueOfDouble(vacancy.getSalary(), 0));
            vacancyDoc.setSalaryMax(Converter.valueOfDouble(vacancy.getSalaryMax(), 0));
            if (nonNull(vacancy.getCurrency())) {
                vacancyDoc.setCurrency(convertToCurrencyEmbedded(vacancy.getCurrency()));
            }
            if (Objects.nonNull(vacancy.getCurrency())) {
                double usdRate = vacancy.getCurrency().getValidUnitPerUsd();
                double salaryUsd = vacancy.getSalary() / usdRate;
                vacancyDoc.setSalaryUsd(salaryUsd);
                double salaryMaxUsd = vacancy.getSalaryMax() / usdRate;
                vacancyDoc.setSalaryMaxUsd(salaryMaxUsd);
            }
            vacancyDoc.setShortDescription(vacancy.getShortDescription());
            vacancyDoc.setLogo(vacancy.getLogo());
            vacancyDoc.setShortDescription(vacancy.getShortDescription());
            vacancyDoc.setStatus(vacancy.getStatus());

            if (nonNull(vacancy.getVacancyBenefits())) {
                vacancyDoc.setBenefits(vacancy.getVacancyBenefits().stream()
                        .map(ub -> convertToBenefitEmbedded(ub.getBenefit())).collect(toImmutableList()));
            }

            if (nonNull(vacancy.getVacancyAssessmentLevels())) {
                vacancyDoc.setQualifications(vacancy.getVacancyAssessmentLevels().stream()
                        .map(al -> convertToQualificationEmbedded(al.getAssessmentLevel()))
                        .collect(toImmutableList()));
            }

            if (nonNull(vacancy.getCity())) {
                vacancyDoc.setCity(convertToCityEmbedded(vacancy.getCity()));
            }
            if (nonNull(vacancy.getSearchCity())) {
                vacancyDoc.setSearchCity(convertToCityEmbedded(vacancy.getSearchCity()));
            }
            if (nonNull(vacancy.getJobLocation())) {
                vacancyDoc.setJobLocation(convertToLocationEmbedded(vacancy.getJobLocation()));
            }
            if (nonNull(vacancy.getSearchLocation())) {
                vacancyDoc.setSearchLocation(convertToLocationEmbedded(vacancy.getSearchLocation()));
            }
            if (nonNull(vacancy.getCompany())) {
                vacancyDoc.setCompany(convertToCompanyEmbedded(vacancy.getCompany()));
            }
            if (nonNull(vacancy.getContactPerson())) {
                vacancyDoc.setContactPerson(convertToStaffEmbedded(vacancy.getContactPerson()));
            }
            if (nonNull(vacancy.getVacancyDesiredHours())) {
                vacancyDoc.setDesiredHours(vacancy.getVacancyDesiredHours().stream()
                        .map(uw -> convertToWorkingHourEmbedded(uw.getWorkingHour())).collect(toImmutableList()));
            }
            if (nonNull(vacancy.getEducation())) {
                vacancyDoc.setEducation(convertToEducationEmbedded(vacancy.getEducation()));
            }

            if (nonNull(vacancy.getExpectedStartDate())) {
                vacancyDoc.setExpectedStartDate(DateUtils.toServerTimeForMongo(vacancy.getExpectedStartDate()));
            }

            if (nonNull(vacancy.getJob())) {
                vacancyDoc.setJob(convertToJobEmbedded(vacancy.getJob()));
            }
            if (nonNull(vacancy.getVacancyLanguages())) {
                List<LanguageEmbedded> nativeLanguages = new ArrayList<>();
                List<LanguageEmbedded> languages = new ArrayList<>();
                for (VacancyLanguage vacancyLanguage : vacancy.getVacancyLanguages()) {
                    if (vacancyLanguage.isNative()) {
                        nativeLanguages.add(convertToLanguageEmbedded(vacancyLanguage.getLanguage()));
                    } else {
                        languages.add(convertToLanguageEmbedded(vacancyLanguage.getLanguage()));
                    }
                }
                vacancyDoc.setNativeLanguages(nativeLanguages);
                vacancyDoc.setLanguages(languages);
            }

            if (nonNull(vacancy.getVacancySoftSkills())) {
                vacancyDoc.setSoftSkills(vacancy.getVacancySoftSkills().stream()
                        .map(us -> convertToSoftSkillEmbedded(us.getSoftSkill())).collect(toImmutableList()));
            }

            if (nonNull(vacancy.getExpectedStartDate())) {
                vacancyDoc.setUpdatedDate(DateUtils.toServerTimeForMongo(vacancy.getUpdatedDate()));
            }

            if (nonNull(vacancy.getVacancyAppointments())) {
                vacancyDoc.setAppointments(vacancy.getVacancyAppointments().stream()
                        .map(MongoConverters::convertToAppointmentEmbedded).collect(toImmutableList()));
            }

            if (CollectionUtils.isNotEmpty(vacancy.getClosedCandidates())) {
                Map<Long, UserCvDoc> userCVs = ListUtils.emptyIfNull(userCvDocs).stream().collect(Collectors.toMap(UserCvDoc::getId, Function.identity()));
                List<UserProfileCvEmbedded> closedCandidates = vacancy.getClosedCandidates().stream()
                        .map(c -> convertToUserProfileCvEmbedded(userCVs.get(c.getCandidate().getCurriculumVitaeId())))
                        .collect(toImmutableList());
                vacancyDoc.setClosedCandidates(closedCandidates);
            }
        }
        return vacancyDoc;
    }

    public static VacancyEmbedded convertToVacancyEmbedded(VacancyDoc vacancyDoc) {
        VacancyEmbedded vacancyEmbedded = null;
        if (nonNull(vacancyDoc)) {
            vacancyEmbedded = new VacancyEmbedded();
            vacancyEmbedded.setId(vacancyDoc.getId());
            vacancyEmbedded.setAsap(vacancyDoc.isAsap());
            vacancyEmbedded.setCompany(nonNull(vacancyDoc.getCompany()) ? vacancyDoc.getCompany() : null);
            vacancyEmbedded.setContactPerson(nonNull(vacancyDoc.getContactPerson()) ? vacancyDoc.getContactPerson() : null);
            vacancyEmbedded.setCurrency(nonNull(vacancyDoc.getCurrency()) ? vacancyDoc.getCurrency() : null);
            vacancyEmbedded.setEducation(nonNull(vacancyDoc.getEducation()) ? vacancyDoc.getEducation() : null);
            vacancyEmbedded.setJobLocation(nonNull(vacancyDoc.getJobLocation()) ? vacancyDoc.getJobLocation() : null);
            vacancyEmbedded.setSearchLocation(nonNull(vacancyDoc.getSearchLocation()) ? vacancyDoc.getSearchLocation() : null);
            vacancyEmbedded.setJob(nonNull(vacancyDoc.getJob()) ? vacancyDoc.getJob() : null);
            vacancyEmbedded.setExpectedStartDate(vacancyDoc.getExpectedStartDate());
            vacancyEmbedded.setFullTime(vacancyDoc.isFullTime());
            vacancyEmbedded.setHourSalary(vacancyDoc.isHourSalary());
            vacancyEmbedded.setLogo(vacancyDoc.getLogo());
            vacancyEmbedded.setNumberOfSeat(vacancyDoc.getNumberOfSeat());
            vacancyEmbedded.setSalary(vacancyDoc.getSalary());
            vacancyEmbedded.setSalaryMax(vacancyDoc.getSalaryMax());
            vacancyEmbedded.setCity(nonNull(vacancyDoc.getCity()) ? vacancyDoc.getCity() : null);
            vacancyEmbedded.setSearchCity(nonNull(vacancyDoc.getSearchCity()) ? vacancyDoc.getSearchCity() : null);
            vacancyEmbedded.setSearchRange(vacancyDoc.getSearchRange());
            vacancyEmbedded.setStartSuspendDate(vacancyDoc.getStartSuspendDate());
            vacancyEmbedded.setSuspendDays(vacancyDoc.getSuspendDays());
            vacancyEmbedded.setNumberOfClosedCandidate(CollectionUtils.isNotEmpty(vacancyDoc.getClosedCandidates()) ? vacancyDoc.getClosedCandidates().size() : null);
            vacancyEmbedded.setStatus(ofNullable(vacancyDoc.getStatus()).orElse(0));
        }
        return vacancyEmbedded;
    }

    public static StaffEmbedded convertToStaffEmbedded(Staff staff) {
        StaffEmbedded staffEmbedded = null;
        if (nonNull(staff)) {
            staffEmbedded = StaffEmbedded.builder()
                    .id(staff.getStaffId())
                    .userProfile(nonNull(staff.getUserFit()) ? convertToUserProfileEmbedded(staff.getUserFit()) : null)
                    .company(nonNull(staff.getCompany()) ? convertToCompanyEmbedded(staff.getCompany()) : null)
                    .roleCompany(nonNull(staff.getRole()) ? new RoleCompanyEmbedded(staff.getRole()) : null)
                    .build();
        }
        return staffEmbedded;
    }


    private static UserProfileBasicEmbedded convertToUserProfileBasicEmbedded(UserFit user) {
        UserProfileBasicEmbedded embedded = null;
        if (nonNull(user)) {
            embedded = UserProfileBasicEmbedded.builder()
                    .avatar(user.getAvatar())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .userProfileId(user.getUserProfileId())
                    .phone(user.getPhoneNumber())
                    .userType(UserType.SELECT)
                    .gender(nonNull(user.getGender()) ? user.getGender().ordinal() : 0)
                    .build();
        }
        return embedded;
    }

    private static UserProfileBasicEmbedded convertToUserProfileBasicEmbedded(UserProfile user) {
        UserProfileBasicEmbedded embedded = null;
        if (nonNull(user)) {
            embedded = UserProfileBasicEmbedded.builder()
                    .avatar(user.getAvatar())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .username(user.getUsername())
                    .userProfileId(user.getUserProfileId())
                    .phone(user.getPhoneNumber())
                    .userType(UserType.PROFILE)
                    .gender(nonNull(user.getGender()) ? user.getGender().ordinal() : 0)
                    .build();
        }
        return embedded;
    }

    public static UserProfileEmbedded convertToUserProfileEmbedded(UserFit userFit) {
        UserProfileEmbedded embedded = null;
        if (nonNull(userFit)) {
            embedded = new UserProfileEmbedded(convertToUserProfileBasicEmbedded(userFit));
            embedded.setBirthday(DateUtils.getUtcForOracle(userFit.getBirthday()));
            embedded.setNationalId(userFit.getNationalId());
            embedded.setPersonalPhotos(StringUtils.isNotBlank(userFit.getPersonalPhotos()) ? StringUtil.convertToList(userFit.getPersonalPhotos()) : null);
            embedded.setCountry(nonNull(userFit.getCountry()) ? convertToCountryEmbedded(userFit.getCountry()) : null);
            if (nonNull(userFit.getUserFitLanguages())) {
                embedded.setNativeLanguages(
                        userFit.getUserFitLanguages().stream().filter(ul -> nonNull(ul) && ul.isNative())
                                .map(ul -> convertToLanguageEmbedded(ul.getLanguage())).collect(toImmutableList()));
                embedded.setLanguages(
                        userFit.getUserFitLanguages().stream().filter(ul -> nonNull(ul) && !ul.isNative())
                                .map(ul -> convertToLanguageEmbedded(ul.getLanguage())).collect(toImmutableList()));
            }
        }
        return embedded;
    }

    private static UserProfileEmbedded convertToUserProfileEmbedded(UserProfile userProfile) {
        UserProfileEmbedded embedded = null;
        if (nonNull(userProfile)) {
            embedded = new UserProfileEmbedded(convertToUserProfileBasicEmbedded(userProfile));
            embedded.setBirthday(DateUtils.getUtcForOracle(userProfile.getBirthday()));
            embedded.setNationalId(userProfile.getNationalId());
            embedded.setPersonalPhotos(StringUtils.isNotBlank(userProfile.getPersonalPhotos()) ? StringUtil.convertToList(userProfile.getPersonalPhotos()) : null);
            embedded.setCity(nonNull(userProfile.getCity()) ? convertToCityEmbedded(userProfile.getCity()) : null);
            embedded.setCountry(nonNull(userProfile.getCountry()) ? convertToCountryEmbedded(userProfile.getCountry()) : null);

            if (nonNull(userProfile.getUserLanguageList())) {
                embedded.setNativeLanguages(
                        userProfile.getUserLanguageList().stream().filter(ul -> nonNull(ul) && ul.isNative())
                                .map(ul -> convertToLanguageEmbedded(ul.getLanguage())).collect(toImmutableList()));
                embedded.setLanguages(
                        userProfile.getUserLanguageList().stream().filter(ul -> nonNull(ul) && !ul.isNative())
                                .map(ul -> convertToLanguageEmbedded(ul.getLanguage())).collect(toImmutableList()));
            }
        }
        return embedded;
    }

    public static UserProfileCvEmbedded convertToUserProfileCvEmbedded(UserProfile userProfile) {
        return convertToUserProfileCvEmbedded(userProfile, null);
    }

    public static UserProfileCvMessageEmbedded convertToUserProfileCvMessageEmbedded(UserProfile userProfile) {
        return new UserProfileCvMessageEmbedded(convertToUserProfileCvEmbedded(userProfile, null));
    }

    public static UserProfileCvEmbedded convertToUserProfileCvEmbedded(UserProfile userProfile, UserCurriculumVitae userCurriculumVitae) {
        UserProfileCvEmbedded embedded = null;
        if (nonNull(userProfile)) {
            embedded = new UserProfileCvEmbedded(convertToUserProfileEmbedded(userProfile));
            embedded.setAddress(userProfile.getAddress());
            embedded.setPhone(userProfile.getPhoneNumber());

            if (nonNull(userCurriculumVitae)) {
                embedded.setAsap(userCurriculumVitae.isAsap());
                embedded.setFullTime(userCurriculumVitae.isFullTime());
                embedded.setHourSalary(userCurriculumVitae.isHourSalary());
                embedded.setUserProfileCvId(userCurriculumVitae.getCurriculumVitaeId());
                embedded.setMaxSalary(userCurriculumVitae.getMaxSalary());
                embedded.setMinSalary(userCurriculumVitae.getMinSalary());
                embedded.setCurrency(convertToCurrencyEmbedded(userCurriculumVitae.getCurrency()));
                embedded.setProfileStrength(UserProfileUtils.calculateProfileStrength(userProfile, userCurriculumVitae, null));
                embedded.setEducation(nonNull(userCurriculumVitae.getEducation()) ? convertToEducationEmbedded(userCurriculumVitae.getEducation()) : null);
                embedded.setExpectedStartDate(nonNull(userCurriculumVitae.getExpectedStartDate()) ? DateUtils.toServerTimeForMongo(userCurriculumVitae.getExpectedStartDate()) : null);
                embedded.setJobs(nonNull(userCurriculumVitae.getCurriculumVitaeJobs()) ?
                        userCurriculumVitae.getCurriculumVitaeJobs().stream().map(uj -> convertToJobEmbedded(uj.getJob())).collect(toImmutableList())
                        : null);
            }
        }
        return embedded;
    }

    public static UserProfileCvEmbedded convertToUserProfileCvEmbedded(UserFit fit) {
        UserProfileCvEmbedded embedded = null;
        if (nonNull(fit)) {
            embedded = new UserProfileCvEmbedded(convertToUserProfileEmbedded(fit));
            embedded.setAddress(fit.getAddress());
            embedded.setPhone(fit.getPhoneNumber());
        }
        return embedded;
    }

    public static UserProfileCvMessageEmbedded convertToUserProfileCvMessageEmbedded(UserFit fit) {
        UserProfileCvEmbedded embedded = convertToUserProfileCvEmbedded(fit);
        return new UserProfileCvMessageEmbedded(embedded);
    }

    public static UserProfileCvEmbedded convertToUserProfileCvEmbedded(UserCvDoc userCvDoc) {
        UserProfileCvEmbedded embedded = null;
        if (nonNull(userCvDoc)) {
            embedded = new UserProfileCvEmbedded(userCvDoc.getUserProfile())
                    .setAsap(userCvDoc.isAsap())
                    .setFullTime(userCvDoc.isFullTime())
                    .setHourSalary(userCvDoc.isHourSalary())
                    .setUserProfileCvId(userCvDoc.getId())
                    .setMaxSalary(userCvDoc.getMaxSalary())
                    .setMinSalary(userCvDoc.getMinSalary())
                    .setProfileStrength(UserProfileUtils.calculateProfileStrength(userCvDoc, null))
                    .setCurrency(userCvDoc.getCurrency())
                    .setEducation(userCvDoc.getEducation())
                    .setExpectedStartDate(userCvDoc.getExpectedStartDate())
                    .setJobs(userCvDoc.getJobs())
                    .setPreferredHotels(userCvDoc.getPreferredHotels());
            embedded.setUserType(UserType.PROFILE);
        }
        return embedded;
    }

    public static UserProfileCvMessageEmbedded convertToUserProfileCvMessageEmbedded(UserCvDoc userCvDoc) {
        UserProfileCvEmbedded embedded = convertToUserProfileCvEmbedded(userCvDoc);
        return new UserProfileCvMessageEmbedded(embedded);
    }

    private static QualificationEmbedded convertToQualificationEmbedded(SaveQualificationInMongo saveQualificationInMongo) {
        return convertToQualificationEmbedded(saveQualificationInMongo.getQualification(), saveQualificationInMongo.getAssessment());
    }

    public static QualificationEmbedded convertToQualificationEmbedded(AssessmentLevel assessmentLevel) {
        QualificationEmbedded qualificationEmbedded = null;
        if (nonNull(assessmentLevel)) {
            qualificationEmbedded = QualificationEmbedded.builder()
                    .assessment(convertToAssessmentEmbedded(assessmentLevel.getAssessment()))
                    .level(toAssessmentLevelEmbedded(assessmentLevel))
                    .build();
        }
        return qualificationEmbedded;
    }

    private static QualificationEmbedded convertToQualificationEmbedded(UserQualification qualification, Assessment assessment) {
        QualificationEmbedded embedded = null;
        if (nonNull(qualification) && nonNull(assessment)) {
            AssessmentEmbedded assessmentEmbedded = convertToAssessmentEmbedded(assessment);
            AssessmentLevelEmbedded assessmentLevelEmbedded = AssessmentLevelEmbedded.builder()
                    .assessmentLevel(qualification.getLevelValue())
                    .levelName(qualification.getLevelName())
                    .scaleId(qualification.getScaleId())
                    .build();

            embedded = QualificationEmbedded.builder()
                    .id(qualification.getId())
                    .submissionTime(qualification.getSubmissionTime())
                    .level(assessmentLevelEmbedded)
                    .assessment(assessmentEmbedded)
                    .build();
        }
        return embedded;
    }

    private static AssessmentEmbedded convertToAssessmentEmbedded(Assessment assessment) {
        AssessmentEmbedded embedded = null;
        if (nonNull(assessment)) {
            embedded = AssessmentEmbedded.assessmentEmbeddedBuilder()
                    .id(assessment.getId())
                    .type(assessment.getType())
                    .name(assessment.getName())
                    .price(assessment.getPrice())
                    .picture(assessment.getPicture())
                    .numberCompanyRequire(assessment.getNumberCompanyRequire())

                    .scaleId(assessment.getScaleId())
                    .mappingId(assessment.getMappingId())
                    .packageId(assessment.getPackageId())
                    .topicId(assessment.getTopicId())
                    .categoryId(assessment.getCategoryId())
                    .build();
        }
        return embedded;
    }

    public static LevelTestScaleDoc convertToLevelTestScaleDoc(LevelTestScale levelTestScale, String scaleId, long timestamp) {
        LevelTestScaleDoc levelTestScaleDoc = new LevelTestScaleDoc();
        if (nonNull(levelTestScale)) {
            levelTestScaleDoc.setId(scaleId);
            levelTestScaleDoc.setMappingID(levelTestScale.getMappingID());
            if (MapUtils.isNotEmpty(levelTestScale.getLevels())) {
                List<LevelEmbedded> levelEmbeddeds = new ArrayList<>();
                levelTestScale.getLevels().forEach((key, value) -> levelEmbeddeds.add(convertToLevelEmbedded(value, key)));
                levelTestScaleDoc.setLevels(levelEmbeddeds);
            }
        }
        levelTestScaleDoc.setTimestamp(DateUtils.toServerTimeForMongo(new Date(timestamp)));
        return levelTestScaleDoc;
    }

    public static AssessmentTestHistoryDoc convertToAssessmentTestHistoryDoc(TestHistory levelTestHistory, Long assessmentId,
                                                                             Assessment assessment, Date finalUpdatedTime, Long userProfileId, boolean isItSelf) {
        if (nonNull(levelTestHistory)) {
            AssessmentTestHistoryDoc testHistoryDoc = new AssessmentTestHistoryDoc();
            testHistoryDoc.setId(IdGeneration.generateTestHistory(userProfileId, assessmentId, levelTestHistory.getTimestamp().getTime()));

            testHistoryDoc.setUserProfileId(userProfileId);
            testHistoryDoc.setAssessmentId(assessmentId);
            if (nonNull(assessment)) {
                testHistoryDoc.setAssessmentName(assessment.getName());
            }
            if (Objects.isNull(testHistoryDoc.getLevel())) {
                testHistoryDoc.setLevel(new AssessmentLevelEmbedded());
            }
            testHistoryDoc.getLevel().setAssessmentLevel(levelTestHistory.getLevel());
            testHistoryDoc.getLevel().setScaleId(levelTestHistory.getScaleId());
            testHistoryDoc.setMinLevel(levelTestHistory.getMinLevel());
            testHistoryDoc.setMaxLevel(levelTestHistory.getMaxLevel());
            testHistoryDoc.setScaleId(levelTestHistory.getScaleId());
            testHistoryDoc.setScore(levelTestHistory.getLevelScore());
            testHistoryDoc.setUpdatedDate(finalUpdatedTime);
            testHistoryDoc.setDuration(levelTestHistory.getDuration());
            if (isItSelf) {
                testHistoryDoc.setUpdatedDateByItSelf(finalUpdatedTime);
            }
            testHistoryDoc.setSubmissionTime(levelTestHistory.getTimestamp());
            return testHistoryDoc;
        }
        return null;
    }

    public static List<OwnedPackageDoc> convertToOwnedPackageDoc(Long userProfile, String packageId, Package ownedPackage, Date timeStamp) {
        List<OwnedPackageDoc> results = new ArrayList<>();
        OwnedPackageDoc packageDoc;
        if (nonNull(ownedPackage) && ArrayUtils.isNotEmpty(ownedPackage.getUnlockedLessons())) {
            String[] lessons = ownedPackage.getUnlockedLessons();
            for (String lesson : lessons) {
                if (StringUtils.isNotBlank(lesson) && NumberUtils.isNumber(lesson)) {
                    packageDoc = new OwnedPackageDoc();
                    packageDoc.setId(IdGeneration.generateOwnedPackage(userProfile, packageId, lesson));
                    packageDoc.setUserProfileId(userProfile);
                    packageDoc.setPackageId(NumberUtils.toLong(packageId));
                    packageDoc.setLesson(new AssessmentEmbedded(NumberUtils.toLong(lesson)));
                    packageDoc.setExpires(ownedPackage.getExpires());
                    packageDoc.setActivationDate(ownedPackage.getActivationDate());
                    packageDoc.setLimitPassCount(ownedPackage.getLimitPassCount());
                    packageDoc.setOrderByLesson(ownedPackage.getOrderByLesson());
                    packageDoc.setOrderByTopic(ownedPackage.getOrderByTopic());
                    packageDoc.setTopicLimit(ownedPackage.getTopicLimit());
                    packageDoc.setTimestamp(timeStamp);

                    results.add(packageDoc);
                }
            }
        }
        return results;
    }


    public static LanguageEmbedded convertToLanguageEmbedded(Language language) {
        LanguageEmbedded languageEmbedded = null;
        if (nonNull(language)) {
            languageEmbedded = new LanguageEmbedded();
            languageEmbedded.setCode(language.getCode());
            languageEmbedded.setId(language.getLanguageId());
            languageEmbedded.setName(language.getName());
        }
        return languageEmbedded;
    }

    private static SoftSkillEmbedded convertToSoftSkillEmbedded(SoftSkill softSkill) {
        SoftSkillEmbedded softSkillEmbedded = null;
        if (nonNull(softSkill)) {
            softSkillEmbedded = new SoftSkillEmbedded();
            softSkillEmbedded.setDescription(softSkill.getDescription());
            softSkillEmbedded.setId(softSkill.getSoftSkillId());
            softSkillEmbedded.setName(softSkill.getName());
        }
        return softSkillEmbedded;
    }

    private static PreviousPositionEmbedded convertToPreviousPositionEmbedded(UserPreviousPosition userPreviousPosition) {
        PreviousPositionEmbedded previousPositionEmbedded = null;
        if (nonNull(userPreviousPosition)) {
            previousPositionEmbedded = new PreviousPositionEmbedded();
            previousPositionEmbedded.setCompanyName(userPreviousPosition.getCompanyName());
            previousPositionEmbedded.setContactPerson(userPreviousPosition.getContactPerson());
            if (nonNull(userPreviousPosition.getCurrency())) {
                previousPositionEmbedded.setCurrency(convertToCurrencyEmbedded(userPreviousPosition.getCurrency()));
            }
            if (nonNull(userPreviousPosition.getEndDate())) {
                previousPositionEmbedded.setEndDate(DateUtils.toServerTimeForMongo(userPreviousPosition.getEndDate()));
            }
            previousPositionEmbedded.setId(userPreviousPosition.getId());
            if (StringUtils.isNotBlank(userPreviousPosition.getPhoto())) {
                previousPositionEmbedded.setPhotos(StringUtil.convertToList(userPreviousPosition.getPhoto()));
            }
            previousPositionEmbedded.setPositionName(userPreviousPosition.getPositionName());
            previousPositionEmbedded.setSalary(userPreviousPosition.getSalary());
            if (nonNull(userPreviousPosition.getStartDate())) {
                previousPositionEmbedded.setStartDate(
                        DateUtils.toServerTimeForMongo(userPreviousPosition.getStartDate()));
            }
        }
        return previousPositionEmbedded;
    }

    private static JobEmbedded convertToJobEmbedded(Job job) {
        JobEmbedded jobEmbedded = null;
        if (nonNull(job)) {
            jobEmbedded = JobEmbedded.builder()
                    .id(job.getJobId())
                    .name(job.getJobName())
                    .description(job.getJobDescription())
                    .build();
        }
        return jobEmbedded;
    }

    private static EducationEmbedded convertToEducationEmbedded(Education education) {
        EducationEmbedded educationEmbedded = null;
        if (nonNull(education)) {
            educationEmbedded = EducationEmbedded.builder()
                    .id(education.getEducationId())
                    .name((education.getName()))
                    .description(education.getDescription())
                    .build();
        }
        return educationEmbedded;
    }

    public static AppointmentEmbedded convertToAppointmentEmbedded(Appointment appointment) {
        AppointmentEmbedded appointmentEmbedded = null;
        if (nonNull(appointment)) {
            appointmentEmbedded = new AppointmentEmbedded();
            appointmentEmbedded.setId(appointment.getId());
            appointmentEmbedded.setManager(convertToStaffShortEmbedded(appointment.getManager()));
            appointmentEmbedded.setLocation(convertToLocationEmbedded(appointment.getLocation()));
            appointmentEmbedded.setAppointmentDate(appointment.getAppointmentDate());
            if (CollectionUtils.isNotEmpty(appointment.getAppointmentTimeRange())) {
                List<Date> timeRanges = appointment.getAppointmentTimeRange().stream()
                        .map(atr -> DateUtils.toServerTimeForMongo(atr.getAppointmentTime()))
                        .collect(toList());
                appointmentEmbedded.setTimeRanges(timeRanges);
            }

            if (CollectionUtils.isNotEmpty(appointment.getAppointmentDateRange())) {
                List<Date> dateRanges = appointment.getAppointmentDateRange().stream()
                        .map(atr -> DateUtils.toServerTimeForMongo(atr.getAppointmentDate()))
                        .collect(toList());
                appointmentEmbedded.setDateRanges(dateRanges);
            }

            appointmentEmbedded.setType(appointment.getType());
            appointmentEmbedded.setFromDate(DateUtils.toServerTimeForMongo(appointment.getFromDate()));
            appointmentEmbedded.setToDate(DateUtils.toServerTimeForMongo(appointment.getAppointmentDate()));
            appointmentEmbedded.setDeleted(appointment.getIsDeleted());
        }
        return appointmentEmbedded;
    }

    public static AppointmentEmbedded convertToAppointmentEmbedded(SaveAppointmentInMongo appointmentSDO) {
        if (nonNull(appointmentSDO)) {
            Appointment appointment = appointmentSDO.getAppointment();
            return convertToAppointmentEmbedded(appointment);
        }
        return null;
    }

    public static AppointmentSlotEmbedded convertToAppointmentSlotEmbedded(AppointmentDetail appointmentDetail) {
        AppointmentSlotEmbedded appointmentSlotEmbedded = null;
        if (nonNull(appointmentDetail)) {
            appointmentSlotEmbedded = new AppointmentSlotEmbedded();
            appointmentSlotEmbedded.setAppointmentId(appointmentDetail.getAppointment().getId());
            if (nonNull(appointmentDetail.getAppointmentTime())) {
                appointmentSlotEmbedded.setAppointmentTime(DateUtils.toServerTimeForMongo(appointmentDetail.getAppointmentTime()));
            }
            appointmentSlotEmbedded.setId(appointmentDetail.getId());
            appointmentSlotEmbedded.setStatus(appointmentDetail.getStatus());
        }
        return appointmentSlotEmbedded;
    }

    public static StaffShortEmbedded convertToStaffShortEmbedded(Staff staff) {
        StaffShortEmbedded embedded = null;
        if (nonNull(staff)) {
            embedded = new StaffShortEmbedded();
            embedded.setId(staff.getStaffId());
            embedded.setUserProfile(convertToUserProfileBasicEmbedded(staff.getUserFit()));
            embedded.setRoleCompany(new RoleCompanyEmbedded(staff.getRole()));
        }
        return embedded;
    }

    private static LocationEmbedded convertToLocationEmbedded(Location location) {
        LocationEmbedded embedded = null;
        if (nonNull(location)) {
            embedded = new LocationEmbedded();
            embedded.setAddress(location.getAddress());
            embedded.setCity(convertToCityEmbedded(location.getCity()));
            embedded.setCompany(convertToCompanyEmbedded(location.getCompany()));
            embedded.setId(location.getLocationId());
        }
        return embedded;
    }

    private static WorkingHourEmbedded convertToWorkingHourEmbedded(WorkingHour workingHour) {
        WorkingHourEmbedded embedded = null;
        if (nonNull(workingHour)) {
            embedded = new WorkingHourEmbedded();
            embedded.setDescription(workingHour.getWorkingHourDescription());
            embedded.setEndDate(workingHour.getEndDate());
            embedded.setFullTime(workingHour.isWorkingType());
            embedded.setId(workingHour.getWorkingHourId());
            embedded.setStartDate(workingHour.getStartDate());
        }
        return embedded;
    }

    private static BenefitEmbedded convertToBenefitEmbedded(Benefit benefit) {
        BenefitEmbedded benefitEmbedded = null;
        if (nonNull(benefit)) {
            benefitEmbedded = new BenefitEmbedded();
            benefitEmbedded.setDescription(benefit.getDescription());
            benefitEmbedded.setId(benefit.getBenefitId());
            benefitEmbedded.setName(benefit.getName());
        }
        return benefitEmbedded;
    }

    private static AssessmentLevelEmbedded toAssessmentLevelEmbedded(AssessmentLevel assessmentLevel) {
        AssessmentLevelEmbedded assessmentLevelEmbedded = null;
        if (nonNull(assessmentLevel)) {
            assessmentLevelEmbedded = new AssessmentLevelEmbedded();
            assessmentLevelEmbedded.setScaleId(assessmentLevel.getScaleId());
            assessmentLevelEmbedded.setLevelId(assessmentLevel.getId());
            assessmentLevelEmbedded.setLevelDescription(assessmentLevel.getLevelDescription());
            assessmentLevelEmbedded.setLevelName(assessmentLevel.getLevelName());
            assessmentLevelEmbedded.setAssessmentLevel(NumberUtils.toInt(assessmentLevel.getAssessmentLevel()));
        }
        return assessmentLevelEmbedded;
    }

    private static CityEmbedded convertToCityEmbedded(City city) {
        CityEmbedded cityEmbedded = null;
        if (nonNull(city)) {
            cityEmbedded = new CityEmbedded();
            cityEmbedded.setId(city.getCityId());
            cityEmbedded.setName(city.getCityName());
            cityEmbedded.setLatitude(city.getLatitude());
            cityEmbedded.setLongitude(city.getLongitude());
            if (nonNull(city.getProvince())) {
                cityEmbedded.setProvince(convertToProvinceEmbedded(city.getProvince()));
            }
        }
        return cityEmbedded;
    }

    private static ProvinceEmbedded convertToProvinceEmbedded(Province province) {
        ProvinceEmbedded provinceEmbedded = null;
        if (nonNull(province)) {
            provinceEmbedded = new ProvinceEmbedded();
            provinceEmbedded.setId(province.getProvinceId());
            provinceEmbedded.setCode(province.getCode());
            provinceEmbedded.setName(province.getName());
            provinceEmbedded.setType(province.getType());
            if (nonNull(province.getCountry())) {
                provinceEmbedded.setCountry(convertToCountryEmbedded(province.getCountry()));
            }
        }
        return provinceEmbedded;
    }

    public static CountryEmbedded convertToCountryEmbedded(Country country) {
        CountryEmbedded countryEmbedded = null;
        if (nonNull(country)) {
            countryEmbedded = new CountryEmbedded();
            countryEmbedded.setId(country.getCountryId());
            countryEmbedded.setCode(country.getCountryCode());
            countryEmbedded.setName(country.getCountryName());
            countryEmbedded.setPhoneCode(country.getPhoneCode());
        }
        return countryEmbedded;
    }

    private static HotelTypeEmbedded convertToHotelTypeEmbedded(HotelType hotelType) {
        HotelTypeEmbedded hotelTypeEmbedded = null;
        if (nonNull(hotelType)) {
            hotelTypeEmbedded = new HotelTypeEmbedded();
            hotelTypeEmbedded.setId(hotelType.getHotelTypeId());
            hotelTypeEmbedded.setName(hotelType.getHotelTypeName());
        }
        return hotelTypeEmbedded;
    }

    public static CurrencyEmbedded convertToCurrencyEmbedded(Currency currency) {
        CurrencyEmbedded currencyEmbedded = null;
        if (nonNull(currency)) {
            currencyEmbedded = new CurrencyEmbedded();
            currencyEmbedded.setCode(currency.getCode());
            currencyEmbedded.setId(currency.getCurrencyId());
            currencyEmbedded.setMaxSalary(currency.getMaxSalary());
            currencyEmbedded.setMinSalary(currency.getMinSalary());
            currencyEmbedded.setName(currency.getName());
            currencyEmbedded.setSymbol(currency.getSymbol());
            currencyEmbedded.setUnitPerUsd(currency.getUnitPerUsd());
            currencyEmbedded.setUsdPerUnit(currency.getUsdPerUnit());
        }
        return currencyEmbedded;
    }

    public static WizardSkinDoc convertToWizardSkinDoc(WizardSkin wizardSkin, String mappingId, Date updateTime) {
        WizardSkinDoc wizardSkinDoc = new WizardSkinDoc();
        if (nonNull(wizardSkin)) {
            wizardSkinDoc.setId(wizardSkin.getWizardId());
            wizardSkinDoc.setMappingId(mappingId);
            wizardSkinDoc.setTextResId(wizardSkin.getTextResId());
            if (nonNull(updateTime)) {
                wizardSkinDoc.setTimestamp(DateUtils.toServerTimeForMongo(updateTime));
            } else {
                wizardSkinDoc.setTimestamp(DateUtils.toServerTimeForMongo(wizardSkin.getTimestamp()));
            }
            if (CollectionUtils.isNotEmpty(wizardSkin.getQuestions())) {
                List<QuestionEmbedded> questions = new ArrayList<>();
                wizardSkin.getQuestions().forEach(q -> questions.add(convertToQuestionEmbedded(q)));
                wizardSkinDoc.setQuestions(questions);
            }
            if (CollectionUtils.isNotEmpty(wizardSkin.getTestsByValue())) {
                List<TestByValueEmbedded> testByValueEmbeddeds = new ArrayList<>();
                wizardSkin.getTestsByValue().forEach(t -> testByValueEmbeddeds.add(convertToTestValue(t)));
                wizardSkinDoc.setTestsByValue(testByValueEmbeddeds);
            }
        }
        return wizardSkinDoc;
    }

    private static QuestionEmbedded convertToQuestionEmbedded(Question question) {
        QuestionEmbedded questionEmbedded = null;
        if (nonNull(question)) {
            questionEmbedded = new QuestionEmbedded();
            questionEmbedded.setType(question.getType());
            questionEmbedded.setId(question.getQuestionId());
            questionEmbedded.setImageResId(question.getImageResId());
            questionEmbedded.setTextResId(question.getTextResId());
            if (CollectionUtils.isNotEmpty(question.getAnswers())) {
                List<AnswerEmbedded> answerEmbeddeds = new ArrayList<>();
                question.getAnswers().forEach(q -> answerEmbeddeds.add(convertToAnswerEmbedded(q)));
                questionEmbedded.setAnswers(answerEmbeddeds);
            }
        }
        return questionEmbedded;
    }

    private static AnswerEmbedded convertToAnswerEmbedded(Answer answer) {
        AnswerEmbedded answerEmbedded = null;
        if (nonNull(answer)) {
            answerEmbedded = new AnswerEmbedded();
            answerEmbedded.setId(answer.getAnswerId());
            answerEmbedded.setImage(answer.getImage());
            answerEmbedded.setTestId(answer.getTestId());
            answerEmbedded.setText(answer.getText());
            answerEmbedded.setTextResId(answer.getTextResId());
            answerEmbedded.setValue(answer.getValue());
            answerEmbedded.setDescResId(answer.getDescResId());
            answerEmbedded.setShowInDebugModeOnly(answer.isShowInDebugModeOnly());
        }
        return answerEmbedded;
    }

    private static TestByValueEmbedded convertToTestValue(TestByValue testByValue) {
        TestByValueEmbedded testByValueEmbedded = null;
        if (nonNull(testByValue)) {
            testByValueEmbedded = new TestByValueEmbedded();
            testByValueEmbedded.setTestId(testByValue.getTestId());
            testByValueEmbedded.setMaxValue(testByValue.getMaxValue());
            testByValueEmbedded.setCategoryId(testByValue.getCategoryId());
        }
        return testByValueEmbedded;
    }

    private static LevelEmbedded convertToLevelEmbedded(Level level, String levelValue) {
        LevelEmbedded levelEmbedded = null;
        if (nonNull(level)) {
            levelEmbedded = new LevelEmbedded();
            levelEmbedded.setDescr(level.getDescr());
            levelEmbedded.setName(level.getName());
            levelEmbedded.setValue(levelValue);
        }
        return levelEmbedded;
    }
}
