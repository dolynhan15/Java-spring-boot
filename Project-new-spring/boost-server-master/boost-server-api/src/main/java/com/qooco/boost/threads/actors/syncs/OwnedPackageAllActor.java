package com.qooco.boost.threads.actors.syncs;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.UntypedAbstractActor;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.data.oracle.services.UserProfileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/*
 * Date: 10/16/2018 - 9:28 AM
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnedPackageAllActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(OwnedPackageAllActor.class);
    public static final String ACTOR_NAME = "ownedPackageAllActor";
    private static final int PAGE_SIZE = 10;

    @Autowired
    private ActorSystem system;

    private UserProfileService userProfileService;

    public OwnedPackageAllActor(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof Long) {
            List<Long> userProfileIds = userProfileService.findNextUserProfileIds((Long)message, PAGE_SIZE);
            if(CollectionUtils.isNotEmpty(userProfileIds)) {
                logger.info("Sync owned package for users: " + userProfileIds.toString());
                syncOwnedPackages(userProfileIds);
            }
        }
    }

    private void syncOwnedPackages(List<Long> userProfileIds){
        ActorRef caller = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(OwnedPackageActor.ACTOR_NAME));

        ActorRef itself = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(OwnedPackageAllActor.ACTOR_NAME));

        caller.tell(userProfileIds, itself);

    }
}
