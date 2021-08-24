package com.qooco.boost.threads.services.impl;

import com.google.common.collect.Lists;
import com.qooco.boost.business.BusinessReferralService;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.constants.MessageConstants;
import com.qooco.boost.data.model.count.CountByLocation;
import com.qooco.boost.data.model.count.LongCount;
import com.qooco.boost.data.mongo.embedded.AssessmentFullEmbedded;
import com.qooco.boost.data.mongo.embedded.JobEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.AssessmentLevelEmbedded;
import com.qooco.boost.data.mongo.embedded.assessment.QualificationEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.UserFeedbackDataService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.oracle.services.AssessmentService;
import com.qooco.boost.data.oracle.services.ShareCodeService;
import com.qooco.boost.data.oracle.services.VacancyCandidateService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.enumeration.BoostHelperEventType;
import com.qooco.boost.models.sdo.UserCreatedDateSDO;
import com.qooco.boost.threads.services.BoostHelperService;
import com.qooco.boost.threads.services.FeedbackDataProfileUserService;
import com.qooco.boost.utils.DateUtils;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.qooco.boost.constants.Const.Vacancy.CandidateStatus.RECRUITED;
import static com.qooco.boost.enumeration.BoostHelperEventType.*;
import static com.qooco.boost.threads.models.DataFeedback.*;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.*;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.springframework.util.StringUtils.isEmpty;

@Service
public class FeedbackDataProfileUserServiceImpl implements FeedbackDataProfileUserService {
    protected Logger logger = LogManager.getLogger(FeedbackDataProfileUserServiceImpl.class);
    @Autowired
    private VacancyDocService vacancyDocService;
    @Autowired
    private BoostHelperService boostHelperService;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private VacancyCandidateService vacancyCandidateService;
    @Autowired
    private BusinessReferralService businessReferralService;
    @Autowired
    private UserCvDocService userCvDocService;
    @Autowired
    private ShareCodeService shareCodeService;
    @Autowired
    private UserFeedbackDataService userFeedbackDataService;

    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    @Override
    public void dataFeedbackChangeLocateSuggestion(UserCvDoc userCvDoc, MatchingCandidateConfig candidateConfig) {
        //Data feedback - Change location suggestion
        if (nonNull(userCvDoc.getUserProfile().getCity()) && isNotEmpty(userCvDoc.getJobs())) {
            var userCity = userCvDoc.getUserProfile().getCity();
            var citySuggestions = new HashMap<Long, List<CountByLocation>>();
            for (JobEmbedded job : userCvDoc.getJobs()) {
                var vacancyCounts = vacancyDocService.countVacancyGroupByLocation(userCvDoc, candidateConfig);
                logger.info("vacancy count for user " + userCvDoc.getUserProfile().getUsername() + " : " + vacancyCounts.size());
                var groupByCity = vacancyCounts.stream().collect(groupingBy(CountByLocation::getCityId));

                if (Objects.isNull(groupByCity.get(userCity.getId())) || vacancyCounts.stream().anyMatch(it -> userCity.getId().equals(it.getCityId()) && it.getTotal() == 0)) {
                    var countByProvince = vacancyCounts.stream().collect(groupingBy(CountByLocation::getProvinceId, summingInt(CountByLocation::getTotal)));
                    var provinceVacancyNumber = countByProvince.get(userCity.getProvince().getId());

                    if (Objects.nonNull(provinceVacancyNumber) && provinceVacancyNumber >= MIN_VACANCY_NUMBER) {
                        //Suggestion change the city
                        var cityHaveVacancy = vacancyCounts.stream().filter(vacancyCount -> userCity.getProvince().getId().equals(vacancyCount.getProvinceId()) && vacancyCount.getTotal() > 0).collect(toImmutableList());
                        citySuggestions.put(job.getId(), cityHaveVacancy);
                    } else {
                        countByProvince.entrySet().stream()
                                .filter(provinceCount -> !userCity.getProvince().getId().equals(provinceCount.getKey()) && provinceCount.getValue() >= MIN_VACANCY_NUMBER)
                                .forEach(entryMap -> {
                                    //Suggestion to city in another province
                                    var cityHaveVacancy = vacancyCounts.stream().filter(vacancyCount -> entryMap.getKey().equals(vacancyCount.getProvinceId()) && vacancyCount.getTotal() > 0).collect(toImmutableList());
                                    var result = ofNullable(citySuggestions.get(job.getId())).orElse(new ArrayList<>());
                                    result.addAll(cityHaveVacancy);
                                    citySuggestions.put(job.getId(), result);
                                });
                    }
                } else {
                    citySuggestions.clear();
                    break;
                }
            }
            //Suggestion after have result
            if (MapUtils.isNotEmpty(citySuggestions)) {
                sendMessageSuggestion(userCvDoc, BoostHelperEventType.BOOST_FEEDBACK_DATA_LOCATION_SUGGESTION);
            }
        }
    }

