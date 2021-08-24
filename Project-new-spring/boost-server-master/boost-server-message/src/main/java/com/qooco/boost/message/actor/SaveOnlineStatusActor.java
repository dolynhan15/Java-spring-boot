package com.qooco.boost.message.actor;

import akka.actor.ActorSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaveOnlineStatusActor {

    @Autowired
    private ActorSystem system;

}
