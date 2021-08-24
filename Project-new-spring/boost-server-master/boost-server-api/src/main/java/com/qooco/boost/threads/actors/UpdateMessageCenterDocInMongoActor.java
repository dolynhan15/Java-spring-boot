package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.mongodb.WriteResult;
import com.qooco.boost.constants.ApplicationConstant;
import com.qooco.boost.data.mongo.embedded.UserProfileCvEmbedded;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.MessageCenterDocService;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.data.utils.MatchingCandidateConfig;
import com.qooco.boost.utils.MongoConverters;
import com.qooco.boost.utils.StringUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateMessageCenterDocInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(UpdateMessageCenterDocInMongoActor.class);
    public static final String ACTOR_NAME = "updateMessageCenterDocInMongoActor";

    private MessageCenterDocService messageCenterDocService;
    private UserCvDocService userCvDocService;
    private VacancyDocService vacancyDocService;
    //Service link from other system
    @Value(ApplicationConstant.BOOST_PATA_VACANCY_JOB_ALPHA_TESTER)
    private int vacancyJobAlphaTest;

    @Value(ApplicationConstant.MATCHING_CANDIDATE_BOOST_SCORE)
    private boolean matchingBoostScoreEnabled;

    @Value(ApplicationConstant.BOOST_PATA_VACANCY_REJECTED_LIMIT_TIME)
    private int rejectedLimitTime;

    @Value(ApplicationConstant.LIMITED_CANDIDATES_SIZE)
    private int limitedCandidateSize;

    public UpdateMessageCenterDocInMongoActor(MessageCenterDocService messageCenterDocService,
                                              UserCvDocService userCvDocService,
                                              VacancyDocService vacancyDocService) {
        this.messageCenterDocService = messageCenterDocService;
        this.userCvDocService = userCvDocService;
        this.vacancyDocService = vacancyDocService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof VacancyDoc) {
            VacancyDoc vacancyDoc = ((VacancyDoc) message);
            Long vacancyId = vacancyDoc.getId();
            if (Objects.nonNull(vacancyId)) {

                MatchingCandidateConfig config = new MatchingCandidateConfig();
                config.setVacancyJobAlphaTest(vacancyJobAlphaTest);
                config.setBoostScoreEnabled(matchingBoostScoreEnabled);

                Page<UserCvDoc> foundCandidate = userCvDocService.findMatchingCvForVacancy(
                        vacancyDoc, 0, limitedCandidateSize, rejectedLimitTime, config);
                long countCandidate = userCvDocService.countMatchingCvForVacancy(vacancyDoc, rejectedLimitTime, config);
                List<UserCvDoc> userCvDocs = foundCandidate.getContent();
                List<UserProfileCvEmbedded> candidateFound = new ArrayList<>();
                if (CollectionUtils.isNotEmpty(userCvDocs)) {
                    userCvDocs.forEach(u -> candidateFound.add(MongoConverters.convertToUserProfileCvEmbedded(u)));
                }

                vacancyDocService.updateCandidateProfiles(candidateFound, vacancyDoc.getId(), countCandidate);
                var messageCenterDoc = messageCenterDocService.findByVacancy(vacancyId);
                if (Objects.nonNull(messageCenterDoc)) {
                    messageCenterDoc.setNumberOfCandidate(countCandidate);
                    WriteResult result = messageCenterDocService.updateNumberOfCandidate(messageCenterDoc);
                    logger.info(StringUtil.append("Update message center id =", messageCenterDoc.getId().toString(), " update row ", String.valueOf(result.getN())));
                }
            }
        }
    }

}