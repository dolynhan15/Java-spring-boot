package com.qooco.boost.business.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.business.SystemLoggerService;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.threads.actors.SaveSystemLoggerInMongoActor;
import com.qooco.boost.threads.models.SaveSystemLoggerRequestBodyInMongo;
import com.qooco.boost.threads.models.SaveSystemLoggerResponseInMongo;
import com.qooco.boost.threads.models.SaveSystemLoggerUserInMongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

@Service
public class SystemLoggerServiceImpl implements SystemLoggerService {
    @Autowired
    private ActorSystem system;

    ActorRef updater;

    @PostConstruct
    public void init() {
        updater = system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(SaveSystemLoggerInMongoActor.ACTOR_NAME));
    }

    @Override
    public void saveSystemLogger(ServletRequest servletRequest) {
        updater.tell(servletRequest, ActorRef.noSender());
    }

    @Override
    public void saveSystemLogger(Object body) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SaveSystemLoggerRequestBodyInMongo sdo = new SaveSystemLoggerRequestBodyInMongo(request, body);
        updater.tell(sdo, ActorRef.noSender());
    }

    @Override
    public void saveSystemLogger(UserDetails userDetails) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SaveSystemLoggerUserInMongo sdo = new SaveSystemLoggerUserInMongo(request, userDetails);
        updater.tell(sdo, ActorRef.noSender());
    }

    @Override
    public void saveSystemLogger(BaseResp response, String stackTrace) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SaveSystemLoggerResponseInMongo sdo = new SaveSystemLoggerResponseInMongo(request, response, stackTrace);
        updater.tell(sdo, ActorRef.noSender());
    }
}
