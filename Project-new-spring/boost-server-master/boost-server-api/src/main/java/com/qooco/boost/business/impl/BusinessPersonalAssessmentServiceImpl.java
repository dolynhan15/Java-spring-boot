package com.qooco.boost.business.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessPersonalAssessmentService;
import com.qooco.boost.business.BusinessProfileAttributeEventService;
import com.qooco.boost.business.BusinessValidatorService;
import com.qooco.boost.business.impl.abstracts.BusinessUserServiceAbstract;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.core.model.authentication.AuthenticatedUser;
import com.qooco.boost.data.oracle.entities.PersonalAssessment;
import com.qooco.boost.data.oracle.entities.PersonalAssessmentQuestion;
import com.qooco.boost.data.oracle.entities.UserPersonality;
import com.qooco.boost.data.oracle.services.PersonalAssessmentQuestionService;
import com.qooco.boost.data.oracle.services.PersonalAssessmentService;
import com.qooco.boost.data.oracle.services.UserPersonalityService;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.exception.EntityNotFoundException;
import com.qooco.boost.exception.InvalidParamException;
import com.qooco.boost.exception.NoPermissionException;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.dto.assessment.*;
import com.qooco.boost.models.request.PersonalAssessmentAnswer;
import com.qooco.boost.models.request.SubmitPersonalAssessmentReq;
import com.qooco.boost.threads.BoostActorManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.qooco.boost.constants.AttributeEventType.EVT_GET_A_HIGH_EMPATHY_TEST_SCORE;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 11/13/2018 - 2:22 PM
*/
@Service
public class BusinessPersonalAssessmentServiceImpl extends BusinessUserServiceAbstract implements BusinessPersonalAssessmentService {
    @Autowired
    private PersonalAssessmentService personalAssessmentService;
    @Autowired
    private UserPersonalityService userPersonalityService;
    @Autowired
    private PersonalAssessmentQuestionService personalAssessmentQuestionService;
    @Autowired
    private BoostActorManager boostActorManager;
    @Autowired
    private BusinessValidatorService businessValidatorService;
    @Autowired
    private BusinessProfileAttributeEventService businessProfileAttributeEventService;

    @Value(ApplicationConstant.PERSONAL_ASSESSMENT_MULTI_TEST)
    private boolean enableMultiTest;

    public static final String TEQ = "TEQ";
    public static final double MIN_PERCENT_TO_GET_HIGH_EMPATHY = 0.7;

