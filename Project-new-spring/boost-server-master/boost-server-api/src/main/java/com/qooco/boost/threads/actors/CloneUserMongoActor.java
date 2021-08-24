package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.google.gson.Gson;
import com.qooco.boost.data.mongo.entities.UserCvDoc;
import com.qooco.boost.data.mongo.services.UserCvDocService;
import com.qooco.boost.models.request.admin.CloneStressTestUserReq;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@RequiredArgsConstructor
public class CloneUserMongoActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(CloneUserMongoActor.class);
    public static final String ACTOR_NAME = "cloneUserMongoActor";
    private final  UserCvDocService userCvDocService;

    @Override
    public void onReceive(Object message) {
        if (message instanceof CloneStressTestUserReq) {
            CloneStressTestUserReq req = (CloneStressTestUserReq) message;
            UserCvDoc userCvDoc = userCvDocService.findByUserProfileId(req.getUserProfileId());

            List<UserCvDoc> newUsers = new ArrayList<>();
            for (int i = 1; i <= req.getRepeatNumber(); i++) {
                Gson gson = new Gson();
                UserCvDoc deepCopy = gson.fromJson(gson.toJson(userCvDoc), UserCvDoc.class);
                deepCopy.setId(userCvDoc.getId() + i);
                deepCopy.getUserProfile().setUserProfileId(userCvDoc.getUserProfile().getUserProfileId() + i);
                deepCopy.getUserProfile().setUserProfileId(deepCopy.getUserProfile().getUserProfileId());
                newUsers.add(deepCopy);
                if (i % 1000 == 0) {
                    userCvDocService.save(newUsers);
                    logger.info("Insert ids = " + (newUsers.stream().map(UserCvDoc::getId).collect(Collectors.toList())).toString());
                    newUsers = new ArrayList<>();
                }
            }
        }
    }
}