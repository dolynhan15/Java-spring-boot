package com.qooco.boost.threads.actors;

import akka.actor.UntypedAbstractActor;
import com.qooco.boost.business.FileStorageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DeleteFileActor extends UntypedAbstractActor {
    protected Logger logger = LogManager.getLogger(DeleteFileActor.class);
    public static final String ACTOR_NAME = "deleteFileActor";

    private FileStorageService fileStorageService;

    public DeleteFileActor(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof Date) {
            Date expiredDate = (Date) message;
            fileStorageService.deleteExpiredFile(expiredDate);
        }
    }
}
