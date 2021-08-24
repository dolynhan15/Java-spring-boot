package com.qooco.boost.models.user;

import com.google.common.collect.Lists;
import com.qooco.boost.data.oracle.entities.*;
import com.qooco.boost.models.dto.assessment.PersonalAssessmentDTO;
import com.qooco.boost.models.dto.assessment.PersonalAssessmentQualityResultDTO;
import com.qooco.boost.models.dto.assessment.UserPersonalityResultDTO;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.ServletUriUtils;
import com.qooco.boost.utils.StringUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.codehaus.plexus.util.StringUtils;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UserCvPrint {

    private static final String ASAP = "ASAP";
    public static final String COMMA = ", ";
    private static final String DEFAULT_AVATAR = "/images/default-avatar.png";

    private UserCurriculumVitae userCurriculumVitae;
    private List<UserQualification> userQualifications;
    private List<UserAttribute> userAttributes;
    private List<UserPreviousPosition> userPreviousPositions;
    private List<UserPersonality> userPersonalities;
    private Vacancy vacancy;
    private String timezone;
    private String baseUrl;

    public String getDisplayName() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())) {
            return String.format("%s %s", userCurriculumVitae.getUserProfile().getFirstName(), userCurriculumVitae.getUserProfile().getLastName());
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getUserAvatar() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile()) && StringUtils.isNotEmpty(userCurriculumVitae.getUserProfile().getAvatar())) {
            return ServletUriUtils.getAbsolutePath(userCurriculumVitae.getUserProfile().getAvatar());
        }
        return new StringBuilder(baseUrl).append(DEFAULT_AVATAR).toString();
    }

    public String getJob() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())) {
            return vacancy.getJob().getJobName();
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getEmail() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())) {
            return userCurriculumVitae.getUserProfile().getEmail();
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getPhone() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())) {
            return StringUtil.getPhoneFormat(userCurriculumVitae.getUserProfile().getPhoneNumber());
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getLocation() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())
                && Objects.nonNull(userCurriculumVitae.getUserProfile().getCity())
                && Objects.nonNull(userCurriculumVitae.getUserProfile().getCity().getProvince())) {
            return userCurriculumVitae.getUserProfile().getCity().getProvince().getCountry().getCountryName();
        }
        return StringUtil.EMPTY_STRING;
    }

    public List<List<Integer>> getQualifications() {
        List<Long> assessmentIds = CollectionUtils.emptyIfNull(vacancy.getVacancyAssessmentLevels())
                .stream().map(va -> va.getAssessmentLevel().getAssessment().getId()).collect(Collectors.toList());
        List<Integer> levels = CollectionUtils.emptyIfNull(userQualifications)
                .stream().filter(userQualification -> assessmentIds.contains(userQualification.getAssessmentId()))
                .map(UserQualification::getLevelValue).collect(Collectors.toList());
        List<List<Integer>> parts = new ArrayList<>();
        final int N = levels.size();
        for (int i = 0; i < N; i += 3) {
            parts.add(new ArrayList<>(
                    levels.subList(i, Math.min(N, i + 3)))
            );
        }
        return parts;
    }

    public List<UserCvAttributePrint> getAttributes() {
        return CollectionUtils.emptyIfNull(userAttributes)
                .stream().filter(userAttribute -> userAttribute.getLevel() > 0)
                .map(ua -> new UserCvAttributePrint(ua.getAttribute().getName(), ua.getLevel())).collect(Collectors.toList());
    }

    public boolean isHourlySalary() {
        return userCurriculumVitae.isHourSalary();
    }

    public boolean isShowDesiredSalary() {
        if (Objects.nonNull(userCurriculumVitae.getCurrency())) {
            return true;
        }
        return false;
    }
    public String getDesiredSalaryMonthly(){
        if (Objects.nonNull(userCurriculumVitae.getCurrency())) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            return String.format("%s%s - %s%s Monthly", userCurriculumVitae.getCurrency().getSymbol(),
                    formatter.format(userCurriculumVitae.getMinSalary()),
                    userCurriculumVitae.getCurrency().getSymbol(),
                    formatter.format(userCurriculumVitae.getMaxSalary()));
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getDesiredSalaryHourly(){
        if (Objects.nonNull(userCurriculumVitae.getCurrency())) {
            return String.format("%s%d - %d Hourly", userCurriculumVitae.getCurrency().getSymbol(),
                    (long) userCurriculumVitae.getMinSalary(), (long) userCurriculumVitae.getMaxSalary());
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getStartDateFormatted() {
        if (userCurriculumVitae.isAsap()) {
            return ASAP;
        } else if (Objects.nonNull(userCurriculumVitae.getExpectedStartDate())) {
            return DateUtils.formatExpectedStartDate(userCurriculumVitae.getExpectedStartDate(), timezone);
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getNativeLanguagesFormatted() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())) {
            return CollectionUtils.emptyIfNull(userCurriculumVitae.getUserProfile().getUserLanguageList())
                    .stream().filter(UserLanguage::isNative).map(l -> l.getLanguage().getName())
                    .collect(Collectors.joining(COMMA));
        }
        return StringUtil.EMPTY_STRING;
    }
    public String getLanguagesFormatted() {
        if (Objects.nonNull(userCurriculumVitae.getUserProfile())) {
            return CollectionUtils.emptyIfNull(userCurriculumVitae.getUserProfile().getUserLanguageList())
                    .stream().filter(l -> !l.isNative()).map(l -> l.getLanguage().getName())
                    .collect(Collectors.joining(COMMA));
        }
        return StringUtil.EMPTY_STRING;
    }

    public String getEducation() {
        if (Objects.nonNull(userCurriculumVitae.getEducation())) {
            return userCurriculumVitae.getEducation().getName();
        }
        return StringUtil.EMPTY_STRING;
    }

    private Comparator<UserPreviousPosition> compareEndDate() {
                return Comparator.comparing((UserPreviousPosition u) -> (u == null || Objects.isNull(u.getEndDate())) ? new Date(DateUtils.MAX_DATE_ORACLE) : u.getEndDate()).reversed()
                        .thenComparing((UserPreviousPosition u) -> (u == null || Objects.isNull(u.getStartDate())) ? new Date(DateUtils.MAX_DATE_ORACLE) : u.getStartDate())
                        .thenComparing(UserPreviousPosition::getUpdatedDate).reversed()
                        .thenComparing(UserPreviousPosition::getCreatedDate).reversed()
                        .thenComparing(UserPreviousPosition::getId);
    }

    public List<UserPreviousPosition> getPreviousPositions() {
        return CollectionUtils.emptyIfNull(userPreviousPositions)
                .stream().sorted(compareEndDate())
                .collect(Collectors.toList());
    }

    public List<String> getWorkingHours() {
        return CollectionUtils.emptyIfNull(userCurriculumVitae.getUserDesiredHours()).stream().map(
                userDesiredHour -> String.format("%s - %s",
                        DateUtils.convert24HourTimeTo12HourTime(userDesiredHour.getWorkingHour().getStartDate()),
                        DateUtils.convert24HourTimeTo12HourTime(userDesiredHour.getWorkingHour().getEndDate())))
                .collect(Collectors.toList());
    }

    public String getSoftSkillsFormatted() {
        return CollectionUtils.emptyIfNull(userCurriculumVitae.getUserSoftSkills())
                .stream().map(s -> s.getSoftSkill().getName())
                .collect(Collectors.joining(COMMA));
    }

    public String getBenefitsFormatted() {
        return CollectionUtils.emptyIfNull(userCurriculumVitae.getUserBenefits())
                .stream().map(s -> s.getBenefit().getName())
                .collect(Collectors.joining(COMMA));
    }

    public List<Personality> getPersonalities() {
        Map<Long, List<UserPersonality>> map = CollectionUtils.emptyIfNull(userPersonalities).stream()
                .collect(Collectors.groupingBy(UserPersonality::getPersonalAssessmentId ));
        List<Personality> personalities = new ArrayList<>();
        map.forEach((id, userAnswers) -> {
            Personality personality = new Personality();
            UserPersonalityResultDTO userPersonalityResultDTO = calculatePersonalityResult(userAnswers.get(0).getPersonalAssessmentQuestion().getPersonalAssessmentQuality().getPersonalAssessment(), "en_US", userAnswers);
            personality.name = userPersonalityResultDTO.getPersonalAssessment().getName();
            personality.scoreFormatted = userPersonalityResultDTO.getResults().stream().map(result -> String.format("%s %d", result.getName() , (long) result.getScore())).collect(Collectors.joining(COMMA));
            personalities.add(personality);
        });
        return personalities;
    }

    public boolean isShowPersonality() {
        if (CollectionUtils.isEmpty(userPersonalities)) {
            return false;
        }
        return true;
    }

    private UserPersonalityResultDTO calculatePersonalityResult(PersonalAssessment personalAssessment, String locale,
                                                                List<UserPersonality> userPersonalities) {
        UserPersonalityResultDTO personalityResult = new UserPersonalityResultDTO();
        if (Objects.nonNull(personalAssessment) && org.apache.commons.collections.CollectionUtils.isNotEmpty(userPersonalities)) {
            List<PersonalAssessmentQualityResultDTO> qualityResults = new ArrayList<>();
            personalityResult.setPersonalAssessment(new PersonalAssessmentDTO(personalAssessment, locale));
            personalAssessment.getPersonalAssessmentQualities().forEach(q -> {
                AtomicInteger totalScore = new AtomicInteger();
                totalScore.addAndGet(q.getDefaultValue());
                AtomicInteger score = new AtomicInteger();
                score.addAndGet(q.getDefaultValue());
                userPersonalities.forEach(up -> {
                    PersonalAssessmentQuestion personalQuestion = up.getPersonalAssessmentQuestion();
                    if (q.getQualityType() == personalQuestion.getPersonalAssessmentQuality().getQualityType()) {
                        int rateValue = personalQuestion.getValueRate();
                        if (rateValue < 0) {
                            totalScore.addAndGet(personalQuestion.getMinValue() * rateValue);
                        } else {
                            totalScore.addAndGet(personalQuestion.getMaxValue());
                        }
                        int answerValue = getScoreValue(personalQuestion.getMinValue(),
                                personalQuestion.getMaxValue(), up.getAnswerValue(),
                                personalQuestion.isReversed());
                        score.addAndGet(answerValue * rateValue);

                    }
                });
                PersonalAssessmentQualityResultDTO qualityResult = new PersonalAssessmentQualityResultDTO(
                        q, locale, score.get(), totalScore.get());
                qualityResults.add(qualityResult);
            });
            personalityResult.setResults(qualityResults);
        }

        return personalityResult;
    }

    private int getScoreValue(int min, int max, int index, boolean isReserved) {
        int value = 0;
        List<Integer> valueAnswers = new ArrayList<>();
        if (min > max) {
            value = max;
        } else {
            int valueAnswer = min;
            while (valueAnswer <= max) {
                valueAnswers.add(valueAnswer);
                valueAnswer++;
            }
            List<Integer> formatValue = valueAnswers;
            if (isReserved) {
                formatValue =  Lists.reverse(formatValue);
            }
            if (index < formatValue.size()) {
                value = formatValue.get(index);
            }
        }

        return value;
    }


    @Getter @Setter @NoArgsConstructor
    public class Personality {
        private String name;
        private String scoreFormatted;
    }
}
