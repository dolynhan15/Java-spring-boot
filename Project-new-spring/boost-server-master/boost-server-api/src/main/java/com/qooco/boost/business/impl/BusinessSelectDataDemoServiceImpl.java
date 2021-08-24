package com.qooco.boost.business.impl;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.qooco.boost.business.BusinessSelectDataDemoService;
import com.qooco.boost.core.thread.SpringExtension;
import com.qooco.boost.enumeration.ResponseStatus;
import com.qooco.boost.models.BaseResp;
import com.qooco.boost.models.request.demo.SelectDataReq;
import com.qooco.boost.threads.actors.CreateSelectDataDemoActor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BusinessSelectDataDemoServiceImpl implements BusinessSelectDataDemoService {
    @Autowired
    private ActorSystem system;

    @Override
    public BaseResp genSelectDataDemo(SelectDataReq req) {
        system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(CreateSelectDataDemoActor.ACTOR_NAME))
                .tell(req, ActorRef.noSender());
        return new BaseResp(ResponseStatus.SUCCESS);
    }

    @Override
    public BaseResp deleteSelectDataDemo(Long companyId) {
        system.actorOf(SpringExtension.SPRING_EXTENSION_PROVIDER.get(system)
                .props(CreateSelectDataDemoActor.ACTOR_NAME))
                .tell(companyId, ActorRef.noSender());
        return new BaseResp(ResponseStatus.SUCCESS);
    }
}
