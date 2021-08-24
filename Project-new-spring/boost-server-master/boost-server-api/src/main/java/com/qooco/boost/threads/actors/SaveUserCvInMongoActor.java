package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.oracle.entities.UserCurriculumVitae;
import com.qooco.boost.data.oracle.entities.UserFit;
import com.qooco.boost.data.oracle.entities.UserProfile;
import com.qooco.boost.threads.services.SyncUserCVService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveUserCvInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveUserCvInMongoActor.class);
    public static final String ACTOR_NAME = "saveUserCvInMongoActor";

    private SyncUserCVService syncUserCVService;

    public SaveUserCvInMongoActor(SyncUserCVService syncUserCVService) {
        this.syncUserCVService = syncUserCVService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof UserCurriculumVitae) {
            syncUserCVService.syncUserCV((UserCurriculumVitae) message, false);
        } else if (message instanceof UserProfile) {
            syncUserCVService.syncUserCV((UserProfile) message, false);
        } else if (message instanceof Long) {
            syncUserCVService.updateUserCvPersonalityInOracleAndMongo((Long) message);
        } else if (message instanceof UserFit) {
            syncUserCVService.syncUserCV((UserFit) message);
        } else if (message instanceof List  && ((List) message).get(0) instanceof UserFit) {
            syncUserCVService.syncUserCV((List<UserFit>) message);
        }
    }
}