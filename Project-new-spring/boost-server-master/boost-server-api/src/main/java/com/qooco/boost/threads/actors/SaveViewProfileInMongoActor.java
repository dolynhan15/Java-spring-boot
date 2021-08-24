package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.constants.ViewProfileDocConstants;
import com.qooco.boost.data.mongo.entities.ViewProfileDoc;
import com.qooco.boost.data.mongo.services.ViewProfileDocService;
import com.qooco.boost.models.transfer.ViewProfileTransfer;
import com.qooco.boost.threads.notifications.business.PushNotificationService;
import com.qooco.boost.utils.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SaveViewProfileInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(SaveViewProfileInMongoActor.class);
    public static final String ACTOR_NAME = "saveViewProfileInMongoActor";

    private ViewProfileDocService viewProfileDocService;
    private PushNotificationService pushNotificationService;

    public SaveViewProfileInMongoActor(ViewProfileDocService viewProfileDocService, PushNotificationService pushNotificationService) {
        this.viewProfileDocService = viewProfileDocService;
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof ViewProfileTransfer) {
            ViewProfileDoc viewProfileDoc = new ViewProfileDoc();
            viewProfileDoc.setCandidate(((ViewProfileTransfer) message).getCandidate());
            viewProfileDoc.setViewer(((ViewProfileTransfer) message).getViewer());
            viewProfileDoc.setVacancy(((ViewProfileTransfer) message).getVacancy());
            viewProfileDoc.setCreatedDate(DateUtils.toServerTimeForMongo());
            viewProfileDoc.setUpdatedDate(DateUtils.toServerTimeForMongo());
            viewProfileDoc.setStatus(ViewProfileDocConstants.Status.NOT_VIEWED);
            viewProfileDocService.save(viewProfileDoc);
            pushNotificationService.notifyViewCandidateProfile(viewProfileDoc, true);
        }
    }
}