package com.qooco.boost.business.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.business.BusinessDataFeedbackService;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.constants.Constants;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.entities.VacancyDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.data.mongo.services.VacancyDocService;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.threads.actors.DataFeedbackActor;
import com.qooco.boost.threads.models.DataFeedbackCandidate;
import com.qooco.boost.threads.models.DataFeedbackVacancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.qooco.boost.threads.models.DataFeedback.*;

@Service
public class BusinessDataFeedbackServiceImpl implements BusinessDataFeedbackService {
    @Autowired
    private ActorSystem system;
    @Autowired
    private UserCvDocService userCvDocService;

    @Autowired
    private VacancyDocService vacancyDocService;

    @Override
    public BaseResp sendDataFeedbackMessage(int feedbackType) {
        feedBackToAll(feedbackType);
        return new BaseResp();
    }

    private void feedBackToAll(int feedbackType) {
        if(EVENT_BASE_ON_USER_PROFILE.contains(feedbackType)){
            doSendDataFeedbackMessageBaseOnUserProfile(feedbackType);
        } else if (EVENT_BASE_ON_VACANCY.contains(feedbackType)){
            doSendDataFeedbackMessageBaseOnVacancy(feedbackType);
        }
    }

    private void doSendDataFeedbackMessageBaseOnUserProfile(int feedbackType) {
        AtomicLong cvId = new AtomicLong();
        int size;
        if (PROFILE_CODES_SHARED == feedbackType) {
            doSendDataFeedbackMessageBaseOnUserProfile(feedbackType, null);
            return;
        }

        do {
            List<UserCvDoc> userCvDocs = userCvDocService.getByIdGreaterThan(cvId.get(), Constants.DEFAULT_LIMITED_ITEM);

            size = userCvDocs.size();
            userCvDocs.forEach(cv -> {
                doSendDataFeedbackMessageBaseOnUserProfile(feedbackType, cv);
                cvId.set(cv.getId());
            });
        } while (size > 0);
    }

    private void doSendDataFeedbackMessageBaseOnUserProfile(int feedbackType, UserCvDoc cv) {
        var dataFeedback = new DataFeedbackCandidate(feedbackType);
        if (Objects.nonNull(cv)) dataFeedback.userProfileId(cv.getUserProfile().getUserProfileId());
        system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props(DataFeedbackActor.ACTOR_NAME))
                .tell(dataFeedback, ActorRef.noSender());
    }

    private void doSendDataFeedbackMessageBaseOnVacancy(int feedbackType) {
        switch (feedbackType) {
            case PROFILE_NO_QUALIFICATION:
            case PROFILE_NOT_ENOUGH_EXPERIENCE:
            case PROFILE_SALARY_TOO_HIGH:
                system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props(DataFeedbackActor.ACTOR_NAME))
                        .tell(new DataFeedbackVacancy(feedbackType), ActorRef.noSender());
                break;
            default:
                AtomicLong vacancyId = new AtomicLong();
                int size;
                do {
                    List<VacancyDoc> vacancyDocs = vacancyDocService.findOpenVacancyByIdGreaterThan(vacancyId.get(), Constants.DEFAULT_LIMITED_ITEM);

                    size = vacancyDocs.size();
                    vacancyDocs.forEach(v -> {
                        system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system).props(DataFeedbackActor.ACTOR_NAME))
                                .tell(new DataFeedbackVacancy(feedbackType).vacancy(v), ActorRef.noSender());
                        vacancyId.set(v.getId());
                    });
                } while (size > 0);
                break;
        }
    }
}