    @Override
    public void dataFeedbackNoQualification(MatchingCandidateConfig candidateConfig) {
        Date qualificationExpiredDate = DateUtils.addDays(DateUtils.toServerTimeForMongo(), -candidateConfig.getExpiredDays());
        var warningUsers = new HashSet<Long>();
        var vacancyId = new AtomicLong();

        for (; ; ) {
            List<VacancyDoc> vacancies = vacancyDocService.findOpenVacancyByIdGreaterThan(vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);

            if (vacancies.isEmpty()) break;
            vacancies.forEach(vacancy -> {
                var candidates = userFeedbackDataService.findMatchingCvForVacancyNoQualification(vacancy, rejectedLimitTime, candidateConfig)
                        .stream().map(it -> LongCount.builder().id(it.getId())
                                .total(ofNullable(it.getQualifications())
                                        .map(qualifications -> qualifications.stream()
                                                .filter(qualification -> qualificationExpiredDate.before(qualification.getSubmissionTime()))
                                                .count()).orElse(0L)).build()).collect(toImmutableList());

                var userNoQualification = candidates.stream().filter(it -> it.getTotal() == 0).map(LongCount::getId).collect(toList());
                var userHasQualification = candidates.stream().filter(it -> it.getTotal() > 0).map(LongCount::getId).collect(toImmutableList());

                if (candidates.size() >= MIN_USER_HAS_QUALIFICATION && CollectionUtils.isNotEmpty(userNoQualification) && ((double) userHasQualification.size()) / candidates.size() >= MIN_SIMILAR_PROFILE_RATE) {
                    logger.info(String.format("dataFeedbackNoQualification %d List %s: ", vacancy.getId(), StringUtil.convertToJson(userNoQualification)));
                    warningUsers.addAll(userNoQualification);
                }

                vacancyId.set(vacancy.getId());
            });
        }
        userCvDocService.findAllById(warningUsers).forEach(it -> sendMessageSuggestion(it, BOOST_FEEDBACK_DATA_NO_QUALIFICATION));
    }

    @Override
    public void dataFeedbackLowQualifications(UserCvDoc userCvDoc, MatchingCandidateConfig candidateConfig) {
        List<QualificationEmbedded> qualifications = getActiveQualifications(userCvDoc, candidateConfig.getExpiredDays());
        qualifications.forEach(it -> {
            long count = vacancyDocService.countLowQualificationForAssessment(it, userCvDoc, candidateConfig);
            if (count >= MIN_VACANCY_NUMBER) {
                var recipient = MongoConverters.convertToUserProfileCvMessageEmbedded(userCvDoc);
                var messageDoc = boostHelperService.createMessageDoc(recipient, BoostHelperEventType.BOOST_FEEDBACK_DATA_LOW_QUALIFICATION, MessageConstants.RECEIVE_IN_CAREER_APP);
                messageDoc.getBoostHelperMessage().setText(String.format(messageDoc.getBoostHelperMessage().getText(), it.getAssessment().getName(), getFormattedAssessmentLevel(it.getLevel())));
                messageDoc.getBoostHelperMessage().setAssessment(new AssessmentFullEmbedded(assessmentService.findById(it.getAssessment().getId()), it.getId()));
                boostHelperService.saveAndSendMessage(messageDoc, null);
            }
        });
    }

