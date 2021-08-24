package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.data.mongo.entities.MessageDoc;
import com.qooco.boost.data.mongo.services.MessageDocService;
import com.qooco.boost.data.oracle.entities.CompanyJoinRequest;
import com.qooco.boost.utils.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/*
* Copyright: Falcon Team - AxonActive
 User: nhphuc
 Date: 8/7/2018 - 10:47 AM
*/
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpdateJoinCompanyRequestOtherAdminInMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(UpdateJoinCompanyRequestOtherAdminInMongoActor.class);
    public static final String ACTOR_NAME = "updateJoinCompanyRequestOtherAdminInMongoActor";

    private MessageDocService messageDocService;


    public UpdateJoinCompanyRequestOtherAdminInMongoActor(MessageDocService messageDocService) {
        this.messageDocService = messageDocService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof CompanyJoinRequest) {
            CompanyJoinRequest joinRequest = ((CompanyJoinRequest) message);
            Long joinRequestId = joinRequest.getCompanyJoinRequestId();
            if (Objects.nonNull(joinRequestId)) {
                List<MessageDoc> messageDocs = messageDocService.findByJoinCompanyRequestId(joinRequestId);
                for (MessageDoc messageDoc : messageDocs) {
                    messageDoc.getAuthorizationMessage().setResponseStatus(joinRequest.getStatus().getCode());
                }
                messageDocService.save(messageDocs);

                logger.info(StringUtil.append("Update JoinCompanyRequestOtherAdminInMongoActor Id =", joinRequest.getCompanyJoinRequestId().toString()));
            }
        }
    }

}