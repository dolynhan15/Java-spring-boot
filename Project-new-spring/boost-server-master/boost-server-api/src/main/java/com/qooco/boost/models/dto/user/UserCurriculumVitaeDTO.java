package com.qooco.boost.models.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qooco.boost.data.model.UserCvDocAggregation;
import com.qooco.boost.data.mongo.embedded.*;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.models.dto.BenefitDTO;
import com.qooco.boost.models.dto.EducationDTO;
import com.qooco.boost.models.dto.SoftSkillDTO;
import com.qooco.boost.models.dto.WorkingHourDTO;
import com.qooco.boost.models.dto.assessment.PersonalAssessmentDTO;
import com.qooco.boost.models.dto.assessment.QualificationDTO;
import com.qooco.boost.models.dto.company.CompanyBaseDTO;
import com.qooco.boost.models.dto.currency.CurrencyDTO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.StringUtil;
import com.qooco.boost.utils.UserProfileUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserCurriculumVitaeDTO extends ShortUserCVDTO {

    private boolean isAsap;
    @Getter
    @Setter
    private Date expectedStartDate;
    @Getter
    @Setter
    private String[] socialLinks;
    @Getter
    @Setter
    private UserProfileDTO userProfile;
    @Getter
    @Setter
    private EducationDTO education;
    @Getter
    @Setter
    private List<WorkingHourDTO> desiredHours;
    @Getter
    @Setter
    private List<BenefitDTO> benefits;
    @Getter
    @Setter
    private List<UserPreviousPositionDTO> userPreviousPositions;
    @Getter
    @Setter
    private List<SoftSkillDTO> softSkills;
    @Getter
    @Setter
    private List<QualificationDTO> qualifications;
    @Getter
    @Setter
    private List<PersonalAssessmentDTO> personalAssessments;
    @Getter
    @Setter
    private List<CompanyBaseDTO> preferredHotels;
    @Getter
    @Setter
    private boolean hasPersonality;

    @Getter
    @Setter
    @JsonIgnore
    private boolean hasQualification;

    public UserCurriculumVitaeDTO(Long userCVId) {
        this.setId(userCVId);
    }

    public int getExperience() {
        return calculateYearExperience();
    }

    public int getMonthExperience() {
        return calculateMonthExperience();
    }

    public int getProfileStrength() {
        return calculateProfileStrength();
    }

    public UserCurriculumVitaeDTO(String locale) {
        super();
    }

    public UserCurriculumVitaeDTO(UserProfile userProfile, String locale) {
        this(new UserCurriculumVitae(userProfile), locale);
    }

    public UserCurriculumVitaeDTO(UserProfileCvEmbedded embedded, String locale) {
        super(embedded, locale);
        if (Objects.nonNull(embedded)) {
            this.isAsap = embedded.isAsap();
            ofNullable(embedded.getExpectedStartDate()).ifPresent(it -> this.expectedStartDate = it);
            this.userProfile = new UserProfileDTO(embedded, locale);
            this.education = EducationDTO.init(embedded.getEducation(), locale);
            this.hasPersonality = embedded.isHasPersonality();
            this.preferredHotels = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(embedded.getPreferredHotels())) {
                this.preferredHotels = embedded.getPreferredHotels().stream().map(CompanyBaseDTO::new).collect(Collectors.toList());
            }
        }
    }

    public UserCurriculumVitaeDTO(UserCurriculumVitae userCurriculumVitae, String locale) {
        super(userCurriculumVitae, locale);
        if (Objects.nonNull(userCurriculumVitae)) {
            this.isAsap = userCurriculumVitae.isAsap();
            ofNullable(userCurriculumVitae.getExpectedStartDate()).ifPresent(it -> this.expectedStartDate = DateUtils.getUtcForOracle(it));
            ofNullable(userCurriculumVitae.getSocialLinks()).ifPresent(it -> this.socialLinks = StringUtil.convertToArray(it));
            ofNullable(userCurriculumVitae.getUserProfile()).ifPresent(it -> this.userProfile = new UserProfileDTO(it, locale));
            this.education = EducationDTO.init(userCurriculumVitae.getEducation(), locale);
            ofNullable(this.userProfile).ifPresent(it -> it.setEducation(education));
            this.desiredHours = getWorkingHourDTOS(userCurriculumVitae.getUserDesiredHours());
            this.benefits = getBenefitDTOS(userCurriculumVitae.getUserBenefits(), locale);
            this.softSkills = getSoftSkillDTOS(userCurriculumVitae.getUserSoftSkills(), locale);
            this.userPreviousPositions = new ArrayList<>();
            this.hasPersonality = userCurriculumVitae.isHasPersonality();
            this.preferredHotels = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userCurriculumVitae.getPreferredHotels())) {
                this.preferredHotels = userCurriculumVitae.getPreferredHotels().stream().map(item -> new CompanyBaseDTO(item.getHotel())).collect(Collectors.toList());
            }
        }
    }

    public UserCurriculumVitaeDTO(UserCvDoc userCvDoc, String locale) {
        super(userCvDoc, locale);
        if (Objects.nonNull(userCvDoc)) {
            this.isAsap = userCvDoc.isAsap();
            ofNullable(userCvDoc.getExpectedStartDate()).ifPresent(it -> this.expectedStartDate = it);
            ofNullable(userCvDoc.getSocialLinks()).filter(CollectionUtils::isNotEmpty).ifPresent(it -> this.socialLinks = it.toArray(new String[0]));
            ofNullable(userCvDoc.getUserProfile()).ifPresent(it -> this.userProfile = new UserProfileDTO(it, locale));
            this.education = EducationDTO.init(userCvDoc.getEducation(), locale);
            ofNullable(this.userProfile).ifPresent(it -> it.setEducation(education));
            this.desiredHours = getWorkingHourDTOSFromEmbedded(userCvDoc.getDesiredHours());
            this.benefits = getBenefitDTOSEmbedded(userCvDoc.getBenefits(), locale);
            this.softSkills = getSoftSkillDTOSEmbedded(userCvDoc.getSoftSkills(), locale);
            this.userPreviousPositions = getUserPreviousPositionDTOSEmbedded(userCvDoc.getPreviousPositions(), locale);
            this.hasPersonality = userCvDoc.isHasPersonality();
            this.preferredHotels = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userCvDoc.getPreferredHotels())) {
                this.preferredHotels = userCvDoc.getPreferredHotels().stream().map(CompanyBaseDTO::new).collect(Collectors.toList());
            }
        }
    }

    public UserCurriculumVitaeDTO(UserCvDoc userCvDoc, int expiredDays, String locale) {
        this(userCvDoc, locale);
        if (Objects.nonNull(userCvDoc) && CollectionUtils.isNotEmpty(userCvDoc.getQualifications())) {
            this.qualifications = userCvDoc.getQualifications().stream().map(q -> new QualificationDTO(q, expiredDays))
                    .collect(Collectors.toList());
        }
    }

    public UserCurriculumVitaeDTO(UserCvDocAggregation userCvDoc, int expiredDays, String locale) {
        if (Objects.nonNull(userCvDoc)) {
            this.setId(userCvDoc.getId());
            this.setUserProfile(new ShortUserDTO(userCvDoc.getUserProfile(), locale));
            this.setMinSalary(userCvDoc.getMinSalary());
            this.setMaxSalary(userCvDoc.getMaxSalary());
            ofNullable(userCvDoc.getCurrency()).map(it -> new CurrencyDTO(it, locale)).ifPresent(this::setCurrency);
            this.setFullTime(userCvDoc.isFullTime());
            this.setHourSalary(userCvDoc.isHourSalary());
            this.setJobs(getJobDOTSFromEmbedded(userCvDoc.getJobs(), locale));
            this.isAsap = userCvDoc.isAsap();

            ofNullable(userCvDoc.getExpectedStartDate()).ifPresent(this::setExpectedStartDate);
            this.socialLinks = ofNullable(userCvDoc.getSocialLinks()).filter(CollectionUtils::isNotEmpty)
                    .map(it -> it.toArray(new String[0])).orElse(null);

            ofNullable(userCvDoc.getUserProfile()).map(it -> new UserProfileDTO(it, locale)).ifPresent(this::setUserProfile);

            this.education = EducationDTO.init(userCvDoc.getEducation(), locale);
            this.desiredHours = getWorkingHourDTOSFromEmbedded(userCvDoc.getDesiredHours());
            this.benefits = getBenefitDTOSEmbedded(userCvDoc.getBenefits(), locale);
            this.userPreviousPositions = getUserPreviousPositionDTOSEmbedded(userCvDoc.getPreviousPositions(), locale);
            this.softSkills = getSoftSkillDTOSEmbedded(userCvDoc.getSoftSkills(), locale);
            this.hasPersonality = userCvDoc.isHasPersonality();
            this.preferredHotels = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(userCvDoc.getPreferredHotels())) {
                this.preferredHotels = userCvDoc.getPreferredHotels().stream().map(CompanyBaseDTO::new).collect(Collectors.toList());
            }
        }
        ofNullable(userCvDoc).map(UserCvDocAggregation::getQualifications).filter(CollectionUtils::isNotEmpty)
                .ifPresent(it -> this.qualifications = it.stream().map(q -> new QualificationDTO(q, expiredDays))
                        .collect(Collectors.toList()));
    }

    private List<BenefitDTO> getBenefitDTOS(List<UserBenefit> userBenefits, String locale) {
        List<BenefitDTO> results = new ArrayList<>();
        if (Objects.nonNull(userBenefits)) {
            results = userBenefits.stream().map(ub -> new BenefitDTO(ub.getBenefit(), locale)).collect(Collectors.toList());
        }
        return results;
    }

    private List<BenefitDTO> getBenefitDTOSEmbedded(List<BenefitEmbedded> embeddeds, String locale) {
        List<BenefitDTO> results = new ArrayList<>();
        if (Objects.nonNull(embeddeds)) {
            results = embeddeds.stream().map(it -> new BenefitDTO(it, locale)).collect(Collectors.toList());
        }
        return results;
    }

    private List<UserPreviousPositionDTO> getUserPreviousPositionDTOSEmbedded(List<PreviousPositionEmbedded> embeddeds, String locale) {
        List<UserPreviousPositionDTO> results = new ArrayList<>();
        if (Objects.nonNull(embeddeds)) {
            results = embeddeds.stream().map(it -> new UserPreviousPositionDTO(it, locale)).collect(Collectors.toList());
        }
        return results;
    }

    private List<SoftSkillDTO> getSoftSkillDTOS(List<UserSoftSkill> userSoftSkills, String locale) {
        List<SoftSkillDTO> results = new ArrayList<>();
        if (Objects.nonNull(userSoftSkills)) {
            results = userSoftSkills.stream().map(usk -> new SoftSkillDTO(usk.getSoftSkill(), locale)).collect(Collectors.toList());
        }
        return results;
    }

    private List<SoftSkillDTO> getSoftSkillDTOSEmbedded(List<SoftSkillEmbedded> embeddeds, String locale) {
        List<SoftSkillDTO> results = new ArrayList<>();
        if (Objects.nonNull(embeddeds)) {
            results = embeddeds.stream().map(it -> new SoftSkillDTO(it, locale)).collect(Collectors.toList());
        }
        return results;
    }

    private List<WorkingHourDTO> getWorkingHourDTOS(List<UserDesiredHour> userDesiredHours) {
        List<WorkingHourDTO> results = new ArrayList<>();
        if (Objects.nonNull(userDesiredHours)) {
            results = userDesiredHours.stream().map(udh -> new WorkingHourDTO(udh.getWorkingHour())).collect(Collectors.toList());
        }
        return results;
    }

    private List<WorkingHourDTO> getWorkingHourDTOSFromEmbedded(List<WorkingHourEmbedded> embeddeds) {
        List<WorkingHourDTO> results = new ArrayList<>();
        if (Objects.nonNull(embeddeds)) {
            results = embeddeds.stream().map(WorkingHourDTO::new).collect(Collectors.toList());
        }
        return results;
    }

    private int calculateProfileStrength() {
        return UserProfileUtils.calculateProfileStrength(userProfile,
                expectedStartDate,
                isAsap,
                CollectionUtils.isNotEmpty(this.getUserPreviousPositions()),
                CollectionUtils.isNotEmpty(softSkills),
                ArrayUtils.isNotEmpty(socialLinks),
                hasPersonality,
                hasQualification
        );
    }

    private int calculateMonthExperience() {
        if (CollectionUtils.isNotEmpty(userPreviousPositions)) {
            Optional<Date> minDate = userPreviousPositions.stream().map(UserPreviousPositionDTO::getStartDate).min(Date::compareTo);

            Date currentDate = getMaxDateOfExperience();
            if (minDate.isPresent()) {
                Period diff = Period.between(LocalDate.ofEpochDay(getEpochDayOfDate(minDate.get().getTime())),
                        LocalDate.ofEpochDay(getEpochDayOfDate(currentDate.getTime())));
                return Math.toIntExact(diff.toTotalMonths());
            }
        }
        return 0;
    }

    private int calculateYearExperience() {
        if (CollectionUtils.isNotEmpty(userPreviousPositions)) {
            Optional<Date> minDate = userPreviousPositions.stream().map(UserPreviousPositionDTO::getStartDate).min(Date::compareTo);

            Date currentDate = getMaxDateOfExperience();
            if (minDate.isPresent()) {
                Period diff = Period.between(LocalDate.ofEpochDay(getEpochDayOfDate(minDate.get().getTime())),
                        LocalDate.ofEpochDay(getEpochDayOfDate(currentDate.getTime())));
                return diff.getYears();
            }
        }
        return 0;
    }

    private Date getMaxDateOfExperience() {
        boolean hasNullDate = userPreviousPositions.stream().anyMatch(p -> Objects.isNull(p.getEndDate()));
        Date currentDate = new Date();
        if (!hasNullDate) {
            Optional<Date> maxDate = userPreviousPositions.stream().map(UserPreviousPositionDTO::getEndDate).max(Date::compareTo);
            if (maxDate.isPresent()) {
                currentDate = maxDate.get();
            }
        }
        return currentDate;
    }

    private long getEpochDayOfDate(long timestamp) {
        return (timestamp - (new Date(0).getTime())) / DateUtils.ONE_DAY_IN_DAY_UNIT;
    }

    public boolean isAsap() {
        return isAsap;
    }

    @JsonProperty("isAsap")
    public void setAsap(boolean asap) {
        isAsap = asap;
    }
}