    @Override
    public BaseResp getPersonalAssessment(Long userId, String locale, Authentication authentication) {
        long userProfileId = getUserId(userId, authentication);
        List<PersonalAssessment> personalAssessments = personalAssessmentService.getActivePersonalAssessment();
        List<Long> personalAssessmentIds = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(personalAssessments)) {
           personalAssessmentIds = userPersonalityService.getPersonalAssessmentIdByUserProfileId(userProfileId);
        }
        List<PersonalAssessmentOfUserDTO> assessments = new ArrayList<>();
        List<Long> finalPersonalAssessmentIds = personalAssessmentIds;
        personalAssessments.forEach(p -> {
            Optional<Long> idOption = finalPersonalAssessmentIds.stream().filter(id -> id.equals(p.getId())).findFirst();
            assessments.add(new PersonalAssessmentOfUserDTO(p, locale, idOption.isPresent()));
        });
        return new BaseResp<>(assessments);
    }

    @Override
    public BaseResp getQuestions(Long id, String locale, Long userProfileId) {
        if (Objects.isNull(id)) {
            throw new InvalidParamException(ResponseStatus.ID_IS_EMPTY);
        }
        PersonalAssessment foundPersonalAssessment = personalAssessmentService.findById(id);
        if (Objects.isNull(foundPersonalAssessment)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_PERSONAL_ASSESSMENT);
        }

        List<PersonalAssessmentQuestion> questions = personalAssessmentQuestionService.getByPersonalAssessmentId(id);

        boolean isTested;
        int isExisted = userPersonalityService.countPersonalAssessmentByUserProfileId(userProfileId, id);
        isTested = isExisted > 0;

        PersonalAssessmentFullDTO questionResp = new PersonalAssessmentFullDTO(foundPersonalAssessment, locale, isTested, questions);
        return new BaseResp<>(questionResp);
    }

    @Override
    public BaseResp savePersonalAssessmentAnswer(long userId, long assessmentId,
                                                 SubmitPersonalAssessmentReq personalAssessmentTest) {
        List<UserPersonality> result = submitPersonalAssessment(userId, assessmentId, personalAssessmentTest);
        List<UserPersonalityDTO> userAnswers = result.stream().map(
                up -> new UserPersonalityDTO(up, personalAssessmentTest.getLocale())).collect(Collectors.toList());
        boostActorManager.saveUserPersonalityInOracleAndMongo(userId);
        return new BaseResp<>(userAnswers);
    }

    private List<UserPersonality> submitPersonalAssessment(long userId, long assessmentId,
                                                           SubmitPersonalAssessmentReq personalAssessmentTest) {
        if (Objects.isNull(personalAssessmentTest) || MapUtils.isEmpty(personalAssessmentTest.getAnswers())) {
            throw new InvalidParamException(ResponseStatus.SUBMIT_PERSONAL_ASSESSMENT_EMPTY_ANSWER);
        }
        PersonalAssessment personalAssessment = personalAssessmentService.findById(assessmentId);
        if (Objects.isNull(personalAssessment)) {
            throw new EntityNotFoundException(ResponseStatus.SUBMIT_PERSONAL_ASSESSMENT_NOT_EXIST_ASSESSMENT);
        }
        List<PersonalAssessmentQuestion> questions = personalAssessmentQuestionService.getByPersonalAssessmentId(assessmentId);
        if (CollectionUtils.isEmpty(questions)) {
            throw new NoPermissionException(ResponseStatus.SUBMIT_PERSONAL_ASSESSMENT_NO_ANSWER_IN_ASSESSMENT);
        }
        AtomicInteger countAnswerOfAssessment = new AtomicInteger();
        AtomicInteger countValidAnswerOfAssessment = new AtomicInteger();
        Map<Long, PersonalAssessmentAnswer> answers = new HashMap<>();
        personalAssessmentTest.getAnswers().forEach((key, value) -> {
            if (questions.stream().anyMatch(q -> q.getId().equals(key))) {
                answers.put(key, value);
            }
        });
        answers.forEach((key,value) -> {
            Optional<PersonalAssessmentQuestion> findQuestion = questions.stream().filter(q -> q.getId().equals(key)).findFirst();
            findQuestion.ifPresent(q -> {
                value.setPersonalAssessmentQuestion(findQuestion.get());
                countAnswerOfAssessment.getAndIncrement();
                int maxIndex = findQuestion.get().getMaxValue() - findQuestion.get().getMinValue();
                if (maxIndex >= value.getAnswerValue() && 0 <= value.getAnswerValue()) {
                    countValidAnswerOfAssessment.getAndIncrement();
                }
            });
        });
        if (countAnswerOfAssessment.get() < questions.size()) {
            throw new NoPermissionException(ResponseStatus.SUBMIT_PERSONAL_ASSESSMENT_NOT_ENOUGH_ANSWER);
        }
        if (countValidAnswerOfAssessment.get() < questions.size()) {
            throw new NoPermissionException(ResponseStatus.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_NOT_RANGE_VALID_RANGE);
        }
        int countTestTime = 0;
        if (!enableMultiTest) {
            countTestTime = userPersonalityService.countPersonalAssessmentByUserProfileId(userId, assessmentId);
        }
        if (countTestTime > 0) {
            throw new NoPermissionException(ResponseStatus.SUBMIT_PERSONAL_ASSESSMENT_ANSWER_TESTED_ALREADY);
        }

        List<UserPersonality> userPersonalities = new ArrayList<>();
        answers.forEach((key,value) -> {
            UserPersonality userPersonality = new UserPersonality(userId, value.getPersonalAssessmentQuestion(),
                    value.getAnswerValue(), value.getAnswerTime(), assessmentId);
            userPersonalities.add(userPersonality);
        });

        if (enableMultiTest) {
            userPersonalityService.removeOldResult(userId, assessmentId);
        }

        return userPersonalityService.save(userPersonalities);
    }

    @Override
    public BaseResp savePersonalAssessmentAnswerV2(long userId, long assessmentId,
                                                 SubmitPersonalAssessmentReq personalAssessmentTest) {
        List<UserPersonality> result = submitPersonalAssessment(userId, assessmentId, personalAssessmentTest);
        boostActorManager.saveUserPersonalityInOracleAndMongo(userId);
        PersonalAssessment personalAssessment = result.get(0).getPersonalAssessmentQuestion()
                .getPersonalAssessmentQuality().getPersonalAssessment();
        UserPersonalityResultDTO personalityResult = calculatePersonalityResult(personalAssessment, personalAssessmentTest.getLocale(), result);
        trackingEventGetHighEmpathyTestScore(personalityResult, userId);
        trackingFillAllProfileFields(userId);
        return new BaseResp<>(personalityResult);
    }

    private void trackingEventGetHighEmpathyTestScore(UserPersonalityResultDTO personalityResult, Long userId) {
        AtomicInteger scoreOfAllAnswers = new AtomicInteger();
        AtomicInteger totalScoreOfAllAnswers = new AtomicInteger();
        personalityResult.getResults().forEach(it -> {
            scoreOfAllAnswers.addAndGet((int) it.getScore());
            totalScoreOfAllAnswers.addAndGet(it.getTotalScore());
        });
        if (personalityResult.getPersonalAssessment().getCodeName().equals(TEQ) && ((double) scoreOfAllAnswers.get()/totalScoreOfAllAnswers.get()) > MIN_PERCENT_TO_GET_HIGH_EMPATHY)
            businessProfileAttributeEventService.onAttributeEvent(EVT_GET_A_HIGH_EMPATHY_TEST_SCORE, userId);

    }

    private UserPersonalityResultDTO calculatePersonalityResult(PersonalAssessment personalAssessment, String locale,
                                                                List<UserPersonality> userPersonalities) {
        UserPersonalityResultDTO personalityResult = new UserPersonalityResultDTO();
        if (Objects.nonNull(personalAssessment) && CollectionUtils.isNotEmpty(userPersonalities)) {
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

    @Override
    public BaseResp getPersonalTestResult(Long userProfileId, String locale, Long personalAssessmentId, Authentication authentication) {
        PersonalAssessment personalAssessment = personalAssessmentService.findById(personalAssessmentId);
        if (Objects.isNull(personalAssessment)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_PERSONAL_ASSESSMENT);
        }
        List<UserPersonality> userPersonalities = userPersonalityService.getByUserProfileIdAndPersonalAssessmentId(userProfileId, personalAssessment.getId());
        List<UserPersonalityDTO> userPersonalityDTOS = userPersonalities.stream().map(up -> new UserPersonalityDTO(up, locale)).collect(Collectors.toList());
        return new BaseResp<>(userPersonalityDTOS);
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

    @Override
    public BaseResp getPersonalTestResultV2(Long userProfileId, String locale, Long personalAssessmentId, Authentication authentication) {
        long userId = getUserId(userProfileId, authentication);
        PersonalAssessment personalAssessment = personalAssessmentService.findById(personalAssessmentId);
        if (Objects.isNull(personalAssessment)) {
            throw new EntityNotFoundException(ResponseStatus.NOT_FOUND_PERSONAL_ASSESSMENT);
        }
        List<UserPersonality> userPersonalities = userPersonalityService.getByUserProfileIdAndPersonalAssessmentId(userId, personalAssessment.getId());
        UserPersonalityResultDTO personalityResult = calculatePersonalityResult(personalAssessment, locale, userPersonalities);
        return new BaseResp<>(personalityResult);
    }

    private long getUserId(Long userId, Authentication authentication) {
        long userProfileId;
        if (Objects.isNull(userId)) {
            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            userProfileId = authenticatedUser.getId();
        } else {
            businessValidatorService.checkExistsUserProfile(userId);
            userProfileId = userId;
        }
        return userProfileId;
    }
}