    @Override
    public void dataFeedbackNotEnoughExperience(MatchingCandidateConfig candidateConfig) {
        var receivedWarningUsers = new ArrayList<Long>();
        var vacancyId = new AtomicLong();
        for (; ; ) {
            List<VacancyDoc> vacancies = vacancyDocService.findOpenVacancyByIdGreaterThan(vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);

            if (vacancies.isEmpty()) break;
            vacancies.forEach(vacancy -> {
                Optional.of(dataFeedbackNotEnoughExperience(vacancy, candidateConfig, receivedWarningUsers))
                        .filter(CollectionUtils::isNotEmpty).ifPresent(receivedWarningUsers::addAll);
                vacancyId.set(vacancy.getId());
            });
        }
    }

    private List<Long> dataFeedbackNotEnoughExperience(VacancyDoc vacancyDoc, MatchingCandidateConfig candidateConfig, List<Long> excludeIds) {
        List<UserCvDoc> result = userFeedbackDataService.findMatchingCvForVacancyNotEnoughExperience(vacancyDoc, rejectedLimitTime, candidateConfig, excludeIds);
        result.forEach(it -> {
            logger.info(String.format("NotEnoughExperience - send message to userCvId %d from vacancy %d", it.getId(), vacancyDoc.getId()));
            sendMessageSuggestion(it, BoostHelperEventType.BOOST_FEEDBACK_DATA_NOT_ENOUGH_EXPERIENCE);
        });
        return result.stream().map(UserCvDoc::getId).collect(toImmutableList());
    }

    @Override
    public void dataFeedbackSeatsClosed(UserCvDoc userCvDoc) {
        if (nonNull(userCvDoc.getUserProfile()) && nonNull(userCvDoc.getUserProfile().getCity())) {
            var userCity = userCvDoc.getUserProfile().getCity().getProvince();
            var vacancyGroup = vacancyDocService.countVacancyGroupByProvince(userCity);
            if (nonNull(vacancyGroup) && vacancyGroup.getTotal() != 0) {
                var vacancyIds = vacancyGroup.getVacancyIds();
                var startDate = DateUtils.atStartOfDate(DateUtils.nowUtcForOracle());
                var endDate = DateUtils.atEndOfDate(DateUtils.nowUtcForOracle());
                var closedCandidates = vacancyCandidateService.findByVacancyAndStatusAndDuration(vacancyIds, Lists.newArrayList(RECRUITED), startDate, endDate);
                if (isNotEmpty(closedCandidates) && closedCandidates.stream().noneMatch(it -> userCvDoc.getUserProfile().getUserProfileId().equals(it.getCandidate().getUserProfile().getUserProfileId())) && closedCandidates.size() >= MIN_CLOSED_SEAT_NUMBER) {
                    sendMessageSuggestion(userCvDoc, BOOST_FEEDBACK_DATA_SEATS_CLOSED);
                }
            }
        }
    }

    @Override
    public void dataFeedbackCodesShared() {
        var startDate = DateUtils.atStartOfDate(DateUtils.nowUtcForOracle());
        var endDate = DateUtils.atEndOfDate(DateUtils.nowUtcForOracle());
        var userByLocationList = shareCodeService.findUserSharedCodeGroupByLocationAndDuration(startDate, endDate);
        if (isNotEmpty(userByLocationList)) {
            Map<Long, List<UserCreatedDateSDO>> userGroup = new HashMap<>();
            userByLocationList.forEach(it -> {
                List<UserCreatedDateSDO> userCreatedDateSDOS = userGroup.get((Long) it[PROVINCE_ID_INDEX]);
                if (isEmpty(userCreatedDateSDOS)) {
                    userCreatedDateSDOS = new ArrayList<>();
                }
                var userAndCreatedDate = UserCreatedDateSDO.builder().userId((Long) it[USER_ID_INDEX]).referralCodeId((Long) it[REFERRAL_CODE_INDEX]).createdDateEvent((Date) it[CREATED_DATE_INDEX]).build();
                userCreatedDateSDOS.add(userAndCreatedDate);
                userGroup.put((Long) it[PROVINCE_ID_INDEX], userCreatedDateSDOS);
            });
            userGroup.forEach((key, value) -> {
                if (value.size() >= FIVE_SHARE_TIMES) {
                    var sharedCodeIds = value.stream().map(UserCreatedDateSDO::getReferralCodeId).distinct().collect(Collectors.toList());
                    var userIdsShared = value.stream().map(UserCreatedDateSDO::getUserId).distinct().collect(Collectors.toList());
                    if (sharedCodeIds.size() >= MIN_SHARED_CODES) {
                        var usersReceiveMsg = userCvDocService.findByProvinceAndProfileStrength(key, STRONG_PROFILE);
                        usersReceiveMsg.stream()
                                .filter(it -> !userIdsShared.contains(it.getUserProfile().getUserProfileId()))
                                .forEach(it -> sendMessageSuggestion(it, BOOST_FEEDBACK_CODES_SHARED));
                    }
                }
            });
        }
    }

