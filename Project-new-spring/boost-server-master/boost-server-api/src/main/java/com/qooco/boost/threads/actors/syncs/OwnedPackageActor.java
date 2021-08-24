package com.qooco.boost.threads.actors.syncs;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;
import com.qooco.boost.business.QoocoSyncService;
import com.qooco.boost.data.mongo.entities.OwnedPackageDoc;
import com.qooco.boost.data.mongo.services.OwnedPackageDocService;
import com.qooco.boost.models.qooco.sync.ownedPackage.OwnedPackageResponse;
import com.qooco.boost.threads.services.SyncQualificationService;
import com.qooco.boost.threads.services.SyncUserCVService;
import com.qooco.boost.utils.MongoConverters;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/*
 * Date: 10/16/2018 - 9:28 AM
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class OwnedPackageActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(OwnedPackageActor.class);
    public static final String ACTOR_NAME = "ownedPackageActor";

    private QoocoSyncService qoocoSyncService;
    private OwnedPackageDocService ownedPackageDocService;
    private SyncQualificationService syncQualificationAndUserCV;
    private SyncUserCVService syncUserCVService;

    public OwnedPackageActor(QoocoSyncService qoocoSyncService,
                             OwnedPackageDocService ownedPackageDocService,
                             SyncQualificationService syncQualificationAndUserCV,
                             SyncUserCVService syncUserCVService) {
        this.qoocoSyncService = qoocoSyncService;
        this.ownedPackageDocService = ownedPackageDocService;
        this.syncQualificationAndUserCV = syncQualificationAndUserCV;
        this.syncUserCVService = syncUserCVService;

    }

    @Override
    public void onReceive(Object message) {
        if(message instanceof List) {
            List<Long> userProfiles = (List<Long>) message;
            if(CollectionUtils.isNotEmpty(userProfiles)) {
                for (Long id : userProfiles) {
                    syncOwnedPackage(id);
                }

                if(Objects.nonNull(getSender())) {
                    getSender().tell(userProfiles.get(userProfiles.size() - 1), ActorRef.noSender());
                }

            }
        }
    }

    private void syncOwnedPackage(long userProfile){
        try {
            OwnedPackageDoc packageDoc = ownedPackageDocService.findLatestOwnedPackage(userProfile);
            long timestamp = 0;
            if(Objects.nonNull(packageDoc) && Objects.nonNull(packageDoc.getTimestamp())){
                timestamp = packageDoc.getTimestamp().getTime();
            }
            OwnedPackageResponse ownedPackageResponse = qoocoSyncService.getOwnedPackages(String.valueOf(userProfile), timestamp);

            List<OwnedPackageDoc> ownedPackageDocs = new ArrayList<>();
            if(Objects.nonNull(ownedPackageResponse) && MapUtils.isNotEmpty(ownedPackageResponse.getOwnedPackages())){
                ownedPackageResponse.getOwnedPackages().forEach( (key, value) ->
                        ownedPackageDocs.addAll(MongoConverters.convertToOwnedPackageDoc(userProfile, key, value, ownedPackageResponse.getOwnedPackagesTimestamp()))
                );
                if(CollectionUtils.isNotEmpty(ownedPackageDocs)) {
                    ownedPackageDocService.save(ownedPackageDocs);
                }
            }
        } catch (ResourceAccessException ex) {
            logger.error(ex.getMessage());
        }

    }

}
