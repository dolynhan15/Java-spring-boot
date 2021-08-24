package com.qooco.boost.threads.services.impl;

import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.constants.SearchRange;
import com.qooco.boost.data.constants.UserType;
import com.qooco.boost.data.model.QualificationAverage;
import com.qooco.boost.data.mongo.embedded.UserProfileCvMessageEmbedded;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.UserFeedbackDataService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.enumeration.BoostHelperEventType;
import com.qooco.boost.threads.services.BoostHelperService;
import com.qooco.boost.threads.services.FeedbackDataSelectUserService;
import com.qooco.boost.utils.MongoConverters;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackDataSelectUserServiceImpl implements FeedbackDataSelectUserService {

    protected Logger logger = LogManager.getLogger(FeedbackDataSelectUserServiceImpl.class);
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private UserFeedbackDataService userFeedbackDataService;
    @Autowired
    private BoostHelperService boostHelperService;

    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    private static final Integer MIN_FOUND_CANDIDATE_NUMBER = 2;

    private static final Integer MIN_FOUND_CANDIDATE_FOR_QUALIFICATION = 10;

    @Override
    public void dataFeedbackSearchRangeTooSmall(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig) {
        long countCandidate = userCvDocService.countMatchingCvForVacancy(vacancyDoc, rejectedLimitTime,candidateConfig);
        if (countCandidate == 0) {
            boolean isExpandSearch = false;
            switch (vacancyDoc.getSearchRange()) {
                case SearchRange.CITY:
                    vacancyDoc.setSearchRange(SearchRange.PROVINCE);
                    isExpandSearch = true;
                    break;
                case SearchRange.PROVINCE:
                    vacancyDoc.setSearchRange(SearchRange.COUNTRY);
                    isExpandSearch = true;
                    break;
            }
            if (isExpandSearch) {
                long countInExpandedRange = userCvDocService.countMatchingCvForVacancy(vacancyDoc, rejectedLimitTime,candidateConfig);
                if (countInExpandedRange >= MIN_FOUND_CANDIDATE_NUMBER) {
                    sendMessageSearchRangeTooSmall(vacancyDoc);
                }
            }
        }
    }

    @Override
    public void dataFeedbackQualificationTooHigh(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig) {
        long countCandidate = userFeedbackDataService.countMatchingCvForVacancyIgnoreQualification(vacancyDoc, rejectedLimitTime, candidateConfig);
        if (countCandidate >= MIN_FOUND_CANDIDATE_FOR_QUALIFICATION) {
            List<QualificationAverage> averageLevels = userFeedbackDataService.getAverageLevelByVacancy(
                    vacancyDoc, rejectedLimitTime, candidateConfig);
            boolean isHigherLevel = !averageLevels.isEmpty() && averageLevels.stream()
                    .allMatch(averageLevel -> vacancyDoc.getQualifications().stream()
                            .anyMatch(q -> q.getAssessment().getId() == averageLevel.getId()
                                    && q.getLevel().getAssessmentLevel() > averageLevel.getAvgLevel()));
            if (isHigherLevel) {
                sendMessageQualificationTooHigh(vacancyDoc);
            }
        }
    }

    @Override
    public void dataFeedbackQualificationTooLow(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig) {
        long countCandidate = userFeedbackDataService.countMatchingCvForVacancyIgnoreQualification(vacancyDoc, rejectedLimitTime,candidateConfig);
        if (countCandidate >= MIN_FOUND_CANDIDATE_FOR_QUALIFICATION) {
            List<QualificationAverage> averageLevels = userFeedbackDataService.getAverageLevelByVacancy(
                    vacancyDoc, rejectedLimitTime, candidateConfig);

            boolean isLowerLevel = !averageLevels.isEmpty() && averageLevels.stream()
                    .allMatch(averageLevel -> vacancyDoc.getQualifications().stream()
                            .anyMatch(q -> q.getAssessment().getId() == averageLevel.getId()
                                    && q.getLevel().getAssessmentLevel() < averageLevel.getAvgLevel()));
            if (isLowerLevel) {
                sendMessageQualificationTooLow(vacancyDoc);
            }
        }
    }

    @Override
    public void dataFeedbackSalaryTooHigh(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig) {
        boolean isSalaryTooHigh = userCvDocService.isSalaryTooHigh(vacancyDoc, rejectedLimitTime, candidateConfig);
        if (isSalaryTooHigh) {
            sendMessageSalaryTooHigh(vacancyDoc);
        }
    }

    @Override
    public void dataFeedbackSalaryTooLow(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig) {
        boolean isSalaryTooToo = userCvDocService.isSalaryTooLow(vacancyDoc, rejectedLimitTime, candidateConfig);
        if (isSalaryTooToo) {
            sendMessageSalaryTooLow(vacancyDoc);
        }
    }

    private void sendMessageSearchRangeTooSmall(VacancyDoc vacancyDoc) {
        var recipient = new UserProfileCvMessageEmbedded(vacancyDoc.getContactPerson().getUserProfile(), UserType.SELECT);
        var messageDoc = boostHelperService.createMessageDoc(recipient, BoostHelperEventType.BOOST_FEEDBACK_DATA_SEARCH_RANGE_TOO_SMALL, MessageConstants.RECEIVE_IN_HOTEL_APP);
        messageDoc.getBoostHelperMessage().setText(String.format(messageDoc.getBoostHelperMessage().getText(), vacancyDoc.getJob().getName()));
        messageDoc.getBoostHelperMessage().setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDoc));
        boostHelperService.saveAndSendMessage(messageDoc, null);
    }

    private void sendMessageQualificationTooHigh(VacancyDoc vacancyDoc) {
        var recipient = new UserProfileCvMessageEmbedded(vacancyDoc.getContactPerson().getUserProfile(), UserType.SELECT);
        var messageDoc = boostHelperService.createMessageDoc(recipient, BoostHelperEventType.BOOST_FEEDBACK_DATA_QUALIFICATION_TOO_HIGH, MessageConstants.RECEIVE_IN_HOTEL_APP);
        messageDoc.getBoostHelperMessage().setText(String.format(messageDoc.getBoostHelperMessage().getText(), vacancyDoc.getJob().getName()));
        messageDoc.getBoostHelperMessage().setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDoc));
        boostHelperService.saveAndSendMessage(messageDoc, null);
    }

    private void sendMessageQualificationTooLow(VacancyDoc vacancyDoc) {
        var recipient = new UserProfileCvMessageEmbedded(vacancyDoc.getContactPerson().getUserProfile(), UserType.SELECT);
        var messageDoc = boostHelperService.createMessageDoc(recipient, BoostHelperEventType.BOOST_FEEDBACK_DATA_QUALIFICATION_TOO_LOW, MessageConstants.RECEIVE_IN_HOTEL_APP);
        messageDoc.getBoostHelperMessage().setText(String.format(messageDoc.getBoostHelperMessage().getText(), vacancyDoc.getJob().getName()));
        messageDoc.getBoostHelperMessage().setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDoc));
        boostHelperService.saveAndSendMessage(messageDoc, null);
    }

    private void sendMessageSalaryTooHigh(VacancyDoc vacancyDoc) {
        var recipient = new UserProfileCvMessageEmbedded(vacancyDoc.getContactPerson().getUserProfile(), UserType.SELECT);
        var messageDoc = boostHelperService.createMessageDoc(recipient, BoostHelperEventType.BOOST_FEEDBACK_DATA_SALARY_VACANCY_TOO_HIGH, MessageConstants.RECEIVE_IN_HOTEL_APP);
        messageDoc.getBoostHelperMessage().setText(String.format(messageDoc.getBoostHelperMessage().getText(), vacancyDoc.getJob().getName()));
        messageDoc.getBoostHelperMessage().setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDoc));
        boostHelperService.saveAndSendMessage(messageDoc, null);
    }

    private void sendMessageSalaryTooLow(VacancyDoc vacancyDoc) {
        var recipient = new UserProfileCvMessageEmbedded(vacancyDoc.getContactPerson().getUserProfile(), UserType.SELECT);
        var messageDoc = boostHelperService.createMessageDoc(recipient, BoostHelperEventType.BOOST_FEEDBACK_DATA_SALARY_VACANCY_TOO_LOW, MessageConstants.RECEIVE_IN_HOTEL_APP);
        messageDoc.getBoostHelperMessage().setText(String.format(messageDoc.getBoostHelperMessage().getText(), vacancyDoc.getJob().getName()));
        messageDoc.getBoostHelperMessage().setVacancy(MongoConverters.convertToVacancyEmbedded(vacancyDoc));
        boostHelperService.saveAndSendMessage(messageDoc, null);
    }
}