    @Override
    public void dataFeedbackSalaryTooHigh(MatchingCandidateConfig candidateConfig) {
        var warningUsers = new HashSet<Long>();
        var vacancyId = new AtomicLong();

        for (; ; ) {
            List<VacancyDoc> vacancies = vacancyDocService.findOpenVacancyByIdGreaterThan(vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);

            if (vacancies.isEmpty()) break;
            vacancies.forEach(vacancy -> {
                var candidates = userFeedbackDataService.findMatchingCvForVacancySalaryTooHigh(vacancy, rejectedLimitTime, candidateConfig);
                var similarProfileAcceptableNumber = Math.round(candidates.size() * MIN_SIMILAR_PROFILE_RATE);
                if (similarProfileAcceptableNumber >= MIN_SIMILAR_PROFILE) {
                    var standardUser = candidates.get(candidates.size() - similarProfileAcceptableNumber);
                    logger.info(String.format("dataFeedbackSalaryTooHigh: Vacancy %d have matching %s", vacancy.getId(), StringUtil.convertToJson(candidates.stream().map(UserCvDoc::getId).collect(Collectors.toList()))));
                    for (UserCvDoc candidate : candidates) {
                        logger.info(String.format("dataFeedbackSalaryTooHigh: UserCvId  %d have salary %f. Compare with UserCvId %d have salary %f",
                                candidate.getId(), candidate.getMaxSalaryUsd(), standardUser.getId(), standardUser.getMaxSalaryUsd()));

                        if (candidate.getMaxSalaryUsd() / standardUser.getMaxSalaryUsd() > LOWER_RATE) {
                            warningUsers.add(candidate.getId());
                        } else {
                            break;
                        }
                    }
                }
                vacancyId.set(vacancy.getId());
            });
        }

        userCvDocService.findAllById(warningUsers).forEach(it -> sendMessageSuggestion(it, BOOST_FEEDBACK_DATA_SALARY_HIGH));
    }

    private void sendMessageSuggestion(UserCvDoc userCvDoc, BoostHelperEventType eventType) {
        var recipient = MongoConverters.convertToUserProfileCvMessageEmbedded(userCvDoc);
        if (BOOST_FEEDBACK_CODES_SHARED.equals(eventType)) {
            var referralCode = businessReferralService.generateActiveCode(userCvDoc.getUserProfile().getUserProfileId());
            ofNullable(referralCode).ifPresent(it -> {
                var messageDoc = boostHelperService.createMessageDoc(recipient, eventType, MessageConstants.RECEIVE_IN_CAREER_APP);
                messageDoc.getBoostHelperMessage().setReferralCode(it);
                boostHelperService.saveAndSendMessage(messageDoc, null);
            });
        } else {
            var messageDoc = boostHelperService.createMessageDoc(recipient, eventType, MessageConstants.RECEIVE_IN_CAREER_APP);
            boostHelperService.saveAndSendMessage(messageDoc, null);
        }
    }

    private List<QualificationEmbedded> getActiveQualifications(UserCvDoc userCvDoc, int expiredDays) {
        Date expiredDate = DateUtils.addDays(DateUtils.toServerTimeForMongo(), -expiredDays);
        if (CollectionUtils.isNotEmpty(userCvDoc.getQualifications())) {
            return userCvDoc.getQualifications().stream()
                    .filter(it -> expiredDate.before(it.getSubmissionTime()))
                    .collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private String getFormattedAssessmentLevel(AssessmentLevelEmbedded level) {
        if (Objects.nonNull(level)) {
            switch (level.getAssessmentLevel()) {
                case 1:
                    return "A1";
                case 2:
                    return "A2";
                case 3:
                    return "B1";
                case 4:
                    return "B2";
                default:
                    return ">B2";
            }
        }
        return "A1";
    }
}
