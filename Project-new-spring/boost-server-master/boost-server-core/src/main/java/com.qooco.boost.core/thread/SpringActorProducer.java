package com.qooco.boost.core.thread;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import akka.actor.UntypedAbstractActor;
import org.springframework.context.ApplicationContext;

public class SpringActorProducer implements IndirectActorProducer {

    private ApplicationContext context;

    private String actorName;

    public SpringActorProducer(ApplicationContext context, String actorName) {
        this.context = context;
        this.actorName = actorName;
    }

    @Override
    public Actor produce() {
        return (UntypedAbstractActor) context.getBean(actorName);
    }

    @Override
    public Class<? extends UntypedAbstractActor> actorClass() {
        return (Class<? extends UntypedAbstractActor>) context.getType(actorName);
    }
}