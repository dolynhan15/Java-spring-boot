package com.qooco.boost.threads.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import com.google.common.collect.ImmutableList;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.threads.models.DataFeedback;
import com.qooco.boost.threads.models.DataFeedbackCandidate;
import com.qooco.boost.threads.models.DataFeedbackVacancy;
import com.qooco.boost.threads.services.FeedbackDataProfileUserService;
import com.qooco.boost.threads.services.FeedbackDataSelectUserService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.qooco.boost.data.constants.Constants.DEFAULT_LIMITED_ITEM;
import static com.qooco.boost.threads.models.DataFeedback.*;
import static java.util.Optional.ofNullable;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class DataFeedbackActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(DataFeedbackActor.class);
    public static final String ACTOR_NAME = "dataFeedbackActor";

    private final FeedbackDataProfileUserService feedbackDataProfileUserService;
    private final FeedbackDataSelectUserService feedbackDataSelectUserService;
    private final UserCvDocService userCvDocService;
    private final VacancyDocService vacancyDocService;

    //Service link from other system
    @Value(ApplicationConstant.BOOST_PATA_VACANCY_JOB_ALPHA_TESTER)
    private int vacancyJobAlphaTest;

    //Service link from other system
    @Value(ApplicationConstant.MATCHING_CANDIDATE_BOOST_SCORE)
    private boolean boostScoreEnabled;

    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    @Value(ApplicationConstant.BOOST_PATA_CERTIFICATION_PERIOD)
    private int expiredDays;

    @Override
    public void onReceive(Object message) {
        if (message instanceof DataFeedbackCandidate) {
            doFeedBackDataBase((DataFeedbackCandidate) message);

        } else if (message instanceof DataFeedbackVacancy) {
            doFeedBackDataBase((DataFeedbackVacancy) message);

        } else if (message instanceof String && SCHEDULE_DATA_FEEDBACK_PROFILE.equals(message)) {
            int size;
            var cvId = new AtomicLong();
            do {
                var userCvDocs = userCvDocService.getByIdGreaterThan(cvId.get(), Constants.DEFAULT_LIMITED_ITEM);

                size = userCvDocs.size();
                userCvDocs.forEach(cv -> {
                    ImmutableList.of(
                            new DataFeedbackCandidate(PROFILE_LOW_QUALIFICATION).userProfileId(cv.getUserProfile().getUserProfileId()),
                            new DataFeedbackCandidate(PROFILE_LOCATION_CHANGE).userProfileId(cv.getUserProfile().getUserProfileId()),
                            new DataFeedbackCandidate(PROFILE_SEATS_CLOSED).userProfileId(cv.getUserProfile().getUserProfileId())
                    ).forEach(it -> getSelf().tell(it, ActorRef.noSender()));

                    cvId.set(cv.getId());
                });
            } while (size > 0 && size < DEFAULT_LIMITED_ITEM);


            ImmutableList.of(new DataFeedbackVacancy(PROFILE_NOT_ENOUGH_EXPERIENCE),
                    new DataFeedbackVacancy(PROFILE_NO_QUALIFICATION),
                    new DataFeedbackVacancy(PROFILE_SALARY_TOO_HIGH),
                    new DataFeedbackCandidate(PROFILE_CODES_SHARED)
            ).forEach(it -> getSelf().tell(it, ActorRef.noSender()));

            logger.debug(String.format("Finish %s", SCHEDULE_DATA_FEEDBACK_PROFILE));

        } else if (message instanceof String && SCHEDULE_DATA_FEEDBACK_SELECT.equals(message)) {
            int size;
            var vacancyId = new AtomicLong();
            do {
                var vacancyDocs = vacancyDocService.findOpenVacancyByIdGreaterThan(vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);

                size = vacancyDocs.size();
                vacancyDocs.forEach(v -> {
                    ImmutableList.of(
                            new DataFeedbackVacancy(SELECT_SEARCH_RANGE_TOO_SMALL).vacancy(v),
                            new DataFeedbackVacancy(SELECT_SALARY_TOO_LOW).vacancy(v),
                            new DataFeedbackVacancy(SELECT_QUALIFICATION_TOO_LOW).vacancy(v)
                    ).forEach(it -> getSelf().tell(it, ActorRef.noSender()));
                    vacancyId.set(v.getId());
                });
            } while (size > 0);


            logger.debug(String.format("Finish %s", SCHEDULE_DATA_FEEDBACK_SELECT));

        } else if (message instanceof String && SCHEDULE_THREE_DAYS_DATA_FEEDBACK_SELECT.equals(message)) {
            int size;
            var vacancyId = new AtomicLong();
            do {
                var vacancyDocs = vacancyDocService.findOpenVacancyByIdGreaterThan(vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);

                size = vacancyDocs.size();
                vacancyDocs.forEach(v -> {
                    ImmutableList.of(
                            new DataFeedbackVacancy(SELECT_SALARY_TOO_HIGH).vacancy(v),
                            new DataFeedbackVacancy(SELECT_QUALIFICATION_TOO_HIGH).vacancy(v)
                    ).forEach(it -> getSelf().tell(it, ActorRef.noSender()));
                    vacancyId.set(v.getId());
                });
            } while (size > 0);
            logger.debug(String.format("Finish %s", SCHEDULE_THREE_DAYS_DATA_FEEDBACK_SELECT));

        }
    }

    private void doFeedBackDataBase(DataFeedback dataFeedback) {
        MatchingCandidateConfig candidateConfig = initMatchingCandidateConfig();
        UserCvDoc userCvDoc = null;
        VacancyDoc vacancy = null;
        if (dataFeedback instanceof DataFeedbackCandidate) {
            userCvDoc = getUserCvDoc((DataFeedbackCandidate) dataFeedback);
        } else if (dataFeedback instanceof DataFeedbackVacancy) {
            vacancy = ((DataFeedbackVacancy) dataFeedback).vacancy();
        }

        switch (dataFeedback.feedbackType()) {
            case PROFILE_LOW_QUALIFICATION:
                ofNullable(userCvDoc).ifPresent(it -> feedbackDataProfileUserService.dataFeedbackLowQualifications(it, candidateConfig));
                break;
            case PROFILE_LOCATION_CHANGE:
                ofNullable(userCvDoc).ifPresent(it -> feedbackDataProfileUserService.dataFeedbackChangeLocateSuggestion(it, candidateConfig));
                break;
            case PROFILE_SEATS_CLOSED:
                ofNullable(userCvDoc).ifPresent(feedbackDataProfileUserService::dataFeedbackSeatsClosed);
                break;
            case PROFILE_CODES_SHARED:
                feedbackDataProfileUserService.dataFeedbackCodesShared();
                break;
            case PROFILE_NO_QUALIFICATION:
                feedbackDataProfileUserService.dataFeedbackNoQualification(candidateConfig);
                break;
            case PROFILE_NOT_ENOUGH_EXPERIENCE:
                feedbackDataProfileUserService.dataFeedbackNotEnoughExperience(candidateConfig);
                break;
            case PROFILE_SALARY_TOO_HIGH:
                feedbackDataProfileUserService.dataFeedbackSalaryTooHigh(candidateConfig);
                break;

            case SELECT_SEARCH_RANGE_TOO_SMALL:
                ofNullable(vacancy).ifPresent(it -> feedbackDataSelectUserService.dataFeedbackSearchRangeTooSmall(it, candidateConfig));
                break;
            case SELECT_QUALIFICATION_TOO_HIGH:
                ofNullable(vacancy).ifPresent(it -> feedbackDataSelectUserService.dataFeedbackQualificationTooHigh(it, candidateConfig));
                break;
            case SELECT_QUALIFICATION_TOO_LOW:
                ofNullable(vacancy).ifPresent(it -> feedbackDataSelectUserService.dataFeedbackQualificationTooLow(it, candidateConfig));
                break;
            case SELECT_SALARY_TOO_HIGH:
                ofNullable(vacancy).ifPresent(it -> feedbackDataSelectUserService.dataFeedbackSalaryTooHigh(it, candidateConfig));
                break;
            case SELECT_SALARY_TOO_LOW:
                ofNullable(vacancy).ifPresent(it -> feedbackDataSelectUserService.dataFeedbackSalaryTooLow(it, candidateConfig));
                break;
            default:
                break;
        }
    }

    private UserCvDoc getUserCvDoc(DataFeedbackCandidate dataFeedback) {
        UserCvDoc userCvDoc = null;
        if (dataFeedback.userProfileId() > 0) {
            userCvDoc = userCvDocService.findByUserProfileId(dataFeedback.userProfileId());
            logger.debug(String.format("DataFeedback userId = %d || userCvDoc is non null = %b", dataFeedback.userProfileId(), Objects.nonNull(userCvDoc)));
        }
        return userCvDoc;
    }

    private MatchingCandidateConfig initMatchingCandidateConfig() {
        MatchingCandidateConfig candidateConfig = new MatchingCandidateConfig();
        candidateConfig.setBoostScoreEnabled(boostScoreEnabled);
        candidateConfig.setExpiredDays(expiredDays);
        candidateConfig.setVacancyJobAlphaTest(vacancyJobAlphaTest);
        return candidateConfig;
    }

}
